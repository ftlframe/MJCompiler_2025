package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;

public class TabExtended extends Tab {

    // Definicija nasih custom tipova
	public static final Struct boolType = new Struct(Struct.Bool);
	public static final Struct setType = new Struct(Struct.Array, Tab.intType);

	public static void init() {
		// 1. Inicijalizuj osnovnu tabelu simbola (int, char, chr, ord, len...)
		Tab.init(); 
		
		// 2. Dodaj nase custom tipove u globalni opseg
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "set", setType));
		
        // 3. Deklarisi nove predefinisane metode za rad sa skupovima (set)
        
        // void add(set s, int e)
        Obj addMeth = insert(Obj.Meth, "add", Tab.noType);
        openScope();
        insert(Obj.Var, "s", setType);
        insert(Obj.Var, "e", Tab.intType);
        addMeth.setLocals(currentScope.getLocals());
        addMeth.setLevel(2); // Broj formalnih parametara
        closeScope();

        // void addAll(set s, int[] arr)
        Struct intArrayType = new Struct(Struct.Array, Tab.intType);
        Obj addAllMeth = insert(Obj.Meth, "addAll", Tab.noType);
        openScope();
        insert(Obj.Var, "s", setType);
        insert(Obj.Var, "arr", intArrayType);
        addAllMeth.setLocals(currentScope.getLocals());
        addAllMeth.setLevel(2); // Broj formalnih parametara
        closeScope();
	}

	public static void dump() {
		Tab.dump(new TabDumpExtended());
	}
}