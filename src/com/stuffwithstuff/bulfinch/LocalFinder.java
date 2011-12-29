package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds all of the local variable definitions (including parameters) in a
 * function.
 */
public class LocalFinder implements ExprVisitor<Void> {
  public static List<String> getLocals(FunctionExpr function) {
    List<String> locals = new ArrayList<String>();
    
    locals.addAll(function.getParameters());
    function.getBody().accept(new LocalFinder(locals), null);
    return locals;
  }
  
  @Override
  public void visit(BoolExpr expr, Void arg) {
    // Do nothing.
  }

  @Override
  public void visit(CallExpr expr, Void dummy) {
    expr.getFunction().accept(this, dummy);
    for (Expr arg : expr.getArgs()) arg.accept(this, dummy);
  }

  @Override
  public void visit(FunctionExpr expr, Void arg) {
    throw new RuntimeException("not impl");
  }

  @Override
  public void visit(NameExpr expr, Void arg) {
    // Do nothing.
  }

  @Override
  public void visit(NumberExpr expr, Void arg) {
    // Do nothing.
  }

  @Override
  public void visit(SequenceExpr sequence, Void arg) {
    // TODO Auto-generated method stub
    for (Expr expr : sequence.getExpressions()) expr.accept(this, arg);
  }

  @Override
  public void visit(StringExpr expr, Void arg) {
    // Do nothing.
  }

  @Override
  public void visit(VarExpr expr, Void arg) {
    mLocals.add(expr.getName());
    expr.getValue().accept(this, arg);
  }

  private LocalFinder(List<String> locals) {
    mLocals = locals;
  }
  
  private final List<String> mLocals;
}
