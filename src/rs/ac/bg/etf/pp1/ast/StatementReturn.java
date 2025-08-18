// generated with ast extension for cup
// version 0.8
// 18/7/2025 20:7:26


package rs.ac.bg.etf.pp1.ast;

public class StatementReturn extends Statement {

    private ExprMaybe ExprMaybe;

    public StatementReturn (ExprMaybe ExprMaybe) {
        this.ExprMaybe=ExprMaybe;
        if(ExprMaybe!=null) ExprMaybe.setParent(this);
    }

    public ExprMaybe getExprMaybe() {
        return ExprMaybe;
    }

    public void setExprMaybe(ExprMaybe ExprMaybe) {
        this.ExprMaybe=ExprMaybe;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprMaybe!=null) ExprMaybe.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprMaybe!=null) ExprMaybe.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprMaybe!=null) ExprMaybe.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("StatementReturn(\n");

        if(ExprMaybe!=null)
            buffer.append(ExprMaybe.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [StatementReturn]");
        return buffer.toString();
    }
}
