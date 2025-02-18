package me.lordnumberwang.jackcompiler;

import me.lordnumberwang.jackcompiler.JackToken.keyWord;

public class JackToken {
  public enum tokenType {
    KEYWORD,
    SYMBOL,
    IDENTIFIER,
    INT_CONST,
    STRING_CONST
  }
  public enum keyWord {
    CLASS, METHOD, FUNCTION, CONSTRUCTOR,
    INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD,
    LET, DO, IF, ELSE, WHILE, RETURN,
    TRUE, FALSE, NULL, THIS
  }
  tokenType tokenType;
  keyWord keyWord;

  JackToken(tokenType type, keyWord kw) {
    tokenType = type;
    keyWord = kw;
  }

  JackToken(tokenType type) {
    tokenType = type;
  }
}
