package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class VM {
  public VM(Map<String, Function> functions) {
    mFunctions = functions;
  }
  
  public Object execute() {
    // Call main().
    call(mFunctions.get("main"), 0);
    
    return run();
  }

  private void call(Function function, int topOfStack) {
    mFrames.push(new CallFrame(function, topOfStack));
    
    // This uses an overlapping register window to pass arguments to the called
    // function. The caller sets up the top of the stack like:
    // fn, arg1, arg2, arg3
    // The callee's stack frame is then set up to start at arg1's position.
    // It will use those registers for its parameters.
    
    // TODO(bob): What happens if the number of args doesn't match the number
    // of params? (Note: not an actual issue in Finch since the message name
    // determines the count.)
    
    // Allocate registers for the function.
    while (mStack.size() < topOfStack + function.numRegisters) {
      mStack.add(null);
    }
  }
  
  private Object run() {
    while (true) {
      CallFrame frame = mFrames.peek();
      Op op = frame.function.code.get(frame.ip++);
      
      switch (op.opcode) {
      case Op.CONSTANT: {
        Object value = frame.function.constants.get(op.a);
        store(op.b, value);
        trace("CONSTANT", op.a, op.b);
        break;
      }

      case Op.MOVE: {
        store(op.b, load(op.a));
        trace("MOVE", op.a, op.b);
        break;
      }

      case Op.CALL: {
        Function function = (Function)load(op.a);
        call(function, op.a + 1);
        trace("CALL", op.a);
        break;
      }
      
      case Op.RETURN: {
        Object result = load(op.a);

        mFrames.pop();
        
        if (mFrames.size() == 0) return result;
        
        CallFrame caller = mFrames.peek();

        // Discard the returning function's registers.
        while (mStack.size() > caller.stackStart + caller.function.numRegisters) {
          mStack.remove(mStack.size() - 1);
        }

        // Store the result value in the register set by the caller's CALL
        // instruction.
        // - 1 because we've already advanced past the CALL.
        int register = caller.stackStart + caller.function.code.get(caller.ip - 1).a;
        mStack.set(register, result);
        trace("RETURN", op.a);
        break;
      }

      case Op.LOAD_GLOBAL: {
        // TODO(bob): Right now, all globals are just functions.
        String name = frame.function.constants.get(op.a).toString();
        Function function = mFunctions.get(name);
        store(op.b, function);
        trace("LOAD_GLOBAL", op.a, op.b);
        break;
      }

      case Op.PRINT: {
        Object value = load(op.a);
        System.out.println(value);
        trace("PRINT", op.a);
        break;
      }
      }
    }
  }
  
  private Object load(int register) {
    return mStack.get(mFrames.peek().stackStart + register);
  }

  private Object store(int register, Object value) {
    return mStack.set(mFrames.peek().stackStart + register, value);
  }

  private void trace(String op, int a, int b) {
    trace(op + "(" + a + ", " + b + ")");
  }

  private void trace(String op, int a) {
    trace(op + "(" + a + ")");
  }
  
  private void trace(String op) {
    System.out.print(String.format("%-20s: ", op));
    
    for (int i = 0; i < mStack.size(); i++) {
      System.out.print(String.format("%-8s  ", mStack.get(i)));
    }
    
    System.out.println();
  }
  
  private final Map<String, Function> mFunctions;
  private final List<Object> mStack = new ArrayList<Object>();
  private final Stack<CallFrame> mFrames = new Stack<CallFrame>();
}
