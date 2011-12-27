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

  private final List<String> mParameters;
  private final Expr mBody;
}
