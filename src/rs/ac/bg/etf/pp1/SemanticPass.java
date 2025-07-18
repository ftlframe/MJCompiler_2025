package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticPass extends VisitorAdaptor {

    boolean errorDetected = false;
    private Struct currentType = null;
    private Obj currentMethod = null;
    
    int nVars;
    int printCallCount = 0;
    int readCallCount = 0;
    int varDeclCount = 0;
    
    Logger log = Logger.getLogger(getClass());

    public SemanticPass() {
        TabExtended.init();
    }

    public boolean passed() {
        return !errorDetected;
    }

    // =================================================================
    // Utility Methods for Error Reporting
    // =================================================================

    public void report_error(String message, SyntaxNode info) {
        errorDetected = true;
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0) {
            msg.append(" na liniji ").append(line);
        }
        log.error(msg.toString());
    }

    public void report_info(String message, SyntaxNode info) {
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0) {
            msg.append(" na liniji ").append(line);
        }
        log.info(msg.toString());
    }
    
    // =================================================================
    // Program, Type, and Declaration Processing
    // =================================================================

    public void visit(ProgName progName) {
        progName.obj = TabExtended.insert(Obj.Prog, progName.getProgName(), TabExtended.noType);
        TabExtended.openScope();
    }

    public void visit(Program program) {
    	nVars = TabExtended.currentScope.getnVars();
        TabExtended.chainLocalSymbols(program.getProgName().obj);
        TabExtended.closeScope();
    }
    
    public void visit(Type type) {
        Obj typeNode = TabExtended.find(type.getTypeName());
        if (typeNode == TabExtended.noObj) {
            report_error("Greska: Tip '" + type.getTypeName() + "' nije pronadjen.", type);
            type.struct = TabExtended.noType;
        } else {
            if (Obj.Type == typeNode.getKind()) {
                type.struct = typeNode.getType();
            } else {
                report_error("Greska: Ime '" + type.getTypeName() + "' ne predstavlja tip.", type);
                type.struct = TabExtended.noType;
            }
        }
        currentType = type.struct;
    }

    // CONSTANT DECLARATIONS
    public void visit(FormalConstDeclr formalConst) {
        if (TabExtended.currentScope.findSymbol(formalConst.getConstName()) != null) {
            report_error("Greska: Konstanta '" + formalConst.getConstName() + "' je vec deklarisana.", formalConst);
            return;
        }
        if (formalConst.getConstVals().struct == null) return;
        if (!currentType.equals(formalConst.getConstVals().struct)) {
            report_error("Greska: Tip konstante i vrednosti se ne poklapaju za '" + formalConst.getConstName() + "'.", formalConst);
            return;
        }
        report_info("Deklarisana konstanta '" + formalConst.getConstName() + "'", formalConst);
        Obj con = TabExtended.insert(Obj.Con, formalConst.getConstName(), currentType);
        
        // Attach the created Obj node to the AST node
        formalConst.obj = con;
    }
    
    public void visit(MoreConstValsExist moreConsts) {
        if (TabExtended.currentScope.findSymbol(moreConsts.getConstName()) != null) {
            report_error("Greska: Konstanta '" + moreConsts.getConstName() + "' je vec deklarisana.", moreConsts);
            return;
        }
        if (currentType == null || moreConsts.getConstVals().struct == null) return;
        if (!currentType.equals(moreConsts.getConstVals().struct)) {
            report_error("Greska: Tip konstante i vrednosti se ne poklapaju za '" + moreConsts.getConstName() + "'.", moreConsts);
            return;
        }
        report_info("Deklarisana konstanta '" + moreConsts.getConstName() + "'", moreConsts);
        Obj con = TabExtended.insert(Obj.Con, moreConsts.getConstName(), currentType);
        
        // Attach the created Obj node to the AST node
        moreConsts.obj = con;
    }
    
    // VARIABLE DECLARATIONS (GLOBAL AND LOCAL)
    private void declareVar(String varName, SyntaxNode info) {
        if (TabExtended.currentScope.findSymbol(varName) != null) {
            report_error("Greska: Promenljiva '" + varName + "' je vec deklarisana u ovom opsegu.", info);
            return;
        }
        report_info("Deklarisana promenljiva '" + varName + "'", info);
        TabExtended.insert(Obj.Var, varName, currentType);
        //var.setAdr(Code.dataSize++);
        varDeclCount++;
    }

    private void declareArray(String arrayName, Brackets brackets, SyntaxNode info) {
        if (TabExtended.currentScope.findSymbol(arrayName) != null) {
            report_error("Greska: Niz/Matrica '" + arrayName + "' je vec deklarisana u ovom opsegu.", info);
            return;
        }
        Struct arrayType = new Struct(Struct.Array, currentType);
        if (brackets instanceof BracketsMatrix) {
            report_info("Deklarisana matrica '" + arrayName + "'", info);
            arrayType = new Struct(Struct.Array, arrayType);
        } else {
            report_info("Deklarisan niz '" + arrayName + "'", info);
        }
        TabExtended.insert(Obj.Var, arrayName, arrayType);
//        arr.setAdr(Code.dataSize++);
    }

    public void visit(VarDeclVar varDecl) { declareVar(varDecl.getVarName(), varDecl); varDeclCount++; }
    public void visit(VarDeclArray varDecl) { declareArray(varDecl.getArrayName(), varDecl.getBrackets(), varDecl); varDeclCount++; }
    public void visit(MoreVarDeclsVars moreVars) { declareVar(moreVars.getVarName(), moreVars); }
    public void visit(MoreVarDeclsArray moreArrays) { declareArray(moreArrays.getArrayName(), moreArrays.getBrackets(), moreArrays); }
    public void visit(VarDeclInFunctionVars varDecl) { declareVar(varDecl.getVarName(), varDecl); }
    public void visit(VarDeclInFunctionArray varDecl) { declareArray(varDecl.getArrName(), varDecl.getBrackets(), varDecl); }

    // =================================================================
    // Method Declarations
    // =================================================================

    public void visit(MethodDeclNameVOID methodDeclName) {
        currentMethod = TabExtended.insert(Obj.Meth, methodDeclName.getMethodName(), TabExtended.noType);
        methodDeclName.obj = currentMethod;
        TabExtended.openScope();
        report_info("Obradjuje se funkcija " + methodDeclName.getMethodName(), methodDeclName);
    }
    
    public void visit(MethodDeclNameType methodDeclNameType) {
        currentMethod = TabExtended.insert(Obj.Meth, methodDeclNameType.getMethodName(), methodDeclNameType.getType().struct);
        methodDeclNameType.obj = currentMethod;
        TabExtended.openScope();
        report_info("Obradjuje se funkcija " + methodDeclNameType.getMethodName(), methodDeclNameType);
    }

    public void visit(MethodDecl methodDecl) {
        TabExtended.chainLocalSymbols(currentMethod);
        TabExtended.closeScope();
        currentMethod = null;
    }
    
    // =================================================================
    // Designator Resolution
    // =================================================================
    
    public void visit(MyObj myObj) {
        Obj obj = TabExtended.find(myObj.getName());
        if (obj == TabExtended.noObj) {
            report_error("Greska: Ime '" + myObj.getName() + "' nije definisano.", myObj);
        }
        myObj.obj = obj;
    }

    public void visit(DesignatorIdent designator) {
        designator.obj = designator.getMyObj().obj;
    }

    public void visit(DesignatorArrayElem designator) {
        Obj arrayObj = designator.getMyObj().obj;
        if (arrayObj == TabExtended.noObj) { designator.obj = TabExtended.noObj; return; }
        if (arrayObj.getType().getKind() != Struct.Array) {
            report_error("Greska: Promenljiva '" + arrayObj.getName() + "' nije niz.", designator);
            designator.obj = TabExtended.noObj; return;
        }
        if (designator.getExpr().struct == null) { designator.obj = TabExtended.noObj; return; }
        if (!designator.getExpr().struct.equals(TabExtended.intType)) {
            report_error("Greska: Indeks niza mora biti tipa int.", designator);
            designator.obj = TabExtended.noObj; return;
        }
        designator.obj = new Obj(Obj.Elem, arrayObj.getName() + "_elem", arrayObj.getType().getElemType());
    }

    public void visit(DesignatorMatrixElem designator) {
        Obj matrixObj = designator.getMyObj().obj;
        if (matrixObj == TabExtended.noObj) { designator.obj = TabExtended.noObj; return; }
        if (matrixObj.getType().getKind() != Struct.Array || matrixObj.getType().getElemType().getKind() != Struct.Array) {
            report_error("Greska: Promenljiva '" + matrixObj.getName() + "' nije matrica.", designator);
            designator.obj = TabExtended.noObj; return;
        }
        if (designator.getExpr().struct == null || designator.getExpr1().struct == null) { designator.obj = TabExtended.noObj; return; }
        if (!designator.getExpr().struct.equals(TabExtended.intType) || !designator.getExpr1().struct.equals(TabExtended.intType)) {
             report_error("Greska: Indeksi matrice moraju biti tipa int.", designator);
             designator.obj = TabExtended.noObj; return;
        }
        designator.obj = new Obj(Obj.Elem, matrixObj.getName() + "_elem", matrixObj.getType().getElemType().getElemType());
    }
    
    // =================================================================
    // Statements
    // =================================================================

    public void visit(DesignatorStatementAssign desAssign) {
        Obj lhsObj = desAssign.getDesignator().obj;
        Struct rhsType = desAssign.getExpr().struct;
        if (lhsObj == TabExtended.noObj || rhsType == null) return;
        if (lhsObj.getKind() != Obj.Var && lhsObj.getKind() != Obj.Elem && lhsObj.getKind() != Obj.Fld) {
             report_error("Leva strana dodele mora biti promenljiva, element niza ili polje.", desAssign); return;
        }
        if (!rhsType.assignableTo(lhsObj.getType())) {
            report_error("Nekompatibilni tipovi u dodeli.", desAssign);
        }
    }

    public void visit(DesignatorStatementInc desInc) {
        Obj designatorObj = desInc.getDesignator().obj;
        if (designatorObj == TabExtended.noObj) return;
        if (designatorObj.getKind() != Obj.Var && designatorObj.getKind() != Obj.Elem && designatorObj.getKind() != Obj.Fld) {
             report_error("Operand za '++' mora biti promenljiva, element niza ili polje.", desInc); return;
        }
        if (!designatorObj.getType().equals(TabExtended.intType)) {
            report_error("Operand za '++' mora biti tipa int.", desInc);
        }
    }

    public void visit(DesignatorStatementDec desDec) {
        Obj designatorObj = desDec.getDesignator().obj;
        if (designatorObj == TabExtended.noObj) return;
        if (designatorObj.getKind() != Obj.Var && designatorObj.getKind() != Obj.Elem && designatorObj.getKind() != Obj.Fld) {
             report_error("Operand za '--' mora biti promenljiva, element niza ili polje.", desDec); return;
        }
        if (!designatorObj.getType().equals(TabExtended.intType)) {
            report_error("Operand za '--' mora biti tipa int.", desDec);
        }
    }
    
    public void visit(StatementRead readStmt) {
        Obj designatorObj = readStmt.getDesignator().obj;
        if (designatorObj == TabExtended.noObj) return;
        if (designatorObj.getKind() != Obj.Var && designatorObj.getKind() != Obj.Elem && designatorObj.getKind() != Obj.Fld) {
             report_error("Argument za 'read' mora biti promenljiva, element niza ili polje.", readStmt); return;
        }
        Struct t = designatorObj.getType();
        if (!t.equals(TabExtended.intType) && !t.equals(TabExtended.charType) && !t.equals(TabExtended.boolType)) {
            report_error("Argument za 'read' mora biti tipa int, char ili bool.", readStmt);
        }
        readCallCount++;
    }

    public void visit(StatementPrint printStmt) {
        Struct t = printStmt.getExpr().struct;
        if (t == null) return;
        boolean isPrintable = t.equals(TabExtended.intType) || t.equals(TabExtended.charType) || t.equals(TabExtended.boolType) || t.getKind() == Struct.Array;
        if (!isPrintable) {
            report_error("Argument za 'print' mora biti tipa int, char, bool, ili niz/set.", printStmt);
        }
        printCallCount++;
    }

    // =================================================================
    // Expressions and Factors
    // =================================================================
    
    public void visit(ConstNum cn) { cn.struct = TabExtended.intType; }
    public void visit(ConstChar cc) { cc.struct = TabExtended.charType; }
    public void visit(ConstBool cb) { cb.struct = TabExtended.boolType; }
    
    public void visit(FactorNum factor) { factor.struct = TabExtended.intType; }
    public void visit(FactorChar factor) { factor.struct = TabExtended.charType; }
    public void visit(FactorBool factor) { factor.struct = TabExtended.boolType; }

    public void visit(FactorDesignator factor) {
        if(factor.getDesignator().obj != null)
            factor.struct = factor.getDesignator().obj.getType();
        else
            factor.struct = TabExtended.noType;
    }

    public void visit(FactorNewArray factorNewArray) {
        if (factorNewArray.getExpr().struct == null) return;
        if (!factorNewArray.getExpr().struct.equals(TabExtended.intType)) {
            report_error("Greska: Velicina za set ili niz mora biti tipa int.", factorNewArray);
            factorNewArray.struct = TabExtended.noType; return;
        }
        Struct baseType = factorNewArray.getType().struct;
        if (baseType.equals(TabExtended.setType)) {
            factorNewArray.struct = TabExtended.setType;
        } else {
            factorNewArray.struct = new Struct(Struct.Array, baseType);
        }
    }
    
    public void visit(FactorNewMatrix factorNewMatrix) {
		if (factorNewMatrix.getExpr().struct == null || factorNewMatrix.getExpr1().struct == null) return;
        if (!factorNewMatrix.getExpr().struct.equals(TabExtended.intType) || !factorNewMatrix.getExpr1().struct.equals(TabExtended.intType)) {
			report_error("Indeksi matrice moraju biti tipa int.", factorNewMatrix);
            factorNewMatrix.struct = TabExtended.noType; return;
		}
		Struct baseType = factorNewMatrix.getType().struct;
        Struct arrayType = new Struct(Struct.Array, baseType);
        factorNewMatrix.struct = new Struct(Struct.Array, arrayType);
	}

    public void visit(FactorExpr factorExpr) { factorExpr.struct = factorExpr.getExpr().struct; }

    public void visit(TermMulOpFactor term) {
        Struct termType = term.getTerm().struct;
        Struct factorType = term.getFactor().struct;
        if(termType == null || factorType == null) { term.struct = TabExtended.noType; return; }
        if (termType.equals(TabExtended.intType) && factorType.equals(TabExtended.intType)) {
            term.struct = TabExtended.intType;
        } else {
            report_error("Operandi za mnozenje/deljenje moraju biti tipa int.", term);
            term.struct = TabExtended.noType;
        }
    }
    
    public void visit(FactorNoTerm term) {
        term.struct = term.getFactor().struct;
    }
    
    public void visit(ExprAddopTerm expr) {
        Struct exprType = expr.getExpr().struct;
        Struct termType = expr.getTerm().struct;
        if(exprType == null || termType == null) { expr.struct = TabExtended.noType; return; }
        if (exprType.equals(TabExtended.intType) && termType.equals(TabExtended.intType)) {
            expr.struct = TabExtended.intType;
        } else {
            report_error("Operandi za sabiranje/oduzimanje moraju biti tipa int.", expr);
            expr.struct = TabExtended.noType;
        }
    }

    public void visit(ExprMinus expr) {
        if (expr.getTerm().struct == null) { expr.struct = TabExtended.noType; return; }
        if (!expr.getTerm().struct.equals(TabExtended.intType)) {
             report_error("Izraz ispred koga stoji minus mora biti tipa int.", expr);
             expr.struct = TabExtended.noType;
        } else {
            expr.struct = TabExtended.intType;
        }
    }

    public void visit(ExprTerm expr) {
        expr.struct = expr.getTerm().struct;
    }

    // =================================================================
    // Visitors for new productions
    // =================================================================
    
    public void visit(ExprUnion expr) {
        Struct lhs = expr.getExpr().struct;
        Struct rhs = expr.getTerm().struct;
        if (lhs != null && rhs != null && lhs.equals(TabExtended.setType) && rhs.equals(TabExtended.setType)) {
            expr.struct = TabExtended.setType;
        } else {
            report_error("Operatori za uniju moraju biti tipa set.", expr);
            expr.struct = TabExtended.noType;
        }
    }

    // =================================================================
    // Visitors for function calls
    // =================================================================
    
    // Handles calls like funcCall();
    
    public void visit(DesignatorStatementCall call) {
        Obj func = call.getDesignator().obj;
        if (func.getKind() != Obj.Meth) {
            report_error("Greska: '" + func.getName() + "' nije metoda.", call);
            return;
        }
        checkActualParams(func, call.getActPars(), call);
    }
    
    // Handles calls like a = funcCall();
    
    public void visit(FactorFuncCall call) {
        Obj func = call.getDesignator().obj;
        if (func.getKind() != Obj.Meth) {
            report_error("Greska: '" + func.getName() + "' nije metoda.", call);
            call.struct = TabExtended.noType;
            return;
        }
        if (func.getType() == TabExtended.noType) {
        	report_error("Greska: Metoda '" + func.getName() + "' je tipa void i ne moze se koristiti u izrazu.", call);
        	call.struct = TabExtended.noType;
        	return;
        }
        

        // SPECIAL CASE for add(set, int)
        if (func.getName().equals("add")) {
            List<Struct> actuals = collectActualParams(call.getActPars());
            if (actuals.size() != 2) {
                report_error("Greska: Metoda 'add' ocekuje 2 argumenta.", call);
            } else if (actuals.get(0) != TabExtended.setType || actuals.get(1) != Tab.intType) {
                report_error("Greska: Metoda 'add' mora biti pozvana sa (set, int).", call);
            }
            return; // Skip generic check
        }

        // SPECIAL CASE for addAll(set, int[])
        if (func.getName().equals("addAll")) {
            List<Struct> actuals = collectActualParams(call.getActPars());
            Struct intArrayType = new Struct(Struct.Array, Tab.intType);
            if (actuals.size() != 2) {
                report_error("Greska: Metoda 'addAll' ocekuje 2 argumenta.", call);
            } else if (actuals.get(0) != TabExtended.setType || !actuals.get(1).equals(intArrayType)) {
                report_error("Greska: Metoda 'addAll' mora biti pozvana sa (set, int[]).", call);
            }
            return; // Skip generic check
        }
        
        // <<< SPECIAL CASE FOR len() >>>
        if (func == TabExtended.lenMeth) {
            List<Struct> actuals = collectActualParams(call.getActPars());
            if (actuals.size() != 1) {
                report_error("Greska: Funkcija 'len' ocekuje tacno 1 argument.", call);
                call.struct = TabExtended.noType;
                return;
            }
            Struct argType = actuals.get(0);
            if (argType.getKind() != Struct.Array) {
                report_error("Greska: Argument za funkciju 'len' mora biti niz.", call);
                call.struct = TabExtended.noType;
                return;
            }
            // Call is valid, return type is int.
            call.struct = TabExtended.intType;
            return; // Skip generic check
        }
        
        checkActualParams(func, call.getActPars(), call);
        call.struct = func.getType();
    }

    private void checkActualParams(Obj func, ActPars actPars, SyntaxNode info) {
        List<Struct> actuals = collectActualParams(actPars);
        Collection<Obj> formals = func.getLocalSymbols();
        if (actuals.size() != func.getLevel()) {
            report_error("Greska: Broj prosledjenih argumenata (" + actuals.size() + ") ne odgovara broju parametara metode '" + func.getName() + "' (" + func.getLevel() + ").", info);
            return;
        }

        List<Obj> formalParamList = new ArrayList<>(formals);
        for (int i = 0; i < actuals.size(); i++) {
            Struct actualType = actuals.get(i);
            Struct formalType = formalParamList.get(i).getType();
            
            if (actualType == null) continue; // Error already reported in expression

            if (!actualType.assignableTo(formalType)) {
                report_error("Greska: Tip " + (i + 1) + ". argumenta u pozivu metode '" + func.getName() + "' nije kompatibilan sa ocekivanim tipom.", info);
            }
        }
    }

    private List<Struct> collectActualParams(ActPars actPars) {
        List<Struct> actuals = new ArrayList<>();
        if (actPars instanceof NoActuals) {
            return actuals;
        }
        
        Actuals params = (Actuals) actPars;
        
        // Traverse the list of parameters from left to right
        List<Expr> exprList = new ArrayList<>();
        MoreActPars more = params.getMoreActPars();
        exprList.add(params.getExpr());
        while(more instanceof MoreActuals) {
            exprList.add(((MoreActuals)more).getExpr());
            more = ((MoreActuals)more).getMoreActPars();
        }
        
        // Add structs in the correct order
        for(Expr e : exprList) {
            actuals.add(e.struct);
        }
        
        return actuals;
    }
}
