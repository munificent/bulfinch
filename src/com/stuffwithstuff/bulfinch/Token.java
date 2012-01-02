package com.stuffwithstuff.bulfinch;

public final class Token {
  public Token(final TokenType type) {
    mType = type;
    mStringValue = "";
  }

  public Token(final TokenType type, final String value) {
    mType = type;
    mStringValue = value;
  }

  public TokenType getType() {
    return mType;
  }

  public String getString() {
    return mStringValue;
  }

  public String toString() {
    switch (mType) {
    case LEFT_PAREN:
      return "(";
    case RIGHT_PAREN:
      return ")";
    case LEFT_BRACKET:
      return "[";
    case RIGHT_BRACKET:
      return "]";
    case LEFT_BRACE:
      return "{";
    case RIGHT_BRACE:
      return "}";
    case COMMA:
      return ",";
    case LINE:
      return "(line)";
    case DOT:
      return ".";
    case EQUALS:
      return "=";
      
    case NAME:
      return mStringValue + " (name)";
    case OPERATOR:
      return mStringValue + " (op)";
    case KEYWORD:
      return mStringValue + " (keyword)";

    case STRING:
      return "\"" + mStringValue + "\"";

    case EOF:
      return "(eof)";

    default:
      return "(unknown token?!)";
    }
  }

  private final TokenType mType;
  private final String mStringValue;
}
