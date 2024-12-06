package me.lordnumberwang.vmcompiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import me.lordnumberwang.CodeWriter;
import java.nio.file.Files;
import me.lordnumberwang.vmcompiler.VmCommand.Command;

public class VmCodeWriter implements CodeWriter<VmCommand> {
  //handle generation of output
  // generate assembly code from parsed commands
  // args: output file / stream
  //Project 7 -
  //add, sub, neg, eq, gt, lt, and, or, not, pop/push segment i

  // project 8:
  // Branching and Function commands
  // label label, goto label, if-goto label
  // function functionName nVars, call functionName nArgs, return
  int loopCtr; //used to generate a unique ID for a loop to avoid collisions
  String fileName;

  VmCodeWriter() {
    loopCtr = 0;
  }

  /**
   * @param commands
   * @param outfile
   */
  @Override
  public void write(Stream<VmCommand> commands, Path outfile) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(outfile)) {
      Path outfileName = outfile.getName(outfile.getNameCount() - 1);
      this.fileName = outfileName.toString().split("[.]")[0];
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

  /**
   *
   * @param writer
   * @param command
   * @throws IOException
   */
  void writeArithmetic(BufferedWriter writer, VmCommand command) throws IOException {
    if (command.args.length != 1) {
      throw new IOException("Attempted to process invalid command");
    }
    writer.write("//" + command.toString()); //write out comment form of string command
    switch (command.args[0].toLowerCase()) {
      case "add":
        writeAdd(writer);
      case "sub":
        writeSub(writer);
      case "neg":
        writer.write("@0");
        writer.write("A=M-1");
        writer.write("M=-M");
      case "eq":
        writeEq(writer);
      case "gt":
        writeGt(writer);
      case "lt":
        writeLt(writer);
      case "and":
        writeAnd(writer);
      case "or":
        writeOr(writer);
      case "not":
        writer.write("@0");
        writer.write("A=M-1");
        writer.write("M=!M");
    }
  }

  /**
   *
   * @param writer -
   * @param command -
   * @throws IOException - if for some reason invalid.
   */
  void writePushPop(BufferedWriter writer, VmCommand command) throws IOException {
    String[] args = command.args;
    if (args.length != 2) {
      throw new IOException("Attempted to process invalid command - "
          + "wrong number of arguments to a pop/push command");
    }
    writer.write("//" + command.toString());
    try {
      int bufferIndex = Integer.parseInt(args[1]);
      if (command.command == Command.C_PUSH) {
        writePush(writer, args[0].toLowerCase(), bufferIndex);
      } else if (command.command == Command.C_POP) {
        writePop(writer, args[0].toLowerCase(), bufferIndex);
      }
    } catch (NumberFormatException e) {
      // Handle unparsable integer value
      throw new IOException("Invalid index passed to push/pop: " + args[1]);
    }
  }

  void writeAdd(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("AM=M-1");
    writer.write("D=M");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("M=D+M");
  }

  void writeSub(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("AM=M-1");
    writer.write("D=M");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("M=M-D");
  }

  void writeEq(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("AM=M-1");
    writer.write("D=M");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("D=M-D");
    writer.write("@VMLOOP" + loopCtr);
    writer.write("D;JEQ");
    writer.write("D=0");
    writer.write("@VMLOOPEND" + loopCtr);
    writer.write("0;JMP");
    writer.write("(VMLOOP" + loopCtr + ")");
    writer.write("D=-1");
    writer.write("(VMLOOPEND" + loopCtr + ")");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("M=D");
    loopCtr++;
  }

  void writeGt(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("AM=M-1");
    writer.write("D=M");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("D=M-D");
    writer.write("@VMLOOP" + loopCtr);
    writer.write("D;JGT");
    writer.write("D=0");
    writer.write("@VMLOOPEND" + loopCtr);
    writer.write("0;JMP");
    writer.write("(VMLOOP" + loopCtr + ")");
    writer.write("D=-1");
    writer.write("(VMLOOPEND" + loopCtr + ")");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("M=D");
    loopCtr++;
  }

  void writeLt(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("AM=M-1");
    writer.write("D=M");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("D=M-D");
    writer.write("@VMLOOP" + loopCtr);
    writer.write("D;JLT");
    writer.write("D=0");
    writer.write("@VMLOOPEND" + loopCtr);
    writer.write("0;JMP");
    writer.write("(VMLOOP" + loopCtr + ")");
    writer.write("D=-1");
    writer.write("(VMLOOPEND" + loopCtr + ")");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("M=D");
    loopCtr++;
  }

  void writeAnd(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("AM=M-1");
    writer.write("D=M");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("M=D&M");
  }

  void writeOr(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("AM=M-1");
    writer.write("D=M");
    writer.write("@0");
    writer.write("A=M-1");
    writer.write("M=D|M");
  }

  void writePush(BufferedWriter writer, String segment, int index) throws IOException {
    //https://www.coursera.org/learn/nand2tetris2/lecture/lqz8H/unit-1-5-vm-implementation-memory-segments
    switch (segment) {
      case "local":
        //based on pointer LCL (RAM[1])
        writer.write("@"+index);
        writer.write("D=A");
        writer.write("@1");
        writer.write("A=D+M");
        writer.write("D=M");
        //set D to value to push
        pushToStack(writer);
      case "argument":
        //based on pointer ARG (RAM[2])
        writer.write("@"+index);
        writer.write("D=A");
        writer.write("@2");
        writer.write("A=D+M");
        writer.write("D=M");
        //set D to value to push
        pushToStack(writer);
      case "this":
        //based on pointer THIS (RAM[3])
        writer.write("@"+index);
        writer.write("D=A");
        writer.write("@3");
        writer.write("A=D+M");
        writer.write("D=M");
        pushToStack(writer);
      case "that":
        //based on pointer THAT (RAM[4])
        writer.write("@"+index);
        writer.write("D=A");
        writer.write("@4");
        writer.write("A=D+M");
        writer.write("D=M");
        pushToStack(writer);
      case "constant":
        writer.write("@"+index);
        writer.write("D=A");
        pushToStack(writer);
      case "static":
        //Register to assigned location of @file's name and '.i'
        writer.write("@" + fileName + "." + index); //@file.index
        writer.write("D=M");
        pushToStack(writer);
      case "temp":
        //fixed 8 length segment. i.e. 5+i (where i max of 0-7)
        writer.write("@" + (5+index));
        writer.write("D=M");
        pushToStack(writer);
      case "pointer":
        //base address of 'this'/'that' segment. i=0/1
        //if access pointer 0, gets THIS, if access pointer 1 get THAT.
        if (index > 1) {
          throw new IOException("Invalid (value>1) pointer location: " + index);
        }
        //*SP=THIS/THAT, SP++
        if (index == 0) {
          writer.write("@3");
        } else if (index == 1) {
          writer.write("@4");
        }
        writer.write("A=M");
        writer.write("D=M");
        pushToStack(writer);
    }
  }

  void writePop(BufferedWriter writer, String segment, int index) throws IOException {
    switch (segment) {
      case "local":
        //based on pointer LCL (RAM[1])
        popFromStack(writer);
        writer.write("@1");
        //TODO figure out how to keep D set (from popFromStack), yet move to LCL+index.
//        writer.write("@"+index);
//        writer.write("D=A");
//        writer.write("@1");
//        writer.write("A=D+M");
//        writer.write("D=M");


        writer.write("M=D");
      case "argument":
        //based on pointer ARG (RAM[2])
        popFromStack(writer);
      case "this":
        //based on pointer THIS (RAM[3])
        popFromStack(writer);
      case "that":
        //based on pointer THAT (RAM[4])
        popFromStack(writer);
      case "constant":
        throw new IOException("Invalid command - attempt to pop to constant register");
      case "static":
        popFromStack(writer);
        writer.write("@" + fileName + "." + index);
        writer.write("M=D");
      case "temp":
        popFromStack(writer);
        writer.write("@" + (5+index));
        writer.write("M=D");
      case "pointer":
        if (index > 1) {
          throw new IOException("Invalid (value>1) pointer location: " + index);
        }
        //*SP=THIS/THAT, SP++
        popFromStack(writer);
        if (index == 0) {
          writer.write("@3");
        } else if (index == 1) {
          writer.write("@4");
        }
        writer.write("M=D");
    }
  }

  void pushToStack(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("M=M+1");
    writer.write("A=M-1");
    writer.write("M=D");
  }

  void popFromStack(BufferedWriter writer) throws IOException {
    writer.write("@0");
    writer.write("M=M+1");
    writer.write("A=M-1");
    writer.write("D=M");
  }
}
