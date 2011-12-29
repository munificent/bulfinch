package com.stuffwithstuff.bulfinch;

public class BoolExpr implements Expr {
  public BoolExpr(final boolean value) {
    mValue = value;
  }

  public boolean getValue() {
    return mValue;
  }

  @Override
  public String toString() {
    return Boolean.toString(mValue);
  }

  public <A,R> R accept(ExprVisitor<A,R> visitor, A arg) {
    return visitor.visit(this, arg);
  }

  private final boolean mValue;
}
