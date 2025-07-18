// generated with ast extension for cup
// version 0.8
// 18/6/2025 19:2:54


package rs.ac.bg.etf.pp1.ast;

public class DesignatorIdent extends Designator {

    private MyObj MyObj;

    public DesignatorIdent (MyObj MyObj) {
        this.MyObj=MyObj;
        if(MyObj!=null) MyObj.setParent(this);
    }

    public MyObj getMyObj() {
        return MyObj;
    }

    public void setMyObj(MyObj MyObj) {
        this.MyObj=MyObj;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MyObj!=null) MyObj.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MyObj!=null) MyObj.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MyObj!=null) MyObj.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorIdent(\n");

        if(MyObj!=null)
            buffer.append(MyObj.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorIdent]");
        return buffer.toString();
    }
}
