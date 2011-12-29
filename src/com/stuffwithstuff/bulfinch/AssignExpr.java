package com.stuffwithstuff.bulfinch;

public class AssignExpr implements Expr {

  public AssignExpr(String name, Expr value) {
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
    return mName + " = " + mValue.toString();
  }

  public <A,R> R accept(ExprVisitor<A,R> visitor, A arg) {
    return visitor.visit(this, arg);
  }

  private final String mName;
  private final Expr mValue;
}
