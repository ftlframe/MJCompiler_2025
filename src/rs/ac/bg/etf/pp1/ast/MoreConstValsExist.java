// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class MoreConstValsExist extends MoreConstVals {

    private MoreConstVals MoreConstVals;
    private String constName;
    private ConstVals ConstVals;

    public MoreConstValsExist (MoreConstVals MoreConstVals, String constName, ConstVals ConstVals) {
        this.MoreConstVals=MoreConstVals;
        if(MoreConstVals!=null) MoreConstVals.setParent(this);
        this.constName=constName;
        this.ConstVals=ConstVals;
        if(ConstVals!=null) ConstVals.setParent(this);
    }

    public MoreConstVals getMoreConstVals() {
        return MoreConstVals;
    }

    public void setMoreConstVals(MoreConstVals MoreConstVals) {
        this.MoreConstVals=MoreConstVals;
    }

    public String getConstName() {
        return constName;
    }

    public void setConstName(String constName) {
        this.constName=constName;
    }

    public ConstVals getConstVals() {
        return ConstVals;
    }

    public void setConstVals(ConstVals ConstVals) {
        this.ConstVals=ConstVals;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MoreConstVals!=null) MoreConstVals.accept(visitor);
        if(ConstVals!=null) ConstVals.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MoreConstVals!=null) MoreConstVals.traverseTopDown(visitor);
        if(ConstVals!=null) ConstVals.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MoreConstVals!=null) MoreConstVals.traverseBottomUp(visitor);
        if(ConstVals!=null) ConstVals.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MoreConstValsExist(\n");

        if(MoreConstVals!=null)
            buffer.append(MoreConstVals.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+constName);
        buffer.append("\n");

        if(ConstVals!=null)
            buffer.append(ConstVals.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MoreConstValsExist]");
        return buffer.toString();
    }
}
