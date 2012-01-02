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

  
  int findUpvar(List<UpvarRef> upvars, Name name) {
    for (int index = 0; index < upvars.size(); index++) {
      if (upvars.get(index).name.equals(name.getIdentifier())) {
        return index;
      }
    }
    
    // Not found.
    return -1;
  }

  private void resolveName(Name name) {
    // TODO(bob): This could definitely be done more cleanly.
    List<NameResolver> chain = new ArrayList<NameResolver>();
    findName(this, name, chain);

    if (chain.size() > 1) {
      // Defined in enclosing scope.
      UpvarRef upvar = null;
      
      for (int i = 1; i < chain.size(); i++) {
        NameResolver resolver = chain.get(i);
        
        // See if we've already added it to this scope's closure.
        int existing = findUpvar(resolver.mUpvars, name);
        if (existing != -1) {
          upvar = resolver.mUpvars.get(existing);
          continue;
        }
        
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
          int index = findUpvar(outer.mUpvars, name);
          Expect.state(index >= 0, "Couldn't finding matching upvar.");

          // Negative to distinguish closing over local versus upvar.
          upvar = new UpvarRef(name.getIdentifier(),
              resolver.mUpvars.size(), -1 - index);
        }

        resolver.mUpvars.add(upvar);
      }

      // Resolve the name to the final upvar.
      name.resolveUpvar(upvar);
    }
  }
  
  /**
   * Finds which outer scope a given name is defined in. If found, it will
   * fill chain with all of the scopes from where it was defined to this one
   * (inclusive). If the name isn't found, chain will be left empty.
   */
  private boolean findName(NameResolver function, Name name, List<NameResolver> chain) {
    // See if the name is defined here.
    int local = function.mLocals.indexOf(name.getIdentifier());
    if (local != -1) {
      // If it is defined in the current scope, resolve it as local.
      if (function == this) {
        name.resolveLocal(local);
        return false;
      }
      
      // Found it!
      chain.add(function);
      return true;
    }
    
    if (function.mOuterFunction == null) {
      // If we got here, we couldn't find the name in any scope, so we'll
      // assume it's global.
      name.resolveGlobal();
      return false;
    }
    
    // Recurse upwards.
    boolean found = findName(function.mOuterFunction, name, chain);
    if (found) {
      // TODO(bob): Get rid of explicit chain and just move code from
      // resolveName() into here.
      // We found it, so add our link to the chain.
      chain.add(function);
    }
    
    return found;
  }
  
  private final NameResolver mOuterFunction;
  private final List<String> mLocals;
  private final List<UpvarRef> mUpvars;
}
