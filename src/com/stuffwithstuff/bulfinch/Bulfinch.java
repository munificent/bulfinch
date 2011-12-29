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
  }
  
  private void run(String path) throws IOException {
    System.out.println("Running " + path);
    
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
    
    VM vm = new VM(functions);
    Object result = vm.execute();

    System.out.println();

    if (!expect.equals(result.toString())) {
      System.out.println("FAIL: " + result);
      System.out.println();
    }
  }
  
  private Pattern mExpectPattern = Pattern.compile("# expect: (.+)\\n");
}
