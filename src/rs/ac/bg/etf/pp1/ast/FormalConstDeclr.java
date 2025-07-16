// generated with ast extension for cup
// version 0.8
// 16/6/2025 17:12:51


package rs.ac.bg.etf.pp1.ast;

public class FormalConstDeclr implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private ConstType ConstType;
    private String constName;
    private ConstVals ConstVals;

    public FormalConstDeclr (ConstType ConstType, String constName, ConstVals ConstVals) {
        this.ConstType=ConstType;
        if(ConstType!=null) ConstType.setParent(this);
        this.constName=constName;
        this.ConstVals=ConstVals;
        if(ConstVals!=null) ConstVals.setParent(this);
    }

    public ConstType getConstType() {
        return ConstType;
    }

    public void setConstType(ConstType ConstType) {
        this.ConstType=ConstType;
    }

    public String getConstName() {
        return constName;
    }

    public void setConstName(String constName) {
        this.constName=constName;
    }

    public ConstVals getConstVals() {
        return ConstVals;
    }

    public void setConstVals(ConstVals ConstVals) {
        this.ConstVals=ConstVals;
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
        if(ConstType!=null) ConstType.accept(visitor);
        if(ConstVals!=null) ConstVals.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstType!=null) ConstType.traverseTopDown(visitor);
        if(ConstVals!=null) ConstVals.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstType!=null) ConstType.traverseBottomUp(visitor);
        if(ConstVals!=null) ConstVals.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormalConstDeclr(\n");

        if(ConstType!=null)
            buffer.append(ConstType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+constName);
        buffer.append("\n");

        if(ConstVals!=null)
            buffer.append(ConstVals.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormalConstDeclr]");
        return buffer.toString();
    }
}
