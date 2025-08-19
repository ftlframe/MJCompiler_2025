// generated with ast extension for cup
// version 0.8
// 19/7/2025 8:10:20


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclarations extends Declarations {

    private Declarations Declarations;
    private ConstDeclr ConstDeclr;

    public ConstDeclarations (Declarations Declarations, ConstDeclr ConstDeclr) {
        this.Declarations=Declarations;
        if(Declarations!=null) Declarations.setParent(this);
        this.ConstDeclr=ConstDeclr;
        if(ConstDeclr!=null) ConstDeclr.setParent(this);
    }

    public Declarations getDeclarations() {
        return Declarations;
    }

    public void setDeclarations(Declarations Declarations) {
        this.Declarations=Declarations;
    }

    public ConstDeclr getConstDeclr() {
        return ConstDeclr;
    }

    public void setConstDeclr(ConstDeclr ConstDeclr) {
        this.ConstDeclr=ConstDeclr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Declarations!=null) Declarations.accept(visitor);
        if(ConstDeclr!=null) ConstDeclr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Declarations!=null) Declarations.traverseTopDown(visitor);
        if(ConstDeclr!=null) ConstDeclr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Declarations!=null) Declarations.traverseBottomUp(visitor);
        if(ConstDeclr!=null) ConstDeclr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclarations(\n");

        if(Declarations!=null)
            buffer.append(Declarations.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstDeclr!=null)
            buffer.append(ConstDeclr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclarations]");
        return buffer.toString();
    }
}
