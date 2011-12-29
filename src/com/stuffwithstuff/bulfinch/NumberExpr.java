package com.stuffwithstuff.bulfinch;

import java.text.NumberFormat;

public class NumberExpr implements Expr {
  public NumberExpr(final double value) {
    mValue = value;
  }

  public double getValue() {
    return mValue;
  }

  @Override
  public String toString() {
    return sFormat.format(mValue);
  }

  public <A,R> R accept(ExprVisitor<A,R> visitor, A arg) {
    return visitor.visit(this, arg);
  }

  private static final NumberFormat sFormat;

  static {
    sFormat = NumberFormat.getInstance();
    sFormat.setGroupingUsed(false);
    sFormat.setMinimumFractionDigits(0);
  }

  private final double mValue;
}
