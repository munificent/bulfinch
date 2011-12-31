package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves all name references in a function. Modifies the AST to include
 * resolution information, and returns the list of all local variables
 * declared in the function.
 */
public class NameResolver implements ExprVisitor<Void, Void> {
  public static void resolve(FunctionExpr function) {
    List<String> locals = new ArrayList<String>();
    
    locals.addAll(function.getParameters());
    function.getBody().accept(new NameResolver(locals), null);
    function.setLocals(locals);
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
    resolve(expr);

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

  private NameResolver(List<String> locals) {
    mLocals = locals;
  }
  
  private void resolveName(Name name) {
    // See if it's a local.
    int local = mLocals.indexOf(name.getIdentifier());
    if (local != -1) {
      name.resolveLocal(local);
    } else {
      // TODO(bob): Variables in outer scopes.
      name.resolveGlobal();
    }
  }
  private final List<String> mLocals;
}
