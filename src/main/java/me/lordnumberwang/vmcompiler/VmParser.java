package me.lordnumberwang.vmcompiler;

import java.util.Arrays;
import java.util.stream.Stream;
import me.lordnumberwang.Parser;
import me.lordnumberwang.vmcompiler.VmCommand.Command;

public class VmParser implements Parser<VmCommand> {
  Stream<String> inStream;
  String filename;

  VmParser() {}

  Command commandType(String commandString) {
    // return one of C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL
    // return constant representing type of command. C-Arithmetic returned for all arithmetic/logical commands
    return switch (commandString) {
      case "push" -> Command.C_PUSH;
      case "pop" -> Command.C_POP;
      case "add" -> Command.C_ARITHMETIC; //writing out each case for clarity
      case "sub" -> Command.C_ARITHMETIC;
      case "neg" -> Command.C_ARITHMETIC;
      case "eq" -> Command.C_ARITHMETIC;
      case "gt" -> Command.C_ARITHMETIC;
      case "lt" -> Command.C_ARITHMETIC;
      case "and" -> Command.C_ARITHMETIC;
      case "or" -> Command.C_ARITHMETIC;
      case "not" -> Command.C_ARITHMETIC;
      case "goto" -> Command.C_GOTO;
      case "if-goto" -> Command.C_IF;
      case "label" -> Command.C_LABEL;
      case "call" -> Command.C_CALL;
      case "function" -> Command.C_FUNCTION;
      case "return" -> Command.C_RETURN;
      default ->
          throw new IllegalArgumentException("Unknown command type: " + commandString);
    };
  }

  @Override
  public Stream<VmCommand> parse(Stream<String> inStream) {
    //Filter whitespace only, blank and comment lines
    return inStream
        .map(String::trim)
        .filter(str -> !str.matches("^//.*"))
        .filter(str -> !str.isBlank())
        .map(this::parseLine);
  }

  private VmCommand parseLine(String line) {
    line = line.split("//")[0]; //ignore inline comments
    line = line.trim(); //strip preceding spaces
    String[] tokens = line.split(" ");
    Command cmd = commandType(tokens[0]);

    String[] args = getCommandArgs(cmd, tokens);
    if (tokens.length > 1) {
      args = Arrays.copyOfRange(tokens,1,tokens.length);
    }
    return new VmCommand(cmd, args, line);
  }

  private String[] getCommandArgs(Command command, String[] tokens) {
    return switch (command) {
      case Command.C_ARITHMETIC ->
          Arrays.copyOfRange(tokens, 0, tokens.length); //includes function name
      case Command.C_PUSH, Command.C_POP ->
          Arrays.copyOfRange(tokens, 1, tokens.length);
      //goto label, if-goto label, label label
      case Command.C_LABEL, Command.C_GOTO, Command.C_IF ->
          Arrays.copyOfRange(tokens, 1,tokens.length);
      //call fName nArgs, function fName nVars
      case Command.C_CALL, Command.C_FUNCTION ->
          Arrays.copyOfRange(tokens, 1,tokens.length);
      case Command.C_RETURN -> new String[0]; //no args
      default -> throw new IllegalArgumentException("Illegal operation " + command.toString());
    };
  }
}
