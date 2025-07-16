package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;

	public int getMainPc() {
		return mainPc;
	}

	/**
	 * Helper function to retrieve the integer value of a constant from the AST.
	 * @param node The AST node representing a constant value.
	 * @return The integer representation of the constant.
	 */
	private int getConstVal(SyntaxNode node) {
		if (node instanceof ConstNum)
			return ((ConstNum) node).getNumVal();
		if (node instanceof ConstChar)
			return ((ConstChar) node).getCharVal();
		if (node instanceof ConstBool) {
			return "true".equalsIgnoreCase(((ConstBool) node).getBoolVal()) ? 1 : 0;
		}
		return -1; // Should not happen with a valid AST
	}

	// =================================================================
	// ## Method Processing
	// =================================================================

	/**
	 * Visits a void method declaration name.
	 * Sets up the entry point for the method.
	 */
	public void visit(MethodDeclNameVOID method) {
		// Check if this is the main method to record its entry point
		if ("main".equalsIgnoreCase(method.getMethodName())) {
			mainPc = Code.pc;
		}
		method.obj.setAdr(Code.pc); // Set the method's starting address

		// Generate the 'enter' instruction.
		// Opcode: enter num_params, num_total_vars
		Code.put(Code.enter);
		Code.put(method.obj.getLevel()); // Number of formal parameters
		Code.put(method.obj.getLocalSymbols().size()); // Total variables (params + locals)
	}
	
	/**
	 * Visits a non-void method declaration name.
	 * Sets up the entry point for the method.
	 */
	public void visit(MethodDeclNameType method) {
		method.obj.setAdr(Code.pc); // Set the method's starting address

		// Generate the 'enter' instruction.
		// Opcode: enter num_params, num_total_vars
		Code.put(Code.enter);
		Code.put(method.obj.getLevel()); // Number of formal parameters
		Code.put(method.obj.getLocalSymbols().size()); // Total variables (params + locals)
	}

	/**
	 * Visits the end of any method declaration.
	 * Emits instructions to exit the method scope and return.
	 */
	public void visit(MethodDecl methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	// =================================================================
	// ## Statement Processing
	// =================================================================

	/**
	 * Generates code for the `print` statement.
	 * Handles both `print(expr)` and `print(expr, width)`.
	 */
	public void visit(StatementPrint statementPrint) {
		// If a width is specified, load it.
		if (statementPrint.getPrintConst() instanceof PrintConstExists) {
			// The PrintConstExists visitor will load the constant width.
		} else {
			// If no width is provided, use a default value (e.g., 5).
			Code.loadConst(5); 
		}

		// Choose print instruction based on expression type.
		if (statementPrint.getExpr().struct == Tab.charType) {
			Code.put(Code.bprint); // Print a byte (char)
		} else {
			Code.put(Code.print);  // Print a word (int/bool)
		}
	}

	/**
	 * Loads the constant width for a `print` statement.
	 */
	public void visit(PrintConstExists pConst) {
		Code.loadConst(pConst.getNum());
	}
	
	/**
	 * Generates code for the `read` statement.
	 */
	public void visit(StatementRead stRead) {
		Obj dObj = stRead.getDesignator().obj;

		// Choose read instruction based on designator type.
		if (dObj.getType().equals(Tab.charType)) {
			Code.put(Code.bread); // Read a character
		} else {
			Code.put(Code.read);  // Read an integer/boolean
		}
		
		// Store the read value into the designator.
		Code.store(dObj);
	}
	
	// =================================================================
	// ## Assignment, Increment, Decrement
	// =================================================================

	/**
	 * Generates code for an assignment statement.
	 * The expression result is already on the stack.
	 */
	public void visit(DesignatorStatementAssign desigAssign) {
		// The designator visitors have prepared the address/indices.
		// Code.store handles both simple vars and array elements.
		Code.store(desigAssign.getDesignator().obj);
	}

	/**
	 * Generates code for an increment statement (`++`).
	 */
	public void visit(DesignatorStatementInc dsStmt) {
		Obj dObj = dsStmt.getDesignator().obj;
		
		if (dsStmt.getDesignator() instanceof DesignatorIdent) {
			// Simple variable: load, inc, store
			Code.load(dObj); // Load current value
			Code.loadConst(1);
			Code.put(Code.add);
			Code.store(dObj);
		} else {
			// Array element: dup address/index, load, inc, store
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.store(dObj);
		}
	}
	
	/**
	 * Generates code for a decrement statement (`--`).
	 */
	public void visit(DesignatorStatementDec dsStmt) {
		Obj dObj = dsStmt.getDesignator().obj;

		if (dsStmt.getDesignator() instanceof DesignatorIdent) {
			// Simple variable: load, dec, store
			Code.load(dObj); // Load current value
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.store(dObj);
		} else {
			// Array element: dup address/index, load, dec, store
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.store(dObj);
		}
	}
	
	// =================================================================
	// ## Designator Loading
	// =================================================================

	/**
	 * Prepares for access to a simple variable (identifier).
	 * If used in an expression, its value is loaded onto the stack.
	 */
	public void visit(DesignatorIdent designatorIdent) {
		SyntaxNode parent = designatorIdent.getParent();
		// Only load if the designator is used as a factor (RHS of expression)
		// or for inc/dec operations. For assignments, the store address is handled by Code.store.
		if (parent instanceof FactorDesignator || 
			parent instanceof DesignatorStatementInc || 
			parent instanceof DesignatorStatementDec) {
			// Do nothing here; Code.load is called in FactorDesignator or handled in Inc/Dec
		}
	}
	
	/**
	 * Prepares for array element access.
	 * Leaves the array address and index on the stack in the correct order.
	 */
	public void visit(DesignatorArrayElem arrayElem) {
		// The index expression is already evaluated and its result is on the stack.
		// Stack: ..., index
		// We need to load the array's base address.
		Code.load(arrayElem.getMyObj().obj); // Stack: ..., index, addr
		
		// Swap to get the correct order for access instructions (addr, index).
		Code.put(Code.dup_x1); // Stack: ..., addr, index, addr
		Code.put(Code.pop);    // Stack: ..., addr, index
	}
	
	// =================================================================
	// ## Expression and Factor Processing
	// =================================================================

	public void visit(ExprMinus exprMinus) {
		Code.put(Code.neg);
	}
	
	public void visit(ExprAddopTerm exprAddopTerm) {
		if (exprAddopTerm.getAddOp() instanceof AddOpPlus) {
			Code.put(Code.add);
		} else { // AddOpMinus
			Code.put(Code.sub);
		}
	}
	
	public void visit(TermMulOpFactor termMulopFactor) {
		if (termMulopFactor.getMulOp() instanceof MulOpMul) {
			Code.put(Code.mul);
		} else if (termMulopFactor.getMulOp() instanceof MulOpDiv) {
			Code.put(Code.div);
		} else { // MulOpMod
			Code.put(Code.rem);
		}
	}
	
	/**
	 * Loads the value of a designator when used as a factor in an expression.
	 */
	public void visit(FactorDesignator factor) {
		// The appropriate designator visitor (Ident or ArrayElem) has prepared
		// the stack. Now, we load the actual value from the address/index.
		Code.load(factor.getDesignator().obj);
	}

	public void visit(FactorNewArray factorNewArray) {
		// The expression for the array size is already on the stack.
		Code.put(Code.newarray);
		// Specify element type: 0 for byte (char), 1 for word (int/bool).
		if (factorNewArray.getType().struct.equals(Tab.charType)) {
			Code.put(0);
		} else {
			Code.put(1);
		}
	}

	// =================================================================
	// ## Constant Loading
	// =================================================================

	/**
	 * For constant declarations, we "pre-calculate" their value and store it
	 * in the Address field of their symbol table object.
	 */
	public void visit(FormalConstDeclr constDeclr) {
		constDeclr.obj.setAdr(getConstVal(constDeclr.getConstVals()));
	}

	public void visit(MoreConstValsExist moreConstDeclr) {
		moreConstDeclr.obj.setAdr(getConstVal(moreConstDeclr.getConstVals()));
	}
	
	// --- Loading constants when used as factors in expressions ---

	public void visit(FactorNum factor) {
		Code.loadConst(factor.getNum());
	}
	
	public void visit(FactorChar factor) {
		Code.loadConst(factor.getCh());
	}
	
	public void visit(FactorBool fBool) {
		Code.loadConst("true".equalsIgnoreCase(fBool.getBl()) ? 1 : 0);
	}
	
	// --- Loading constants directly from AST nodes (e.g., in declarations) ---
	
	public void visit(ConstNum constNum) {
		Code.loadConst(constNum.getNumVal());
	}

	public void visit(ConstChar constChar) {
		Code.loadConst(constChar.getCharVal());
	}

	public void visit(ConstBool constBool) {
		Code.loadConst("true".equalsIgnoreCase(constBool.getBoolVal()) ? 1 : 0);
	}
}