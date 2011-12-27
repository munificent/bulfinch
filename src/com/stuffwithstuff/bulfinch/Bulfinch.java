package com.stuffwithstuff.bulfinch;

import java.io.*;

public class Bulfinch {
  public static void main(String[] args) throws IOException {
    Lexer lexer = new Lexer("add(1, 2)");
    BulfinchParser parser = new BulfinchParser(lexer);
    Expr expr = parser.parse();
    System.out.println(expr);
    
    Function function = new Function();
    function.constants.add("foo");
    function.code.add(new Op(Op.CONSTANT, 0, 0));
    function.code.add(new Op(Op.MOVE, 0, 1));
    function.code.add(new Op(Op.PRINT, 1));
    function.code.add(new Op(Op.RETURN, 1));
    function.numRegisters = 2;
    
    VM vm = new VM();
    Object result = vm.execute(function);
    System.out.println(String.format("result: %s", result));
  }
}
