package me.lordnumberwang.jackcompiler;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import me.lordnumberwang.jackcompiler.JackToken;
import me.lordnumberwang.jackcompiler.JackToken.keyWord;
import me.lordnumberwang.jackcompiler.JackToken.tokenType;

public class JackTokenizer {
  Path outfile;
  private boolean inBlockComment = false;
  Stream<JackToken> tokens;
  JackToken currentToken;
  Queue<JackToken> tokenBuffer;
  Iterator<JackToken> iterator;

  public JackTokenizer() {
  }

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
        .map(this::tokenizeLine);
    return tokens;
  }

  /**
   *
    * @param line - incoming line of code as a string
   * @return line without comments, accounting for multiline comments
   *          starting with /** or /*
   */
  public String removeComments(String line) {
    StringBuilder nonComments= new StringBuilder();
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
          }
        } else {
          nonComments.append(line.charAt(i)); //append as text
          i++;
        }
      }
    }
    return nonComments.toString().trim();
  }

  public JackToken tokenizeLine(String line) {
    //TODO handle tokenization based on incoming word
    String[] words = Arrays.stream(line.split(" "))
            .filter(str -> !str.trim().isEmpty())
            .toArray(String[]::new);
    //need to handle 'words' now to tokens

    // Case 1. Keyword
    // Case 2. Integer (decimal number)
    // Case 3. Symbol (in a set list of chars)
    Set<Character> symbolSet = Set.of(
        '(', ')', '{', '}', '[', ']', ',', '.', ';',
        '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
    );
//    Set<Character> stringSet = Set.of(
//        '(', ')', '{', '}', '[', ']', ',', '.', ';',
//        '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
//    );
    // symbolSet.contains(myChar) to check if contained
    // Case 4. String Constant (within ""s)
    // Case 5. Identifier (start of string not beginning with a digit)

    return new JackToken(tokenType.SYMBOL);
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
  public tokenType tokenType() {
    return currentToken.tokenType;
  }

  /**
   * Returns keyWord enum for current token as a constant.
   * Called only if tokenType is KEYWORD
   * @return keyWord enum value of current token
   */
  public keyWord keyWord() {
    if (currentToken.tokenType == tokenType.KEYWORD) {
      return currentToken.keyWord;
    } else {
      throw new IllegalArgumentException("Token is not a Keyword");
    }
  }

  /**
   * Returns the character which is the current token.
   * Only called if tokenType is SYMBOL
   */
  private char symbol() {
    //TODO -
    if (tokenType() == tokenType.SYMBOL) {
      return 'a';
    } else {
      throw new IllegalArgumentException("Token is not a symbol");
    }
  }

  /**
   * Returns identifier which is the current token.
   * Called only if tokenType is IDENTIFIER
   * @return
   */
  private String identifier() {
    //TODO
    if (tokenType() == tokenType.IDENTIFIER) {
      return "TODO";
    } else {
      throw new IllegalArgumentException("Token is not an identifier");
    }
  }

  /**
   * Returns integer value of current token.
   * Called only if tokenType is INT_CONST
   * @return
   */
  private int intVal() {
    //TODO
    if (tokenType() == tokenType.INT_CONST) {
      return 0;
    } else {
      throw new IllegalArgumentException("Token is not an integer constant");
    }
  }

  /**
   * Returns string value of current token
   * Called only if tokenType is STRING_CONST
   * @return
   */
  private String stringVal() {
    //TODO
    if (tokenType() == tokenType.STRING_CONST) {
      return "TODO";
    } else {
      throw new IllegalArgumentException("Token is not a string");
    }
  }

  public JackToken getCurrentToken() {
    return currentToken;
  }
}