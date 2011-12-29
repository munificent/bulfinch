package com.stuffwithstuff.bulfinch;

public interface Expr {
  <A,R> R accept(ExprVisitor<A,R> visitor, A arg);
}
