package me.lordnumberwang.vmcompiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import me.lordnumberwang.CodeWriter;
import java.nio.file.Files;

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
   * @param commands
   * @param outfile
   */
  @Override
  public void write(Stream<VmCommand> commands, Path outfile) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(outfile)) {
      commands.forEach(cmd -> {
        //line by line parser code
        try {
          switch(cmd.command) {
            case C_ARITHMETIC -> writeArithmetic(writer, cmd);
            case C_POP, C_PUSH -> writePushPop(writer, cmd);
            //case C_CALL, C_FUNCTION, C_GOTO, C_IF, C_LABEL, C_RETURN -> writeIf();
          }
        } catch (IOException e) {
          throw new RuntimeException("Unable to write line: " + cmd.toString());
        }
      });
    }
  }

  void writeArithmetic(BufferedWriter writer, VmCommand command) throws IOException {
    if (command.args[0].length() != 1) {
      throw new IOException("Attempted to process invalid command");
    }
    String operation = command.args[0].toLowerCase();
    String[] args = Arrays.copyOfRange(command.args, 1, command.args.length);
    writer.write("//" + command.toString()); //write out comment form of string command
    switch (operation) {
      case "add":
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=M");
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=D+M");
        writer.write("M=D");
        writer.write("@0");
        writer.write("M=M+1");
      case "sub":
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=M");
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=D-M");
        writer.write("M=D");
        writer.write("@0");
        writer.write("M=M+1");
      case "neg":
        writer.write("@0");
        writer.write("A=M-1");
        writer.write("M=-M");
      case "eq":
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=M");
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=D-M");
        //FYI save 'true' as -1 a.k.a 11111(16 1s), 'false' as 0 (16 0s)
        //use JEQ jump condition and set it
//        writer.write("@0");
//        writer.write("M=M+1");
      case "gt":
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=M");
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=D-M");
        //FYI save 'true' as -1 a.k.a 11111(16 1s), 'false' as 0 (16 0s)
        //use JGT jump condition
      case "lt":
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=M");
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=D-M");
        //FYI save 'true' as -1 a.k.a 11111(16 1s), 'false' as 0 (16 0s)
        // use LT jump condition
      case "and":
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=M");
        writer.write("@0");
        writer.write("AM=M-1");

        //writer.write("D=D-M");
        //how to handle interaction of two values
        //FYI save 'true' as -1 a.k.a 11111(16 1s), 'false' as 0 (16 0s)
        //handle with some combinatorial logic?
      case "or":
        writer.write("@0");
        writer.write("AM=M-1");
        writer.write("D=M");
        writer.write("@0");
        writer.write("AM=M-1");
        //
        //FYI save 'true' as -1 a.k.a 11111(16 1s), 'false' as 0 (16 0s)
      case "not":
        writer.write("@0");
        writer.write("A=M-1");
        //writer.write("M=????"); //store calculation
        //FYI save 'true' as -1 a.k.a 11111(16 1s), 'false' as 0 (16 0s)
    }
  }

  void writePushPop(BufferedWriter writer, VmCommand command) throws IOException {
    writer.write("//" + command.toString());
    //String command, String segment, int index
    // NB: only for C_PUSH / C_POP
    // command is either push or pop
    // Project 7: pop segment i, push segment i
    //Suggested to write out // push/pop command at top
  }
}
