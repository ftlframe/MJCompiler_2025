// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class MethodDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private MethodDeclName MethodDeclName;
    private FormPars FormPars;
    private VarDeclInFunction VarDeclInFunction;
    private StatementList StatementList;

    public MethodDecl (MethodDeclName MethodDeclName, FormPars FormPars, VarDeclInFunction VarDeclInFunction, StatementList StatementList) {
        this.MethodDeclName=MethodDeclName;
        if(MethodDeclName!=null) MethodDeclName.setParent(this);
        this.FormPars=FormPars;
        if(FormPars!=null) FormPars.setParent(this);
        this.VarDeclInFunction=VarDeclInFunction;
        if(VarDeclInFunction!=null) VarDeclInFunction.setParent(this);
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
    }

    public MethodDeclName getMethodDeclName() {
        return MethodDeclName;
    }

    public void setMethodDeclName(MethodDeclName MethodDeclName) {
        this.MethodDeclName=MethodDeclName;
    }

    public FormPars getFormPars() {
        return FormPars;
    }

    public void setFormPars(FormPars FormPars) {
        this.FormPars=FormPars;
    }

    public VarDeclInFunction getVarDeclInFunction() {
        return VarDeclInFunction;
    }

    public void setVarDeclInFunction(VarDeclInFunction VarDeclInFunction) {
        this.VarDeclInFunction=VarDeclInFunction;
    }

    public StatementList getStatementList() {
        return StatementList;
    }

    public void setStatementList(StatementList StatementList) {
        this.StatementList=StatementList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodDeclName!=null) MethodDeclName.accept(visitor);
        if(FormPars!=null) FormPars.accept(visitor);
        if(VarDeclInFunction!=null) VarDeclInFunction.accept(visitor);
        if(StatementList!=null) StatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodDeclName!=null) MethodDeclName.traverseTopDown(visitor);
        if(FormPars!=null) FormPars.traverseTopDown(visitor);
        if(VarDeclInFunction!=null) VarDeclInFunction.traverseTopDown(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodDeclName!=null) MethodDeclName.traverseBottomUp(visitor);
        if(FormPars!=null) FormPars.traverseBottomUp(visitor);
        if(VarDeclInFunction!=null) VarDeclInFunction.traverseBottomUp(visitor);
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDecl(\n");

        if(MethodDeclName!=null)
            buffer.append(MethodDeclName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormPars!=null)
            buffer.append(FormPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclInFunction!=null)
            buffer.append(VarDeclInFunction.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDecl]");
        return buffer.toString();
    }
}
