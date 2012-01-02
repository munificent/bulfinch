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

  public List<UpvarRef> getUpvars() {
    Expect.state(mUpvars != null, "Unresolved function.");
    return mUpvars;
  }
  
  public void resolve(List<String> locals, List<UpvarRef> upvars) {
    Expect.state(mLocals == null, "Already resolved function.");
    mLocals = locals;
    mUpvars = upvars;
  }
  
  public <A> void accept(ExprVisitor<A> visitor, A arg) {
    visitor.visit(this, arg);
  }

  private final List<String> mParameters;
  private final Expr mBody;
  private List<String> mLocals;
  private List<UpvarRef> mUpvars;
}
