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

  public <T> void accept(ExprVisitor<T> visitor, T arg) {
    visitor.visit(this, arg);
  }

  private static final NumberFormat sFormat;

  static {
    sFormat = NumberFormat.getInstance();
    sFormat.setGroupingUsed(false);
    sFormat.setMinimumFractionDigits(0);
  }

  private final double mValue;
}
