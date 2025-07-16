// generated with ast extension for cup
// version 0.8
// 16/6/2025 17:12:51


package rs.ac.bg.etf.pp1.ast;

public class VarDeclArray extends VarDeclr {

    private VarType VarType;
    private String arrayName;
    private Brackets Brackets;
    private MoreVarDecls MoreVarDecls;

    public VarDeclArray (VarType VarType, String arrayName, Brackets Brackets, MoreVarDecls MoreVarDecls) {
        this.VarType=VarType;
        if(VarType!=null) VarType.setParent(this);
        this.arrayName=arrayName;
        this.Brackets=Brackets;
        if(Brackets!=null) Brackets.setParent(this);
        this.MoreVarDecls=MoreVarDecls;
        if(MoreVarDecls!=null) MoreVarDecls.setParent(this);
    }

    public VarType getVarType() {
        return VarType;
    }

    public void setVarType(VarType VarType) {
        this.VarType=VarType;
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

    public MoreVarDecls getMoreVarDecls() {
        return MoreVarDecls;
    }

    public void setMoreVarDecls(MoreVarDecls MoreVarDecls) {
        this.MoreVarDecls=MoreVarDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarType!=null) VarType.accept(visitor);
        if(Brackets!=null) Brackets.accept(visitor);
        if(MoreVarDecls!=null) MoreVarDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarType!=null) VarType.traverseTopDown(visitor);
        if(Brackets!=null) Brackets.traverseTopDown(visitor);
        if(MoreVarDecls!=null) MoreVarDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarType!=null) VarType.traverseBottomUp(visitor);
        if(Brackets!=null) Brackets.traverseBottomUp(visitor);
        if(MoreVarDecls!=null) MoreVarDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclArray(\n");

        if(VarType!=null)
            buffer.append(VarType.toString("  "+tab));
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

        if(MoreVarDecls!=null)
            buffer.append(MoreVarDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclArray]");
        return buffer.toString();
    }
}
