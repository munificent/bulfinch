package com.stuffwithstuff.bulfinch;

public class CallFrame {
  CallFrame(Closure closure, int stackStart) {
    this.closure = closure;
    this.stackStart = stackStart;
  }
  
  public Function getFunction() {
    return closure.getFunction();
  }
  
  public int ip;
  public int stackStart;
  public Closure closure;
}
