package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;

public class TabExtended extends Tab {

    // Definicija nasih custom tipova
	public static final Struct boolType = new Struct(Struct.Bool);
	public static final Struct setType = new Struct(Struct.Array, Tab.intType);

	public static Obj lenMeth;
	
	public static void init() {
		// 1. Inicijalizuj osnovnu tabelu simbola (int, char, chr, ord, len...)
		Tab.init(); 
		
		// 2. Dodaj nase custom tipove u globalni opseg
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "set", setType));
		
		// 3. Declare the new predefined 'len' method
        // int len(any_array a)
        lenMeth = insert(Obj.Meth, "len", Tab.intType);
        openScope();
        // The parameter type is checked semantically, so we can use a placeholder here.
        insert(Obj.Var, "a", new Struct(Struct.Array, noType));
        lenMeth.setLocals(currentScope.getLocals());
        lenMeth.setLevel(1); // 1 formal parameter
        closeScope();
		
        // 4. Deklarisi nove predefinisane metode za rad sa skupovima (set)
        
        // void add(set s, int e)
        Obj addMeth = insert(Obj.Meth, "add", Tab.noType);
        openScope();
        insert(Obj.Var, "s", setType);
        insert(Obj.Var, "e", Tab.intType);
        addMeth.setLocals(currentScope.getLocals());
        addMeth.setLevel(2); // Broj formalnih parametara
        closeScope();

        Struct intArrayType = new Struct(Struct.Array, Tab.intType);
        Obj addAllMeth = insert(Obj.Meth, "addAll", Tab.noType);
        openScope();
        insert(Obj.Var, "s", setType);
        insert(Obj.Var, "arr", intArrayType);
        insert(Obj.Var, "isSourceSet", Tab.intType); // The "type tag" parameter
        addAllMeth.setLocals(currentScope.getLocals());
        addAllMeth.setLevel(3); // Now expects 3 formal parameters
        closeScope();
        
        
        // set union(set s1, set s2)
        Obj unionMeth = insert(Obj.Meth, "union", setType); // Name is "union", returns a `set`
        openScope();
        insert(Obj.Var, "s1", setType);
        insert(Obj.Var, "s2", setType);
        unionMeth.setLocals(currentScope.getLocals());
        unionMeth.setLevel(2); // 2 formal parameters
        closeScope();
	}

	public static void dump() {
		Tab.dump(new TabDumpExtended());
	}
}