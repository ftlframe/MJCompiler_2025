// generated with ast extension for cup
// version 0.8
// 16/6/2025 17:12:51


package rs.ac.bg.etf.pp1.ast;

public class OtherFormParamsListExist extends OtherFormParamsList {

    private OtherFormParamsList OtherFormParamsList;
    private FormalParamsExist FormalParamsExist;

    public OtherFormParamsListExist (OtherFormParamsList OtherFormParamsList, FormalParamsExist FormalParamsExist) {
        this.OtherFormParamsList=OtherFormParamsList;
        if(OtherFormParamsList!=null) OtherFormParamsList.setParent(this);
        this.FormalParamsExist=FormalParamsExist;
        if(FormalParamsExist!=null) FormalParamsExist.setParent(this);
    }

    public OtherFormParamsList getOtherFormParamsList() {
        return OtherFormParamsList;
    }

    public void setOtherFormParamsList(OtherFormParamsList OtherFormParamsList) {
        this.OtherFormParamsList=OtherFormParamsList;
    }

    public FormalParamsExist getFormalParamsExist() {
        return FormalParamsExist;
    }

    public void setFormalParamsExist(FormalParamsExist FormalParamsExist) {
        this.FormalParamsExist=FormalParamsExist;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OtherFormParamsList!=null) OtherFormParamsList.accept(visitor);
        if(FormalParamsExist!=null) FormalParamsExist.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OtherFormParamsList!=null) OtherFormParamsList.traverseTopDown(visitor);
        if(FormalParamsExist!=null) FormalParamsExist.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OtherFormParamsList!=null) OtherFormParamsList.traverseBottomUp(visitor);
        if(FormalParamsExist!=null) FormalParamsExist.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("OtherFormParamsListExist(\n");

        if(OtherFormParamsList!=null)
            buffer.append(OtherFormParamsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormalParamsExist!=null)
            buffer.append(FormalParamsExist.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [OtherFormParamsListExist]");
        return buffer.toString();
    }
}
