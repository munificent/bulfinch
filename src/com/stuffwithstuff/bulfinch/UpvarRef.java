package com.stuffwithstuff.bulfinch;

public class UpvarRef {
  public UpvarRef(String name, int index, int register) {
    this.name = name;
    this.index = index;
    this.register = register;
  }
  
  public final String name;
  
  // The index of this upvar in the function's upvar list.
  public final int index;
  
  // The index of the registar that the upvar is bound to in the outer function.
  public final int register;
  
  // TODO(bob): Upvars that reference upvars in the outer function (i.e. vars
  // that close over variables in functions defined outside the immediately
  // closing one).
}
