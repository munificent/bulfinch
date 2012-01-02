package com.stuffwithstuff.bulfinch;

import java.util.*;

public class SequenceExpr implements Expr {
  public SequenceExpr(List<Expr> expressions) {
    mExpressions = expressions;
  }

  public List<Expr> getExpressions() {
    return mExpressions;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append("(");

    Iterator<?> iter = mExpressions.iterator();
    while (iter.hasNext()) {
      builder.append(iter.next());

      if (iter.hasNext()) {
        builder.append("; ");
      }
    }

    builder.append(")");

    return builder.toString();
  }

  public <A> void accept(ExprVisitor<A> visitor, A arg) {
    visitor.visit(this, arg);
  }

  private final List<Expr> mExpressions;
}
