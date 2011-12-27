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
  // Calls a function, passing in arguments. After calling, the return value
  // will be placed in register B.
  // A = function, B = first arg reg, C = last arg reg
  
  public static final int RETURN = 3;
  // Returns from the function.
  // A = result reg
  
  public static final int PRINT = 4;
  // Prints a register. Mostly for debugging.
  // A = reg to print

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
