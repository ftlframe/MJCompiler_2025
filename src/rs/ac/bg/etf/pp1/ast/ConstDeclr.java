// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclr implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private FormalConstDeclr FormalConstDeclr;
    private MoreConstVals MoreConstVals;

    public ConstDeclr (FormalConstDeclr FormalConstDeclr, MoreConstVals MoreConstVals) {
        this.FormalConstDeclr=FormalConstDeclr;
        if(FormalConstDeclr!=null) FormalConstDeclr.setParent(this);
        this.MoreConstVals=MoreConstVals;
        if(MoreConstVals!=null) MoreConstVals.setParent(this);
    }

    public FormalConstDeclr getFormalConstDeclr() {
        return FormalConstDeclr;
    }

    public void setFormalConstDeclr(FormalConstDeclr FormalConstDeclr) {
        this.FormalConstDeclr=FormalConstDeclr;
    }

    public MoreConstVals getMoreConstVals() {
        return MoreConstVals;
    }

    public void setMoreConstVals(MoreConstVals MoreConstVals) {
        this.MoreConstVals=MoreConstVals;
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
        if(FormalConstDeclr!=null) FormalConstDeclr.accept(visitor);
        if(MoreConstVals!=null) MoreConstVals.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormalConstDeclr!=null) FormalConstDeclr.traverseTopDown(visitor);
        if(MoreConstVals!=null) MoreConstVals.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormalConstDeclr!=null) FormalConstDeclr.traverseBottomUp(visitor);
        if(MoreConstVals!=null) MoreConstVals.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclr(\n");

        if(FormalConstDeclr!=null)
            buffer.append(FormalConstDeclr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MoreConstVals!=null)
            buffer.append(MoreConstVals.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclr]");
        return buffer.toString();
    }
}
