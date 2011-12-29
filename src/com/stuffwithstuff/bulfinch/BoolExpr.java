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

  public <T> void accept(ExprVisitor<T> visitor, T arg) {
    visitor.visit(this, arg);
  }

  private final boolean mValue;
}
