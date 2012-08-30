package com.stuffwithstuff.bulfinch;

public class IfExpr implements Expr {
  public IfExpr(Expr condition, Expr thenArm, Expr elseArm) {
    mCondition = condition;
    mThenArm = thenArm;
    mElseArm = elseArm;
  }

  public Expr getCondition() {
    return mCondition;
  }

  public Expr getThenArm() {
    return mThenArm;
  }

  public Expr getElseArm() {
    return mElseArm;
  }

  @Override
  public String toString() {
    return "if " + mCondition + " " + mThenArm + " else " + mElseArm;
  }

  public <A> void accept(ExprVisitor<A> visitor, A arg) {
    visitor.visit(this, arg);
  }

  private final Expr mCondition;
  private final Expr mThenArm;
  private final Expr mElseArm;
}
