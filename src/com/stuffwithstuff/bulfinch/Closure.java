package com.stuffwithstuff.bulfinch;

/**
 * A first-class function and its captured environment.
 */
public class Closure {
  public Closure(Function function) {
    mFunction = function;
  }
  
  public Function getFunction() {
    return mFunction;
  }
  
  @Override
  public String toString() {
    return mFunction.toString();
  }
  
  private final Function mFunction;
}
