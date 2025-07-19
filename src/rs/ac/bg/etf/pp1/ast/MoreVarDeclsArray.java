// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class MoreVarDeclsArray extends MoreVarDecls {

    private MoreVarDecls MoreVarDecls;
    private String arrayName;
    private Brackets Brackets;

    public MoreVarDeclsArray (MoreVarDecls MoreVarDecls, String arrayName, Brackets Brackets) {
        this.MoreVarDecls=MoreVarDecls;
        if(MoreVarDecls!=null) MoreVarDecls.setParent(this);
        this.arrayName=arrayName;
        this.Brackets=Brackets;
        if(Brackets!=null) Brackets.setParent(this);
    }

    public MoreVarDecls getMoreVarDecls() {
        return MoreVarDecls;
    }

    public void setMoreVarDecls(MoreVarDecls MoreVarDecls) {
        this.MoreVarDecls=MoreVarDecls;
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName=arrayName;
    }

    public Brackets getBrackets() {
        return Brackets;
    }

    public void setBrackets(Brackets Brackets) {
        this.Brackets=Brackets;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MoreVarDecls!=null) MoreVarDecls.accept(visitor);
        if(Brackets!=null) Brackets.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MoreVarDecls!=null) MoreVarDecls.traverseTopDown(visitor);
        if(Brackets!=null) Brackets.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MoreVarDecls!=null) MoreVarDecls.traverseBottomUp(visitor);
        if(Brackets!=null) Brackets.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MoreVarDeclsArray(\n");

        if(MoreVarDecls!=null)
            buffer.append(MoreVarDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+arrayName);
        buffer.append("\n");

        if(Brackets!=null)
            buffer.append(Brackets.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MoreVarDeclsArray]");
        return buffer.toString();
    }
}
