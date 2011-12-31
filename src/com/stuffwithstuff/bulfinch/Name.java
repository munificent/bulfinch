package com.stuffwithstuff.bulfinch;

/** A resolvable name in lexical scope. */
public class Name {
  public Name(String identifier) {
    mIdentifier = identifier;
  }
  
  public String getIdentifier() {
    return mIdentifier;
  }
  
  public boolean isLocal() {
    Expect.state(mLocalIndex != UNRESOLVED, "Unresolved name.");

    return mLocalIndex != GLOBAL;
  }
  
  public int getLocalIndex() {
    Expect.state(mLocalIndex != UNRESOLVED, "Unresolved name.");
    Expect.state(mLocalIndex != GLOBAL, "Non-local name.");
    
    return mLocalIndex;
  }
  
  public void resolveLocal(int index) {
    Expect.state(mLocalIndex == UNRESOLVED, "Already resolved name.");

    mLocalIndex = index;
  }
  
  public void resolveGlobal() {
    Expect.state(mLocalIndex == UNRESOLVED, "Already resolved name.");
    
    mLocalIndex = GLOBAL;
  }
  
  public static final int UNRESOLVED = -2;
  public static final int GLOBAL = -1;
  
  private final String mIdentifier;
  private int mLocalIndex = UNRESOLVED;
}
