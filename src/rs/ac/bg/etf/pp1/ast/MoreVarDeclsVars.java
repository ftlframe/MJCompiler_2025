// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class MoreVarDeclsVars extends MoreVarDecls {

    private MoreVarDecls MoreVarDecls;
    private String varName;

    public MoreVarDeclsVars (MoreVarDecls MoreVarDecls, String varName) {
        this.MoreVarDecls=MoreVarDecls;
        if(MoreVarDecls!=null) MoreVarDecls.setParent(this);
        this.varName=varName;
    }

    public MoreVarDecls getMoreVarDecls() {
        return MoreVarDecls;
    }

    public void setMoreVarDecls(MoreVarDecls MoreVarDecls) {
        this.MoreVarDecls=MoreVarDecls;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName=varName;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MoreVarDecls!=null) MoreVarDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MoreVarDecls!=null) MoreVarDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MoreVarDecls!=null) MoreVarDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MoreVarDeclsVars(\n");

        if(MoreVarDecls!=null)
            buffer.append(MoreVarDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+varName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MoreVarDeclsVars]");
        return buffer.toString();
    }
}
