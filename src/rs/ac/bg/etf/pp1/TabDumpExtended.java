package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class TabDumpExtended extends DumpSymbolTableVisitor{
	@Override
    public void visitStructNode(Struct structToVisit) {
        // The order of checks goes from most specific to most general.

        // Case 1: Handle a Matrix (an array of arrays)
        if (structToVisit.getKind() == Struct.Array && structToVisit.getElemType().getKind() == Struct.Array) {
            output.append("Matrix of ");
            switch (structToVisit.getElemType().getElemType().getKind()) {
                case Struct.Int:
                    output.append("int");
                    break;
                case Struct.Char:
                    output.append("char");
                    break;
                case Struct.Bool:
                    output.append("bool");
                    break;
                default:
                    output.append("unknown");
                    break;
            }
        // Case 2: Handle the custom 'set' type (special array of ints)
        } else if (structToVisit.equals(TabExtended.setType)) {
            output.append("Set");
        // Case 3: Handle a one-dimensional array of booleans
        } else if (structToVisit.getKind() == Struct.Array && structToVisit.getElemType().getKind() == Struct.Bool) {
            output.append("Arr of bool");
        // Case 4: Handle the primitive bool type
        } else if (structToVisit.getKind() == Struct.Bool) {
            output.append("bool");
        // Fallback: For any other type (int, char, regular arrays of ints, etc.),
        // let the original DumpSymbolTableVisitor handle it.
        } else {
            super.visitStructNode(structToVisit);
        }
    }
}
