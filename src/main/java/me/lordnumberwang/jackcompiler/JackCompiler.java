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
import me.lordnumberwang.CodeWriter;
import me.lordnumberwang.Parser;

public class JackCompiler {
  private final CompilationEngine compilationEngine;
  private final JackTokenizer jackTokenizer;

  public JackCompiler(JackTokenizer jackTokenizer, CompilationEngine compilationEngine) {
    this.jackTokenizer = jackTokenizer;
    this.compilationEngine = compilationEngine;
  }

  public static void main(String[] args) {
    /*
      construct parser to handle input file
      construct codewriter to handle output file
      march through input file and parse each line and generate code from it.
    */
    // e.g. "/input" directory or "/input/myfile.jack"
    if (args.length < 1) {
      System.out.println("Error - no input directory given");
      return;
    }
    int filesProcessed = 0;
    JackTokenizer tokenizer = new JackTokenizer();
    CompilationEngine compilationEngine = new CompilationEngine();
    JackCompiler compiler = new JackCompiler(tokenizer, compilationEngine);

    try {
      List<Path> jackFiles = getJackFiles(args[0]);
      for (Path jackFile : jackFiles) {
        System.out.println("\n");
        System.out.println("Compiling file: " + jackFile.toString());

        String outName = jackFile.getName(jackFile.getNameCount()-1).
            toString().replace(".jack",".xml");
        Path outFile = Paths.get("src", "main", "resources", "output", outName);

        //Compile file here
        System.out.println("Compiling Jack code once I finish project 9+...");
        //compiler.compile(vmFile, outFile);
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

  public void compile(Path inputPath, Path outputPath) {
    try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
      Stream<JackToken> tokens = jackTokenizer.tokenize(reader.lines());
      //TODO in next project - handle compiler
      // write(tokens, ..., outputPath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write file");
    }
  }
}
