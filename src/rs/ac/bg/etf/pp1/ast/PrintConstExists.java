// generated with ast extension for cup
// version 0.8
// 19/7/2025 8:10:20


package rs.ac.bg.etf.pp1.ast;

public class PrintConstExists extends PrintConst {

    private Integer num;

    public PrintConstExists (Integer num) {
        this.num=num;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num=num;
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
        buffer.append("PrintConstExists(\n");

        buffer.append(" "+tab+num);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintConstExists]");
        return buffer.toString();
    }
}
