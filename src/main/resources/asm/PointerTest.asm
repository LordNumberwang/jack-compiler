//C_PUSH constant 3030
@3030
D=A
@0
M=M+1
A=M-1
M=D
//C_POP pointer 0
@0
AM=M-1
D=M
@3
M=D
//C_PUSH constant 3040
@3040
D=A
@0
M=M+1
A=M-1
M=D
//C_POP pointer 1
@0
AM=M-1
D=M
@4
M=D
//C_PUSH constant 32
@32
D=A
@0
M=M+1
A=M-1
M=D
//C_POP this 2
@2
D=A
@3
M=D+M
@0
AM=M-1
D=M
@3
A=M
M=D
@2
D=A
@3
M=M-D
//C_PUSH constant 46
@46
D=A
@0
M=M+1
A=M-1
M=D
//C_POP that 6
@6
D=A
@4
M=D+M
@0
AM=M-1
D=M
@4
A=M
M=D
@6
D=A
@4
M=M-D
//C_PUSH pointer 0
@3
D=M
@0
M=M+1
A=M-1
M=D
//C_PUSH pointer 1
@4
D=M
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC add
@0
AM=M-1
D=M
@0
A=M-1
M=D+M
//C_PUSH this 2
@2
D=A
@3
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC sub
@0
AM=M-1
D=M
@0
A=M-1
M=M-D
//C_PUSH that 6
@6
D=A
@4
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC add
@0
AM=M-1
D=M
@0
A=M-1
M=D+M
