package com.stuffwithstuff.bulfinch;

public class Expect {
  public static void state(boolean assertion, String message) {
    if (!assertion) throw new IllegalStateException(message);
  }
}
