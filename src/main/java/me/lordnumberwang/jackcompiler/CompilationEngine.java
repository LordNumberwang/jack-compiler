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
  static Set<String> keywordConstant = Set.of("true", "false", "null", "this");
  static Set<String> opSymbols = Set.of("+","-","*","/","&","|","<",">","=");
  BufferedWriter writer;
  Stream<JackToken> tokens;
  Queue<JackToken> buffer;
  JackToken currentToken;
  Iterator<JackToken> tokenReader;
  boolean tokensExhausted;
  int indent; //indentation level
  List<String> definedClasses;
  List<String> validTypes;
  int lineCount;

  public CompilationEngine() {}

  public void setDefinedClasses(List<String> classStrings) {
    definedClasses = new ArrayList<>(classStrings);
    validTypes = new ArrayList<>(defaultClasses);
    validTypes.addAll(classStrings);
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
      lineCount = 0;
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
    // Testing write in general
    if (getCurrentToken().type != TokenType.KEYWORD || getCurrentToken().keyWord != KeyWord.CLASS) {
      throw new RuntimeException("Invalid file - file must start with class declaration.");
    } else {
      writeLine("<class>");
      indent++;
      // class <name> {
      writeToken(); //<keyword> class </keyword>
      writeIfValid(TokenType.IDENTIFIER); //className validation later
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

    writeToken(); //field/static detected
    if (isValidType()) {
      writeToken(); //type: className or defined classes
    } else {
      throw new IllegalArgumentException("Received invalid type: " + currentToken.getValue());
    }
    writeIfValid(TokenType.IDENTIFIER); //varName

    while(isValid(TokenType.SYMBOL,",")) {
      writeToken(); // ","
      writeIfValid(TokenType.IDENTIFIER); //varName
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

    writeToken(); //const/func/method detected on entry
    if (isValid(TokenType.KEYWORD,"void") ||
        (isValidType())) {
      writeToken(); //void or type
    } else {
      throw new IllegalArgumentException("Received invalid type for subroutine: "
          + currentToken.getValue());
    }
    writeIfValid(TokenType.IDENTIFIER); // subroutineName
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
   * Grammar: ( (type varName) (',' varName)* )?
   */
  void compileParameterList() throws IOException {
    //0 or 1 case: ( (type varName) (, type varName)* )?
    writeLine("<parameterList>");

    if (isValidType()) {
      indent++;
      //Params present case: (type varName) (, type varName)*
      writeToken(); //type
      writeIfValid(TokenType.IDENTIFIER); //varName
      while(isValid(TokenType.SYMBOL,",")) {
        writeToken(); //","
        if (isValidType()) {
          writeToken(); //type
        } else {
          throw new IllegalArgumentException("Received invalid type for parameter list: "
              + currentToken.getValue());
        }
        writeIfValid(TokenType.IDENTIFIER); //varName
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
    while (isValid(TokenType.KEYWORD, "var")) {
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

    writeToken(); //var keyword
    if (isValidType()) {
      writeToken(); //type
    } else {
      throw new IllegalArgumentException("Received invalid type for variable declaration: "
          + currentToken.getValue());
    }
    writeIfValid(TokenType.IDENTIFIER); //varName
    while(isValid(TokenType.SYMBOL,",")) {
      writeToken(); //","
      writeIfValid(TokenType.IDENTIFIER); //varName*
    }
    writeIfValid(TokenType.SYMBOL, ";");

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
   * Compile let statement:
   * Grammar: 'let' varName ('[' expression ']')? '=' expression ';'
   * Assumes currentToken is set to let
   */
  void compileLet() throws IOException {
    writeLine("<letStatement>");
    indent++;

    writeToken(); //let keyword
    writeIfValid(TokenType.IDENTIFIER); //varName

    //optional  [ expr ]
    if (isValid(TokenType.SYMBOL, "[")) {
      writeToken(); //write '['
      compileExpression();
      writeIfValid(TokenType.SYMBOL, "]");
    }

    // = expr ;
    writeIfValid(TokenType.SYMBOL, "=");
    compileExpression();
    writeIfValid(TokenType.SYMBOL, ";");

    indent--;
    writeLine("</letStatement>");
  }

  /**
   * Compile If statement (possible training else clause).
   * Grammar: 'if' '(' expression ')' '{' statements '}' (else '{' statements '}')?
   */
  void compileIf() throws IOException {
    writeLine("<ifStatement>");
    indent++;

    // if ( expression )
    writeToken(); //write 'if'
    writeIfValid(TokenType.SYMBOL, "(");
    compileExpression();
    writeIfValid(TokenType.SYMBOL, ")");

    //'{' statements '}'
    writeIfValid(TokenType.SYMBOL, "{");
    compileStatements();
    writeIfValid(TokenType.SYMBOL, "}");

    //optional else '{' statements '}'
    if (isValid(TokenType.KEYWORD, "else")) {
      writeToken(); //else
      writeIfValid(TokenType.SYMBOL, "{");
      compileStatements();
      writeIfValid(TokenType.SYMBOL, "}");
    }

    indent--;
    writeLine("</ifStatement>");
  }

  /**
   * Compile while statement
   * Grammar: 'while' '(' expression ')' '{' statements '}'
   */
  void compileWhile() throws IOException {
    writeLine("<whileStatement>");
    indent++;

    // while ( expression )
    writeToken(); //while keyword
    writeIfValid(TokenType.SYMBOL, "(");

    compileExpression();
    writeIfValid(TokenType.SYMBOL, ")");

    //'{' statements '}'
    writeIfValid(TokenType.SYMBOL, "{");
    compileStatements();
    writeIfValid(TokenType.SYMBOL, "}");

    indent--;
    writeLine("</whileStatement>");
  }

  /**
   * Compile do statement
   * Grammar: 'do' subroutineCall ';'
   */
  void compileDo() throws IOException {
    writeLine("<doStatement>");
    indent++;

    writeToken(); //write 'do'

    // subroutineCall grammar:
    //    (className | varName) '.' subroutineName '(' exprList ') |
    //    subroutineName '(' exprList ')
    // lookahead for '.'
    if (isValid(nextToken(), TokenType.SYMBOL,".")) {
      //in optional (className | varName) '.' case
      //TODO PROJECT 11: validate presence in varName OR className
      //      if (definedClasses.contains(currentToken.getValue())) {
      //        writeLine(currentToken.toXmlElement()); //className
      //        advance();
      //      } else if (varName table check) {
      //      } else { throw error }
      writeIfValid(TokenType.IDENTIFIER); //className/varName validate in table later
      writeToken(); //write '.' (validated via lookahead)
    }
    // shared grammar for both cases: "subroutineName ( expressionList )"
    if (isValidSubroutine()) {
      //above will later validate subRoutine table presence
      writeToken(); //write subroutineName

      writeIfValid(TokenType.SYMBOL,"(");
      compileExpressionList(); //expressionList
      writeIfValid(TokenType.SYMBOL,")");
    } else {
      throw new IllegalArgumentException("Invalid subRoutine referenced - "
        + currentToken.getValue());
    }

    writeIfValid(TokenType.SYMBOL, ";");

    indent--;
    writeLine("</doStatement>");
  }

  /**
   * Compile return statement
   * Grammar: 'return' expression? ';'
   */
  void compileReturn() throws IOException {
    writeLine("<returnStatement>");
    indent++;

    writeToken(); //write 'return'
    //optional expression?
    if (!((currentToken.type == TokenType.SYMBOL) && (Objects.equals(currentToken.getValue(),";")))) {
      compileExpression();
    }
    writeIfValid(TokenType.SYMBOL, ";");

    indent--;
    writeLine("</returnStatement>");
  }

  /**
   * Compile Expression
   * FOR NOW - only term handled. no (op term)*
   * Grammar: term (op term)*
   */
  void compileExpression() throws IOException {
    writeLine("<expression>");
    indent++;

    compileTerm();

    while((currentToken.type == TokenType.SYMBOL) &&
        (opSymbols.contains(currentToken.getValue()))) {
      writeToken(); //Write the operation
      compileTerm();
    }

    indent--;
    writeLine("</expression>");
  }

  /**
   * Compile Term.
   * If currentToken is identifier => must distinguish between variable,
   *                                  array entry, or subroutine call.
   * Single look-ahead token is one of [ ( or . characters.
   * Assumes currentToken passed isTerm validation.
   */
  void compileTerm() throws IOException {
    writeLine("<term>");
    indent++;

    switch(currentToken.type) {
      case INT_CONST, STRING_CONST:
        writeToken(); //write constant
        break;
      case KEYWORD:
        if (keywordConstant.contains(currentToken.getValue())) {
          writeToken(); //write Keyword constant true/false/null/this
        } else {
          throw new IllegalArgumentException("Invalid keyword constant in term - "
              + currentToken.getValue());
        }
        break;
      case SYMBOL:
        switch(currentToken.getValue()) {
          case "-","~":
            //Unary op + term case
            writeToken(); // "-" or "~" unaryOp
            compileTerm();
            break;
          case "(":
            //( expression )
            writeToken(); // (
            compileExpression();
            writeIfValid(TokenType.SYMBOL,")");
            break;
          default:
            throw new IllegalArgumentException("Invalid symbol in term - "
                + currentToken.getValue());
        }
        break;
      case IDENTIFIER:
        //Lookahead to determine cases of: varName, varName[expression], subroutineCall
        if (!(nextToken().type == TokenType.SYMBOL &&
            Set.of("[", "(", ".").contains(nextToken().getValue()))) {
          //Standard varName case assumed
          writeToken();
        } else {
          switch (nextToken().getValue()) {
            case "[":
              // varName[expression] case
              writeToken(); //varName
              writeToken(); //[
              compileExpression();
              writeIfValid(TokenType.SYMBOL,"]");
              break;
            case "(":
              // subroutineName (exprList)
              if (isValidSubroutine()) {
                //above will later validate subRoutine table presence
                writeToken(); //write subroutineName
                writeToken(); // (
                compileExpressionList(); //expressionList
                writeIfValid(TokenType.SYMBOL,")");
              } else {
                throw new IllegalArgumentException("Invalid subRoutine referenced - "
                    + currentToken.getValue());
              }
              break;
            case ".":
              // (className|varName) '.' subroutineName (exprList)
              writeToken(); //className/varName validate in table later
              writeToken(); //write '.' (validated via lookahead)
              if (isValidSubroutine()) {
                //above will later validate subRoutine table presence
                writeToken(); //write subroutineName
                writeToken(); // (
                compileExpressionList(); //expressionList
                writeIfValid(TokenType.SYMBOL,")");
              } else {
                throw new IllegalArgumentException("Invalid subRoutine referenced - "
                    + currentToken.getValue());
              }
              break;
          }
        }
    }

    indent--;
    writeLine("</term>");
  }

  /**
   * Compile Expression List - compile comma separated list of expressions (may be empty)
   * Grammar: (expression ("," expression)* )?
   */
  void compileExpressionList() throws IOException {
    writeLine("<expressionList>");
    // (expression (',' expression)*) )?
    if (!isValid(TokenType.SYMBOL,")")) {
      indent++;
      compileExpression(); //expression
      while(isValid(TokenType.SYMBOL,",")) {
        writeToken(); // ","
        compileExpression(); //expression
      }
      indent--;
    }
    writeLine("</expressionList>");
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
      lineCount++;
      return;
    }
    loadNextToken();
    if (!buffer.isEmpty()) {
      currentToken = buffer.poll();
      lineCount++;
    } else {
      currentToken = null;
    }
  }

  public void loadNextToken() {
    if (!tokensExhausted) {
      if (tokenReader.hasNext()) {
        buffer.add(tokenReader.next());
      } else {
        tokensExhausted = true;
      }
    }
  }

  public JackToken getCurrentToken() {
    return this.currentToken;
  }

  public JackToken nextToken() {
    if (!buffer.isEmpty()) {
      return buffer.peek();
    } else {
      loadNextToken();
      if (tokensExhausted) {
        return null;
      } else {
        return buffer.peek();
      }
    }
  }

  public void writeLine(String input) throws IOException {
    writer.write( " ".repeat(indent*2) + input);
    writer.newLine();
  }

  /**
   * Validate the incoming token
   * @param type of token
   * @param value of token in String form
   * @return true/false if valid/invalid.
   */
  boolean isValid(JackToken token, TokenType type, String value) {
    return (token.type == type &&
        Objects.equals(token.getValue(), value));
  }
  boolean isValid(TokenType type, String value) {
    return isValid(currentToken, type, value);
  }

  boolean isValidType() {
    return ((currentToken.type == TokenType.KEYWORD) &&
          (Set.of(KeyWord.INT, KeyWord.CHAR, KeyWord.BOOLEAN).contains(currentToken.keyWord)))
        || (currentToken.type == TokenType.IDENTIFIER);
    // TODO later implement class table check on the identifier piece
    // && validTypes.contains(currentToken.getValue());
  }

  /**
   * Check if name is in subroutineName table
   * For now, checks only if identifier
   * @return true if valid
   */
  boolean isValidSubroutine() {
    return currentToken.type == TokenType.IDENTIFIER;
    //TODO check subroutine table later
    // && <subroutineNameTable>.contains(currentToken.getValue());
  }

  /**
   * Write the token line if matches the given type and value
   */
  public void writeIfValid(JackToken token, TokenType type, String value) throws IOException {
    if (isValid(type, value)) {
      writeToken(token);
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " of value " +
          value + "expected");
    }
  }
  public void writeIfValid(JackToken token, TokenType type) throws IOException {
    if (token.type == type) {
      writeToken(token);
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " expected");
    }
  }
  public void writeIfValid(TokenType type, String value) throws IOException {
    if (isValid(type, value)) {
      writeToken();
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " of value " +
          value + "expected");
    }
  }
  public void writeIfValid(TokenType type) throws IOException {
    if (currentToken.type == type) {
      writeToken();
    } else {
      throw new RuntimeException("Invalid syntax: " +
          JackToken.typeString(type) + " expected");
    }
  }

  public void writeToken(JackToken token) throws IOException {
    writeLine(token.toXmlElement());
    advance();
  }

  public void writeToken() throws IOException {
    writeLine(currentToken.toXmlElement());
    advance();
  }
}
