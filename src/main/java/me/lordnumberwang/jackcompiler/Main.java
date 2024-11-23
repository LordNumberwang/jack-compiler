package me.lordnumberwang.jackcompiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

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
      //String[] filePath = aFilename.split("/");
      System.out.println(filePath.toString());
      System.out.println(filePath.getFileName());

      // try (FileReader file = new FileReader())
      /*
      try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
          // line operation
        }
        filesProcessed++;
      } catch (IOException e) {
        e.printStackTrace();
      }
      */
    }
    System.out.printf(filesProcessed + " file(s) processed.");
   }
}