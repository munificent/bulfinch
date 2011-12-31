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

  public List<String> getLocals() {
    Expect.state(mLocals != null, "Unresolved function.");
    return mLocals;
  }
  
  public void setLocals(List<String> locals) {
    Expect.state(mLocals == null, "Already resolved function.");
    mLocals = locals;
  }
  
  public <A,R> R accept(ExprVisitor<A,R> visitor, A arg) {
    return visitor.visit(this, arg);
  }

  private final List<String> mParameters;
  private final Expr mBody;
  private List<String> mLocals;
}
