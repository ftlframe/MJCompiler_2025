// generated with ast extension for cup
// version 0.8
// 16/6/2025 17:12:51


package rs.ac.bg.etf.pp1.ast;

public class DesignatorMatrixElem extends Designator {

    private MyObj MyObj;
    private Expr Expr;
    private Expr Expr1;

    public DesignatorMatrixElem (MyObj MyObj, Expr Expr, Expr Expr1) {
        this.MyObj=MyObj;
        if(MyObj!=null) MyObj.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.Expr1=Expr1;
        if(Expr1!=null) Expr1.setParent(this);
    }

    public MyObj getMyObj() {
        return MyObj;
    }

    public void setMyObj(MyObj MyObj) {
        this.MyObj=MyObj;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public Expr getExpr1() {
        return Expr1;
    }

    public void setExpr1(Expr Expr1) {
        this.Expr1=Expr1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MyObj!=null) MyObj.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
        if(Expr1!=null) Expr1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MyObj!=null) MyObj.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(Expr1!=null) Expr1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MyObj!=null) MyObj.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(Expr1!=null) Expr1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorMatrixElem(\n");

        if(MyObj!=null)
            buffer.append(MyObj.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr1!=null)
            buffer.append(Expr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorMatrixElem]");
        return buffer.toString();
    }
}
