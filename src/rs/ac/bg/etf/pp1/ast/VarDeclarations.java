// generated with ast extension for cup
// version 0.8
// 18/6/2025 17:28:28


package rs.ac.bg.etf.pp1.ast;

public class VarDeclarations extends Declarations {

    private Declarations Declarations;
    private VarDeclr VarDeclr;

    public VarDeclarations (Declarations Declarations, VarDeclr VarDeclr) {
        this.Declarations=Declarations;
        if(Declarations!=null) Declarations.setParent(this);
        this.VarDeclr=VarDeclr;
        if(VarDeclr!=null) VarDeclr.setParent(this);
    }

    public Declarations getDeclarations() {
        return Declarations;
    }

    public void setDeclarations(Declarations Declarations) {
        this.Declarations=Declarations;
    }

    public VarDeclr getVarDeclr() {
        return VarDeclr;
    }

    public void setVarDeclr(VarDeclr VarDeclr) {
        this.VarDeclr=VarDeclr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Declarations!=null) Declarations.accept(visitor);
        if(VarDeclr!=null) VarDeclr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Declarations!=null) Declarations.traverseTopDown(visitor);
        if(VarDeclr!=null) VarDeclr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Declarations!=null) Declarations.traverseBottomUp(visitor);
        if(VarDeclr!=null) VarDeclr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclarations(\n");

        if(Declarations!=null)
            buffer.append(Declarations.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclr!=null)
            buffer.append(VarDeclr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclarations]");
        return buffer.toString();
    }
}
