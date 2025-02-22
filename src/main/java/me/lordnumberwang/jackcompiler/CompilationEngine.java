package me.lordnumberwang.jackcompiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import me.lordnumberwang.jackcompiler.JackToken.KeyWord;
import me.lordnumberwang.jackcompiler.JackToken.TokenType;

public class CompilationEngine {
  static Map<String, String> mapXmlSym = Map.of(
      "<", "&lt;",
      ">", "&gt;",
      "\"", "&quot;",
      "&", "&amp;"
  );
  static List<String> defaultClasses = List.of("int", "char", "boolean");
  BufferedWriter writer;
  Stream<JackToken> tokens;
  Queue<JackToken> buffer;
  JackToken currentToken;
  Iterator<JackToken> tokenReader;
  boolean tokensExhausted;
  int indent; //indentation level
  List<String> definedClasses;

  public CompilationEngine() {}

  public void setDefinedClasses(List<String> classStrings) {
    definedClasses = defaultClasses;
    definedClasses.addAll(classStrings);
  }

  public void compileToXML(Stream<JackToken> newTokens, Path outfile) {
    try (BufferedWriter newWriter = Files.newBufferedWriter(outfile,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)) {

      writer = newWriter;
      tokens = newTokens;
      tokenReader = tokens.iterator();
      buffer = new LinkedList<>();
      currentToken = null;
      tokensExhausted = false;
      indent = 0;
      advance();
      compileClass();
    } catch (IOException e) {
      throw new RuntimeException("Unable to write to XML file: " +
          outfile.getFileName().toString());
    }
  }

  /**
   * Create compile class.
   */
  void compileClass() throws IOException {
    //Validate first token is class
    if (getCurrentToken().type != TokenType.KEYWORD || getCurrentToken().keyWord != KeyWord.CLASS) {
      throw new RuntimeException("Invalid file - file must start with class declaration.");
    } else {
      writeLine("<class>");
      indent++;
      // class <name> {
      writeLine(currentToken.toXmlElement()); //<keyword> class </keyword>
      advance();
      writeIfValid(TokenType.IDENTIFIER);
      writeIfValid(TokenType.SYMBOL, "{");

      //class var declarations starting with 'static'/'field'
      while((currentToken.type == TokenType.KEYWORD) &&
          Set.of("static", "field").
              contains(currentToken.getValue())) {
        compileClassVarDec();
      }

      //subroutine declarations (constructor, function, method)
      while((currentToken.type == TokenType.KEYWORD) &&
          Set.of("constructor", "function", "method").
              contains(currentToken.getValue())) {
        compileSubroutineDec();
      }

      // }
      writeIfValid(TokenType.SYMBOL, "}");

      indent--;
      writeLine("</class>");
    }
  }

  /**
   * Compile static variable or field declaration.
   * Advance token afterward
   */
  void compileClassVarDec() throws IOException {
    //(static|field) type varName (',' varName)* ;
    writeLine("<classVarDec>");
    indent++;

    writeLine(currentToken.toXmlElement()); //write field/static detected on entry
    advance();
    if (isValidType()) {
      writeIfValid(TokenType.IDENTIFIER); //type matching defined classes
    } else {
      throw new IllegalArgumentException("Received invalid type: " + currentToken.getValue());
    }
    writeIfValid(TokenType.IDENTIFIER); //varName

    while(isValid(TokenType.SYMBOL,",")) {
      writeLine(currentToken.toXmlElement()); //,
      advance();
      writeIfValid(TokenType.IDENTIFIER);
    }

    writeIfValid(TokenType.SYMBOL, ";");
    indent--;
    writeLine("</classVarDec>");
  }

  /**
   * Compile complete method, function or constructor
   * Advances token afterward
   */
  void compileSubroutineDec() throws IOException {
    //(constructor|function|method) (void|type) name ( parameterList ) subroutineBody;
    writeLine("<subroutineDec>");
    indent++;

    writeLine(currentToken.toXmlElement()); //write const/func/method detected on entry
    advance();
    if (isValid(TokenType.KEYWORD,"void") ||
        (isValidType())) {
      writeLine(currentToken.toXmlElement());
    } else {
      throw new IllegalArgumentException("Received invalid type for subroutine: "
          + currentToken.getValue());
    }
    advance();
    writeIfValid(TokenType.IDENTIFIER); //name
    //( parameterList )
    writeIfValid(TokenType.SYMBOL,"(");
    compileParameterList();
    writeIfValid(TokenType.SYMBOL,")");

    compileSubroutineBody();

    indent--;
    writeLine("</subroutineDec>");
  }

  /**
   * Compile a 0+ parameter list. Does not handle the enclosing '()'
   */
  void compileParameterList() throws IOException {
    //0 or 1 case: ( (type varName) (, type varName)* )?
    writeLine("<parameterList>");

    if (isValidType()) {
      indent++;
      //Params present case: (type varName) (, type varName)*
      writeLine(currentToken.toXmlElement());
      advance();
      writeIfValid(TokenType.IDENTIFIER); //varName
      while(isValid(TokenType.SYMBOL,",")) {
        writeLine(currentToken.toXmlElement()); // ","
        advance();
        if (isValidType()) {
          writeLine(currentToken.toXmlElement()); //write type
        } else {
          throw new IllegalArgumentException("Received invalid type for parameter list: "
              + currentToken.getValue());
        }
        advance();
        writeIfValid(TokenType.IDENTIFIER);
      }
      indent--;
    }

    writeLine("</parameterList>");
  }

  /**
   * Compile a subroutine's body
   * Grammar: '{' varDec* statements '}'
   */
  void compileSubroutineBody() throws IOException {
    writeLine("<subroutineBody>");
    indent++;
    writeIfValid(TokenType.SYMBOL, "{");

    //Handle 0+ variable declarations
    while (isValid(TokenType.SYMBOL, "var")) {
      compileVarDec();
    }

    compileStatements();

    writeIfValid(TokenType.SYMBOL, "}");

    indent--;
    writeLine("</subroutineBody>");
  }

  /**
   * Compile var declaration - assumes current token is 'var' keyword
   * Grammar: 'var' type varName (',' varName)* ';'
   */
  void compileVarDec() throws IOException {
    writeLine("<varDec>");
    indent++;

    writeLine(currentToken.toXmlElement());
    advance();
    if (isValidType()) {
      writeLine(currentToken.toXmlElement()); //write type
    } else {
      throw new IllegalArgumentException("Received invalid type for variable declaration: "
          + currentToken.getValue());
    }
    advance();
    writeIfValid(TokenType.IDENTIFIER); //varName
    while(isValid(TokenType.SYMBOL,",")) {
      writeLine(currentToken.toXmlElement()); // ","
      advance();
      writeIfValid(TokenType.IDENTIFIER); //varName*
    }
    writeIfValid(TokenType.SYMBOL, "}");

    indent--;
    writeLine("</varDec>");
  }

  /**
   * Compile a sequence of Statements, doesn't' handle enclosing {}
   */
  void compileStatements() throws IOException {
    writeLine("<statements>");
    indent++;

    while(currentToken.type == TokenType.KEYWORD &&
        Set.of("let","if","while","do","return").contains(currentToken.getValue())) {
      switch (currentToken.getValue()) {
        case "let" -> compileLet();
        case "if" -> compileIf();
        case "while" -> compileWhile();
        case "do" -> compileDo();
        case "return" -> compileReturn();
        default ->
          throw new IllegalArgumentException("Invalid Statement type");
      }
    }

    indent--;
    writeLine("</statements>");
  }

  /**
   * Compile let statement
   */
  void compileLet() {
    //TODO
    return;
  }

  /**
   * Compile If statement (possible training else clause).
   */
  void compileIf() {
    //TODO
    return;
  }

  /**
   * Compile while statement
   */
  void compileWhile() {
    //TODO
    return;
  }

  /**
   * Compile do statement
   */
  void compileDo() {
    //TODO
    return;
  }

  /**
   * Compile return statement
   */
  void compileReturn() {
    //TODO
    return;
  }

  /**
   * Compile Expression
   */
  void compileExpression() {
    //TODO
    return;
  }

  /**
   * Compile Term.
   * If currentToken is identifier => must distinguish between variable,
   *                                  array entry, or subroutine call.
   * Single look-ahead token is one of [ ( or . characters.
   */
  void compileTerm() {
    //TODO
    return;
  }

  /**
   * Compile Expression List - compile comma separated list of expressions (may be empty)
   */
  void compileExpressionList() {
    //TODO
    return;
  }

  String escapeToXML(String symbolString) {
    return mapXmlSym.getOrDefault(symbolString, symbolString);
  }

  /**
   * Gets next token from the input and makes it the current token.
   * Pulls from buffer first, before loading in next token (via iterator).
   * Initially there is no current token.
   */
  public void advance() {
    if (!buffer.isEmpty()) {
      currentToken = buffer.poll();
      return;
    }
    loadNextToken();
    if (!buffer.isEmpty()) {
      currentToken = buffer.poll();
    } else {
      currentToken = null;
    }
  }

  public void loadNextToken() {
    if (!tokensExhausted) {
      if (tokenReader.hasNext()) {
        buffer.offer(tokenReader.next());
      } else {
        tokensExhausted = true;
      }
    }
  }

  public JackToken getCurrentToken() {
    return this.currentToken;
  }

  public void writeLine(String input) throws IOException {
    writer.write((' '*(indent*2)) + input);
    writer.newLine();
  }

  /**
   * Validate the incoming
   * @param type
   * @param value
   * @return
   */
  boolean isValid(JackToken token, TokenType type, String value) {
    return (token.type == type &&
        Objects.equals(token.getValue(), value));
  }
  boolean isValid(TokenType type, String value) {
    return isValid(currentToken, type, value);
  }

  boolean isValidType() {
    return definedClasses.contains(currentToken.getValue());
  }

  /**
   * Write the token line if matches the given type and value
   */
  public void writeIfValid(JackToken token, TokenType type, String value) throws IOException {
    if (isValid(type, value)) {
      writeLine(token.toXmlElement());
      advance();
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " of value " +
          value + "expected");
    }
  }
  public void writeIfValid(JackToken token, TokenType type) throws IOException {
    if (token.type == type) {
      writeLine(token.toXmlElement());
      advance();
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " expected");
    }
  }
  public void writeIfValid(TokenType type, String value) throws IOException {
    if (isValid(type, value)) {
      writeLine(currentToken.toXmlElement());
      advance();
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " of value " +
          value + "expected");
    }
  }
  public void writeIfValid(TokenType type) throws IOException {
    if (currentToken.type == type) {
      writeLine(currentToken.toXmlElement());
      advance();
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " expected");
    }
  }

  boolean isSubroutine() {
    return isValid(TokenType.KEYWORD, "constructor") ||
        isValid(TokenType.KEYWORD, "function") ||
        isValid(TokenType.KEYWORD, "method");
  }
}
