// generated with ast extension for cup
// version 0.8
// 19/7/2025 8:10:20


package rs.ac.bg.etf.pp1.ast;

public class LabelledStatement extends Statement {

    private LabelDef LabelDef;
    private Statement Statement;

    public LabelledStatement (LabelDef LabelDef, Statement Statement) {
        this.LabelDef=LabelDef;
        if(LabelDef!=null) LabelDef.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public LabelDef getLabelDef() {
        return LabelDef;
    }

    public void setLabelDef(LabelDef LabelDef) {
        this.LabelDef=LabelDef;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(LabelDef!=null) LabelDef.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(LabelDef!=null) LabelDef.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(LabelDef!=null) LabelDef.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("LabelledStatement(\n");

        if(LabelDef!=null)
            buffer.append(LabelDef.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [LabelledStatement]");
        return buffer.toString();
    }
}
