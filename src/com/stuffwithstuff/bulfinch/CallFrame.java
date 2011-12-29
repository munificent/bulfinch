package com.stuffwithstuff.bulfinch;

public class CallFrame {
  CallFrame(Function function, int stackStart) {
    this.function = function;
    this.stackStart = stackStart;
  }
  
  public int ip;
  public int stackStart;
  public Function function;
}
