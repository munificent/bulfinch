package com.stuffwithstuff.bulfinch;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves all name references in a function. Modifies the AST to include
 * resolution information, and returns the list of all local variables
 * declared in the function.
 */
public class NameResolver implements ExprVisitor<Void> {
  public static void resolveTopLevel(FunctionExpr function) {
    new NameResolver(null).resolve(function);
  }
  
  private void resolve(FunctionExpr function) {
    mLocals.addAll(function.getParameters());
    function.getBody().accept(this, null);
    function.resolve(mLocals, mUpvars);
  }

  @Override
  public void visit(AssignExpr expr, Void dummy) {
    resolveName(expr.getName());
    expr.getValue().accept(this, dummy);
  }
  
  @Override
  public void visit(BoolExpr expr, Void arg) {
    // Do nothing.
  }

  @Override
  public void visit(CallExpr expr, Void dummy) {
    expr.getFunction().accept(this, dummy);
    for (Expr arg : expr.getArgs()) arg.accept(this, dummy);
  }

  @Override
  public void visit(FunctionExpr expr, Void arg) {
    // Resolve the function itself.
    new NameResolver(this).resolve(expr);
  }

  @Override
  public void visit(NameExpr expr, Void arg) {
    resolveName(expr.getName());
  }

  @Override
  public void visit(NumberExpr expr, Void arg) {
    // Do nothing.
  }

  @Override
  public void visit(SequenceExpr sequence, Void arg) {
    for (Expr expr : sequence.getExpressions()) expr.accept(this, arg);
  }

  @Override
  public void visit(StringExpr expr, Void arg) {
    // Do nothing.
  }

  @Override
  public void visit(VarExpr expr, Void arg) {
    mLocals.add(expr.getName().getIdentifier());
    expr.getName().resolveLocal(mLocals.size() - 1);
    
    expr.getValue().accept(this, arg);
  }

  private NameResolver(NameResolver outerFunction) {
    mOuterFunction = outerFunction;
    mLocals = new ArrayList<String>();
    mUpvars = new ArrayList<UpvarRef>();
  }
  
  private void resolveName(Name name) {
    // TODO(bob): This could definitely be done more cleanly.
    List<NameResolver> chain = new ArrayList<NameResolver>();
    findName(this, name, chain);

    switch (chain.size()) {
    case 0:
      // Must be a global.
      name.resolveGlobal();
      break;
      
    case 1:
      // Local.
      // TODO(bob): Include this index info in result of findName.
      name.resolveLocal(mLocals.indexOf(name.getIdentifier()));
      break;
      
    default:
      // Defined in enclosing scope.
      UpvarRef upvar = null;
      
      for (int i = 1; i < chain.size(); i++) {
        NameResolver resolver = chain.get(i);
        
        // See if we've already added it to this scope's closure.
        boolean alreadyAdded = false;
        for (int index = 0; index < resolver.mUpvars.size(); index++) {
          if (resolver.mUpvars.get(index).name.equals(name.getIdentifier())) {
            alreadyAdded = true;
            upvar = resolver.mUpvars.get(index);
            break;
          }
        }
        if (alreadyAdded) continue;
        
        // Close over it in this scope.
        NameResolver outer = chain.get(i - 1);
        
        if (i == 1) {
          // Closing over a local in the outer scope.
          int local = outer.mLocals.indexOf(name.getIdentifier());
          upvar = new UpvarRef(name.getIdentifier(),
              resolver.mUpvars.size(), local);
        } else {
          // Closing over an upvar in the outer scope. (I.e. closing over a
          // variable not defined in the immediate outer scope.)
          upvar = null;
          
          for (int index = 0; index < outer.mUpvars.size(); index++) {
            if (outer.mUpvars.get(index).name.equals(name.getIdentifier())) {
              // Negative to distinguish closing over local versus upvar.
              upvar = new UpvarRef(name.getIdentifier(),
                  resolver.mUpvars.size(), -1 - index);
              break;
            }
          }
          
          Expect.state(upvar != null, "Couldn't finding matching upvar.");
        }

        resolver.mUpvars.add(upvar);
      }

      // Resolve the name to the final upvar.
      name.resolveUpvar(upvar);
      break;
    }
  }
  
  /**
   * Finds which outer scope a given name is defined in. If found, it will
   * fill chain with all of the scopes from where it was defined to this one
   * (inclusive). If the name isn't found, chain will be left empty.
   */
  private void findName(NameResolver function, Name name, List<NameResolver> chain) {
    int local = function.mLocals.indexOf(name.getIdentifier());
    if (local != -1) {
      // Found it!
      chain.add(function);
      return;
    }
    
    if (function.mOuterFunction == null) return;
    
    // Recurse upwards.
    findName(function.mOuterFunction, name, chain);
    if (chain.size() > 0) {
      // We found it, so add our link to the chain.
      chain.add(function);
    }
    
    return;
  }
  
  private final NameResolver mOuterFunction;
  private final List<String> mLocals;
  private final List<UpvarRef> mUpvars;
}
