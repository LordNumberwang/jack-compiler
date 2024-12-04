package me.lordnumberwang;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Objects;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

  /**
   *
   * @param args - List of arguments intended to contain filepaths to .vm files
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    //input: space delimited string of "/path/to/filename.vm /path/toanother/filename2.vm"
    //output: files returned in "/path/to/filename.asm /path/toanother/filename2.asm"
    /*
    construct parser to handle input file
    construct codewriter to handle output file
    march through input file and parse each line and generate code from it.
     */

    int filesProcessed = 0;
    for (String aFilename : args) {
      //Processing a single file
      Path filePath = Path.of(aFilename);

      try (BufferedReader reader = Files.newBufferedReader(filePath)) {
        //Check if .vm file...
        Path infile = filePath.getName(filePath.getNameCount() - 1);
        String[] infileName = infile.toString().split("[.]");
        if (infileName.length < 2 || !Objects.equals(infileName[1], "vm")) {
          System.out.println("Invalid filetype for: " + filePath.getFileName());
        }

        System.out.println("Processing file: " + filePath.getFileName() + "...");
        System.out.println("Filepath: " + filePath.subpath(0, filePath.getNameCount() - 1));

        Path outfilePath = filePath.getParent().resolve(infileName[0] + ".asm");

        //Parser vmParser = new VmParser(reader.lines(), infileName[0]);
        //VmCodeWriter asmWriter = new VmCodeWriter(vmParser.outStream(), outfilePath);

        /*
        while ((line = reader.readLine()) != null) {
          // line operation
          System.out.println(line);
        }
        */
        //
        filesProcessed++;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.printf(filesProcessed + " file(s) processed.");
  }

}