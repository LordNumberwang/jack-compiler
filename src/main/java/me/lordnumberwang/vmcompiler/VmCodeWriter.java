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
    write(writer,"//" + command.toString()); //write out comment form of string command
    switch (command.args[0].toLowerCase()) {
      case "add":
        writeAdd(writer);
        break;
      case "sub":
        writeSub(writer);
        break;
      case "neg":
        write(writer,"@0");
        write(writer,"A=M-1");
        write(writer,"M=-M");
        break;
      case "eq":
        writeEq(writer);
        break;
      case "gt":
        writeGt(writer);
        break;
      case "lt":
        writeLt(writer);
        break;
      case "and":
        writeAnd(writer);
        break;
      case "or":
        writeOr(writer);
        break;
      case "not":
        write(writer,"@0");
        write(writer,"A=M-1");
        write(writer,"M=!M");
        break;
      case "default":
        write(writer, "FAIL");
        //dun goofed
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
    write(writer,"//" + command.toString());
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
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"M=D+M");
  }

  void writeSub(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"M=M-D");
  }

  void writeEq(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"D=M-D");
    write(writer,"@VMLOOP" + loopCtr);
    write(writer,"D;JEQ");
    write(writer,"D=0");
    write(writer,"@VMLOOPEND" + loopCtr);
    write(writer,"0;JMP");
    write(writer,"(VMLOOP" + loopCtr + ")");
    write(writer,"D=-1");
    write(writer,"(VMLOOPEND" + loopCtr + ")");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"M=D");
    loopCtr++;
  }

  void writeGt(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"D=M-D");
    write(writer,"@VMLOOP" + loopCtr);
    write(writer,"D;JGT");
    write(writer,"D=0");
    write(writer,"@VMLOOPEND" + loopCtr);
    write(writer,"0;JMP");
    write(writer,"(VMLOOP" + loopCtr + ")");
    write(writer,"D=-1");
    write(writer,"(VMLOOPEND" + loopCtr + ")");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"M=D");
    loopCtr++;
  }

  void writeLt(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"D=M-D");
    write(writer,"@VMLOOP" + loopCtr);
    write(writer,"D;JLT");
    write(writer,"D=0");
    write(writer,"@VMLOOPEND" + loopCtr);
    write(writer,"0;JMP");
    write(writer,"(VMLOOP" + loopCtr + ")");
    write(writer,"D=-1");
    write(writer,"(VMLOOPEND" + loopCtr + ")");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"M=D");
    loopCtr++;
  }

  void writeAnd(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"M=D&M");
  }

  void writeOr(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
    write(writer,"@0");
    write(writer,"A=M-1");
    write(writer,"M=D|M");
  }

  void writePush(BufferedWriter writer, String segment, int index) throws IOException {
    //https://www.coursera.org/learn/nand2tetris2/lecture/lqz8H/unit-1-5-vm-implementation-memory-segments
    switch (segment) {
      case "local":
        //based on pointer LCL (RAM[1])
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@1");
        write(writer,"A=D+M");
        write(writer,"D=M");
        //set D to value to push
        pushToStack(writer);
        break;
      case "argument":
        //based on pointer ARG (RAM[2])
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@2");
        write(writer,"A=D+M");
        write(writer,"D=M");
        //set D to value to push
        pushToStack(writer);
        break;
      case "this":
        //based on pointer THIS (RAM[3])
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@3");
        write(writer,"A=D+M");
        write(writer,"D=M");
        pushToStack(writer);
        break;
      case "that":
        //based on pointer THAT (RAM[4])
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@4");
        write(writer,"A=D+M");
        write(writer,"D=M");
        pushToStack(writer);
        break;
      case "constant":
        write(writer,"@"+index);
        write(writer,"D=A");
        pushToStack(writer);
        break;
      case "static":
        //Register to assigned location of @file's name and '.i'
        write(writer,"@" + fileName + "." + index); //@file.index
        write(writer,"D=M");
        pushToStack(writer);
        break;
      case "temp":
        //fixed 8 length segment. i.e. 5+i (where i max of 0-7)
        write(writer,"@" + (5+index));
        write(writer,"D=M");
        pushToStack(writer);
        break;
      case "pointer":
        //base address of 'this'/'that' segment. i=0/1
        //if access pointer 0, gets THIS, if access pointer 1 get THAT.
        if (index > 1) {
          throw new IOException("Invalid (value>1) pointer location: " + index);
        }
        //*SP=THIS/THAT, SP++
        if (index == 0) {
          write(writer,"@3");
        } else if (index == 1) {
          write(writer,"@4");
        }
        write(writer,"D=M");
        pushToStack(writer);
        break;
    }
  }

  void writePop(BufferedWriter writer, String segment, int index) throws IOException {
    switch (segment) {
      case "local":
        //based on pointer LCL (RAM[1])
        write(writer,"@"+index);
        write(writer,"D=A");
        //Update LCL to index+LCL value
        write(writer,"@1");
        write(writer,"M=D+M");

        //Set D to value from stack
        popFromStack(writer);
        write(writer,"@1");
        write(writer,"A=M");
        write(writer,"M=D");

        //set LCL back to LCL-index
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@1");
        write(writer,"M=M-D");
        break;
      case "argument":
        //based on pointer ARG (RAM[2])
        write(writer,"@"+index);
        write(writer,"D=A");
        //Update ARG to index+ARG value
        write(writer,"@2");
        write(writer,"M=D+M");

        //Set D to value from stack
        popFromStack(writer);
        write(writer,"@2");
        write(writer,"A=M");
        write(writer,"M=D");

        //set ARG back to ARG-index
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@2");
        write(writer,"M=M-D");
        break;
      case "this":
        //based on pointer THIS (RAM[3])
        write(writer,"@"+index);
        write(writer,"D=A");
        //Update THIS to index+ARG value
        write(writer,"@3");
        write(writer,"M=D+M");

        //Set D to value from stack
        popFromStack(writer);
        write(writer,"@3");
        write(writer,"A=M");
        write(writer,"M=D");

        //set THIS back to THIS-index
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@3");
        write(writer,"M=M-D");
        break;
      case "that":
        //based on pointer THAT (RAM[4])
        write(writer,"@"+index);
        write(writer,"D=A");
        //Update THIS to index+ARG value
        write(writer,"@4");
        write(writer,"M=D+M");

        //Set D to value from stack
        popFromStack(writer);
        write(writer,"@4");
        write(writer,"A=M");
        write(writer,"M=D");

        //set THAT back to THAT-index
        write(writer,"@"+index);
        write(writer,"D=A");
        write(writer,"@4");
        write(writer,"M=M-D");
        break;
      case "constant":
        throw new IOException("Invalid command - attempt to pop to constant register");
      case "static":
        popFromStack(writer);
        write(writer,"@" + fileName + "." + index);
        write(writer,"M=D");
        break;
      case "temp":
        popFromStack(writer);
        write(writer,"@" + (5+index));
        write(writer,"M=D");
        break;
      case "pointer":
        if (index > 1) {
          throw new IOException("Invalid (value>1) pointer location: " + index);
        }
        //*SP=THIS/THAT, SP++
        popFromStack(writer);
        if (index == 0) {
          write(writer,"@3");
        } else if (index == 1) {
          write(writer,"@4");
        }
        write(writer,"M=D");
        break;
    }
  }

  void pushToStack(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"M=M+1");
    write(writer,"A=M-1");
    write(writer,"M=D");
  }

  void popFromStack(BufferedWriter writer) throws IOException {
    write(writer,"@0");
    write(writer,"AM=M-1");
    write(writer,"D=M");
  }

  void write(BufferedWriter writer, String str) throws IOException {
    writer.write(str);
    writer.newLine();
  }
}
