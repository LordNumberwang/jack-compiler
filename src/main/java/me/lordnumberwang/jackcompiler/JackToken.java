package me.lordnumberwang.jackcompiler;

import java.util.Map;
import java.util.Set;

public class JackToken {
  public enum TokenType {
    KEYWORD,
    SYMBOL,
    IDENTIFIER,
    INT_CONST,
    STRING_CONST
  }
  public enum KeyWord {
    CLASS, METHOD, FUNCTION, CONSTRUCTOR,
    INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD,
    LET, DO, IF, ELSE, WHILE, RETURN,
    TRUE, FALSE, NULL, THIS
  }
  public static final Set<Character> symbolSet = Set.of(
      '(', ')', '{', '}', '[', ']', ',', '.', ';',
      '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
  );
  public static final Map<String, String> mapXmlSym = Map.of(
      "<", "&lt;",
      ">", "&gt;",
      "\"", "&quot;",
      "&", "&amp;"
  );
  public final TokenType type;
  public KeyWord keyWord;
  public String stringValue;
  public int intValue;
  public char charValue;

  JackToken(TokenType type, KeyWord kw) {
    if (type != TokenType.KEYWORD) {
      throw new IllegalArgumentException("Constructor may only be used with TokenType.KEYWORD");
    }
    this.type = type;
    this.keyWord = kw;
  }

  JackToken(TokenType type, int intValue) {
    if (type != TokenType.INT_CONST) {
      throw new IllegalArgumentException("Constructor may only be used with TokenType.INT_CONST");
    }
    if (intValue > 32767 || intValue < 0) {
      throw new IllegalArgumentException("Invalid int (must be 0-32767");
    }
    this.type = type;
    this.intValue = intValue;
  }

  JackToken(TokenType type, char symbolChar) {
    if (type != TokenType.SYMBOL) {
      throw new IllegalArgumentException("Constructor may only be used with TokenType.INT_CONST");
    }
    this.type = type;
    this.charValue = symbolChar;
  }

  JackToken(TokenType type, String strValue) {
    if (type != TokenType.STRING_CONST && type != TokenType.IDENTIFIER) {
      throw new IllegalArgumentException("Constructor may only be used with TokenType.STRING_CONST "
          + "or TokenType.IDENTIFIER");
    }
    this.type = type;
    this.stringValue = strValue;
  }

  public String getValue() {
    return switch (type) {
      case INT_CONST ->
        String.valueOf(intValue);
      case STRING_CONST, IDENTIFIER ->
        stringValue;
      case SYMBOL ->
        String.valueOf(charValue);
      case KEYWORD ->
        keyWord.toString().toLowerCase();
      default ->
        throw new IllegalArgumentException("No type set");
    };
  }

  public String toXmlElement() {
    String xmlValue = switch (type) {
      case SYMBOL -> mapXmlSym.getOrDefault(getValue(), getValue());
      case STRING_CONST, KEYWORD, INT_CONST, IDENTIFIER -> getValue();
      default ->
        throw new IllegalArgumentException("No type set");
    };
    return writeTag() + " " + getValue() + " " + writeEndTag();
  }

  public String typeString() {
    return switch(type) {
      case KEYWORD -> "keyword";
      case SYMBOL -> "symbol";
      case IDENTIFIER -> "identifier";
      case STRING_CONST -> "stringConstant";
      case INT_CONST -> "integerConstant";
      default ->
        throw new IllegalArgumentException("No type set");
    };
  }

  static public String typeString(TokenType aType) {
    return switch(aType) {
      case KEYWORD -> "keyword";
      case SYMBOL -> "symbol";
      case IDENTIFIER -> "identifier";
      case STRING_CONST -> "stringConstant";
      case INT_CONST -> "integerConstant";
      default ->
          throw new IllegalArgumentException("Invalid type");
    };
  }

  String writeTag() {
    return "<" + typeString() + ">";
  }

  String writeEndTag() {
    return "</" + typeString() + ">";
  }

  @Override
  public String toString() {
    return "JackToken{" +
        "type=" + type +
        ", value=" + getValue() +
        '}';
  }
}
