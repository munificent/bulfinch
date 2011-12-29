package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds all of the local variable definitions (including parameters) in a
 * function.
 */
public class LocalFinder implements ExprVisitor<Void, Void> {
  public static List<String> getLocals(FunctionExpr function) {
    List<String> locals = new ArrayList<String>();
    
    locals.addAll(function.getParameters());
    function.getBody().accept(new LocalFinder(locals), null);
    return locals;
  }
  
  @Override
  public Void visit(AssignExpr expr, Void arg) {
    // Do nothing.
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
    // Do nothing.
    return null;
  }

  @Override
  public Void visit(NameExpr expr, Void arg) {
    // Do nothing.
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
    mLocals.add(expr.getName());
    expr.getValue().accept(this, arg);
    return null;
  }

  private LocalFinder(List<String> locals) {
    mLocals = locals;
  }
  
  private final List<String> mLocals;
}
