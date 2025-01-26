package me.lordnumberwang.vmcompiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
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
  int loopCtr = 0; //used to generate a unique ID for a loop to avoid collisions
  String className;
  String funcName;
  int returnCtr = 0; //counter for return addresses within functions
  BufferedWriter outfileWriter;

  VmCodeWriter() {}

  /**
   * @param commands -
   * @param className - String of class name (taken from name of current file)
   * @param outfile -
   */
  @Override
  public void write(Stream<VmCommand> commands, String className, Path outfile) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(outfile,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND)) {
      //Create file if not present, append otherwise
      this.className = className;
      //Normalize to Camelcase
      this.funcName = ""; //default to empty function
      this.outfileWriter = writer;

      commands.forEach(cmd -> {
        //line by line parser code
        try {
          switch(cmd.command) {
            case C_ARITHMETIC -> writeArithmetic(cmd);
            case C_POP, C_PUSH -> writePushPop(cmd);
            case C_LABEL, C_GOTO, C_IF -> writeBranching(cmd);
            case C_CALL -> writeCall(cmd);
            case C_FUNCTION -> writeFunction(cmd);
            case C_RETURN -> writeReturn(cmd);
            //case C_CALL, C_FUNCTION, C_GOTO, C_IF, C_LABEL, C_RETURN -> writeIf();
          }
        } catch (IOException e) {
          throw new RuntimeException("Unable to write line: " + cmd);
        }
      });
    }
  }

  void writeArithmetic(VmCommand command) throws IOException {
    if (command.args.length != 1) {
      throw new IOException("Attempted to process invalid command");
    }
    write("//" + command); //write out comment form of string command
    switch (command.args[0].toLowerCase()) {
      case "add":
        writeAdd();
        break;
      case "sub":
        writeSub();
        break;
      case "neg":
        write("@0");
        write("A=M-1");
        write("M=-M");
        break;
      case "eq":
        writeEq();
        break;
      case "gt":
        writeGt();
        break;
      case "lt":
        writeLt();
        break;
      case "and":
        writeAnd();
        break;
      case "or":
        writeOr();
        break;
      case "not":
        write("@0");
        write("A=M-1");
        write("M=!M");
        break;
      case "default":
        write( "FAIL");
        //dun goofed
    }
  }

  void writePushPop(VmCommand command) throws IOException {
    String[] args = command.args;
    if (args.length != 2) {
      throw new IOException("Attempted to process invalid command - "
          + "wrong number of arguments to a pop/push command");
    }
    write("//" + command);
    try {
      int bufferIndex = Integer.parseInt(args[1]);
      if (command.command == Command.C_PUSH) {
        writePush(args[0].toLowerCase(), bufferIndex);
      } else if (command.command == Command.C_POP) {
        writePop(args[0].toLowerCase(), bufferIndex);
      }
    } catch (NumberFormatException e) {
      // Handle unparsable integer value
      throw new IOException("Invalid index passed to push/pop: " + args[1]);
    }
  }

  /*
    Utility function alternative to writing push/pop with command, segment and index only.
   */
  void writePushPop(String cmd, String segment, int index) throws IOException {
    if (Objects.equals(cmd, "push")) {
      writePush(segment, index);
    } else if (Objects.equals(cmd, "pop")) {
      writePop(segment, index);
    } else {
      throw new IOException("Passed invalid push/pop command from compiler");
    }
  }

  void writeAdd() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
    write("@0");
    write("A=M-1");
    write("M=D+M");
  }

  void writeSub() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
    write("@0");
    write("A=M-1");
    write("M=M-D");
  }

  void writeEq() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
    write("@0");
    write("A=M-1");
    write("D=M-D");
    write("@VMLOOP" + loopCtr);
    write("D;JEQ");
    write("D=0");
    write("@VMLOOPEND" + loopCtr);
    write("0;JMP");
    write("(VMLOOP" + loopCtr + ")");
    write("D=-1");
    write("(VMLOOPEND" + loopCtr + ")");
    write("@0");
    write("A=M-1");
    write("M=D");
    loopCtr++;
  }

  void writeGt() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
    write("@0");
    write("A=M-1");
    write("D=M-D");
    write("@VMLOOP" + loopCtr);
    write("D;JGT");
    write("D=0");
    write("@VMLOOPEND" + loopCtr);
    write("0;JMP");
    write("(VMLOOP" + loopCtr + ")");
    write("D=-1");
    write("(VMLOOPEND" + loopCtr + ")");
    write("@0");
    write("A=M-1");
    write("M=D");
    loopCtr++;
  }

  void writeLt() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
    write("@0");
    write("A=M-1");
    write("D=M-D");
    write("@VMLOOP" + loopCtr);
    write("D;JLT");
    write("D=0");
    write("@VMLOOPEND" + loopCtr);
    write("0;JMP");
    write("(VMLOOP" + loopCtr + ")");
    write("D=-1");
    write("(VMLOOPEND" + loopCtr + ")");
    write("@0");
    write("A=M-1");
    write("M=D");
    loopCtr++;
  }

  void writeAnd() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
    write("@0");
    write("A=M-1");
    write("M=D&M");
  }

  void writeOr() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
    write("@0");
    write("A=M-1");
    write("M=D|M");
  }

  void writePush(String segment, int index) throws IOException {
    //https://www.coursera.org/learn/nand2tetris2/lecture/lqz8H/unit-1-5-vm-implementation-memory-segments
    switch (segment) {
      case "local":
        //based on pointer LCL (RAM[1])
        write("@"+index);
        write("D=A");
        write("@1");
        write("A=D+M");
        write("D=M");
        //set D to value to push
        pushToStack();
        break;
      case "argument":
        //based on pointer ARG (RAM[2])
        write("@"+index);
        write("D=A");
        write("@2");
        write("A=D+M");
        write("D=M");
        //set D to value to push
        pushToStack();
        break;
      case "this":
        //based on pointer THIS (RAM[3])
        write("@"+index);
        write("D=A");
        write("@3");
        write("A=D+M");
        write("D=M");
        pushToStack();
        break;
      case "that":
        //based on pointer THAT (RAM[4])
        write("@"+index);
        write("D=A");
        write("@4");
        write("A=D+M");
        write("D=M");
        pushToStack();
        break;
      case "constant":
        write("@"+index);
        write("D=A");
        pushToStack();
        break;
      case "static":
        //Register to assigned location of @file's name and '.i'
        write("@" + className + "." + index); //@file.index
        write("D=M");
        pushToStack();
        break;
      case "temp":
        //fixed 8 length segment. i.e. 5+i (where i max of 0-7)
        write("@" + (5+index));
        write("D=M");
        pushToStack();
        break;
      case "pointer":
        //base address of 'this'/'that' segment. i=0/1
        //if access pointer 0, gets THIS, if access pointer 1 get THAT.
        if (index > 1) {
          throw new IOException("Invalid (value>1) pointer location: " + index);
        }
        //*SP=THIS/THAT, SP++
        if (index == 0) {
          write("@3");
        } else if (index == 1) {
          write("@4");
        }
        write("D=M");
        pushToStack();
        break;
    }
  }

  void writePop(String segment, int index) throws IOException {
    switch (segment) {
      case "local":
        //based on pointer LCL (RAM[1])
        write("@"+index);
        write("D=A");
        //Update LCL to index+LCL value
        write("@1");
        write("M=D+M");

        //Set D to value from stack
        popFromStack();
        write("@1");
        write("A=M");
        write("M=D");

        //set LCL back to LCL-index
        write("@"+index);
        write("D=A");
        write("@1");
        write("M=M-D");
        break;
      case "argument":
        //based on pointer ARG (RAM[2])
        write("@"+index);
        write("D=A");
        //Update ARG to index+ARG value
        write("@2");
        write("M=D+M");

        //Set D to value from stack
        popFromStack();
        write("@2");
        write("A=M");
        write("M=D");

        //set ARG back to ARG-index
        write("@"+index);
        write("D=A");
        write("@2");
        write("M=M-D");
        break;
      case "this":
        //based on pointer THIS (RAM[3])
        write("@"+index);
        write("D=A");
        //Update THIS to index+ARG value
        write("@3");
        write("M=D+M");

        //Set D to value from stack
        popFromStack();
        write("@3");
        write("A=M");
        write("M=D");

        //set THIS back to THIS-index
        write("@"+index);
        write("D=A");
        write("@3");
        write("M=M-D");
        break;
      case "that":
        //based on pointer THAT (RAM[4])
        write("@"+index);
        write("D=A");
        //Update THIS to index+ARG value
        write("@4");
        write("M=D+M");

        //Set D to value from stack
        popFromStack();
        write("@4");
        write("A=M");
        write("M=D");

        //set THAT back to THAT-index
        write("@"+index);
        write("D=A");
        write("@4");
        write("M=M-D");
        break;
      case "constant":
        throw new IOException("Invalid command - attempt to pop to constant register");
      case "static":
        popFromStack();
        write("@" + className + "." + index);
        write("M=D");
        break;
      case "temp":
        popFromStack();
        write("@" + (5+index));
        write("M=D");
        break;
      case "pointer":
        if (index > 1) {
          throw new IOException("Invalid (value>1) pointer location: " + index);
        }
        //*SP=THIS/THAT, SP++
        popFromStack();
        if (index == 0) {
          write("@3");
        } else if (index == 1) {
          write("@4");
        }
        write("M=D");
        break;
    }
  }

  void writeBranching(VmCommand cmd) throws IOException {
    String[] args = cmd.args;
    if (args.length != 1) {
      throw new IOException("Wrong number of arguments to a branching command: ");
    }
    String label = funcName + "$" + args[0];
    write("//" + cmd);
    switch (cmd.command) {
      case Command.C_LABEL:
        write("(" + label + ")");
        break;
      case Command.C_GOTO:
        write("@"+label);
        write("0;JMP");
        break;
      case Command.C_IF:
        popFromStack(); //write to condition to D
        //if cond, jump to execute command just after label
        write("@"+label);
        write("D;JNE"); //jump if D != 0
        break;
    }
  }

  void writeFunction(VmCommand cmd) throws IOException {
    String[] args = cmd.args;
    // need function name and number of local vars
    if (args.length != 2) {
      throw new IOException("Wrong number of arguments to function call");
    }
    //this.returnCtr = 0; //reset call/return counter
    this.funcName = args[0]; //set funcName
    int nVars = Integer.parseInt(args[1]); //number of local vars
    // function fName nVars
    write("//" + cmd);

    // 1. Write label to function entry (Xxx.foo / className.funcName;
    String label = funcName;
    write("(" + label + ")");

    // 2. Push nVars amount of 0s.
    // Assumes we start SP=LCL (@0 = @1)
    write("D=0");
    for (int i=0; i<nVars; i++) {
      //Write constant 0 to LCL i
      pushToStack();
      //LCL pointer unchanged, SP incremented by nVars
    }
  }

  void writeCall(VmCommand cmd) throws IOException {
    String[] args = cmd.args;
    // need function name and number of arguments
    if (args.length != 2) {
      throw new IOException("Wrong number of arguments to function call");
    }
    String callee = args[0];
    int numArgs = Integer.parseInt(args[1]);

    // call fName nArgs
    write("//" + cmd);

    //1. Save caller frame: (return address, savedLCL, saved ARG, saved THIS, saved THAT)
    //Write return address label: className+"."+funcName+"$ret."+i => MyClass.currentFunc$ret.0
    write("@" + callee + "$ret." + returnCtr);
    write("D=A"); //get retAddr
    pushToStack(); //save retAddr
    write("@1"); //get LCL
    write("D=M");
    pushToStack(); //Save LCL
    write("@2"); //get ARG
    write("D=M");
    pushToStack(); //Save ARG
    write("@3");
    write("D=M");
    pushToStack(); //Save THIS
    write("@4");
    write("D=M");
    pushToStack(); //Save THAT
    //2. Set Arg pointer by SP-(5+numArgs)
    write("@5");
    write("D=A");
    write("@"+ numArgs);
    write("D=D+A"); //D=5+numArgs
    write("@0");
    write("D=M-D"); //D=SP-(5+nArgs)
    write("@2");
    write("M=D"); //ARG=SP-nArgs
    //3. Set LCL = SP
    write("@0");
    write("D=M");
    write("@1");
    write("M=D");
    //4. GOTO className.funcName label
    write("@" + callee);
    write("0;JMP");
    //5. write (className.funcName$ret.i) label for return address after call finishes
    write("(" + callee + "$ret." + returnCtr + ")");
    //6. Increment returnCtr
    this.returnCtr++;
  }

  void writeReturn(VmCommand cmd) throws IOException {
    String[] args = cmd.args;
    // need function name and number of arguments
    if (args.length != 0) {
      throw new IOException("Return should not receive arguments");
    }

    // return
    write("//" + cmd);
    // 1. Save LCL to temp variable endFrame (Gen Register 0 and 1)
    write("@1");
    write("D=M");
    write("@13"); //@13 General Register 0
    write("M=D"); //endFrame variable stored
    // 2. Get return address, save to Gen-1
    write("@5"); //const 5, D still endFrame value
    write("A=D-A"); // @(endFrame-5)
    write("D=M"); // D= return address
    write("@14"); //@14 Gen-1
    write("M=D"); //return address stored
    // 3. Reposition return value for caller, *ARG = pop()
    popFromStack(); //D = return value
    write("@2");
    write("A=M");
    write("M=D"); //calling writePushPop("pop", 0) less efficient due to int = 0
    // 4. Reposition SP=ARG+1
    write("D=A+1"); //A still = ARG value
    write("@0");
    write("M=D"); //Set SP to ARG+1
    // 5. Reposition THAT = *(endFrame-1)
    write("@13"); //@13 Gen-0
    write("A=M-1"); //@(endFrame-1)
    write("D=M"); // D = *(endFrame-1)
    write("@4"); //THAT
    write("M=D");
    // 6. Reposition THIS = *(endFrame-2)
    write("@2");
    write("D=A"); //D=2
    write("@13"); //@13 Gen-0
    write("A=M-D"); //@(endFrame-2)
    write("D=M"); // D=*(endFrame-2)
    write("@3"); //THIS
    write("M=D");
    // 7. Reposition ARG = *(endFrame-3)
    write("@3");
    write("D=A");
    write("@13"); //@13 Gen-0
    write("A=M-D"); //@(endFrame-3)
    write("D=M"); // D=*(endFrame-3)
    write("@2"); //ARG
    write("M=D");
    // 8. Reposition LCL = *(endFrame-4)
    write("@4");
    write("D=A");
    write("@13"); //@13 Gen-0
    write("A=M-D"); //@(endFrame-4)
    write("D=M"); // D=*(endFrame-4)
    write("@1"); //LCL
    write("M=D");
    // 9. FINALLY jump to retrAddr (goto retAddr => @GenReg1)
    write("@14"); //@Gen-1
    write("A=M");
    write("0;JMP"); //GOTO retAddr
  }

  /*
    Assumes D is set to the value you wish to push to the stack.
   */
  void pushToStack() throws IOException {
    write("@0");
    write("M=M+1");
    write("A=M-1");
    write("M=D");
  }

  /*
    Sets D to the top value of the stack
   */
  void popFromStack() throws IOException {
    write("@0");
    write("AM=M-1");
    write("D=M");
  }

  void write(String str) throws IOException {
    outfileWriter.write(str);
    outfileWriter.newLine();
  }
}
