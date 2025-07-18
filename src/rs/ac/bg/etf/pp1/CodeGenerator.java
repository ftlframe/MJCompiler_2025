package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	
	private boolean builtInFunctionsGenerated = false;
	private int mainPc;

	public int getMainPc() {
		return mainPc;
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
	
	private void generateAddMethod() {
	    Obj addMeth = Tab.find("add");
	    addMeth.setAdr(Code.pc);
	    
	    // Method signature: void add(set s, int element)
	    // enter 2 params, 3 total vars (1 extra local for loop counter `i`)
	    Code.put(Code.enter); Code.put(2); Code.put(3);
	    int i_local_idx = 2;

	    // 1. Check if the set is full: if (s[0] >= capacity) return;
	    Code.put(Code.load_n + 0); // stack: s
	    Code.put(Code.dup);        // stack: s, s
	    Code.put(Code.arraylength); // stack: s, capacity+1
	    Code.loadConst(1);         // stack: s, capacity+1, 1
	    Code.put(Code.sub);        // stack: s, capacity
	    Code.put(Code.dup_x1);     // stack: capacity, s, capacity
	    Code.put(Code.pop);        // stack: capacity, s
	    Code.put(Code.dup_x1);     // stack: s, capacity, s
	    Code.loadConst(0);         // stack: s, capacity, s, 0
	    Code.put(Code.aload);      // stack: s, capacity, size=s[0]
	    // Condition: continue if size < capacity. Jump if size >= capacity.
	    Code.putFalseJump(Code.lt, 0); int notFullPatch = Code.pc - 2;
	    Code.put(Code.exit); Code.put(Code.return_); // If full, exit
	    Code.fixup(notFullPatch);
	    // stack: s

	    // 2. Check for duplicates: for (i = 1; i <= s[0]; i++)
	    Code.loadConst(1); Code.put(Code.store_n + i_local_idx); // i = 1
	    int dupLoopStart = Code.pc;
	    Code.put(Code.load_n + i_local_idx); // stack: s, i
	    Code.put(Code.load_n + 0);           // stack: s, i, s
	    Code.loadConst(0);                   // stack: s, i, s, 0
	    Code.put(Code.aload);                // stack: s, i, s[0] (size)
	    Code.putFalseJump(Code.le, 0); int dupLoopEnd = Code.pc - 2; // Jump if i > size
	    
	    // Body: if (s[i] == element) return;
	    Code.put(Code.load_n + 0); Code.put(Code.load_n + i_local_idx); Code.put(Code.aload); // stack: s, s[i]
	    Code.put(Code.load_n + 1); // stack: s, s[i], element
	    Code.putFalseJump(Code.ne, 0); int noDuplicatePatch = Code.pc - 2;
	    Code.put(Code.exit); Code.put(Code.return_); // If duplicate found, exit
	    Code.fixup(noDuplicatePatch);
	    
	    // i++
	    Code.put(Code.inc); Code.put(i_local_idx); Code.put(1);
	    Code.putJump(dupLoopStart);
	    Code.fixup(dupLoopEnd);
	    // stack: s

	    // 3. Add the element and increment size.
	    // s[s[0] + 1] = element;
	    Code.put(Code.load_n + 0); // s
	    Code.put(Code.dup);        // s, s
	    Code.loadConst(0);         // s, s, 0
	    Code.put(Code.aload);      // s, s, s[0] (size)
	    Code.loadConst(1);         // s, s, size, 1
	    Code.put(Code.add);        // s, s, index_to_add=size+1
	    Code.put(Code.load_n + 1); // s, s, index_to_add, element
	    Code.put(Code.astore);     // Element is stored. stack: s
	    
	    // s[0]++;
	    Code.put(Code.load_n + 0);    // s
	    Code.loadConst(0);          // s, 0 (index)
	    Code.put(Code.dup2);        // s, 0, s, 0
	    Code.put(Code.aload);       // s, 0, current_size
	    Code.loadConst(1);          // s, 0, current_size, 1
	    Code.put(Code.add);         // s, 0, new_size
	    Code.put(Code.astore);      // s[0] = new_size.
	    
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}

	private void generateAddAllMethod() {
	    Obj addAllMeth = Tab.find("addAll");
	    addAllMeth.setAdr(Code.pc);
	    
	    // enter 2 params, 3 total vars (1 for loop counter `i`)
	    Code.put(Code.enter); Code.put(2); Code.put(3);
	    int i_local_idx = 2;
	    
	    // for (i = 0; i < arr.length; i++)
	    Code.loadConst(0); Code.put(Code.store_n + i_local_idx);
	    
	    int loopStart = Code.pc;
	    Code.put(Code.load_n + i_local_idx); Code.put(Code.load_n + 1); Code.put(Code.arraylength);
	    Code.putFalseJump(Code.lt, 0); int loopEnd = Code.pc - 2;
	    
	    // Body: call add(s, arr[i]);
	    Code.put(Code.load_n + 0); // Param 1 for add(): dest_set `s`
	    Code.put(Code.load_n + 1); // Load src_array `arr`
	    Code.put(Code.load_n + i_local_idx);    // Load `i`
	    Code.put(Code.aload);      // Param 2 for add(): element `arr[i]`
	    
	    Obj addMeth = Tab.find("add");
	    Code.put(Code.call);
	    Code.put2(addMeth.getAdr() - Code.pc);
	    
	    Code.put(Code.inc); Code.put(i_local_idx); Code.put(1); // i++
	    Code.putJump(loopStart);
	    Code.fixup(loopEnd);
	    
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}
	
	private void ensureBuiltInFunctions() {
	    if (!builtInFunctionsGenerated) {
	        generateAddMethod();
	        generateAddAllMethod();
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
		Struct type = statementPrint.getExpr().struct;

		if (type.getKind() != Struct.Array) {
			Code.loadConst(5); 
			if (type == Tab.charType) {
				Code.put(Code.bprint);
			} else {
				Code.put(Code.print);
			}
		} else {
			if (type == TabExtended.setType) {
				// Printing a Set (This logic appears correct in your disassembly)
				Code.put(Code.dup);
				Code.loadConst(1); 
				
				int loopStart = Code.pc;
				Code.put(Code.dup_x1); Code.put(Code.dup_x1); Code.put(Code.pop);
				Code.loadConst(0); Code.put(Code.aload);
				Code.putFalseJump(Code.le, 0); int loopEnd = Code.pc - 2;

				Code.put(Code.dup_x1); Code.put(Code.aload);
				Code.loadConst(5); Code.put(Code.print);

				Code.loadConst(' '); Code.loadConst(1); Code.put(Code.bprint);

				Code.loadConst(1); Code.put(Code.add);
				Code.putJump(loopStart);

				Code.fixup(loopEnd);
				Code.put(Code.pop); Code.put(Code.pop);

			} else {
				// Case 2: Printing an Array or a Set
		        // The array address is on the stack.

		        boolean isSet = (type == TabExtended.setType);
		        int loopEnd;

		        if (isSet) {
		            Code.loadConst(1); // For sets, start counter i at 1
		        } else {
		            Code.loadConst(0); // For arrays, start counter i at 0
		        }

		        int loopStart = Code.pc;
		        // stack at loop start: arr_addr, i

		        // --- Loop Condition ---
		        // To check "i < size" without losing our state, we first duplicate it.
		        Code.put(Code.dup2); // stack: arr_addr, i, arr_addr, i
	        	Code.put(Code.dup_x1);	  // stack: arr_addr, i, i, arr_addr, i
	        	
		        // Now we use the copies to get the size/length for comparison.
		        if (isSet) {
		            Code.put(Code.pop);       // stack: arr_addr, i, arr_addr
		            Code.loadConst(0);      // stack: arr_addr, i, arr_addr, 0
		            Code.put(Code.aload);     // stack: arr_addr, i, size
		            Code.putFalseJump(Code.lt, 0); 
		            loopEnd = Code.pc - 2;    // Jump if NOT (i < size)
		        } else {

		            Code.put(Code.pop);       // stack: arr_addr, i, arr_addr
		            Code.put(Code.arraylength); // stack: arr_addr, i, len
		            Code.putFalseJump(Code.lt, 0); 
		            loopEnd = Code.pc - 2;    // Jump if NOT (i < len)
		        }
		        // If we continue, the jump pops the copies, leaving the original state.
		        // stack after check: arr_addr, i

		        // --- Loop Body ---
		        // We still have our original state, so we duplicate it again for use.
		        Code.put(Code.dup2);      // stack: arr_addr, i, arr_addr, i
		        
		        if (type.getElemType() == Tab.charType) {
		            Code.put(Code.baload);
		            Code.loadConst(1);
		            Code.put(Code.bprint);
		        } else {
		            Code.put(Code.aload);
		            Code.loadConst(5);
		            Code.put(Code.print);
		        }
		        // stack after print: arr_addr, i
		        
		        Code.loadConst(' '); 
		        Code.loadConst(1); 
		        Code.put(Code.bprint);
		        
		        // i++
		        Code.loadConst(1);
		        Code.put(Code.add);
		        Code.putJump(loopStart);
		        
		        // --- Cleanup ---
		        Code.fixup(loopEnd);
		        Code.put(Code.pop); // pop final i
		        Code.put(Code.pop); // pop arr_addr
			}
		}
	}

	/**
	 * Loads the constant width for a `print` statement.
	 */
	public void visit(PrintConstExists pConst) {
		Code.loadConst(pConst.getNum());
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
        
        // For predefined methods like add/addAll, we generate a call to the subroutine.
        // We check if the address is not 0, which it won't be for our generated subroutines.
        {
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