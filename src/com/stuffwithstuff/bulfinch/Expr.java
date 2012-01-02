package com.stuffwithstuff.bulfinch;

public interface Expr {
  <A> void accept(ExprVisitor<A> visitor, A arg);
}
