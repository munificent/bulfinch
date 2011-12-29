package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * A compiled function.
 */
public class Function {
  public Function(String debugName, List<String> locals) {
    mDebugName = debugName;
    mLocals = locals;
    constants = new ArrayList<Object>();
    code = new ArrayList<Op>();
  }
  
  public List<String> getLocals() { return mLocals; }
  
  @Override
  public String toString() {
    return mDebugName;
  }
  
  private final String mDebugName;
  private final List<String> mLocals;
  
  public List<Object> constants;
  public List<Op> code;
  public int numRegisters;
}
