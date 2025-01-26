//push argument 1
@1
D=A
@2
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//pop pointer 1
@0
AM=M-1
D=M
@4
M=D
//push constant 0
@0
D=A
@0
M=M+1
A=M-1
M=D
//pop that 0
@0
D=A
@4
M=D+M
@0
AM=M-1
D=M
@4
A=M
M=D
@0
D=A
@4
M=M-D
//push constant 1
@1
D=A
@0
M=M+1
A=M-1
M=D
//pop that 1
@1
D=A
@4
M=D+M
@0
AM=M-1
D=M
@4
A=M
M=D
@1
D=A
@4
M=M-D
//push argument 0
@0
D=A
@2
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//push constant 2
@2
D=A
@0
M=M+1
A=M-1
M=D
//sub
@0
AM=M-1
D=M
@0
A=M-1
M=M-D
//pop argument 0
@0
D=A
@2
M=D+M
@0
AM=M-1
D=M
@2
A=M
M=D
@0
D=A
@2
M=M-D
//label LOOP
($LOOP)
//push argument 0
@0
D=A
@2
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//if-goto COMPUTE_ELEMENT
@0
AM=M-1
D=M
@$COMPUTE_ELEMENT
D;JNE
//goto END
@$END
0;JMP
//label COMPUTE_ELEMENT
($COMPUTE_ELEMENT)
//push that 0
@0
D=A
@4
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//push that 1
@1
D=A
@4
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//add
@0
AM=M-1
D=M
@0
A=M-1
M=D+M
//pop that 2
@2
D=A
@4
M=D+M
@0
AM=M-1
D=M
@4
A=M
M=D
@2
D=A
@4
M=M-D
//push pointer 1
@4
D=M
@0
M=M+1
A=M-1
M=D
//push constant 1
@1
D=A
@0
M=M+1
A=M-1
M=D
//add
@0
AM=M-1
D=M
@0
A=M-1
M=D+M
//pop pointer 1
@0
AM=M-1
D=M
@4
M=D
//push argument 0
@0
D=A
@2
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//push constant 1
@1
D=A
@0
M=M+1
A=M-1
M=D
//sub
@0
AM=M-1
D=M
@0
A=M-1
M=M-D
//pop argument 0
@0
D=A
@2
M=D+M
@0
AM=M-1
D=M
@2
A=M
M=D
@0
D=A
@2
M=M-D
//goto LOOP
@$LOOP
0;JMP
//label END
($END)
