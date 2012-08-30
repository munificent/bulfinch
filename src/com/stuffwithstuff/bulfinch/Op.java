package com.stuffwithstuff.bulfinch;

/**
 * A single bytecode instruction, including opcode and operands.
 */
public class Op {
  public static final int CONSTANT = 0;
  // Loads a constant into a register.
  // A = constant, B = dest reg
  
  public static final int MOVE = 1;
  // Copy one register to another.
  // A = source reg, B = dest reg
  
  public static final int CALL = 2;
  // Calls a function. The function to call is in register B. All C arguments
  // are in successive registers after B. After calling, the result will be
  // in register A.
  // A = result reg, B = fn reg, C = num args
  
  public static final int RETURN = 3;
  // Returns from the function.
  // A = result reg
  
  public static final int JUMP = 4;
  // Jumps unconditionally.
  // A = offset to jump by

  public static final int JUMP_IF_FALSE = 5;
  // Jumps if a value is falsey.
  // A = register to test
  // B = offset to jump by if A if falsey

  public static final int LOAD_GLOBAL = 6;
  // Loads a named global into a register.
  // A = index of constant containing name
  // B = dest reg

  public static final int LOAD_UPVAR = 7;
  // Loads an upvar into a register.
  // A = index of upvar to load
  // B = dest reg

  public static final int STORE_UPVAR = 8;
  // Stores a register into an upvar.
  // A = index of upvar to store
  // B = source reg
  
  public static final int CLOSURE = 9;
  // Creates a new closure for the given function.
  // A = index of constant containing function
  // B = dest reg

  public static final int ADD_UPVAR = 10;
  // Pseudo-opcode for adding an upvar to a recently created closure.
  // A = register of variable in current function
  
  public static final int ADD_OUTER_UPVAR = 11;
  // Pseudo-opcode for adding an upvar in the current function to a recently
  // created closure.
  // A = index of upvar in current function
  
  public final int opcode;
  public final int a;
  public final int b;
  public final int c;
  
  public Op(int opcode, int a, int b, int c) {
    this.opcode = opcode;
    this.a = a;
    this.b = b;
    this.c = c;
  }
  
  public Op(int opcode, int a, int b) {
    this(opcode, a, b, -1);
  }
  
  public Op(int opcode, int a) {
    this(opcode, a, -1);
  }
  
  public Op(int opcode) {
    this(opcode, -1);
  }
}
