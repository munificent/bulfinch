package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VM {
  public Object execute(Function function) {
    mFrames.push(new CallFrame(function, mStack.size()));
    
    // Allocate registers for the function.
    for (int i = 0; i < function.numRegisters; i++) {
      mStack.add(null);
    }
    
    return run();
  }

  private Object run() {
    while (true) {
      CallFrame frame = mFrames.peek();
      Op op = frame.function.code.get(frame.ip++);
      
      switch (op.opcode) {
      case Op.CONSTANT: {
        Object value = frame.function.constants.get(op.a);
        store(op.b, value);
        break;
      }

      case Op.MOVE: {
        store(op.b, load(op.a));
        break;
      }

      case Op.PRINT: {
        Object value = load(op.a);
        System.out.println(String.format("print: %s", value));
        break;
      }

      case Op.RETURN:
        Object result = load(op.a);

        // Discard the returning function's registers.
        while (mStack.size() > frame.stackStart) mStack.remove(mStack.size() - 1);
        
        mFrames.pop();
        
        // Store the result value in the register set by the caller's CALL
        // instruction.
        if (mFrames.size() > 0) {
          CallFrame caller = mFrames.peek();
          // - 1 because we've already advanced past the CALL.
          int register = caller.stackStart + caller.function.code.get(caller.ip - 1).a;
          mStack.set(register, result);
        } else {
          // Completely done, so return the final result.
          return result;
        }
        break;
      }
    }
  }
  
  private Object load(int register) {
    return mStack.get(mFrames.peek().stackStart + register);
  }

  private Object store(int register, Object value) {
    return mStack.set(mFrames.peek().stackStart + register, value);
  }

  private final List<Object> mStack = new ArrayList<Object>();
  private final Stack<CallFrame> mFrames = new Stack<CallFrame>();
}
