package com.stuffwithstuff.bulfinch;

public class StringExpr implements Expr {
  public StringExpr(final String value) {
    mValue = value;
  }

  public String getValue() {
    return mValue;
  }

  @Override
  public String toString() {
    return mValue;
  }

  public <A,R> R accept(ExprVisitor<A,R> visitor, A arg) {
    return visitor.visit(this, arg);
  }

  private final String mValue;
}
