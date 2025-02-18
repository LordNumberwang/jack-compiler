package me.lordnumberwang.jackcompiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class JackAnalyzer {
  private final CompilationEngine compilationEngine;
  private final JackTokenizer jackTokenizer;

  public JackAnalyzer(JackTokenizer tokenizer, CompilationEngine compilationEngine) {
    this.jackTokenizer = tokenizer;
    this.compilationEngine = compilationEngine;
  }

  /**
   *
   * @param args - List of arguments intended to contain filepaths to .jack files:
   *             Either a single .jack file, or a directory with .jack files
   * Side effects:
   *   Compiled output files returned in "/path/to/filename.asm /path/to/another/filename2.asm"
   */
  public static void main(String[] args) {
    /*
      construct tokenizer to handle input file
      construct compilation engine to handle generating XML
      march through input file and parse each line and generate code from it.
    */
    // e.g. "/input" directory or "/input/myfile.jack"
    if (args.length < 1) {
      System.out.println("Error - no input directory given");
      return;
    }
    int filesProcessed = 0;
    System.out.println("Compiling Jack code to .XML intermediate...");

    JackTokenizer tokenizer = new JackTokenizer();
    CompilationEngine compilationEngine = new CompilationEngine();
    JackAnalyzer analyzer = new JackAnalyzer(tokenizer, compilationEngine);
    try {
      List<Path> jackFiles = getJackFiles(args[0]);
      for (Path jackFile : jackFiles) {
        System.out.println("\n");
        System.out.println("Compiling file: " + jackFile.toString());

        String outName = jackFile.getName(jackFile.getNameCount()-1).
            toString().replace(".jack",".xml");
        Path outFile = Paths.get("src", "main", "resources", "output", outName);

        //Analyze file
        analyzer.analyze(jackFile, outFile);
        filesProcessed++;
        System.out.printf(" - " + filesProcessed + " file(s) processed.");
      }
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  private static List<Path> getJackFiles(String inputPathString) throws IOException {
    URL url = JackCompiler.class.getClassLoader().getResource(inputPathString);
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

    //Handle .jack single file
    if (Files.isRegularFile(inPath)) {
      //Check if single file is .jack
      if (inPath.toString().endsWith(".jack")) {
        return List.of(inPath);
      } else {
        throw new IllegalArgumentException("Passed invalid filetype: " + inPath);
      }
    }

    //Handle directory
    if (Files.isDirectory(inPath)) {
      try (Stream<Path> walk = Files.walk(inPath)) {
        List<Path> jackFiles = walk
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".jack"))
            .toList();

        if (jackFiles.isEmpty()) {
          throw new IllegalArgumentException("No Jack files found in directory: " + inPath);
        }
        return jackFiles;
      }
    }
    throw new IllegalArgumentException("Path is neither file nor directory: " + inPath);
  }

  public void analyze(Path inputPath, Path outputPath) {
    try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
      Stream<JackToken> tokens = jackTokenizer.tokenize(reader.lines());
      compilationEngine.compileToXML(tokens, outputPath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write file");
    }
  }
}
