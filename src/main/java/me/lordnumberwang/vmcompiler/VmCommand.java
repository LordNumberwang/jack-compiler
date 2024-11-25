package me.lordnumberwang.vmcompiler;

public class VmCommand {
  public enum Command {
    C_ARITHMETIC,
    C_PUSH, C_POP,
    C_LABEL, C_GOTO,
    C_IF, C_FUNCTION,
    C_RETURN, C_CALL
  }
  Command command;
  String[] args;

  VmCommand(Command command, String[] args) {
    this.command = command;
    this.args = args;
  }

  public Command getCommand() {
    return command;
  }

  public String[] getArgs() {
    return args;
  }

  public void setCommand(Command command) {
    this.command = command;
  }

  public void setArgs(String[] args) {
    this.args = args;
  }
}
