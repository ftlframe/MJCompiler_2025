// generated with ast extension for cup
// version 0.8
// 18/6/2025 19:2:54


package rs.ac.bg.etf.pp1.ast;

public class VarDeclInFunctionArray extends VarDeclInFunction {

    private VarDeclInFunction VarDeclInFunction;
    private Type Type;
    private String arrName;
    private Brackets Brackets;

    public VarDeclInFunctionArray (VarDeclInFunction VarDeclInFunction, Type Type, String arrName, Brackets Brackets) {
        this.VarDeclInFunction=VarDeclInFunction;
        if(VarDeclInFunction!=null) VarDeclInFunction.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.arrName=arrName;
        this.Brackets=Brackets;
        if(Brackets!=null) Brackets.setParent(this);
    }

    public VarDeclInFunction getVarDeclInFunction() {
        return VarDeclInFunction;
    }

    public void setVarDeclInFunction(VarDeclInFunction VarDeclInFunction) {
        this.VarDeclInFunction=VarDeclInFunction;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public String getArrName() {
        return arrName;
    }

    public void setArrName(String arrName) {
        this.arrName=arrName;
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
        if(VarDeclInFunction!=null) VarDeclInFunction.accept(visitor);
        if(Type!=null) Type.accept(visitor);
        if(Brackets!=null) Brackets.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclInFunction!=null) VarDeclInFunction.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(Brackets!=null) Brackets.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclInFunction!=null) VarDeclInFunction.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(Brackets!=null) Brackets.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclInFunctionArray(\n");

        if(VarDeclInFunction!=null)
            buffer.append(VarDeclInFunction.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+arrName);
        buffer.append("\n");

        if(Brackets!=null)
            buffer.append(Brackets.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclInFunctionArray]");
        return buffer.toString();
    }
}
