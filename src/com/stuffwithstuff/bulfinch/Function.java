package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * A compiled function.
 */
public class Function {
  public Function(String debugName, List<String> locals, List<String> upvarNames) {
    mDebugName = debugName;
    mLocals = locals;
    mUpvarNames = upvarNames;
    mConstants = new ArrayList<Object>();
    mCode = new ArrayList<Op>();
  }

  public String getDebugName() {
    return mDebugName;
  }

  public int addConstant(Object constant) {
    // TODO(bob): Check for duplicates.
    mConstants.add(constant);
    return mConstants.size() - 1;
  }

  public Object getConstant(int index) {
    return mConstants.get(index);
  }

  public int getNumConstants() {
    return mConstants.size();
  }
  
  public int getNumRegisters() {
    return mNumRegisters;
  }

  public int getNumUpvars() {
    return mNumUpvars;
  }

  public int ensureRegisters(int numRegisters) {
    return mNumRegisters = Math.max(numRegisters, mNumRegisters);
  }

  public void setNumUpvars(int numUpvars) {
    mNumUpvars = numUpvars;
  }

  public List<Op> getCode() {
    return mCode;
  }

  public void dump() {
    System.out.println(mDebugName);

    // Dump the constants.
    if (mConstants.size() > 0) {
      System.out.println("constants");

      for (int i = 0; i < mConstants.size(); i++) {
        System.out.println(String.format("  %-2s : %s", i, mConstants.get(i)));
      }
    }

    // Dump the upvars.
    if (mUpvarNames.size() > 0) {
      System.out.println("upvars");
      
      for (int i = 0; i < mNumUpvars; i++) {
        String name = "<???>";
        if (i < mUpvarNames.size()) {
          name = mUpvarNames.get(i);
        }
        System.out.println(String.format("  %-2s : %s", i, name));
      }
    }
    
    // Dump the registers.
    System.out.println("registers");
    for (int i = 0; i < mNumRegisters; i++) {
      String name = "<temp>";
      if (i < mLocals.size()) {
        name = mLocals.get(i);
      }
      System.out.println(String.format("  %-2s : %s", i, name));
    }

    // Dump the code.
    System.out.println("code");
    for (int i = 0; i < mCode.size(); i++) {
      Op op = mCode.get(i);
      switch (op.opcode) {
      case Op.CONSTANT:
        System.out.println(String.format("  CONSTANT     %s -> %s",
            prettyConst(op.a), prettyReg(op.b)));
        break;

      case Op.MOVE:
        System.out.println(String.format("  MOVE         %s -> %s",
            prettyReg(op.a), prettyReg(op.b)));
        break;

      case Op.CALL:
        System.out.println(String.format("  CALL         %s <- %s %s",
            prettyReg(op.a), op.b, op.c));
        break;

      case Op.RETURN:
        System.out.println(String.format("  RETURN       %s", prettyReg(op.a)));
        break;

      case Op.LOAD_GLOBAL:
        System.out.println(String.format("  LOAD_GLOBAL  %s -> %s",
            prettyConst(op.a), prettyReg(op.b)));
        break;

      case Op.LOAD_UPVAR:
        System.out.println(String.format("  LOAD_UPVAR   %s -> %s",
            prettyUpvar(op.a), prettyReg(op.b)));
        break;

      case Op.STORE_UPVAR:
        System.out.println(String.format("  STORE_UPVAR  %s <- %s",
            prettyUpvar(op.a), prettyReg(op.b)));
        break;

      case Op.CLOSURE:
        System.out.println(String.format("  CLOSURE      %s -> %s",
            prettyConst(op.a), prettyReg(op.b)));
        break;

      case Op.ADD_UPVAR:
        System.out.println(String.format("    ADD_UPVAR  %s",
            prettyReg(op.a)));
        break;

      case Op.ADD_OUTER_UPVAR:
        System.out.println(String.format("    ADD_OUTER_UPVAR  %s",
            prettyUpvar(op.a)));
        break;

      default:
        System.out.println("??? Unknown opcode " + op.opcode);
        break;
      }
    }

    System.out.println();

    // Dump nested functions too.
    for (Object constant : mConstants) {
      if (constant instanceof Function) {
        ((Function)constant).dump();
      }
    }
  }

  @Override
  public String toString() {
    return mDebugName;
  }

  private String prettyConst(int constant) {
    return String.format("%s (%s)", constant, mConstants.get(constant));
  }

  private String prettyReg(int register) {
    if (register < mLocals.size()) {
      return String.format("%s (%s)", register, mLocals.get(register));
    }

    return String.format("%s", register);
  }
  
  private String prettyUpvar(int upvar) {
    if (upvar < mUpvarNames.size()) {
      return String.format("%s (%s)", upvar, mUpvarNames.get(upvar));
    }

    return String.format("%s", upvar);
  }

  private final String mDebugName;
  private final List<String> mLocals;
  private final List<String> mUpvarNames;
  private List<Object> mConstants;
  private List<Op> mCode;
  private int mNumRegisters;
  private int mNumUpvars;
}
