package com.stuffwithstuff.bulfinch;

import java.util.List;

public class CallExpr implements Expr {

  public CallExpr(Expr function, List<Expr> args) {
    mFunction = function;
    mArgs = args;
  }

  public Expr getFunction() {
    return mFunction;
  }

  public List<Expr> getArgs() {
    return mArgs;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(mFunction.toString()).append("(");

    for (int i = 0; i < mArgs.size(); i++) {
      if (i > 0) buffer.append(", ");
      buffer.append(mArgs.get(i));
    }
    
    buffer.append(")");
    return buffer.toString();
  }

  private final Expr mFunction;
  private final List<Expr> mArgs;
}
