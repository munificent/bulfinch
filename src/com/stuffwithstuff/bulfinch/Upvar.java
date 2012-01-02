package com.stuffwithstuff.bulfinch;

import java.util.List;

public class Upvar {
  public Upvar(int stackIndex) {
    mStackIndex = stackIndex;
  }
  
  public Object get(List<Object> stack) {
    if (isOpen()) {
      return stack.get(mStackIndex);
    } else {
      return mValue;
    }
  }
  
  public void set(List<Object> stack, Object value) {
    if (isOpen()) {
      stack.set(mStackIndex, value);
    } else {
      mValue = value;
    }
  }
  
  public void close(List<Object> stack) {
    // Capture the value.
    mValue = stack.get(mStackIndex);
    
    // Detach from the stack.
    mStackIndex = -1;
  }
  
  int getIndex() {
    return mStackIndex;
  }
  
  boolean isOpen() {
    return mStackIndex != -1;
  }
  
  private int mStackIndex; // will be -1 if it's closed.
  private Object mValue; // only used when closed.
}
