package me.lordnumberwang.jackcompiler;

import static org.junit.jupiter.api.Assertions.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
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
}
