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
  
  public void dump() {
    System.out.println(mDebugName);
    
    // Dump the constants.
    if (constants.size() > 0) {
      System.out.println("constants");
      
      for (int i = 0; i < constants.size(); i++) {
        System.out.println(String.format("  %-2s : %s",
            i, constants.get(i)));
      }
    }
    
    // Dump the registers.
    System.out.println("registers");
    for (int i = 0; i < numRegisters; i++) {
      String name = "<temp>";
      if (i < mLocals.size()) {
        name = mLocals.get(i);
      }
      System.out.println(String.format("  %-2s : %s", i, name));
    }
    
    // Dump the code.
    System.out.println("code");
    for (int i = 0; i < code.size(); i++) {
      Op op = code.get(i);
      switch (op.opcode) {
      case Op.CONSTANT:
        System.out.println(String.format(
            "  CONSTANT     %s -> %s", prettyConst(op.a), prettyReg(op.b)));
        break;
      
      case Op.MOVE:
        System.out.println(String.format(
            "  MOVE         %s -> %s", prettyReg(op.a), prettyReg(op.b)));
        break;
      
      case Op.CALL:
        System.out.println(String.format(
            "  CALL         %s <- %s %s", prettyReg(op.a), op.b, op.c));
        break;
      
      case Op.RETURN:
        System.out.println(String.format(
            "  RETURN       %s", prettyReg(op.a)));
        break;
      
      case Op.LOAD_GLOBAL:
        System.out.println(String.format(
            "  LOAD_GLOBAL  %s -> %s", prettyConst(op.a), prettyReg(op.b)));
        break;
      
      case Op.PRINT:
        System.out.println(String.format(
            "  PRINT        %s", prettyReg(op.a)));
        break;
      }
    }
  }

  
  @Override
  public String toString() {
    return mDebugName;
  }
  
  private String prettyConst(int constant) {
    return String.format("%s (%s)", constant, constants.get(constant));
  }

  private String prettyReg(int register) {
    if (register < mLocals.size()) {
      return String.format("%s (%s)", register, mLocals.get(register));
    }
    
    return String.format("%s", register);
  }
  
  private final String mDebugName;
  private final List<String> mLocals;
  
  public List<Object> constants;
  public List<Op> code;
  public int numRegisters;
}
