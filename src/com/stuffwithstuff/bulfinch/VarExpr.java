package com.stuffwithstuff.bulfinch;

public class VarExpr implements Expr {

  public VarExpr(String name, Expr value) {
    mName = name;
    mValue = value;
  }

  public String getName() {
    return mName;
  }

  public Expr getValue() {
    return mValue;
  }

  @Override
  public String toString() {
    return "var " + mName + " = " + mValue.toString();
  }

  public <T> void accept(ExprVisitor<T> visitor, T arg) {
    visitor.visit(this, arg);
  }

  private final String mName;
  private final Expr mValue;
}
