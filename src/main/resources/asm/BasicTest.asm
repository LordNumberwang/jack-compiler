//C_PUSH constant 10
@10
D=A
@0
M=M+1
A=M-1
M=D
//C_POP local 0
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
//C_PUSH constant 21
@21
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 22
@22
D=A
@0
M=M+1
A=M-1
M=D
//C_POP argument 2
@2
D=A
@2
M=D+M
@0
AM=M-1
D=M
@2
A=M
M=D
@2
D=A
@2
M=M-D
//C_POP argument 1
@1
D=A
@2
M=D+M
@0
AM=M-1
D=M
@2
A=M
M=D
@1
D=A
@2
M=M-D
//C_PUSH constant 36
@36
D=A
@0
M=M+1
A=M-1
M=D
//C_POP this 6
@6
D=A
@3
M=D+M
@0
AM=M-1
D=M
@3
A=M
M=D
@6
D=A
@3
M=M-D
//C_PUSH constant 42
@42
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 45
@45
D=A
@0
M=M+1
A=M-1
M=D
//C_POP that 5
@5
D=A
@4
M=D+M
@0
AM=M-1
D=M
@4
A=M
M=D
@5
D=A
@4
M=M-D
//C_POP that 2
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
//C_PUSH constant 510
@510
D=A
@0
M=M+1
A=M-1
M=D
//C_POP temp 6
@0
AM=M-1
D=M
@11
M=D
//C_PUSH local 0
@0
D=A
@1
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//C_PUSH that 5
@5
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
//C_PUSH argument 1
@1
D=A
@2
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
//C_PUSH this 6
@6
D=A
@3
A=D+M
D=M
@0
M=M+1
A=M-1
M=D
//C_PUSH this 6
@6
D=A
@3
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
//C_ARITHMETIC sub
@0
AM=M-1
D=M
@0
A=M-1
M=M-D
//C_PUSH temp 6
@11
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
