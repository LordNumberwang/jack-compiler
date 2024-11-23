package me.lordnumberwang.jackcompiler;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class CompilerTest {
  @Test
  public void testInvalidFile() throws IOException {
    Main.main(new String[]{"en"});
  }
}