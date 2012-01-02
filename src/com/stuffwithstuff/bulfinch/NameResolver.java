package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves all name references in a function. Modifies the AST to include
 * resolution information, and returns the list of all local variables
 * declared in the function.
 */
public class NameResolver implements ExprVisitor<Void, Void> {
  public static void resolveTopLevel(FunctionExpr function) {
    new NameResolver(null).resolve(function);
  }
  
  private void resolve(FunctionExpr function) {
    mLocals.addAll(function.getParameters());
    function.getBody().accept(this, null);
    function.resolve(mLocals, mUpvars);
  }

  @Override
  public Void visit(AssignExpr expr, Void dummy) {
    resolveName(expr.getName());
    expr.getValue().accept(this, dummy);
    return null;
  }
  
  @Override
  public Void visit(BoolExpr expr, Void arg) {
    // Do nothing.
    return null;
  }

  @Override
  public Void visit(CallExpr expr, Void dummy) {
    expr.getFunction().accept(this, dummy);
    for (Expr arg : expr.getArgs()) arg.accept(this, dummy);
    return null;
  }

  @Override
  public Void visit(FunctionExpr expr, Void arg) {
    // Resolve the function itself.
    new NameResolver(this).resolve(expr);

    return null;
  }

  @Override
  public Void visit(NameExpr expr, Void arg) {
    resolveName(expr.getName());
    return null;
  }

  @Override
  public Void visit(NumberExpr expr, Void arg) {
    // Do nothing.
    return null;
  }

  @Override
  public Void visit(SequenceExpr sequence, Void arg) {
    for (Expr expr : sequence.getExpressions()) expr.accept(this, arg);
    return null;
  }

  @Override
  public Void visit(StringExpr expr, Void arg) {
    // Do nothing.
    return null;
  }

  @Override
  public Void visit(VarExpr expr, Void arg) {
    mLocals.add(expr.getName().getIdentifier());
    expr.getName().resolveLocal(mLocals.size() - 1);
    
    expr.getValue().accept(this, arg);
    return null;
  }

  private NameResolver(NameResolver outerFunction) {
    mOuterFunction = outerFunction;
    mLocals = new ArrayList<String>();
    mUpvars = new ArrayList<UpvarRef>();
  }
  
  private void resolveName(Name name) {
    // See if it's a local.
    int local = mLocals.indexOf(name.getIdentifier());
    if (local != -1) {
      name.resolveLocal(local);
      return;
    }

    // See if it's a known upvar.
    for (int upvar = 0; upvar < mUpvars.size(); upvar++) {
      if (mUpvars.get(upvar).name.equals(name.getIdentifier())) {
        name.resolveUpvar(mUpvars.get(upvar));
        return;
      }
    }
    
    // See if it's a new upvar (a variable defined in the enclosing function).
    if (mOuterFunction != null) {
      int outer = mOuterFunction.mLocals.indexOf(name.getIdentifier());
      if (outer != -1) {
        // Create a new upvar slot for it.
        UpvarRef upvar = new UpvarRef(name.getIdentifier(), mUpvars.size(), outer);
        mUpvars.add(upvar);
        name.resolveUpvar(upvar);
        return;
      }
    }
    
    // TODO(bob): Variables in outer scopes beyond the immediately enclosing fn.
    
    // Must be a global.
    name.resolveGlobal();
  }
  
  private final NameResolver mOuterFunction;
  private final List<String> mLocals;
  private final List<UpvarRef> mUpvars;
}
