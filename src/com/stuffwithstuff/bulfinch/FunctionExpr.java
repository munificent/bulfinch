package com.stuffwithstuff.bulfinch;

import java.util.*;

public class FunctionExpr implements Expr {
  public FunctionExpr(List<String> parameters, Expr body) {
    mParameters = new ArrayList<String>(parameters);
    mBody = body;
  }

  public List<String> getParameters() {
    return mParameters;
  }

  public Expr getBody() {
    return mBody;
  }

  public <A,R> R accept(ExprVisitor<A,R> visitor, A arg) {
    return visitor.visit(this, arg);
  }

  private final List<String> mParameters;
  private final Expr mBody;
}
