package com.stuffwithstuff.bulfinch;

import java.util.List;

/**
 * Compiles functions to bytecode and handles allocating the registers.
 * Registers will be set up like:
 * 
 * [params...] [locals...] [temporaries]
 * 
 * The parameters will use an overlapping register window from the caller's
 * registers. After that, we allocate a register for each local variable
 * declared in the function. Then we create as many temporary registers as
 * needed for things like function call arguments.
 * 
 * Whenever we compile a call, we set it up so that the function to be called
 * and its arguments are at the end of the registers so that the callee can
 * share them.
 * 
 * The fact that we set up registers for all locals up front means that we
 * can't use a single-pass compiler: we need to know the full set of locals
 * before we can start allocating temporaries. The reason is programs like this:
 * 
 *  fn main() {
 *    foo(2, var a = 3)
 *  }
 * 
 * When we compile that, we compile the "2" before the "var a = 3". Which
 * register should we load it into? If we were to restrict the grammar to not
 * allow variable declarations inside calls, we could allocate local variable
 * registers incrementally as they are evaluated in the function.
 */
public class Compiler implements ExprVisitor<Integer> {
  public static Function compileTopLevel(FunctionExpr function, String name) {
    NameResolver.resolveTopLevel(function);
    return compileInnerFunction(function, name);
  }

  private static Function compileInnerFunction(FunctionExpr function, String name) {
    Compiler compiler = new Compiler();
    compiler.compile(function, name);
    return compiler.mFunction;
  }
  
  /**
   * This special register means that we don't care about the result value. It
   * is used as the destination for all but the last expression in a sequence.
   * Other expression types will check for this and omit work if they know it
   * isn't needed because the result will be ignored anyway.
   */
  private static final int DISCARD = -1;
  
  @Override
  public void visit(AssignExpr expr, Integer dest) {
    if (expr.getName().isLocal()) {
      int register = expr.getName().getLocalIndex();
      
      // Assign the value to the local.
      expr.getValue().accept(this, register);
      
      // If we have another destination, copy there too. This would be something
      // like:
      //
      //   foo(a = 1)
      //
      // where we want to assign to 'a' but also use the value as an argument.
      if ((dest != DISCARD) && (register != dest)) {
        write(Op.MOVE, register, dest);
      }
    } else if (expr.getName().isUpvar()) {
      // Get a temp register to store the value in if we don't already have one.
      int register = dest;
      if (register == DISCARD) {
        register = push();
      }
      
      // Evaluate the value and store it in a register.
      expr.getValue().accept(this, register);
      
      // Assign to the upvar.
      write(Op.STORE_UPVAR, expr.getName().getUpvar().index, register);
      
      if (dest == DISCARD) {
        pop(register);
      }
    }
  }
  
  @Override
  public void visit(BoolExpr expr, Integer dest) {
    compileConstant(expr.getValue(), dest);
  }

  @Override
  public void visit(CallExpr expr, Integer dest) {
    // If we don't have a destination to put the result, make a temp one.
    int register = dest;
    if (register == DISCARD) {
      register = push();
    }
    
    // Load the function.
    int fn = push();
    expr.getFunction().accept(this, fn);
    
    // Evaluate the arguments.
    for (int i = 0; i < expr.getArgs().size(); i++) {
      int arg = push();
      expr.getArgs().get(i).accept(this, arg);
    }
    
    // Call it.
    write(Op.CALL, register, fn, expr.getArgs().size());
    
    for (int i = 0; i < expr.getArgs().size(); i++) {
      pop();
    }
    
    pop(fn);
    
    if (dest == DISCARD) {
      pop(register);
    }
  }

  @Override
  public void visit(FunctionExpr expr, Integer dest) {
    // Don't load the function if we aren't loading it into anything.
    if (dest == DISCARD) return;
    
    // Compile the function and add it to the constant pool.
    Function function = Compiler.compileInnerFunction(expr, "<anon>");
    int index = mFunction.addConstant(function);
    
    // Write an op to create a closure for the function.
    write(Op.CLOSURE, index, dest);
    
    // Capture the upvars.
    for (UpvarRef upvar : expr.getUpvars()) {
      write(Op.ADD_UPVAR, upvar.register);
    }
  }

  @Override
  public void visit(NameExpr expr, Integer dest) {
    // Do nothing if we are ignoring the result.
    if (dest == DISCARD) return;
    
    if (expr.getName().isLocal()) {
      write(Op.MOVE, expr.getName().getLocalIndex(), dest);
    } else if (expr.getName().isUpvar()) {
      write(Op.LOAD_UPVAR, expr.getName().getUpvar().index, dest);
    } else {
      // Must be a global.
      int name = mFunction.addConstant(expr.getName().getIdentifier());
      write(Op.LOAD_GLOBAL, name, dest);
    }
  }

  @Override
  public void visit(NumberExpr expr, Integer dest) {
    compileConstant(expr.getValue(), dest);
  }

  @Override
  public void visit(SequenceExpr sequence, Integer dest) {
    for (int i = 0; i < sequence.getExpressions().size(); i++) {
      Expr expr = sequence.getExpressions().get(i);
      // Discard the results of all but the last expression in the sequence.
      int thisDest = dest;
      if (i < sequence.getExpressions().size() - 1) thisDest = DISCARD;
      expr.accept(this, thisDest);
    }
  }

  @Override
  public void visit(StringExpr expr, Integer dest) {
    compileConstant(expr.getValue(), dest);
  }

  @Override
  public void visit(VarExpr expr, Integer dest) {
    // Initialize the local.
    int localRegister = expr.getName().getLocalIndex();
    
    expr.getValue().accept(this, localRegister);
    
    // Then copy the value to the desired slot.
    if ((dest != DISCARD) && (localRegister != dest)) {
      write(Op.MOVE, localRegister, dest);
    }
  }

  private Compiler() {}
  
  private void compile(FunctionExpr function, String name) {
    List<String> locals = function.getLocals();
    mFunction = new Function(name, locals);
    
    // Make sure we have registers for each local.
    mUsedRegisters = mFunction.ensureRegisters(locals.size());

    int resultRegister = push();

    // Compile the body.
    function.getBody().accept(this,  resultRegister);
    write(Op.RETURN, resultRegister);
    
    pop(resultRegister);
    
    mFunction.setNumUpvars(function.getUpvars().size());
  }

  private int push() {
    mUsedRegisters++;
    
    // Make sure we have enough.
    mFunction.ensureRegisters(mUsedRegisters);
    
    return mUsedRegisters - 1;
  }
  
  private void pop(int pushed) {
    if (pushed != mUsedRegisters - 1) {
      throw new RuntimeException("mismatch!");
    }
    
    pop();
  }
  
  private void pop() {
    mUsedRegisters--;
  }
  
  private void write(int op, int a, int b, int c) {
    mFunction.getCode().add(new Op(op, a, b, c));
  }
  
  private void write(int op, int a, int b) {
    mFunction.getCode().add(new Op(op, a, b));
  }
  
  private void write(int op, int a) {
    mFunction.getCode().add(new Op(op, a));
  }

  private void compileConstant(Object value, int dest) {
    // Don't load the constant if we aren't loading it into anything.
    if (dest == DISCARD) return;
    
    write(Op.CONSTANT, mFunction.addConstant(value), dest);
  }
  
  private Function mFunction;
  private int mUsedRegisters;
}
