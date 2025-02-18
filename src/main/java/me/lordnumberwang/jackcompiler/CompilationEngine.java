package me.lordnumberwang.jackcompiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class CompilationEngine {
  public CompilationEngine() {}

  public void compileToXML(Stream<JackToken> tokens, Path outfile) {
    try (BufferedWriter writer = Files.newBufferedWriter(outfile,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      //TODO export to file

    } catch (IOException e) {
      throw new RuntimeException("Unable to write to XML file: " + outfile.getFileName().toString());
    }
    return;
  }

  /**
   * Create compile class.
   */
  private void compileClass() {
    //TODO
    return;
  }

  /**
   * Compile static variable or field declaration.
   */
  private void compileClassVarDec() {
    //TODO
    return;
  }

  /**
   * Compile complete method, function or constructor
   */
  private void compileSubroutineDec() {
    //TODO
    return;
  }

  /**
   * Compile a 0+ parameter list. Does not handle the enclosing '()'
   */
  private void compileParameterList() {
    //TODO
    return;
  }

  /**
   * Compile a subroutine's body
   */
  private void compileSubroutineBody() {
    //TODO
    return;
  }

  /**
   * Compile var declaration.
   */
  private void compileVarDec() {
    //TODO
    return;
  }

  /**
   * Compile a sequence of Statements, doesn't' handle enclosing {}
   */
  private void compileStatements() {
    //TODO
    return;
  }

  /**
   * Compile let statement
   */
  private void compileLet() {
    //TODO
    return;
  }

  /**
   * Compile If statement (possible training else clause).
   */
  private void compileIf() {
    //TODO
    return;
  }

  /**
   * Compile while statement
   */
  private void compileWhile() {
    //TODO
    return;
  }

  /**
   * Compile return statement
   */
  private void compileReturn() {
    //TODO
    return;
  }

  /**
   * Compile Expression
   */
  private void compileExpression() {
    //TODO
    return;
  }

  /**
   * Compile Term.
   * If currentToken is identifier => must distinguish between variable,
   *                                  array entry, or subroutine call.
   * Single look-ahead token is one of [ ( or . characters.
   */
  private void compileTerm() {
    //TODO
    return;
  }

  /**
   * Compile Expression List - compile comma separated list of expressions (may be empty)
   */
  private void compileExpressionList() {
    //TODO
    return;
  }
}
