package me.lordnumberwang.jackcompiler;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.*;
import me.lordnumberwang.jackcompiler.JackToken.KeyWord;
import me.lordnumberwang.jackcompiler.JackToken.TokenType;

public class JackTokenizer {
  Path outfile;
  private boolean inBlockComment = false;
  Stream<JackToken> tokens;
  JackToken currentToken;
  Queue<JackToken> tokenBuffer;
  Iterator<JackToken> iterator;

  static Set<Character> symbolSet = Set.of(
      '(', ')', '{', '}', '[', ']', ',', '.', ';',
      '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
  );
  static Set<String> keywordSet = Set.of(
      "class", "constructor", "function", "method", "field", "static",
      "var", "int", "char", "boolean",
      "void", "true", "false", "null",
      "this", "let", "do", "if", "else", "while", "return"
  );
  static Predicate<Character> identifierChar = c -> (Character.isLetterOrDigit(c) || c == '_');

  public JackTokenizer() { }

  /**
   * Ignores all comments and whitespace in input stream and serializes to JackTokens
   * @param lines - incoming Stream of Strings, for lines of code from a file
   */
  public Stream<JackToken> tokenize(Stream<String> lines) {
    //Temporary holder - empty stream
    //Set currentToken and tokens list to null
    currentToken = null;
    tokens = lines.map(String::trim)
        .map(this::removeComments)
        .filter(str -> !str.isBlank())
        .map(this::tokenizeLine)
        .flatMap(List::stream);
    return tokens;
  }
  /**
   *
    * @param line - incoming line of code as a string
   * @return line without comments, accounting for multiline comments
   *          starting with /** or /*
   */
  public String removeComments(String line) {
    StringBuilder nonComments = new StringBuilder();
    int i=0;
    while (i< line.length()) {
      if (inBlockComment) {
        if (i+1 < line.length() && line.charAt(i) == '*' && line.charAt(i+1)=='/') {
          //comment closed
          inBlockComment = false;
          i+=2;
        } else {
          i++;
        }
      } else {
        //Not starting as block comment
        if (i+1 < line.length() && line.charAt(i) == '/') {
          if (line.charAt(i+1) == '*') {
            inBlockComment = true;
            i+=2;
          } else if (line.charAt(i+1) == '/') {
            //Entirely comment line - break out
            break;
          } else {
            //Not a comment, just / operator
            nonComments.append(line.charAt(i)); //append as text
            i++;
          }
        } else {
          nonComments.append(line.charAt(i)); //append as text
          i++;
        }
      }
    }
    return nonComments.toString().trim();
  }


  /**
   * Recursive function generating tokens for a given line/substring
   * @param line - represents a line or substring to be interpreted to tokens
   * @return
   *    a List<JackTokens> representing the tokens this line/substring represents
   */
  public List<JackToken> tokenizeLine(String line) {
    //TODO recursively handle tokenization based on incoming word
    //    String[] words = Arrays.stream(line.split(" "))
    //            .filter(str -> !str.isBlank())
    //            .toArray(String[]::new);
    //This needs to be recursive, without splitting by " "

    if (line.isBlank()) {
      return new ArrayList<>(); //return empty arraylist
    }
    line = line.trim();
    List<JackToken> lineTokens = new ArrayList<>(List.of());
    // Token interpretation hierarchy:
    // 1. If starts with " => String Const
    // 2. If char(0) is Symbol in list => Symbol
    // 3. If starts with decimal value => Integer Constant
    // 4. check String => keyword (go until non-space char)
    // 5. else identifier => String.valueOf(char).matches("[a-zA-Z_]")

    char firstChar = line.charAt(0);
    if (firstChar == '"') {
      //Case 1: String Constant encased in "..."
      return tokenizeString(line, lineTokens);
    } else if (symbolSet.contains(firstChar)) {
      //Case 2: Token is Symbol (not contained in "")
      lineTokens.add(new JackToken(TokenType.SYMBOL, line.charAt(0)));
      if (line.length() > 1) {
        lineTokens.addAll(tokenizeLine(line.substring(1)));
      }
      return lineTokens;
    } else if (firstChar >= '0' && firstChar <= '9') {
      // Case 3: Integer Constant - If starts with decimal value
      return tokenizeInteger(line, lineTokens);
    } else if (identifierChar.test(firstChar)) {
      // Read in until reach non-'string' character:
      // letter, digit, _ not starting with digit (handled by previous case)
      // Case 4: Check if this string is a keyword
      // Case 5: If not a keyword, parse as Identifier
      return tokenizeKeywordOrString(line, lineTokens);
    } else {
      throw new RuntimeException("Illegal/Unparseable Character passed in: " + firstChar);
    }
  }

  /**
   * Returns: Are there more tokens in the input?
   */
  public boolean hasMoreTokens() {
    //TODO handle this with a buffer allowing for lookahead
    return true;
  }

  /**
   * Gets next token from the input and makes it the current token.
   * Called only if hasMoreTokens is true.
   * Initially there is no current token.
   */
  public void advance() {
    //TODO handle reading from lookahead buffer as needed

  }

  /**
   * Returns the type of the current token as a constant
   */
  public TokenType tokenType() {
    return currentToken.type;
  }

  /**
   * Returns KeyWord enum for current token as a constant.
   * Called only if type is KEYWORD
   * @return KeyWord enum value of current token
   */
  public KeyWord keyWord() {
    if (currentToken.type == TokenType.KEYWORD) {
      return currentToken.keyWord;
    } else {
      throw new IllegalArgumentException("Token is not a Keyword");
    }
  }

  /**
   * Returns the character which is the current token.
   * Only called if type is SYMBOL
   */
  public char symbol() {
    if (tokenType() == TokenType.SYMBOL) {
      return currentToken.charValue;
    } else {
      throw new IllegalArgumentException("Token is not a symbol");
    }
  }

  /**
   * Returns identifier which is the current token.
   * Called only if type is IDENTIFIER
   * @return
   */
  public String identifier() {
    if (tokenType() == TokenType.IDENTIFIER) {
      return currentToken.stringValue;
    } else {
      throw new IllegalArgumentException("Token is not an identifier");
    }
  }

  /**
   * Returns integer value of current token.
   * Called only if type is INT_CONST
   * @return
   */
  public int intVal() {
    if (tokenType() == TokenType.INT_CONST) {
      return currentToken.intValue;
    } else {
      throw new IllegalArgumentException("Token is not an integer constant");
    }
  }

  /**
   * Returns string value of current token
   * Called only if type is STRING_CONST
   * @return
   */
  public String stringVal() {
    if (tokenType() == TokenType.STRING_CONST) {
      return currentToken.stringValue;
    } else {
      throw new IllegalArgumentException("Token is not a string");
    }
  }

  public JackToken getCurrentToken() {
    return currentToken;
  }

  private List<JackToken> tokenizeString(String line, List<JackToken> lineTokens) {
    StringBuilder strConst = new StringBuilder();
    int idx = 1;
    while (idx < line.length()) {
      char currentChar = line.charAt(idx);
      if (currentChar == '"') {
        //strConst = line.substring(0,idx-1);
        lineTokens.add(new JackToken(TokenType.STRING_CONST, strConst.toString()));
        if (idx+1 < line.length()) {
          //recursively parse rest
          lineTokens.addAll(tokenizeLine(line.substring(idx+1)));
        }
        return lineTokens;
      } else if (currentChar == '\n') {
        throw new RuntimeException("Newline found in string constant.");
      }
      strConst.append(currentChar);
      idx++;
    }
    throw new RuntimeException("Unpaired \", invalid string in parsing: " + line);
  }

  private List<JackToken> tokenizeInteger(String line, List<JackToken> lineTokens) {
    char firstChar = line.charAt(0);
    Predicate<Character> charTest = x -> x >= '0' && x <= '9';
    if (firstChar == '0' && line.length() > 1
        && (charTest.test(line.charAt(1)))) {
      // Handle invalid number of leading 0.
      throw new RuntimeException("Leading 0 on a number detected: " + line);
    }
    int idx = 1;
    while (idx < line.length()) {
      if (!charTest.test(line.charAt(idx))) {
        lineTokens.add(new JackToken(
            TokenType.INT_CONST,
            Integer.parseInt(line.substring(0,idx))
        ));
        lineTokens.addAll(tokenizeLine(line.substring(idx)));
        return lineTokens;
      }
      idx++;
    }
    //If stays numeric until end of line:
    lineTokens.add(new JackToken(
        TokenType.INT_CONST,
        Integer.parseInt(line))
    );
    return lineTokens;
  }

  /**
   * Assumes first char passed the string test
   * @param line
   * @param lineTokens
   * @return list of JackTokens from line.
   */
  private List<JackToken> tokenizeKeywordOrString(String line, List<JackToken> lineTokens) {
    int idx = 1;
    StringBuilder word = new StringBuilder();
    word.append(line.charAt(0));
    while (idx < line.length()) {
      char currentChar = line.charAt(idx);
      if (!identifierChar.test(currentChar)) {
        //reached end of string
        break;
      };
      idx++;
      word.append(currentChar);
    }

    if (keywordSet.contains(word.toString().toLowerCase())) {
      // Case 4: Parse as a keyword
      lineTokens.add(new JackToken(TokenType.KEYWORD,
          KeyWord.valueOf(word.toString().toUpperCase())));
    } else {
      // Case 5: If not a keyword, parse as Identifier
      lineTokens.add(new JackToken(TokenType.IDENTIFIER,
          word.toString()));
    }
    if (idx != line.length()) {
      // recursive call the remainder
      lineTokens.addAll(tokenizeLine(line.substring(idx)));
    }
    return lineTokens;
  }
}