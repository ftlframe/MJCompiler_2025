We'll use a hypothetical new function, int max(int a, int b), as an example.

Workflow for Adding a New Predefined Function

Adding a new function always involves modifying the same three parts of the compiler: the Symbol Table, the Semantic Analyzer, and the Code Generator.

## Step 1: Declare the Function in the Symbol Table

First, you need to make the compiler aware that your new function exists globally.

File: TabExtended.java

    Create a static Obj reference for your function so you can easily access it from other parts of the compiler.
    Java

public static Obj maxMeth;

Inside the init() method, define the function's signature and add it to the symbol table.

    Insert the Method: Define its name and return type.

    Define Parameters: Open a new scope, add its parameters, and set the parameter count (setLevel).

    // Inside TabExtended.init()

    // int max(int a, int b)
    maxMeth = insert(Obj.Meth, "max", Tab.intType); // Name "max", returns int
    openScope();
    insert(Obj.Var, "a", Tab.intType); // Param 1: int a
    insert(Obj.Var, "b", Tab.intType); // Param 2: int b
    maxMeth.setLocals(currentScope.getLocals());
    maxMeth.setLevel(2); // 2 formal parameters
    closeScope();

## Step 2: Add Semantic (Type) Checking

Next, you must teach the semantic analyzer how to validate calls to your new function.

File: SemanticPass.java

    Go to the appropriate visitor:

        visit(FactorFuncCall) for functions that return a value (like max).

        visit(DesignatorStatementCall) for void functions.

    Add a special case to check the function's arguments. This ensures the correct number and types of arguments are used.
    Java

    // Inside SemanticPass.visit(FactorFuncCall)

    Obj func = call.getDesignator().obj;

    // ...

    // SPECIAL CASE for max()
    if (func == TabExtended.maxMeth) {
        List<Struct> actuals = collectActualParams(call.getActPars());
        if (actuals.size() != 2) { // Check argument count
            report_error("Greska: Funkcija 'max' ocekuje tacno 2 argumenta.", call);
        } else if (!actuals.get(0).equals(Tab.intType) || !actuals.get(1).equals(Tab.intType)) { // Check argument types
             report_error("Greska: Argumenti za funkciju 'max' moraju biti tipa int.", call);
        }

        // If checks pass, the result of the expression is an int
        call.struct = Tab.intType;
        return; // Skip generic checking
    }

    // ... logic for other functions ...

## Step 3: Generate the Bytecode

Finally, you tell the code generator what to do when it sees a call to your function. You have two main approaches.

File: CodeGenerator.java

Approach A: Function Maps to a Single Instruction

This is the simplest case, used for functions like len().

    In the call-site visitor (visit(FactorFuncCall)), check if the function is yours.

    Emit the instruction. The arguments will already be on the stack.
    Java

    // Example from len()
    if (functionObj == TabExtended.lenMeth) {
        Code.put(Code.arraylength);
    }

Approach B: Function Requires Complex Logic (Most Common)

This is used for functions like add, addAll, or our max example.

    Create a generate... method to produce the bytecode for your function's logic. This becomes a callable subroutine.
    Java

private void generateMaxMethod() {
    Obj maxMeth = Tab.find("max");
    maxMeth.setAdr(Code.pc);

    // enter 2 params, 2 total vars
    Code.put(Code.enter); Code.put(2); Code.put(2);

    // Logic: return (a > b) ? a : b;
    Code.put(Code.load_n + 0); // load a
    Code.put(Code.load_n + 1); // load b

    // if a <= b, jump to load b instead
    Code.putFalseJump(Code.gt, 0); int else_adr = Code.pc - 2;

    // 'then' block: a is greater, it's already on top of stack. Jump to end.
    Code.put(Code.load_n + 0); 
    Code.putJump(0); int end_adr = Code.pc - 2;

    // 'else' block: b is greater or equal
    Code.fixup(else_adr);
    Code.put(Code.load_n + 1);

    // End
    Code.fixup(end_adr);

    Code.put(Code.exit);
    Code.put(Code.return_); // The max value is left on the stack for the return
}

Call your generate... method inside ensureBuiltInFunctions() so the subroutine is generated once.
Java

private void ensureBuiltInFunctions() {
    if (!builtInFunctionsGenerated) {
        generateAddMethod();
        generateAddAllMethod();
        generateMaxMethod(); // Add your new function here
        builtInFunctionsGenerated = true;
    }
}

In the call-site visitor (visit(FactorFuncCall)), simply generate a call instruction.
Java

// Inside CodeGenerator.visit(FactorFuncCall)
Obj functionObj = call.getDesignator().obj;
if (functionObj == TabExtended.maxMeth) {
    int offset = functionObj.getAdr() - Code.pc;
    Code.put(Code.call);
    Code.put2(offset);
}