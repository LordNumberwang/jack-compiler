package me.lordnumberwang;

import java.nio.file.Path;

public interface Compiler {
  //compiler generalization case needs work

  /**
   * Compile - Compiles a single input file to the specified output file
   * @param inputPath
   * @param outputPath
   */
  void compile(Path inputPath, Path outputPath);

  /**
   * isValidFiletype - Validation for whether the input file has the right extension
   * @param inputPath
   * @return
   */
  boolean isValidFiletype(Path inputPath);
}
