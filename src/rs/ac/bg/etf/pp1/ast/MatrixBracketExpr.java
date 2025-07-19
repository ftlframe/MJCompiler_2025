// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class MatrixBracketExpr extends OptionBracketExpr {

    private BracketExpression BracketExpression;
    private BracketExpression BracketExpression1;

    public MatrixBracketExpr (BracketExpression BracketExpression, BracketExpression BracketExpression1) {
        this.BracketExpression=BracketExpression;
        if(BracketExpression!=null) BracketExpression.setParent(this);
        this.BracketExpression1=BracketExpression1;
        if(BracketExpression1!=null) BracketExpression1.setParent(this);
    }

    public BracketExpression getBracketExpression() {
        return BracketExpression;
    }

    public void setBracketExpression(BracketExpression BracketExpression) {
        this.BracketExpression=BracketExpression;
    }

    public BracketExpression getBracketExpression1() {
        return BracketExpression1;
    }

    public void setBracketExpression1(BracketExpression BracketExpression1) {
        this.BracketExpression1=BracketExpression1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(BracketExpression!=null) BracketExpression.accept(visitor);
        if(BracketExpression1!=null) BracketExpression1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(BracketExpression!=null) BracketExpression.traverseTopDown(visitor);
        if(BracketExpression1!=null) BracketExpression1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(BracketExpression!=null) BracketExpression.traverseBottomUp(visitor);
        if(BracketExpression1!=null) BracketExpression1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MatrixBracketExpr(\n");

        if(BracketExpression!=null)
            buffer.append(BracketExpression.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(BracketExpression1!=null)
            buffer.append(BracketExpression1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MatrixBracketExpr]");
        return buffer.toString();
    }
}
