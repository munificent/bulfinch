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
 * register should we load it into?
 */
public class Compiler implements ExprVisitor<Integer, Integer> {
  Compiler() {}
  
  Function compile(FunctionExpr function, String name) {
    mFunction = new Function(name);
    
    mLocals = LocalFinder.getLocals(function);
    
    // Make sure we have registers for each local and one for the result.
    mFunction.numRegisters = mLocals.size() + 1;

    // Compile the body.
    function.getBody().accept(this,  mLocals.size());
    write(Op.RETURN,  mLocals.size());

    return mFunction;
  }

  @Override
  public Integer visit(AssignExpr expr, Integer dest) {
    int register = mLocals.indexOf(expr.getName());
    if (register == -1) {
      throw new RuntimeException("Cannot assign to unknown local " +
          expr.getName());
    }
    
    expr.getValue().accept(this, register);
    return dest;
  }
  
  @Override
  public Integer visit(BoolExpr expr, Integer dest) {
    compileConstant(expr.getValue(), dest);
    return dest;
  }

  @Override
  public Integer visit(CallExpr expr, Integer dest) {
    // Load the function.
    expr.getFunction().accept(this, dest);
    
    // Evaluate the arguments.
    for (int i = 0; i < expr.getArgs().size(); i++) {
      expr.getArgs().get(i).accept(this, dest + 1 + i);
    }
    
    // Make sure we have enough registers for the arguments.
    mFunction.numRegisters = Math.max(mFunction.numRegisters,
        dest + expr.getArgs().size() + 1);
    
    // Call it.
    write(Op.CALL, dest);
    return dest;
  }

  @Override
  public Integer visit(FunctionExpr expr, Integer dest) {
    throw new RuntimeException("Not impl.");
  }

  @Override
  public Integer visit(NameExpr expr, Integer dest) {
    // See if it's a local.
    for (int i = 0; i < mLocals.size(); i++) {
      if (mLocals.get(i).equals(expr.getName())) {
        write(Op.MOVE, i, dest);
        return dest;
      }
    }
    
    // Must be a global.
    mFunction.constants.add(expr.getName());
    int name = mFunction.constants.size() - 1;
    
    write(Op.LOAD_GLOBAL, name, dest);
    return dest;
  }

  @Override
  public Integer visit(NumberExpr expr, Integer dest) {
    compileConstant(expr.getValue(), dest);
    return dest;
  }

  @Override
  public Integer visit(SequenceExpr sequence, Integer dest) {
    // Reuse the destination so that the results of earlier expressions in the
    // sequence just get overwritten.
    for (Expr expr : sequence.getExpressions()) {
      expr.accept(this, dest);
    }
    return dest;
  }

  @Override
  public Integer visit(StringExpr expr, Integer dest) {
    compileConstant(expr.getValue(), dest);
    return dest;
  }

  @Override
  public Integer visit(VarExpr expr, Integer dest) {
    // Initialize the local.
    int localRegister = mLocals.indexOf(expr.getName());
    if (localRegister == -1) {
      throw new RuntimeException("Could not find local " + expr.getName());
    }
    
    expr.getValue().accept(this, localRegister);
    
    // Then copy the value to the desired slot.
    if (localRegister != dest) {
      write(Op.MOVE, localRegister, dest);
    }
    
    return dest;
  }
  
  public void write(int op, int a, int b, int c) {
    mFunction.code.add(new Op(op, a, b, c));
  }
  
  public void write(int op, int a, int b) {
    mFunction.code.add(new Op(op, a, b));
  }
  
  public void write(int op, int a) {
    mFunction.code.add(new Op(op, a));
  }
  
  public void write(int op) {
    mFunction.code.add(new Op(op));
  }

  private void compileConstant(Object value, int dest) {
    // TODO(bob): Look for duplicates.
    mFunction.constants.add(value);
    write(Op.CONSTANT, mFunction.constants.size() - 1, dest);
  }
  
  private Function mFunction;
  private List<String> mLocals;
  
}
