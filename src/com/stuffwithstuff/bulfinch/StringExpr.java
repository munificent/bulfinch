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

  public <T> void accept(ExprVisitor<T> visitor, T arg) {
    visitor.visit(this, arg);
  }

  private final String mValue;
}
