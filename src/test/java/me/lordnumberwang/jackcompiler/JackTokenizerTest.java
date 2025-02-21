package me.lordnumberwang.jackcompiler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.lordnumberwang.jackcompiler.JackToken.KeyWord;
import me.lordnumberwang.jackcompiler.JackToken.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class JackTokenizerTest {
  JackTokenizer subject;
  Stream<String> lines;

  @BeforeEach
  public void setupTests() {
    this.subject = new JackTokenizer();
  }

  @Test
  public void testRemoveComments() {
    String line1 = "Test thing";
    String line2 = "   stuff up front //comment line";
    String line3 = "//whole line comment";
    String line4 = "inline /* block comment here */ comment test";
    String line5 = "starting new /* block comment erase";
    String line6 = "in block delete this all";
    String line7 = "and this too */ block comment ended   ";


    String translated1 = subject.removeComments(line1);
    String translated2 = subject.removeComments(line2);
    String translated3 = subject.removeComments(line3);
    String translated4 = subject.removeComments(line4);
    String translated5 = subject.removeComments(line5);
    String translated6 = subject.removeComments(line6);
    String translated7 = subject.removeComments(line7);

    assertEquals("Test thing", translated1);
    //Check trim of preceding and trailing spaces
    assertEquals("stuff up front",translated2);
    assertEquals("",translated3);
    assertEquals("inline  comment test",translated4);
    assertEquals("starting new",translated5);
    assertEquals("",translated6);
    assertEquals("block comment ended",translated7);
  }

  @Nested
  @DisplayName("String Constant Parsing")
  class StringParseTests {
    @Test
    @DisplayName("Sample String")
    public void testTokenizeLine() {
      String lineWithString = "\"a string\"";
      List<JackToken> tokens = subject.tokenizeLine(lineWithString);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.STRING_CONST, testToken.type);
      assertEquals("a string", testToken.stringValue);
    }

    @Test
    @DisplayName("Blank String")
    public void testTokenizeBlank() {
      String lineWithString = "\"\"";
      List<JackToken> tokens = subject.tokenizeLine(lineWithString);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.STRING_CONST, testToken.type);
      assertEquals("", testToken.stringValue);
    }

    @Test
    @DisplayName("Invalid String")
    public void testInvalidString() {
      String lineWithString = "\"candleja";

      Exception exception = assertThrows(RuntimeException.class,
          () -> subject.tokenizeLine(lineWithString));
      assertTrue(exception.getMessage().contains("Unpaired"),
          "Unpaired \", invalid string in parsing: \"candleja");
    }
  }

  @Nested
  @DisplayName("Symbol Parsing")
  class SymbolParseTests {

    @Test
    @DisplayName("Simple {")
    public void testLeftBraceParse() {
      String lineWithSymbol = "{";
      List<JackToken> tokens = subject.tokenizeLine(lineWithSymbol);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.SYMBOL, testToken.type);
      assertEquals('{', testToken.charValue);
      assertEquals("{", testToken.getValue());
    }

    @Test
    @DisplayName("Simple |")
    public void testPipeParse() {
      String lineWithSymbol = "|";
      List<JackToken> tokens = subject.tokenizeLine(lineWithSymbol);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.SYMBOL, testToken.type);
      assertEquals('|', testToken.charValue);
      assertEquals("|", testToken.getValue());
    }
  }

  @Nested
  @DisplayName("Symbol Parsing")
  class IntegerParseTests {
    @Test
    @DisplayName("of 0")
    public void testZero() {
      String line = "0";
      List<JackToken> tokens = subject.tokenizeLine(line);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.INT_CONST, testToken.type);
      assertEquals(0, testToken.intValue);
      assertEquals("0", testToken.getValue());
    }

    @Test
    @DisplayName("of 0123")
    public void testLeadingZeroError() {
      String line = "0123";

      Exception exception = assertThrows(RuntimeException.class,
          () -> subject.tokenizeLine(line));
      assertTrue(exception.getMessage().contains("Leading"),
          "Leading 0 on a number detected: 0123");
    }

    @Test
    @DisplayName("of 123")
    public void test123() {
      String line = "123";
      List<JackToken> tokens = subject.tokenizeLine(line);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.INT_CONST, testToken.type);
      assertEquals(123, testToken.intValue);
      assertEquals("123", testToken.getValue());
    }
  }

  @Nested
  @DisplayName("Keyword Parsing")
  class KeywordParseTests {

    @Test
    @DisplayName("class")
    public void testKeywordClassParse() {
      String line = "class";
      List<JackToken> tokens = subject.tokenizeLine(line);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.KEYWORD, testToken.type);
      assertEquals(KeyWord.CLASS, testToken.keyWord);
      assertEquals("CLASS", testToken.getValue());
    }

    @Test
    @DisplayName("static")
    public void testKeywordStaticParse() {
      String line = "static";
      List<JackToken> tokens = subject.tokenizeLine(line);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.KEYWORD, testToken.type);
      assertEquals(KeyWord.STATIC, testToken.keyWord);
      assertEquals("STATIC", testToken.getValue());
    }
  }

  @Nested
  @DisplayName("Identifier Parsing")
  class identifierParseTests {

    @Test
    @DisplayName("myVar")
    public void testIdentifierMyvarParse() {
      //TODO WRITE THESE TESTS
      String line = "myVar";
      List<JackToken> tokens = subject.tokenizeLine(line);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.IDENTIFIER, testToken.type);
      assertEquals("myVar", testToken.stringValue);
      assertEquals("myVar", testToken.getValue());
    }

    @Test
    @DisplayName("myClass")
    public void testIdentifierMyClassParse() {
      //TODO WRITE THESE TESTS
      String line = "myClass";
      List<JackToken> tokens = subject.tokenizeLine(line);
      assertEquals(1, tokens.size());
      JackToken testToken = tokens.getFirst();
      assertEquals(TokenType.IDENTIFIER, testToken.type);
      assertEquals("myClass", testToken.stringValue);
      assertEquals("myClass", testToken.getValue());
    }
  }

  @Nested
  @DisplayName("Full Line Testing")
  class IntegrationTests {
    @Test
    @DisplayName("Sample Class Parse")
    public void testLineParse() {
      String line = "class Main { var int x = 5; }";
      List<JackToken> expectedTokens = List.of(
          new JackToken(TokenType.KEYWORD, KeyWord.CLASS),
          new JackToken(TokenType.IDENTIFIER, "Main"),
          new JackToken(TokenType.SYMBOL, '{'),
          new JackToken(TokenType.KEYWORD, KeyWord.VAR),
          new JackToken(TokenType.KEYWORD, KeyWord.INT),
          new JackToken(TokenType.IDENTIFIER, "x"),
          new JackToken(TokenType.SYMBOL, '='),
          new JackToken(TokenType.INT_CONST, 5),
          new JackToken(TokenType.SYMBOL, ';'),
          new JackToken(TokenType.SYMBOL, '}')
      );

      List<JackToken> tokens = subject.tokenizeLine(line);

      assertEquals(expectedTokens.size(), tokens.size());
      for (int i = 0; i < tokens.size(); i++) {
        assertEquals(expectedTokens.get(i).type, tokens.get(i).type);
        assertEquals(expectedTokens.get(i).getValue(), tokens.get(i).getValue());
      }
    }
  }

  @Nested
  @DisplayName("Full File Testing")
  class SampleFileParse {
    @Test
    @DisplayName("Parse sample file")
    public void testFileParse() throws IOException {
      Path testFile = Paths.get("src","test","resources","jack","sample.jack");
      try (BufferedReader reader = Files.newBufferedReader(testFile)) {
        Stream<String> lines = reader.lines();
        Stream<JackToken> tokens = subject.tokenize(lines);
        List<JackToken> tokensList = tokens.toList();

        int x = tokensList.size();

      } catch (IOException e) {
        throw new IOException("Failed to read test file.");
      }
    }
  }
}
