package com.stuffwithstuff.bulfinch;

import java.util.List;

/**
 * A first-class function and its captured environment.
 */
public class Closure {
  public Closure(Function function) {
    mFunction = function;
//    mUpvars = null; // TODO(bob): Do stuff.
  }
  
  public Function getFunction() {
    return mFunction;
  }
  
  @Override
  public String toString() {
    return mFunction.toString();
  }
  
  private final Function mFunction;
//  private final List<Upvar> mUpvars;
}
