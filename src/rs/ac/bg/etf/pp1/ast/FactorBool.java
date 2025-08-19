// generated with ast extension for cup
// version 0.8
// 19/7/2025 8:10:20


package rs.ac.bg.etf.pp1.ast;

public class FactorBool extends Factor {

    private String bl;

    public FactorBool (String bl) {
        this.bl=bl;
    }

    public String getBl() {
        return bl;
    }

    public void setBl(String bl) {
        this.bl=bl;
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
        buffer.append("FactorBool(\n");

        buffer.append(" "+tab+bl);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorBool]");
        return buffer.toString();
    }
}
