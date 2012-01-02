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
    Expect.state(mScope != Scope.UNRESOLVED, "Unresolved name.");

    return mScope == Scope.LOCAL;
  }

  public boolean isUpvar() {
    Expect.state(mScope != Scope.UNRESOLVED, "Unresolved name.");

    return mScope == Scope.UPVAR;
  }

  public int getLocalIndex() {
    Expect.state(mScope != Scope.UNRESOLVED, "Unresolved name.");
    Expect.state(mScope == Scope.LOCAL, "Non-local name.");
    
    return mIndex;
  }
  
  public UpvarRef getUpvar() {
    Expect.state(mScope != Scope.UNRESOLVED, "Unresolved name.");
    Expect.state(mScope == Scope.UPVAR, "Non-upvar name.");
    
    return mUpvar;
  }
  
  public void resolveLocal(int index) {
    Expect.state(mScope == Scope.UNRESOLVED, "Already resolved name.");

    mScope = Scope.LOCAL;
    mIndex = index;
  }
  
  public void resolveUpvar(UpvarRef upvar) {
    Expect.state(mScope == Scope.UNRESOLVED, "Already resolved name.");

    mScope = Scope.UPVAR;
    mUpvar = upvar;
  }
  
  public void resolveGlobal() {
    Expect.state(mScope == Scope.UNRESOLVED, "Already resolved name.");
    
    mScope = Scope.GLOBAL;
  }
  
  @Override
  public String toString() {
    return mIdentifier;
  }
  
  private enum Scope {
    UNRESOLVED,
    LOCAL,
    GLOBAL,
    UPVAR
  }
  
  private final String mIdentifier;
  private Scope mScope = Scope.UNRESOLVED;
  private int mIndex;
  private UpvarRef mUpvar;
}
