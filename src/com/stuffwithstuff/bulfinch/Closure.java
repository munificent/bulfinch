package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * A first-class function and its captured environment.
 */
public class Closure {
  public Closure(Function function) {
    mFunction = function;
    mUpvars = new ArrayList<Upvar>();
  }
  
  public Function getFunction() {
    return mFunction;
  }
  
  public void addUpvar(Upvar upvar) {
    mUpvars.add(upvar);
  }
  
  public Upvar getUpvar(int index) {
    return mUpvars.get(index);
  }
  
  @Override
  public String toString() {
    return mFunction.toString();
  }
  
  private final Function mFunction;
  private final List<Upvar> mUpvars;
}
