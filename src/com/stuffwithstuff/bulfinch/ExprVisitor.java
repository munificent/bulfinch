package com.stuffwithstuff.bulfinch;

public interface ExprVisitor<T> {
  void visit(BoolExpr expr, T arg);
  void visit(CallExpr expr, T arg);
  void visit(FunctionExpr expr, T arg);
  void visit(NameExpr expr, T arg);
  void visit(NumberExpr expr, T arg);
  void visit(SequenceExpr expr, T arg);
  void visit(StringExpr expr, T arg);
  void visit(VarExpr expr, T arg);
}
