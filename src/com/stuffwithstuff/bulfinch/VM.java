package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class VM {
  public VM(Map<String, Closure> functions) {
    mFunctions = functions;
  }
  
  public Object execute() {
    // Call main().
    call(mFunctions.get("main"), 0, 0);
    
    return run();
  }

  private void call(Closure closure, int firstArg, int numArgs) {
    // This uses an overlapping register window to pass arguments to the called
    // function. The caller sets up the top of the stack like:
    // fn, arg1, arg2, arg3
    // The callee's stack frame is then set up to start at arg1's position.
    // It will use those registers for its parameters. This assumes that in
    // the caller's frame, any registers *past* the arguments to this call are
    // unused and can be trashed by the callee.
    
    CallFrame frame = new CallFrame(closure, firstArg);
    mFrames.push(frame);
    
    // Allocate registers for the function.
    while (mStack.size() < frame.stackStart + closure.getFunction().getNumRegisters()) {
      mStack.add(null);
    }
  }
  
  private Object run() {
    while (true) {
      CallFrame frame = mFrames.peek();
      Op op = frame.getFunction().getCode().get(frame.ip++);
      
      switch (op.opcode) {
      case Op.CONSTANT: {
        Object value = frame.getFunction().getConstant(op.a);
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
        Closure closure = (Closure)load(op.b);
        call(closure, frame.stackStart + op.b + 1, op.c);
        trace("CALL", op.a, op.b, op.c);
        break;
      }
      
      case Op.RETURN: {
        Object result = load(op.a);

        mFrames.pop();
        
        if (mFrames.size() == 0) {
          trace("RETURN", op.a);
          return result;
        }
        
        CallFrame caller = mFrames.peek();

        // Discard the returning function's registers.
        while (mStack.size() > caller.stackStart +
            caller.getFunction().getNumRegisters()) {
          mStack.remove(mStack.size() - 1);
        }

        // Store the result value in the register set by the caller's CALL
        // instruction.
        // - 1 because we've already advanced past the CALL.
        int dest = caller.getFunction().getCode().get(caller.ip - 1).a;
        
        // If the destination register is -1 that means it's result is being
        // discarded, so don't store anything.
        if (dest != -1) {
          int register = caller.stackStart + dest;
          mStack.set(register, result);
        }
        trace("RETURN", op.a);
        break;
      }

      case Op.LOAD_GLOBAL: {
        // TODO(bob): Right now, all globals are just functions.
        String name = frame.getFunction().getConstant(op.a).toString();
        Closure closure = mFunctions.get(name);
        if (closure == null) throw new RuntimeException("Unknown global " + name);
        
        store(op.b, closure);
        trace("LOAD_GLOBAL", op.a, op.b);
        break;
      }
      
      case Op.CLOSURE: {
        Function function = (Function)frame.getFunction().getConstant(op.a);
        
        // TODO(bob): Capture upvars.
        Closure closure = new Closure(function);
        store(op.b, closure);
        trace("CLOSURE", op.a, op.b);
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

  private void trace(String op, int a, int b, int c) {
    trace(op + "(" + a + ", " + b + ", " + c + ")");
  }

  private void trace(String op, int a, int b) {
    trace(op + "(" + a + ", " + b + ")");
  }

  private void trace(String op, int a) {
    trace(op + "(" + a + ")");
  }
  
  private void trace(String op) {
    System.out.print(String.format("%-20s", op));
    
    int j = 0;
    for (int i = 0; i < mStack.size(); i++) {
      if ((j < mFrames.size()) && (mFrames.get(j).stackStart == i)) {
        System.out.print(" | ");
        j++;
      } else {
        System.out.print("   ");
      }
      System.out.print(String.format("%-5s", mStack.get(i)));
    }
    
    System.out.println();
  }
  
  private final Map<String, Closure> mFunctions;
  private final List<Object> mStack = new ArrayList<Object>();
  private final Stack<CallFrame> mFrames = new Stack<CallFrame>();
}
