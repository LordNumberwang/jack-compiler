package me.lordnumberwang.vmcompiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import me.lordnumberwang.Parser;
import org.junit.jupiter.api.*;

public class VmCodeWriterTest {
  VmCodeWriter subject = new VmCodeWriter();
  String testFile;
  static final String testOutfile="test.asm";

  @BeforeAll
  public static void setUp() throws IOException {
    try {
      FileWriter fileWriter = new FileWriter(testOutfile);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  public void tearDown() {
    // Clean up test file after each test
    try {
      Files.deleteIfExists(Paths.get(testFile));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BeforeEach
  public void setupTest() {
    this.subject = new VmCodeWriter();
    this.testFile = "H:\\My Documents\\Learning\\Java\\IntelliJWorkspace\\nandToTetris2\\JackCompiler\\src\\test\\resources\\vm\\SimpleAdd.vm";
  }

  @Test
  public void testParseLine() throws IOException {
    Path filePath1 = Path.of(testFile);
    Path outfile = Path.of(testFile);
    Stream<String> lines = Files.lines(filePath1);
    Parser parser = new VmParser();
    Stream<VmCommand> commands = parser.parse(lines);
    subject.write(commands, "SimpleAdd", outfile);
    VmCommand[] cmds = commands.toArray(VmCommand[]::new);
  }

  @Test
  public void testWriteArithmetic() throws IOException {
    // subject.writeArithmetic();
  }

  @Test
  public void testWritePushPop() throws IOException {
    // subject.writePushPop();
  }
}