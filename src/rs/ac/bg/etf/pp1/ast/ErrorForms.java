// generated with ast extension for cup
// version 0.8
// 16/6/2025 17:12:51


package rs.ac.bg.etf.pp1.ast;

public class ErrorForms extends FormPars {

    public ErrorForms () {
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
        buffer.append("ErrorForms(\n");

        buffer.append(tab);
        buffer.append(") [ErrorForms]");
        return buffer.toString();
    }
}
