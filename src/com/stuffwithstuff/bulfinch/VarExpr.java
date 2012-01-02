package com.stuffwithstuff.bulfinch;

public class VarExpr implements Expr {

  public VarExpr(String name, Expr value) {
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
    return "var " + mName + " = " + mValue.toString();
  }

  public <A> void accept(ExprVisitor<A> visitor, A arg) {
    visitor.visit(this, arg);
  }

  private final Name mName;
  private final Expr mValue;
}
