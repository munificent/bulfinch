package com.stuffwithstuff.bulfinch;

/**
 * A static descriptor of an upvar. This is basically the compiler's version
 * of an Upvar.
 */
public class UpvarRef {
  public UpvarRef(String name, boolean isLocal, int index) {
    mName = name;
    mIsLocal = isLocal;
    mIndex = index;
    mSlot = -1;
  }
  
  public String getName() {
    return mName;
  }
  
  /** True if the variable is a local, false if it's an upvar. */
  public boolean isLocal() {
    return mIsLocal;
  }

  /**
   * Gets the slot where this upvar is stored in its function's closure. This
   * is what the LOAD_UPVAR and STORE_UPVAR opcodes use.
   */
  public int getSlot() {
    return mSlot;
  }
  
  /** Sets the slot where this upvar is stored in its function's closure. */
  public void setSlot(int slot) {
    Expect.arg(slot >= 0, "Must have non-negative slot.");
    Expect.state(mSlot == -1, "Can only set slot once.");
    
    mSlot = slot;
  }
  
  /**
   * Gets the index of the variable that this upvar is closing over. This
   * will be a register if it's closing over a local, or an index into a
   * closures's upvar list if it's closing over an upvar.
   */
  public int getIndex() {
    return mIndex;
  }
  
  private final String mName;
  private final boolean mIsLocal;
  private final int mIndex;
  private int mSlot;
}
