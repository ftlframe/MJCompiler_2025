// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class NoMoreVarDecls extends MoreVarDecls {

    public NoMoreVarDecls () {
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
        buffer.append("NoMoreVarDecls(\n");

        buffer.append(tab);
        buffer.append(") [NoMoreVarDecls]");
        return buffer.toString();
    }
}
