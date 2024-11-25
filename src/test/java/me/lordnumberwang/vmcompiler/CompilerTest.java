package me.lordnumberwang.vmcompiler;

import java.io.IOException;
import me.lordnumberwang.Main;
import org.junit.jupiter.api.Test;

public class CompilerTest {
  @Test
  public void testInvalidFile() throws IOException {
    Main.main(new String[]{"en"});
  }
}