package me.lordnumberwang.vmcompiler;

import java.nio.file.Path;
import java.util.stream.Stream;
import me.lordnumberwang.CodeWriter;

public class VmCodeWriter implements CodeWriter<VmCommand> {
  //handle generation of output
  // generate assembly code from parsed commands
  // args: output file / stream
  //for now: Project 7 -
  //add, sub, neg, eq, gt, lt, and, or, not, pop/push segment i

  // project 8:
  // Branching and Function commands
  // label label, goto label, if-goto label
  // function functionName nVars, call functionName nArgs, return

  VmCodeWriter() {}

  /**
   * @param transformedCode
   * @param outfile
   */
  @Override
  public void write(Stream<VmCommand> transformedCode, Path outfile) {
    //Path outFile = filePath.getParent().resolve(infileName[0]+".asm");
    //returns ...
  }

  void writeArithmetic(String command) {
    // most of work is here.
    // write to output file the assembly code that implements the given arithmetic command.
    //Suggested to write out command at top.

  }

  void writePushPop(String command, String segment, int index) {
    //NB: only for C_PUSH / C_POP
    // command is either push or pop
    // Project 7: pop segment i, push segment i
    //Suggested to write out // push/pop command at top.
  }
}
