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
  String cmd_string;

  VmCommand(Command command, String[] args, String cmd_str) {
    this.command = command;
    this.args = args;
    this.cmd_string = cmd_str;
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

  @Override
  public String toString() {
    return this.cmd_string;
  }
}
