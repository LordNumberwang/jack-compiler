package me.lordnumberwang.vmcompiler;

import java.io.BufferedWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.lordnumberwang.*;
import java.util.*;
import me.lordnumberwang.Compiler;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.net.URISyntaxException;
import java.net.URL;
import me.lordnumberwang.vmcompiler.VmCommand.Command;

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
    // e.g. "/input" directory or "/input/myfile.vm"
    if (args.length < 1) {
      System.out.println("Error - no input directory given");
      return;
    }

    int filesProcessed = 0;
    Parser<VmCommand> parser = new VmParser();
    CodeWriter<VmCommand> codeWriter = new VmCodeWriter();
    VmCompiler compiler = new VmCompiler(parser, codeWriter);

    try {
      List<Path> vmFiles = getVmFiles(args[0]);
      Path firstFile = vmFiles.getFirst();
      String projectName;
      boolean bootstrap=false;
      //probably want to use resolveSibling()
      if (vmFiles.toArray().length > 1) {
        //Handle folder case
        projectName = firstFile.getName(firstFile.getNameCount()-2).toString();
        bootstrap = true;
      } else {
        //Handle single file case
        projectName = firstFile.getName(firstFile.getNameCount()-1).toString().replace(".vm","");
        if (projectName.equals("Sys")) {
          projectName = firstFile.getName(firstFile.getNameCount()-2).toString();
          bootstrap = true;
        }
      }
      Path outFile = Paths.get("src", "main", "resources", "output", projectName + ".asm");
      System.out.println("Compiling VM Files");

      if (bootstrap) {
        // Add bootstrap code
        compiler.bootstrap(outFile);
      }

      for (Path vmFile : vmFiles) {
        System.out.println("\n");
        System.out.println("Compiling file: " + vmFile.toString());
        //Compile file here
        compiler.compile(vmFile, outFile);
        filesProcessed++;
        System.out.printf(" - " + filesProcessed + " file(s) processed.");
      }
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  private static List<Path> getVmFiles(String inputPathString) throws IOException {
    URL url = VmCompiler.class.getClassLoader().getResource(inputPathString);
    if (url == null) {
      throw new IOException("Resource not found: " + inputPathString);
    }

    Path inPath;
    try {
      if (url.getProtocol().equals("jar")) {
        FileSystem fs = FileSystems.newFileSystem(url.toURI(), java.util.Collections.emptyMap());
        inPath = fs.getPath(inputPathString);
      } else {
        inPath = Paths.get(url.toURI());
      }
    } catch (URISyntaxException e) {
      throw new IOException("Unable to convert filepath URI: "+url.toString());
    }

    //Handle .VM single file
    if (Files.isRegularFile(inPath)) {
      //Check if single file is VM
      if (inPath.toString().endsWith(".vm")) {
        return List.of(inPath);
      } else {
        throw new IllegalArgumentException("Passed invalid filetype: " + inPath);
      }
    }

    //Handle directory
    if (Files.isDirectory(inPath)) {
      try (Stream<Path> walk = Files.walk(inPath)) {
        List<Path> vmFiles = walk
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".vm"))
            .toList();

        if (vmFiles.isEmpty()) {
          throw new IllegalArgumentException("No VM files found in directory: " + inPath);
        }
        return vmFiles;
      }
    }
    throw new IllegalArgumentException("Path is neither file nor directory: " + inPath);
  }

  private void bootstrap(Path outputPath) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      //overwrite existing files with new compiled content
      //Set SP=256
      codeWriter.write("@256", writer);
      codeWriter.write("D=A", writer);
      codeWriter.write("@0", writer);
      codeWriter.write("M=D", writer);
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
    //call Sys.init
    String line = "call Sys.init 0";
    String[] args = Arrays.copyOfRange(line.split(" "), 1 ,3);
    VmCommand callInit = new VmCommand(Command.C_CALL, args, line);
    Stream<VmCommand> bootCommand = Stream.of(callInit);
    codeWriter.write(bootCommand, "Sys", outputPath);
  }

  /**
   * Compile: Compiles a single file to the given output file path
   */
  @Override
  public void compile(Path inputPath, Path outputPath) {
    try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
      String className = inputPath.getName(inputPath.getNameCount() - 1).toString().split("[.]")[0];
      codeWriter.write(parser.parse(reader.lines()), className, outputPath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write file");
    }
  }
}