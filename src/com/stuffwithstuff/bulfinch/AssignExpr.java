package com.stuffwithstuff.bulfinch;

public class AssignExpr implements Expr {

  public AssignExpr(String name, Expr value) {
    mName = new Name(name);
    mValue = value;
  }

  public Name getName() {
    return mName;
  }

  public Expr getValue() {
    return mValue;
  }

  @Override
  public String toString() {
    return mName + " = " + mValue.toString();
  }

  public <A> void accept(ExprVisitor<A> visitor, A arg) {
    visitor.visit(this, arg);
  }

  private final Name mName;
  private final Expr mValue;
}
