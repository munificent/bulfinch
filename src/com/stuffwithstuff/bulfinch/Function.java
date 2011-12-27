package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * A compiled function.
 */
public class Function {
  public Function() {
    constants = new ArrayList<Object>();
    code = new ArrayList<Op>();
  }
  
  public List<Object> constants;
  public List<Op> code;
  public int numRegisters;
}
