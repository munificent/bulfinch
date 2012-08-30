package com.stuffwithstuff.bulfinch;

public interface ExprVisitor<A> {
  void visit(AssignExpr expr, A arg);
  void visit(CallExpr expr, A arg);
  void visit(FunctionExpr expr, A arg);
  void visit(IfExpr expr, A arg);
  void visit(NameExpr expr, A arg);
  void visit(SequenceExpr expr, A arg);
  void visit(StringExpr expr, A arg);
  void visit(VarExpr expr, A arg);
}
