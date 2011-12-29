package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * A compiled function.
 */
public class Function {
  public Function(String debugName) {
    mDebugName = debugName;
    constants = new ArrayList<Object>();
    code = new ArrayList<Op>();
  }
  
  @Override
  public String toString() {
    return mDebugName;
  }
  
  private final String mDebugName;
  
  public List<Object> constants;
  public List<Op> code;
  public int numRegisters;
}
