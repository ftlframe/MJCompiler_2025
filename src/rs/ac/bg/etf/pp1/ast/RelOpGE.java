// generated with ast extension for cup
// version 0.8
// 18/7/2025 20:7:26


package rs.ac.bg.etf.pp1.ast;

public class RelOpGE extends RelOp {

    public RelOpGE () {
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
        buffer.append("RelOpGE(\n");

        buffer.append(tab);
        buffer.append(") [RelOpGE]");
        return buffer.toString();
    }
}
