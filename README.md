# jack-compiler
Jack Compiler for NAND to Tetris II

## Current commands:

### vmCompile

Compiles incoming .vm files (either a single file or a folder containing .vm files, including a Sys.vm with an init function)
Bootstrap code will be generated for any file that contains a Sys.vm file. 

**Usage**

> gradlew vmCompile -PPath="relative/path/from/src/resource"

Input: Add your files somewhere within the resources directory (/src/main/resources/*).
This file may be a single .vm file, or a folder containing multiple vm files.

Output: Compiled to a single .asm file at /src/resources/output/\<your project name>.asm
