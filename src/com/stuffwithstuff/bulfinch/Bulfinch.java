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
    
    Map<String, Closure> functions = new HashMap<String, Closure>();
    for (Entry<String, FunctionExpr> entry : program.entrySet()) {
      Function function = Compiler.compileTopLevel(entry.getValue(), entry.getKey());
      functions.put(entry.getKey(), new Closure(function));
    }
    
    //dumpProgram(functions);
    
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
  
  /*
  private void dumpProgram(Map<String, Function> program) {
    for (Entry<String, Function> entry : program.entrySet()) {
      entry.getValue().dump();
      System.out.println();
    }
  }
  */
  
  private Pattern mExpectPattern = Pattern.compile("# expect: (.+)\\n");
  private int mTests = 0;
  private int mPasses = 0;
}
