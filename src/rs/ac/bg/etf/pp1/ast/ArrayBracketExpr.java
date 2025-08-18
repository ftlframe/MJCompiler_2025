// generated with ast extension for cup
// version 0.8
// 18/7/2025 20:7:26


package rs.ac.bg.etf.pp1.ast;

public class ArrayBracketExpr extends OptionBracketExpr {

    private BracketExpression BracketExpression;

    public ArrayBracketExpr (BracketExpression BracketExpression) {
        this.BracketExpression=BracketExpression;
        if(BracketExpression!=null) BracketExpression.setParent(this);
    }

    public BracketExpression getBracketExpression() {
        return BracketExpression;
    }

    public void setBracketExpression(BracketExpression BracketExpression) {
        this.BracketExpression=BracketExpression;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(BracketExpression!=null) BracketExpression.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(BracketExpression!=null) BracketExpression.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(BracketExpression!=null) BracketExpression.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ArrayBracketExpr(\n");

        if(BracketExpression!=null)
            buffer.append(BracketExpression.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ArrayBracketExpr]");
        return buffer.toString();
    }
}
