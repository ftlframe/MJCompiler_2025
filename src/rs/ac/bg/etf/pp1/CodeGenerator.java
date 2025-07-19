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
	    
	    Code.put(Code.enter); Code.put(2); Code.put(3);
	    int i_local_idx = 2;

	    // <<< SECTION 1: CORRECTED FULLNESS CHECK >>>
	    // We will check if size >= capacity. If so, we exit.
	    
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
	    // Jump to exit if size >= capacity.
	    // The inverse of "greater or equal" is "less than".
	    Code.putFalseJump(Code.ge, 0); int canAddPatch = Code.pc - 2;
	    // If the condition is met (set is full), fall through to exit.
	    Code.put(Code.exit); Code.put(Code.return_);
	    Code.fixup(canAddPatch);
	    // If we get here, the set is not full and the stack is clean.

	    // 2. Check for duplicates (This logic is now correct)
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

	    // 3. Add element and increment size (This logic is now correct)
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
			// Case 2: Printing an Array or a Set
	        boolean isSet = (type == TabExtended.setType);
	        int loopEnd;

	        if (isSet) {
	            Code.loadConst(1); // For sets, start counter i at 1
	        } else {
	            Code.loadConst(0); // For arrays, start counter i at 0
	        }

	        int loopStart = Code.pc;
	        // stack at loop start: addr, i

	        // --- Loop Condition using the robust pattern for BOTH cases ---
	        Code.put(Code.dup2);      // Duplicate state for the check (addr, i, addr, i)
	        Code.put(Code.dup_x1);    // Manipulate the copy
	        Code.put(Code.pop);       // The copy is now (i, addr)

	        if (isSet) {
	            Code.loadConst(0);
	            Code.put(Code.aload);     // Get size from set[0] (consumes copy of addr)
	            Code.putFalseJump(Code.le, 0); 
	            loopEnd = Code.pc - 2;    // Jumps if i > size
	        } else {
	            Code.put(Code.arraylength); // Get array length (consumes copy of addr)
	            Code.putFalseJump(Code.lt, 0); 
	            loopEnd = Code.pc - 2;    // Jumps if i >= len
	        }
	        // stack after check (if continuing): addr, i

	        // --- Loop Body ---
	        Code.put(Code.dup2);      // Duplicate state for use in the body
	        
	        if (type.getElemType() == Tab.charType) {
	            Code.put(Code.baload);
	            Code.loadConst(1);
	            Code.put(Code.bprint);
	        } else {
	            Code.put(Code.aload);
	            Code.loadConst(5);
	            Code.put(Code.print);
	        }
	        
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
	        Code.put(Code.pop); // pop addr
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
		// This is called for the expression `s2 union s3`.
	    // The addresses of s2 and s3 are on the stack from visiting the children.
	    // Stack: ..., s2_addr, s3_addr

	    // --- 1. Calculate required capacity (s2[0] + s3[0]) ---
	    Code.put(Code.dup2);        // Stack: s2, s3, s2, s3
	    Code.loadConst(0);          // Stack: s2, s3, s2, s3, 0
	    Code.put(Code.aload);       // Pops s3, 0; Pushes s3[0]. Stack: s2, s3, s2, size(s3)
	    Code.put(Code.dup_x1);      // Stack: s2, s3, size(s3), s2, size(s3)
	    Code.put(Code.pop);         // Stack: s2, s3, size(s3), s2
	    Code.loadConst(0);          // Stack: s2, s3, size(s3), s2, 0
	    Code.put(Code.aload);       // Pops s2, 0; Pushes s2[0]. Stack: s2, s3, size(s3), size(s2)
	    Code.put(Code.add);         // Stack: s2, s3, new_capacity

	    // --- 2. Create the new temporary set ---
	    Code.loadConst(1); 
	    Code.put(Code.add);         // Stack: s2, s3, new_capacity + 1
	    Code.put(Code.newarray); 
	    Code.put(1);                // Stack: s2, s3, temp_set_addr
	    Code.put(Code.dup); 
	    Code.loadConst(0); 
	    Code.loadConst(0); 
	    Code.put(Code.astore);      // Initialize temp_set[0] = 0
	    // Stack: s2_addr, s3_addr, temp_set_addr

	    // --- 3. Populate the temporary set using addAll ---
	    // a) Call addAllFromSet(temp_set, s2)
	    Code.put(Code.dup_x2);      // temp, s2, s3, temp
	    Code.put(Code.dup_x2);      // temp, temp, s2, s3, temp
	    Code.put(Code.pop);         // temp, temp, s2, s3
	    Code.put(Code.dup_x2);		// temp, s3, temp, s2, s3
	    Code.put(Code.pop);         // temp, s3, temp (dest), s2 (src) -- ready for call
	    
	    Obj addAllSetMeth = Tab.find("addAllFromSet");
	    Code.put(Code.call);
	    Code.put2(addAllSetMeth.getAdr() - Code.pc + 2);
	    // After the call, arguments are consumed. Stack: temp_set_addr, s3_addr

	    // b) Call addAllFromSet(temp_set, s3)
	    Code.put(Code.dup_x1);
	    Code.put(Code.pop);         // s3_addr, temp_set_addr (swapped and ready for call)

	    Code.put(Code.call);
	    Code.put2(addAllSetMeth.getAdr() - Code.pc + 2);
	    // After the call, arguments are consumed. The stack is left with the final result.
	    // Stack: ..., temp_set_addr
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