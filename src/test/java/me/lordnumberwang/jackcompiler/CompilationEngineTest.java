package me.lordnumberwang.jackcompiler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;


public class CompilationEngineTest {

  CompilationEngine subject;
  Stream<JackToken> tokens;

  @BeforeEach
  void setupTokenizer() {
    JackTokenizer tokenizer = new JackTokenizer();
  }

  void setupTokens() {

  }


}
