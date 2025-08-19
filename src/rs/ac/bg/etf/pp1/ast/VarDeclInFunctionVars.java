// generated with ast extension for cup
// version 0.8
// 19/7/2025 8:10:20


package rs.ac.bg.etf.pp1.ast;

public class VarDeclInFunctionVars extends VarDeclInFunction {

    private VarDeclInFunction VarDeclInFunction;
    private Type Type;
    private String varName;

    public VarDeclInFunctionVars (VarDeclInFunction VarDeclInFunction, Type Type, String varName) {
        this.VarDeclInFunction=VarDeclInFunction;
        if(VarDeclInFunction!=null) VarDeclInFunction.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.varName=varName;
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
        if(VarDeclInFunction!=null) VarDeclInFunction.accept(visitor);
        if(Type!=null) Type.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclInFunction!=null) VarDeclInFunction.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclInFunction!=null) VarDeclInFunction.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclInFunctionVars(\n");

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

        buffer.append(" "+tab+varName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclInFunctionVars]");
        return buffer.toString();
    }
}
