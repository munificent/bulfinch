package com.stuffwithstuff.bulfinch;

public class CallFrame {
  CallFrame(Function function, int stackStart) {
    if (stackStart < 0) throw new IllegalArgumentException("Start start must be non-negative.");
    
    this.function = function;
    this.stackStart = stackStart;
  }
  
  public int ip;
  public int stackStart;
  public Function function;
}
