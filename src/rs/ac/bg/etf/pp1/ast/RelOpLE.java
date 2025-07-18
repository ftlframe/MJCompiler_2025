// generated with ast extension for cup
// version 0.8
// 18/6/2025 19:2:54


package rs.ac.bg.etf.pp1.ast;

public class RelOpLE extends RelOp {

    public RelOpLE () {
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
        buffer.append("RelOpLE(\n");

        buffer.append(tab);
        buffer.append(") [RelOpLE]");
        return buffer.toString();
    }
}
