package com.stuffwithstuff.bulfinch;

public interface ExprVisitor {
  void visit(BoolExpr expr);
  void visit(CallExpr expr);
  void visit(FunctionExpr expr);
  void visit(NameExpr expr);
  void visit(NumberExpr expr);
  void visit(SequenceExpr expr);
  void visit(StringExpr expr);
}
