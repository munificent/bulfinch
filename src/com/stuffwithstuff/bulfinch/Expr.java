package com.stuffwithstuff.bulfinch;

public interface Expr {
  <T> void accept(ExprVisitor<T> visitor, T arg);
}
