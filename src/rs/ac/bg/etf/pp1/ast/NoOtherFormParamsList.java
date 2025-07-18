// generated with ast extension for cup
// version 0.8
// 18/6/2025 19:2:54


package rs.ac.bg.etf.pp1.ast;

public class NoOtherFormParamsList extends OtherFormParamsList {

    public NoOtherFormParamsList () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NoOtherFormParamsList(\n");

        buffer.append(tab);
        buffer.append(") [NoOtherFormParamsList]");
        return buffer.toString();
    }
}
