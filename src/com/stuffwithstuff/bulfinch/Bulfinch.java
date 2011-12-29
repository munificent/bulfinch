package com.stuffwithstuff.bulfinch;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bulfinch {
  public static void main(String[] args) throws IOException {
    new Bulfinch().runTests();
  }
  
  private void runTests() throws IOException {
    File testDir = new File("test");
    
    for (File script : testDir.listFiles()) {
      if (script.getPath().endsWith(".bf")) {
        run(script.getPath());
      }
    }
    
    System.out.println();
    System.out.println(String.format("%s tests out of %s passed.",
        mPasses, mTests));
  }
  
  private void run(String path) throws IOException {
    System.out.println("Running " + path);
    
    mTests++;
    
    BulfinchScript script = new BulfinchScript(path);
    String code = script.getSource();
    
    Matcher m = mExpectPattern.matcher(code);
    m.find();
    String expect = m.group(1);
    
    Lexer lexer = new Lexer(code);
    BulfinchParser parser = new BulfinchParser(lexer);
    Map<String, FunctionExpr> program = parser.parseProgram();
    
    Compiler compiler = new Compiler();
    Map<String, Function> functions = new HashMap<String, Function>();
    for (Entry<String, FunctionExpr> entry : program.entrySet()) {
      Function function = compiler.compile(entry.getValue(), entry.getKey());
      functions.put(entry.getKey(), function);
    }
    
    dumpProgram(functions);
    
    VM vm = new VM(functions);
    Object result = vm.execute();

    System.out.println();

    if (!expect.equals(result.toString())) {
      System.out.println("FAIL: " + result);
      System.out.println();
    } else {
      mPasses++;
    }
  }
  
  private void dumpProgram(Map<String, Function> program) {
    for (Entry<String, Function> entry : program.entrySet()) {
      System.out.println(entry.getKey());
      
      Function function = entry.getValue();
      
      // Dump the constants.
      if (function.constants.size() > 0) {
        System.out.println("constants");
        
        for (int i = 0; i < function.constants.size(); i++) {
          System.out.println(String.format("  %-2s : %s",
              i, function.constants.get(i)));
        }
      }
      
      // Dump the registers.
      System.out.println("registers");
      for (int i = 0; i < function.numRegisters; i++) {
        String name = "<temp>";
        if (i < function.getLocals().size()) {
          name = function.getLocals().get(i);
        }
        System.out.println(String.format("  %-2s : %s", i, name));
      }
      
      // Dump the code.
      System.out.println("code");
      for (int i = 0; i < function.code.size(); i++) {
        Op op = function.code.get(i);
        switch (op.opcode) {
        case Op.CONSTANT:
          System.out.println(String.format(
              "  CONSTANT     %s -> %s", prettyConst(function, op.a), prettyReg(function, op.b)));
          break;
        
        case Op.MOVE:
          System.out.println(String.format(
              "  MOVE         %s -> %s", prettyReg(function, op.a), prettyReg(function, op.b)));
          break;
        
        case Op.CALL:
          System.out.println(String.format(
              "  CALL         %s <- %s %s", prettyReg(function, op.a), op.b, op.c));
          break;
        
        case Op.RETURN:
          System.out.println(String.format(
              "  RETURN       %s", prettyReg(function, op.a)));
          break;
        
        case Op.LOAD_GLOBAL:
          System.out.println(String.format(
              "  LOAD_GLOBAL  %s -> %s", prettyConst(function, op.a), prettyReg(function, op.b)));
          break;
        
        case Op.PRINT:
          System.out.println(String.format(
              "  PRINT        %s", prettyReg(function, op.a)));
          break;
        }
      }
      
      System.out.println();
    }
  }
  
  private String prettyConst(Function function, int constant) {
    return String.format("%s (%s)", constant, function.constants.get(constant));
  }

  private String prettyReg(Function function, int register) {
    if (register < function.getLocals().size()) {
      return String.format("%s (%s)", register, function.getLocals().get(register));
    }
    
    return String.format("%s", register);
  }
  
  private Pattern mExpectPattern = Pattern.compile("# expect: (.+)\\n");
  private int mTests = 0;
  private int mPasses = 0;
}
