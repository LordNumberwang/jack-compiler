//push constant 0
@0
D=A
@0
M=M+1
A=M-1
M=D
//pop local 0
@0
D=A
@1
M=D+M
@0
AM=M-1
D=M
@1
A=M
M=D
@0
D=A
@1
M=M-D
//label LOOP
(BasicLoop.$LOOP)
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
//push local 0
@0
D=A
@1
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
//pop local 0
@0
D=A
@1
M=D+M
@0
AM=M-1
D=M
@1
A=M
M=D
@0
D=A
@1
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
//if-goto LOOP
@0
AM=M-1
D=M
@BasicLoop.$LOOP
D;JNE
//push local 0
@0
D=A
@1
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
