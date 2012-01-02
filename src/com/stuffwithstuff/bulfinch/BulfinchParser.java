package com.stuffwithstuff.bulfinch;

import java.util.*;

public class BulfinchParser extends Parser {
  public BulfinchParser(Lexer lexer) {
    super(lexer);
  }
  
  public Map<String, FunctionExpr> parseProgram() {
    Map<String, FunctionExpr> functions = new HashMap<String, FunctionExpr>();
    
    while (!match(TokenType.EOF)) {
      consume(TokenType.FN);
      String name = consume(TokenType.NAME).getString();
      
      List<String> params = parseParams();
      Expr body = parseBody();
      
      functions.put(name, new FunctionExpr(params, body));
      
      match(TokenType.LINE);
    }
    
    return functions;
  }
  
  private Expr sequence() {
    List<Expr> exprs = new ArrayList<Expr>();

    do {
      // ignore trailing lines before closing a group
      if (isMatch(TokenType.RIGHT_PAREN)) break;
      if (isMatch(TokenType.RIGHT_BRACE)) break;
      if (isMatch(TokenType.RIGHT_BRACKET)) break;
      if (isMatch(TokenType.EOF)) break;

      exprs.add(assign());
    } while (match(TokenType.LINE));

    // only create a list if we actually had a ;
    if (exprs.size() == 1) return exprs.get(0);

    return new SequenceExpr(exprs);
  }
  
  private Expr assign() {
    if (match(TokenType.NAME, TokenType.EQUALS)) {
      String name = getMatch()[0].getString();
      Expr value = assign();
      return new AssignExpr(name, value);
    }
    
    return call();
  }
  
  private Expr call() {
    Expr expr = primary();
    
    while (match(TokenType.LEFT_PAREN)) {
      List<Expr> args = new ArrayList<Expr>();
      
      if (match(TokenType.RIGHT_PAREN)) {
        // No args.
      } else {
        do {
          args.add(assign());
        } while (match(TokenType.COMMA));
        
        consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
      }
      
      expr = new CallExpr(expr, args);
    }
    
    return expr;
  }
  
  private Expr primary() {
    if (match(TokenType.NAME)) {
      String name = getMatch()[0].getString();

      // check for reserved names
      if (name.equals("true")) return new BoolExpr(true);
      if (name.equals("false")) return new BoolExpr(false);

      return new NameExpr(name);

    } else if (match(TokenType.VAR)) {
      String name = consume(TokenType.NAME).getString();
      consume(TokenType.EQUALS);
      Expr value = call();
      return new VarExpr(name, value);
      
    } else if (match(TokenType.FN)) {
      List<String> params = parseParams();
      Expr body = parseBody();
      return new FunctionExpr(params, body);
      
    } else if (match(TokenType.NUMBER)) {
      return new NumberExpr(getMatch()[0].getDouble());

    } else if (match(TokenType.STRING)) {
      return new StringExpr(getMatch()[0].getString());

    } else if (match(TokenType.LEFT_PAREN)) {
      Expr expr = assign();

      consume(TokenType.RIGHT_PAREN, "Missing closing ')'.");
      
      return expr;
    }

    throw new ParseException("Couldn't parse primary.");
  }


  private List<String> parseParams() {
    consume(TokenType.LEFT_PAREN);
    List<String> params = new ArrayList<String>();
    if (match(TokenType.RIGHT_PAREN)) {
      // No params.
    } else {
      do {
        String param = consume(TokenType.NAME).getString();
        params.add(param);
      } while (match(TokenType.COMMA));
      
      consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
    }
    
    return params;
  }
  
  private Expr parseBody() {
    // Parse the body.
    consume(TokenType.LEFT_BRACE);
    Expr body = sequence();
    consume(TokenType.RIGHT_BRACE);
    return body;
  }
}
