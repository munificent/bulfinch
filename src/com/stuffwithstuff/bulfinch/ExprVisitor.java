package com.stuffwithstuff.bulfinch;

public interface ExprVisitor<A,R> {
  R visit(AssignExpr expr, A arg);
  R visit(BoolExpr expr, A arg);
  R visit(CallExpr expr, A arg);
  R visit(FunctionExpr expr, A arg);
  R visit(NameExpr expr, A arg);
  R visit(NumberExpr expr, A arg);
  R visit(SequenceExpr expr, A arg);
  R visit(StringExpr expr, A arg);
  R visit(VarExpr expr, A arg);
}
