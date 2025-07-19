// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class MoreActuals extends MoreActPars {

    private MoreActPars MoreActPars;
    private Expr Expr;

    public MoreActuals (MoreActPars MoreActPars, Expr Expr) {
        this.MoreActPars=MoreActPars;
        if(MoreActPars!=null) MoreActPars.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
    }

    public MoreActPars getMoreActPars() {
        return MoreActPars;
    }

    public void setMoreActPars(MoreActPars MoreActPars) {
        this.MoreActPars=MoreActPars;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MoreActPars!=null) MoreActPars.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MoreActPars!=null) MoreActPars.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MoreActPars!=null) MoreActPars.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MoreActuals(\n");

        if(MoreActPars!=null)
            buffer.append(MoreActPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MoreActuals]");
        return buffer.toString();
    }
}
