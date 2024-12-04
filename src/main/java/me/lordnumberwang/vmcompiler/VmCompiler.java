package me.lordnumberwang.vmcompiler;

import me.lordnumberwang.Compiler;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Objects;
import me.lordnumberwang.*;

public class VmCompiler implements Compiler {
  private final Parser<VmCommand> parser;
  private final CodeWriter<VmCommand> codeWriter;

  VmCompiler(Parser<VmCommand> parser, CodeWriter<VmCommand> codeWriter) {
    this.parser = parser;
    this.codeWriter = codeWriter;
  }
  /**
   *
   * @param args - List of arguments intended to contain filepaths to .vm files:
   *                space delimited string of "/path/to/filename.vm /path/to/another/filename2.vm"
   * Side effects:
   *   Compiled output files returned in "/path/to/filename.asm /path/to/another/filename2.asm"
   */
  public static void main(String[] args) {
    /*
    construct parser to handle input file
    construct codewriter to handle output file
    march through input file and parse each line and generate code from it.
    */

    int filesProcessed = 0;
    Parser<VmCommand> parser = new VmParser();
    CodeWriter<VmCommand> codeWriter = new VmCodeWriter();
    VmCompiler compiler = new VmCompiler(parser, codeWriter);

    for (String aFilename : args) {
      //Processing a single file
      Path inputPath = Path.of(aFilename);
      if (compiler.isValidFiletype(inputPath)) {
        compiler.compile(inputPath);
        filesProcessed++;
      }
    }
    System.out.printf(filesProcessed + " file(s) processed.");
  }

  /**
   * Compile: Compiles a single file to the given output file path
   */
  @Override
  public void compile(Path inputPath, Path outputPath) {
    try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
      codeWriter.write(parser.parse(reader.lines()), outputPath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write file");
    }
  }

  public void compile(Path inputPath) {
    compile(inputPath, getOutputPath(inputPath));
  }

  @Override
  public boolean isValidFiletype(Path inputPath) {
    //Check if .vm file...
    if (Files.isReadable(inputPath)) {
      Path infile = inputPath.getName(inputPath.getNameCount() - 1);
      String[] infileName = infile.toString().split("[.]");
      if (infileName.length < 2 || !Objects.equals(infileName[1], "vm")) {
        System.out.println("Invalid filetype for: " + inputPath.getFileName());
        return false;
      }
      return true;
    } else {
      System.out.println("Unable to access file: " + inputPath.getFileName());
      return false;
    }
  }

  private Path getOutputPath(Path inputPath) {
    Path infile = inputPath.getName(inputPath.getNameCount() - 1);
    String[] infileName = infile.toString().split("[.]");
    return inputPath.getParent().resolve(infileName[0] + ".asm");
  }
}