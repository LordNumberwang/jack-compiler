package me.lordnumberwang.languageapp;

import org.junit.jupiter.api.Test;
import me.lordnumberwang.languageapp.SayHello;
import java.io.IOException;

public class SayHelloTest {
  @Test
  public void testSayHello() throws IOException {
    SayHello.main(new String[]{"en"});
  }
}
