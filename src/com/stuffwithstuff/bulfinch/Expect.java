package com.stuffwithstuff.bulfinch;

public class Expect {
  public static void arg(boolean assertion, String message) {
    if (!assertion) throw new IllegalArgumentException(message);
  }
  
  public static void argNotNull(Object arg, String name) {
    arg(arg != null, "Argument '" + name + "' should not be null.");
  }
  
  public static void state(boolean assertion, String message) {
    if (!assertion) throw new IllegalStateException(message);
  }
}
