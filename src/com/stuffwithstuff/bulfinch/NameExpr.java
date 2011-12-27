package com.stuffwithstuff.bulfinch;

public class NameExpr implements Expr {
  public NameExpr(final String name) {
    mName = name;
  }

  public String getName() {
    return mName;
  }

  @Override
  public String toString() {
    return mName;
  }

  private final String mName;
}
