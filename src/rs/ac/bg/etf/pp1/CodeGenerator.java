package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	
	private boolean builtInFunctionsGenerated = false;
	private int mainPc;
	private Obj currentMethod = null;
	public int getMainPc() {
		return mainPc;
	}
	
	// Maps a label name to its bytecode address (pc)
	private Map<String, Integer> labelAddresses = new HashMap<>();
	// Maps an undefined label name to a list of `jmp` instructions that need patching
	private Map<String, List<Integer>> forwardJumpPatches = new HashMap<>();
	

	private void storeLocal(int slot) {
	    if (slot >= 0 && slot <= 3) {
	        Code.put(Code.store_n + slot);
	    } else {
	        Code.put(Code.store);
	        Code.put(slot);
	    }
	}

	private void loadLocal(int slot) {
	    if (slot >= 0 && slot <= 3) {
	        Code.put(Code.load_n + slot);
	    } else {
	        Code.put(Code.load);
	        Code.put(slot);
	    }
	}

	/**
	 * Helper function to retrieve the integer value of a constant from the AST.
	 * @param node The AST node representing a constant value.
	 * @return The integer representation of the constant.
	 */
	private int getConstVal(SyntaxNode node) {
		if (node instanceof ConstNum)
			return ((ConstNum) node).getNumVal();
		if (node instanceof ConstChar)
			return ((ConstChar) node).getCharVal();
		if (node instanceof ConstBool) {
			return "true".equalsIgnoreCase(((ConstBool) node).getBoolVal()) ? 1 : 0;
		}
		return -1; // Should not happen with a valid AST
	}
	
	private void generateUnionMethod() {
		Obj unionMeth = Tab.find("union");
	    unionMeth.setAdr(Code.pc);
	    
	    // enter: 2 params (s2, s3), 1 extra local for the temp_set address
	    Code.put(Code.enter); Code.put(2); Code.put(3);
	    int temp_set_slot = 2;
	    
	    // --- 1. Calculate capacity and create the temporary set ---
	    Code.put(Code.load_n + 0); // push s2_addr
	    Code.put(Code.load_n + 1); // push s3_addr
	    
	    Code.put(Code.dup2); Code.loadConst(0); Code.put(Code.aload);
	    Code.put(Code.dup_x1); Code.put(Code.pop); Code.put(Code.dup_x2); Code.put(Code.pop);
	    Code.put(Code.dup_x1); Code.put(Code.pop); Code.loadConst(0); Code.put(Code.aload);
	    Code.put(Code.add); // Stack: new_capacity
	    
	    Code.loadConst(1); Code.put(Code.add);
	    Code.put(Code.newarray); Code.put(1);
	    Code.put(Code.dup); Code.loadConst(0); Code.loadConst(0); Code.put(Code.astore);
	    
	    Code.put(Code.store_n + temp_set_slot); // Save the new set's address
	    Code.put(Code.pop);
	    Code.put(Code.pop);
	    // Stack is now clean.

	    // --- 2. Populate the temporary set ---
	    Obj addAllMeth = Tab.find("addAll");

	    // a) Call addAll(temp_set, s2, 1)
	    Code.put(Code.load_n + temp_set_slot); // push temp_set_addr (dest)
	    Code.put(Code.load_n + 0);             // push s2_addr (src)
	    Code.loadConst(1);                   // Push the type tag (1 for set)
	    Code.put(Code.call);
	    Code.put2(addAllMeth.getAdr() - Code.pc + 1);
	    
	    // b) Call addAll(temp_set, s3, 1)
	    Code.put(Code.load_n + temp_set_slot); // push temp_set_addr (dest)
	    Code.put(Code.load_n + 1);             // push s3_addr (src)
	    Code.loadConst(1);                   // Push the type tag (1 for set)
	    Code.put(Code.call);
	    Code.put2(addAllMeth.getAdr() - Code.pc + 1);
	    
	    // --- 3. Prepare the return value ---
	    Code.put(Code.load_n + temp_set_slot); 
	    
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}
	
	private void generateIntersectionMethod() {
	    Obj meth = Tab.find("intersection");
	    meth.setAdr(Code.pc);

	    // enter: 2 params (s1, s2), 4 locals (i, j, elem, temp_set)
	    Code.put(Code.enter); Code.put(2); Code.put(6);
	    int temp_set_slot = 2, i_slot = 3, j_slot = 4, elem_slot = 5;

	    List<Integer> innerLoopBreakPatches = new ArrayList<>();

	    // 1. Create a new temporary set with capacity = s1[0]
	    loadLocal(0);
	    Code.loadConst(0); 
	    Code.put(Code.aload);
	    Code.loadConst(1); Code.put(Code.add);
	    Code.put(Code.newarray); Code.put(1);
	    Code.put(Code.dup); Code.loadConst(0); Code.loadConst(0); Code.put(Code.astore);
	    storeLocal(temp_set_slot);

	    // 2. For each element in s1...
	    Code.loadConst(1); storeLocal(i_slot); // i = 1
	    int outerLoopStart = Code.pc;
	    loadLocal(i_slot); loadLocal(0); Code.loadConst(0); Code.put(Code.aload);
	    Code.putFalseJump(Code.le, 0); int outerLoopEnd = Code.pc - 2;
	    
	    loadLocal(0); loadLocal(i_slot); Code.put(Code.aload);
	    storeLocal(elem_slot);

	    // 3. ...check if it exists in s2.
	    Code.loadConst(1); storeLocal(j_slot); // j = 1
	    int innerLoopStart = Code.pc;
	    loadLocal(j_slot); loadLocal(1); Code.loadConst(0); Code.put(Code.aload);
	    Code.putFalseJump(Code.le, 0); int innerLoopEnd = Code.pc - 2;

	    loadLocal(elem_slot);
	    loadLocal(1); loadLocal(j_slot); Code.put(Code.aload);
	    Code.putFalseJump(Code.eq, 0); int notFound = Code.pc - 2;
	    
	    // If found, add to result and then break the inner loop
	    loadLocal(temp_set_slot); loadLocal(elem_slot);
	    Code.put(Code.call); Code.put2(Tab.find("add").getAdr() - Code.pc + 1);
	    
	    Code.putJump(0);
	    innerLoopBreakPatches.add(Code.pc - 2);
	    
	    Code.fixup(notFound);

	    Code.put(Code.inc); Code.put(j_slot); Code.put(1); // j++
	    Code.putJump(innerLoopStart);
	    
	    // This is the point after the inner loop. Patch all breaks to jump here.
	    Code.fixup(innerLoopEnd);
	    for (Integer patchAddr : innerLoopBreakPatches) {
	        Code.fixup(patchAddr);
	    }
	    innerLoopBreakPatches.clear();

	    Code.put(Code.inc); Code.put(i_slot); Code.put(1); // i++
	    Code.putJump(outerLoopStart);
	    Code.fixup(outerLoopEnd);
	    
	    // 4. Return the new set
	    loadLocal(temp_set_slot);
	    Code.put(Code.exit); Code.put(Code.return_);
	}

	private void generateDifferenceMethod() {
	    Obj meth = Tab.find("difference");
	    meth.setAdr(Code.pc);

	    // enter: 2 params (s1, s2), 5 locals (i, j, elem, found, temp_set)
	    Code.put(Code.enter); Code.put(2); Code.put(7);
	    int temp_set_slot = 2, i_slot = 3, j_slot = 4, elem_slot = 5, found_slot = 6;

	    List<Integer> innerLoopBreakPatches = new ArrayList<>();

	    // 1. Create a new temporary set
	    loadLocal(0); Code.loadConst(0); Code.put(Code.aload);
	    Code.loadConst(1); Code.put(Code.add);
	    Code.put(Code.newarray); Code.put(1);
	    Code.put(Code.dup); Code.loadConst(0); Code.loadConst(0); Code.put(Code.astore);
	    storeLocal(temp_set_slot);

	    // 2. For each element in s1...
	    Code.loadConst(1); storeLocal(i_slot); // i = 1
	    int outerLoopStart = Code.pc;
	    loadLocal(i_slot); loadLocal(0); Code.loadConst(0); Code.put(Code.aload);
	    Code.putFalseJump(Code.le, 0); int outerLoopEnd = Code.pc - 2;
	    
	    loadLocal(0); loadLocal(i_slot); Code.put(Code.aload);
	    storeLocal(elem_slot);
	    Code.loadConst(0); storeLocal(found_slot); // found = false

	    // 3. ...check if it exists in s2.
	    Code.loadConst(1); storeLocal(j_slot); // j = 1
	    int innerLoopStart = Code.pc;
	    loadLocal(j_slot); loadLocal(1); Code.loadConst(0); Code.put(Code.aload);
	    Code.putFalseJump(Code.le, 0); int innerLoopEnd = Code.pc - 2;

	    loadLocal(elem_slot);
	    loadLocal(1); loadLocal(j_slot); Code.put(Code.aload);
	    Code.putFalseJump(Code.eq, 0); int notFound = Code.pc - 2; // Jump if NOT equal
	    
	    // If found (fall-through), set found=true and break
	    Code.loadConst(1); storeLocal(found_slot);
	    Code.putJump(0);
	    innerLoopBreakPatches.add(Code.pc - 2);
	    
	    Code.fixup(notFound);

	    Code.put(Code.inc); Code.put(j_slot); Code.put(1); // j++
	    Code.putJump(innerLoopStart);
	    
	    Code.fixup(innerLoopEnd);
	    for (Integer patchAddr : innerLoopBreakPatches) {
	        Code.fixup(patchAddr);
	    }
	    innerLoopBreakPatches.clear();

	    // 4. If not found in s2 (found == 0), add it to the result.
	    loadLocal(found_slot); Code.loadConst(0);

	    // Jump if found != 0. The inverse of 'ne' is 'eq'.
	    Code.putFalseJump(Code.eq, 0); int endIf = Code.pc - 2;
	    
	    // Fall-through here if found == 0
	    loadLocal(temp_set_slot); loadLocal(elem_slot);
	    Code.put(Code.call); Code.put2(Tab.find("add").getAdr() - Code.pc + 1);
	    Code.fixup(endIf); // This is the jump target

	    Code.put(Code.inc); Code.put(i_slot); Code.put(1); // i++
	    Code.putJump(outerLoopStart);
	    Code.fixup(outerLoopEnd);
	    
	    // 5. Return the new set
	    loadLocal(temp_set_slot);
	    Code.put(Code.exit); Code.put(Code.return_);
	}

	private void generateAddMethod() {
	    Obj addMeth = Tab.find("add");
	    addMeth.setAdr(Code.pc);
	    
	    Code.put(Code.enter); Code.put(2); Code.put(3);
	    int i_local_idx = 2;

	    // --- 1. Check if the set is full ---
	    // Push s[0] (current size)
	    Code.put(Code.load_n + 0); 
	    Code.loadConst(0);
	    Code.put(Code.aload);
	    
	    // Push capacity (arraylength - 1)
	    Code.put(Code.load_n + 0);
	    Code.put(Code.arraylength);
	    Code.loadConst(1);
	    Code.put(Code.sub);
	    
	    // Stack is now: size, capacity
	    // If size < capacity, jump past the trap instruction.
	    Code.putFalseJump(Code.ge, 0); int canAddPatch = Code.pc - 2;
	    
	    // <<< THE FIX IS HERE >>>
	    // If the set is full (size >= capacity), fall through and trap.
	    Code.put(Code.trap); 
	    Code.put(1); // Error code 1 for "Set capacity exceeded".
	    
	    Code.fixup(canAddPatch); // Continue here if not full.
	    // The stack is now clean.

	    // 2. Check for duplicates
	    Code.loadConst(1); Code.put(Code.store_n + i_local_idx);
	    int dupLoopStart = Code.pc;
	    
	    Code.put(Code.load_n + i_local_idx);
	    Code.put(Code.load_n + 0);
	    Code.loadConst(0);
	    Code.put(Code.aload);
	    Code.putFalseJump(Code.le, 0); int dupLoopEnd = Code.pc - 2;
	    
	    Code.put(Code.load_n + 0);
	    Code.put(Code.load_n + i_local_idx);
	    Code.put(Code.aload);
	    Code.put(Code.load_n + 1);
	    Code.putFalseJump(Code.eq, 0); int noDuplicatePatch = Code.pc - 2;
	    Code.put(Code.exit); Code.put(Code.return_);
	    Code.fixup(noDuplicatePatch);
	    
	    Code.put(Code.inc); Code.put(i_local_idx); Code.put(1);
	    Code.putJump(dupLoopStart);
	    Code.fixup(dupLoopEnd);

	    // 3. Add element and increment size
	    Code.put(Code.load_n + 0);
	    Code.put(Code.dup);
	    Code.loadConst(0);
	    Code.put(Code.aload);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.put(Code.load_n + 1);
	    Code.put(Code.astore);
	    
	    Code.put(Code.load_n + 0);
	    Code.loadConst(0);
	    Code.put(Code.dup2);
	    Code.put(Code.aload);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.put(Code.astore);
	    
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}
	
	private void generateAddAllMethod() {
	    Obj meth = Tab.find("addAll");
	    meth.setAdr(Code.pc);
	    
	    // enter: 3 params (dest_set, src, isSourceSet), 1 local var (i)
	    Code.put(Code.enter); Code.put(3); Code.put(4);
	    int i_local_idx = 3; // `i` is now the 4th variable (index 3)
	    
	    int endOfMethodLabel = 0; // To hold the address for the final jump

	    // --- Dispatch based on the 'isSourceSet' tag ---
	    Code.put(Code.load_n + 2); // Load the isSourceSet tag (param 2)
	    Code.loadConst(0);
	    Code.putFalseJump(Code.ne, 0); int isArrayBranch = Code.pc - 2;

	    // --- Branch 1: Source is a Set (tag == 1) ---
	    {
	        // Set-specific loop: for (i = 1; i <= src_set[0]; i++)
	        Code.loadConst(1); 
	        Code.put(Code.store_n + i_local_idx);
	        
	        int loopStart = Code.pc;
	        Code.put(Code.load_n + i_local_idx); 
	        Code.put(Code.load_n + 1); // param 1 is the source set
	        Code.loadConst(0);                   
	        Code.put(Code.aload); // get size from src_set[0]
	        Code.putFalseJump(Code.le, 0); int loopEnd = Code.pc - 2; 
	        
	        // Body: call add(dest_set, src_set[i]);
	        Code.put(Code.load_n + 0); 
	        Code.put(Code.load_n + 1); 
	        Code.put(Code.load_n + i_local_idx); 
	        Code.put(Code.aload); 
	        
	        Obj addMeth = Tab.find("add");
	        Code.put(Code.call);
	        Code.put2(addMeth.getAdr() - Code.pc + 1);
	        
	        Code.put(Code.inc); Code.put(i_local_idx); Code.put(1);
	        Code.putJump(loopStart);
	        Code.fixup(loopEnd);
	        Code.putJump(0); endOfMethodLabel = Code.pc - 2; // Jump to the end
	    }

	    // --- Branch 2: Source is an Array (tag == 0) ---
	    Code.fixup(isArrayBranch);
	    {
	        // Standard loop: for (i = 0; i < src_array.length; i++)
	        Code.loadConst(0); 
	        Code.put(Code.store_n + i_local_idx);
	        
	        int loopStart = Code.pc;
	        Code.put(Code.load_n + i_local_idx); 
	        Code.put(Code.load_n + 1); // param 1 is the source array
	        Code.put(Code.arraylength);
	        Code.putFalseJump(Code.lt, 0); int loopEnd = Code.pc - 2;
	        
	        // Body: call add(dest_set, src_array[i]);
	        Code.put(Code.load_n + 0);
	        Code.put(Code.load_n + 1);
	        Code.put(Code.load_n + i_local_idx);
	        Code.put(Code.aload);
	        
	        Obj addMeth = Tab.find("add");
	        Code.put(Code.call);
	        Code.put2(addMeth.getAdr() - Code.pc + 1);
	        
	        Code.put(Code.inc); Code.put(i_local_idx); Code.put(1);
	        Code.putJump(loopStart);
	        Code.fixup(loopEnd);
	    }
	    
	    // Both branches arrive here
	    if(endOfMethodLabel != 0) Code.fixup(endOfMethodLabel);
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}
	
	private void generateRemoveMethod() {
	    Obj meth = Tab.find("remove");
	    meth.setAdr(Code.pc);

	    // enter: 2 params (set, elem), 2 locals (i, index_found)
	    Code.put(Code.enter); Code.put(2); Code.put(4);
	    int i_slot = 2;
	    int index_found_slot = 3;

	    List<Integer> breakPatches = new ArrayList<>();

	    // 1. Find the index of the element to remove.
	    Code.loadConst(-1); storeLocal(index_found_slot); // index_found = -1
	    Code.loadConst(1); storeLocal(i_slot); // i = 1
	    
	    int loopStart = Code.pc;
	    // Loop condition: for (i = 1; i <= set[0]; i++)
	    loadLocal(i_slot); loadLocal(0); Code.loadConst(0); Code.put(Code.aload);
	    Code.putFalseJump(Code.le, 0); int loopEnd = Code.pc - 2;

	    // if (set[i] == elem)
	    loadLocal(0); loadLocal(i_slot); Code.put(Code.aload);
	    loadLocal(1); // element
	    
	    // <<< FIX 1: Corrected jump condition >>>
	    // Jump if NOT equal. The inverse of 'eq' is 'ne'.
	    Code.putFalseJump(Code.eq, 0); int notFound = Code.pc - 2;
	    
	    // If found (fall-through):
	    loadLocal(i_slot); storeLocal(index_found_slot);
	    
	    // <<< FIX 2: Correct 'break' implementation >>>
	    Code.putJump(0); // Generate placeholder jump for the 'break'
	    breakPatches.add(Code.pc - 2); // Save its address to be patched
	    
	    Code.fixup(notFound);
	    Code.put(Code.inc); Code.put(i_slot); Code.put(1); // i++
	    Code.putJump(loopStart);
	    
	    // This is the point after the loop. Patch all breaks to jump here.
	    Code.fixup(loopEnd);
	    for (int addr : breakPatches) {
	        Code.fixup(addr);
	    }

	    // 2. If element was found (index_found != -1)...
	    loadLocal(index_found_slot); Code.loadConst(-1);
	    Code.putFalseJump(Code.ne, 0); int endOfMethod = Code.pc - 2;

	    // 3. ...move the last element into its place.
	    // set[index_found] = set[set[0]];
	    loadLocal(0); // dest address: set
	    loadLocal(index_found_slot); // dest index: index_found
	    loadLocal(0); // src address: set
	    loadLocal(0); Code.loadConst(0); Code.put(Code.aload); // src index: set[0]
	    Code.put(Code.aload); // value: set[set[0]]
	    Code.put(Code.astore);

	    // 4. Decrement the size counter: set[0]--
	    loadLocal(0); Code.loadConst(0); Code.put(Code.dup2);
	    Code.put(Code.aload); Code.loadConst(1); Code.put(Code.sub);
	    Code.put(Code.astore);
	    
	    Code.fixup(endOfMethod);
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}
	

	private void generateClearMethod() {
	    Obj meth = Tab.find("clear");
	    meth.setAdr(Code.pc);
	
	    // enter: 1 param (set), 0 extra locals
	    Code.put(Code.enter); Code.put(1); Code.put(1);
	    
	    // Logic: set[0] = 0;
	    Code.put(Code.load_n + 0); // push address of set (param 0)
	    Code.loadConst(0);         // push index 0
	    Code.loadConst(0);         // push value 0
	    Code.put(Code.astore);     // Perform the store operation
	    
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}
	
	private void ensureBuiltInFunctions() {
	    if (!builtInFunctionsGenerated) {
	        generateAddMethod();
	        generateAddAllMethod();
	        generateUnionMethod();
	        generateIntersectionMethod();
	        generateDifferenceMethod();
	        generateRemoveMethod();
	        generateClearMethod();
	        builtInFunctionsGenerated = true;
	    }
	}
	// =================================================================
	// ## Method Processing
	// =================================================================

	/**
	 * Visits a void method declaration name.
	 * Sets up the entry point for the method.
	 */
	public void visit(MethodDeclNameVOID method) {
		ensureBuiltInFunctions();
		this.currentMethod = method.obj;
	    labelAddresses.clear();
	    forwardJumpPatches.clear();
		// Check if this is the main method to record its entry point
		if ("main".equalsIgnoreCase(method.getMethodName())) {
			mainPc = Code.pc;
			Code.mainPc = Code.pc;
		}
		method.obj.setAdr(Code.pc); // Set the method's starting address

		// Generate the 'enter' instruction.
		// Opcode: enter num_params, num_total_vars
		Code.put(Code.enter);
		Code.put(method.obj.getLevel()); // Number of formal parameters
		Code.put(method.obj.getLocalSymbols().size()); // Total variables (params + locals)
	}
	
	/**
	 * Visits a non-void method declaration name.
	 * Sets up the entry point for the method.
	 */
	public void visit(MethodDeclNameType method) {
		ensureBuiltInFunctions();
		this.currentMethod = method.obj;
		labelAddresses.clear();
		forwardJumpPatches.clear();

		method.obj.setAdr(Code.pc); // Set the method's starting address
		
		// Generate the 'enter' instruction.
		// Opcode: enter num_params, num_total_vars
		Code.put(Code.enter);
		Code.put(method.obj.getLevel()); // Number of formal parameters
		Code.put(method.obj.getLocalSymbols().size()); // Total variables (params + locals)
	}

	/**
	 * Visits the end of any method declaration.
	 * Emits instructions to exit the method scope and return.
	 */
	public void visit(MethodDecl methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
		this.currentMethod = null;
	}
	
	// =================================================================
	// ## Statement Processing
	// =================================================================

	/**
	 * Generates code for the `print` statement.
	 * Handles both `print(expr)` and `print(expr, width)`.
	 */
	@Override
	public void visit(StatementPrint statementPrint) {
	    // 1. Determine the print width first.
	    int width;
	    if (statementPrint.getPrintConst() instanceof PrintConstExists) {
	        // A width was provided, e.g., print(x, 10)
	        width = ((PrintConstExists)statementPrint.getPrintConst()).getNum();
	    } else {
	        // No width was provided, use a default.
	        width = 1;
	    }
	    
	    // The expression result (a single value or an array address) is already on the stack.
	    Struct type = statementPrint.getExpr().struct;

	    if (type.getKind() != Struct.Array) {
	        // --- Case 1: Printing a single value ---
	        Code.loadConst(width); // Push the determined width
	        if (type == Tab.charType) {
	            Code.put(Code.bprint);
	        } else {
	            Code.put(Code.print);
	        }
	    } else {
	        // --- Case 2: Printing an Array or a Set ---
	        boolean isSet = (type == TabExtended.setType);
	        int loopEnd;

	        if (isSet) {
	            Code.loadConst(1); // For sets, start counter i at 1
	        } else {
	            Code.loadConst(0); // For arrays, start counter i at 0
	        }

	        int loopStart = Code.pc;
	        // stack: addr, i

	        // Loop Condition
	        Code.put(Code.dup2);
	        Code.put(Code.dup_x1);
	        Code.put(Code.pop);

	        if (isSet) {
	            Code.loadConst(0);
	            Code.put(Code.aload);
	            Code.putFalseJump(Code.le, 0); 
	            loopEnd = Code.pc - 2;
	        } else {
	            Code.put(Code.arraylength);
	            Code.putFalseJump(Code.lt, 0); 
	            loopEnd = Code.pc - 2;
	        }
	        // stack after check: addr, i

	        // Loop Body
	        Code.put(Code.dup2);
	        
	        if (type.getElemType() == Tab.charType) {
	            Code.put(Code.baload);
	            Code.loadConst(width); // <<< FIX: Use the determined width
	            Code.put(Code.bprint);
	        } else {
	            Code.put(Code.aload);
	            Code.loadConst(width); // <<< FIX: Use the determined width
	            Code.put(Code.print);
	        }
	        
	        Code.loadConst(' '); 
	        Code.loadConst(1); 
	        Code.put(Code.bprint);
	        
	        // i++
	        Code.loadConst(1);
	        Code.put(Code.add);
	        Code.putJump(loopStart);
	        
	        // Cleanup
	        Code.fixup(loopEnd);
	        Code.put(Code.pop);
	        Code.put(Code.pop);
	    }
	}

	/**
	 * Loads the constant width for a `print` statement.
	 */
	public void visit(PrintConstExists pConst) {
//		Code.loadConst(pConst.getNum());
	}
	
	/**
	 * Generates code for the `read` statement.
	 */
	public void visit(StatementRead stRead) {
		Obj dObj = stRead.getDesignator().obj;

		// Choose read instruction based on designator type.
		if (dObj.getType().equals(Tab.charType)) {
			Code.put(Code.bread); // Read a character
		} else {
			Code.put(Code.read);  // Read an integer/boolean
		}
		
		// Store the read value into the designator.
		Code.store(dObj);
	}
	
	// =================================================================
    // ## Function Call Processing
    // =================================================================
    
    /**
     * Handles a function call used within an expression (e.g., x = myFunc(y);).
     * This now includes the special case for len().
     */
    public void visit(FactorFuncCall call) {
        Obj functionObj = call.getDesignator().obj;

        // <<< SPECIAL CASE for len() >>>
        if (functionObj == TabExtended.lenMeth) {
            // The designator has already pushed the array address onto the stack.
            // The arraylength instruction consumes the address and pushes the length.
            Code.put(Code.arraylength);
        } else {
            // Placeholder for other value-returning function calls.
        }
    }

    /**
     * Handles a function call used as a statement (e.g., myFunc(a,b);).
     */
    public void visit(DesignatorStatementCall call) {

        Obj functionObj = call.getDesignator().obj;
        
        if (functionObj.getName().equals("addAll")) {
        	// Arguments (dest_set, src_array) are already on the stack.
            
            // Get the type of the second argument to determine the tag.
            ActPars actPars = call.getActPars();
            MoreActPars moreActPars = ((Actuals)actPars).getMoreActPars();
            Expr secondArgExpr = ((MoreActuals)moreActPars).getExpr();
            Struct secondArgType = secondArgExpr.struct;

            // Push the "type tag" as the third argument.
            if (secondArgType == TabExtended.setType) {
                Code.loadConst(1); // 1 for true (is a Set)
            } else {
                Code.loadConst(0); // 0 for false (is a regular Array)
            }

            // Now call the single, unified addAll subroutine.
            int offset = functionObj.getAdr() - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
            
        }
        
        // For predefined methods like add/addAll, we generate a call to the subroutine.
        // We check if the address is not 0, which it won't be for our generated subroutines.
        else {
            int offset = functionObj.getAdr() - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
        }
        
        // If the function returned a value (not the case for add/addAll), 
        // we would need to pop it from the stack since it's not being used.
        if (functionObj.getType() != Tab.noType) {
            Code.put(Code.pop);
        }
    }
	
	
	// =================================================================
	// ## Assignment, Increment, Decrement
	// =================================================================

	/**
	 * Generates code for an assignment statement.
	 * The expression result is already on the stack.
	 */
	public void visit(DesignatorStatementAssign desigAssign) {
		// The designator visitors have prepared the address/indices.
		// Code.store handles both simple vars and array elements.
		Code.store(desigAssign.getDesignator().obj);
	}

	
	/**
	 * Generates code for an increment statement (`++`).
	 */
	public void visit(DesignatorStatementInc dsStmt) {
		Obj dObj = dsStmt.getDesignator().obj;
		
		if (dsStmt.getDesignator() instanceof DesignatorIdent) {
			// Simple variable: load, inc, store
			Code.load(dObj); // Load current value
			Code.loadConst(1);
			Code.put(Code.add);
			Code.store(dObj);
		} else {
			// Array element: dup address/index, load, inc, store
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.store(dObj);
		}
	}
	
	/**
	 * Generates code for a decrement statement (`--`).
	 */
	public void visit(DesignatorStatementDec dsStmt) {
		Obj dObj = dsStmt.getDesignator().obj;

		if (dsStmt.getDesignator() instanceof DesignatorIdent) {
			// Simple variable: load, dec, store
			Code.load(dObj); // Load current value
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.store(dObj);
		} else {
			// Array element: dup address/index, load, dec, store
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.store(dObj);
		}
	}
	
	// =================================================================
	// ## Designator Loading
	// =================================================================

	/**
	 * Prepares for access to a simple variable (identifier).
	 * If used in an expression, its value is loaded onto the stack.
	 */
	public void visit(DesignatorIdent designatorIdent) {
		SyntaxNode parent = designatorIdent.getParent();
		// Only load if the designator is used as a factor (RHS of expression)
		// or for inc/dec operations. For assignments, the store address is handled by Code.store.
		if (parent instanceof FactorDesignator || 
			parent instanceof DesignatorStatementInc || 
			parent instanceof DesignatorStatementDec) {
			// Do nothing here; Code.load is called in FactorDesignator or handled in Inc/Dec
		}
	}
	
	/**
	 * Prepares for array element access.
	 * Leaves the array address and index on the stack in the correct order.
	 */
	public void visit(DesignatorArrayElem arrayElem) {
		// The index expression is already evaluated and its result is on the stack.
		// Stack: ..., index
		// We need to load the array's base address.
		Code.load(arrayElem.getMyObj().obj); // Stack: ..., index, addr
		
		// Swap to get the correct order for access instructions (addr, index).
		Code.put(Code.dup_x1); // Stack: ..., addr, index, addr
		Code.put(Code.pop);    // Stack: ..., addr, index
	}
	
	// =================================================================
	// ## Expression and Factor Processing
	// =================================================================

	public void visit(ExprMinus exprMinus) {
		Code.put(Code.neg);
	}
	
	public void visit(ExprAddopTerm exprAddopTerm) {
		if (exprAddopTerm.getAddOp() instanceof AddOpPlus) {
			Code.put(Code.add);
		} else { // AddOpMinus
			Code.put(Code.sub);
		}
	}
	
	public void visit(TermMulOpFactor termMulopFactor) {
		if (termMulopFactor.getMulOp() instanceof MulOpMul) {
			Code.put(Code.mul);
		} else if (termMulopFactor.getMulOp() instanceof MulOpDiv) {
			Code.put(Code.div);
		} else { // MulOpMod
			Code.put(Code.rem);
		}
	}
	
	/**
	 * Loads the value of a designator when used as a factor in an expression.
	 */
	public void visit(FactorDesignator factor) {
		// The appropriate designator visitor (Ident or ArrayElem) has prepared
		// the stack. Now, we load the actual value from the address/index.
		Code.load(factor.getDesignator().obj);
	}

	public void visit(FactorNewArray factorNewArray) {
		// The expression for size/capacity N has been evaluated and is on the stack.
	    Struct type = factorNewArray.getType().struct;
	    
	    if (type == TabExtended.setType) {
	        // === Special Case: Creating a new set ===
	        
	        // 1. Allocate N+1 elements for a capacity of N.
	        Code.loadConst(1);
	        Code.put(Code.add); // Stack: N+1
	        Code.put(Code.newarray);
	        Code.put(1); // Create an array of words (int). Stack: set_addr
	        
	        // 2. Initialize the counter at index 0 to zero: set[0] = 0.
	        Code.put(Code.dup);      // Duplicate address for initialization. Stack: set_addr, set_addr
	        Code.loadConst(0);       // Push index 0. Stack: set_addr, set_addr, 0
	        Code.loadConst(0);       // Push value 0. Stack: set_addr, set_addr, 0, 0
	        Code.put(Code.astore);   // set[0] = 0. Consumes addr, index, value. Stack: set_addr
	        
	    } else {
	        // === Default Case: Creating a regular array ===
	        Code.put(Code.newarray);
	        if (factorNewArray.getType().struct.equals(Tab.charType)) {
	            Code.put(0); // Create a byte array.
	        } else {
	            Code.put(1); // Create a word array.
	        }
	    }
	}
	
	/**
	 * Called when a label definition (e.g., "myLabel:") is encountered.
	 * It records the current address and patches any pending forward jumps.
	 */
	@Override
	public void visit(LabelDef label) {
	    String labelName = label.getLabelName();
	    int labelAddress = Code.pc;
	    
	    // Store the address of this label.
	    labelAddresses.put(labelName, labelAddress);

	    // Check if there are any forward jumps waiting for this label.
	    if (forwardJumpPatches.containsKey(labelName)) {
	        // If so, patch all of them to jump to the current address.
	        for (int addressToPatch : forwardJumpPatches.get(labelName)) {
	            Code.fixup(addressToPatch);
	        }
	        // Remove the entry, as all jumps to this label are now resolved.
	        forwardJumpPatches.remove(labelName);
	    }
	}

	/**
	 * Called when a "goto label;" statement is encountered.
	 * It generates a jump instruction, either to a known address (backward jump)
	 * or with a placeholder for a future address (forward jump).
	 */
	@Override
	public void visit(GotoStmt goTo) {
	    String targetLabel = goTo.getTargetLabel();

	    // Check if we have already seen the label (it's a backward jump).
	    if (labelAddresses.containsKey(targetLabel)) {
	        int targetAddr = labelAddresses.get(targetLabel);
	        Code.putJump(targetAddr);
	    } else {
	        // This is a forward jump. The address is not yet known.
	        // Generate a jump with a placeholder (0) and save its location to be patched later.
	        Code.putJump(0);
	        
	        // Add the address of the placeholder to our patch list for this label.
	        forwardJumpPatches.computeIfAbsent(targetLabel, k -> new ArrayList<>()).add(Code.pc - 2);
	    }
	}

	// =================================================================
	// ## Constant Loading
	// =================================================================

	/**
	 * For constant declarations, we "pre-calculate" their value and store it
	 * in the Address field of their symbol table object.
	 */
	public void visit(FormalConstDeclr constDeclr) {
		constDeclr.obj.setAdr(getConstVal(constDeclr.getConstVals()));
	}

	public void visit(MoreConstValsExist moreConstDeclr) {
		moreConstDeclr.obj.setAdr(getConstVal(moreConstDeclr.getConstVals()));
	}
	
	// =================================================================
	// ## Expressions
	// =================================================================
	
	@Override
	public void visit(ExprUnion expr) {
		Obj unionMeth = Tab.find("union");
	    Code.put(Code.call);
	    Code.put2(unionMeth.getAdr() - Code.pc + 1);
	}
	@Override
	public void visit(ExprIntersection expr) {
		Obj unionMeth = Tab.find("intersection");
		Code.put(Code.call);
		Code.put2(unionMeth.getAdr() - Code.pc + 1);
	}
	@Override
	public void visit(ExprDifference expr) {
		Obj unionMeth = Tab.find("difference");
		Code.put(Code.call);
		Code.put2(unionMeth.getAdr() - Code.pc + 1);
	}
	
	// --- Loading constants when used as factors in expressions ---

	public void visit(FactorNum factor) {
		Code.loadConst(factor.getNum());
	}
	
	public void visit(FactorChar factor) {
		Code.loadConst(factor.getCh());
	}
	
	public void visit(FactorBool fBool) {
		Code.loadConst("true".equalsIgnoreCase(fBool.getBl()) ? 1 : 0);
	}
	
	// --- Loading constants directly from AST nodes (e.g., in declarations) ---
	
	public void visit(ConstNum constNum) {
		Code.loadConst(constNum.getNumVal());
	}

	public void visit(ConstChar constChar) {
		Code.loadConst(constChar.getCharVal());
	}

	public void visit(ConstBool constBool) {
		Code.loadConst("true".equalsIgnoreCase(constBool.getBoolVal()) ? 1 : 0);
	}
}