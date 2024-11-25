package me.lordnumberwang.vmcompiler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import me.lordnumberwang.Parser;
import me.lordnumberwang.vmcompiler.VmCommand.Command;
import org.junit.jupiter.api.*;

public class VmParserTest {
  Parser<VmCommand> subject;
  String testFile1;

  @BeforeEach
  public void setupTest() {
    this.subject = new VmParser();
    this.testFile1 = "H:\\My Documents\\Learning\\Java\\IntelliJWorkspace\\nandToTetris2\\JackCompiler\\src\\test\\resources\\vm\\SimpleAdd.vm";

  }

  @Test
  public void testInvalidFile() throws IOException {

  }

  @Test
  public void testParseLine() throws IOException {
    Path filePath1 = Path.of(testFile1);
    Stream<String> lines = Files.lines(filePath1);
    Stream<VmCommand> commands = subject.parse(lines);
    VmCommand[] cmds = commands.toArray(VmCommand[]::new);
    assertEquals(cmds[0].command, Command.C_PUSH);
    assertArrayEquals(cmds[0].args,new String[] { "constant","7" });
    assertEquals(cmds[1].command, Command.C_PUSH);
    assertArrayEquals(cmds[1].args,new String[] { "constant","8" });
    assertEquals(cmds[2].command, Command.C_ARITHMETIC);
    assertArrayEquals(cmds[2].args,new String[] { "add" } );
  }
}
