//C_PUSH constant 17
@17
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 17
@17
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC eq
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP0
D;JEQ
D=0
@VMLOOPEND0
0;JMP
(VMLOOP0)
D=-1
(VMLOOPEND0)
@0
A=M-1
M=D
//C_PUSH constant 17
@17
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 16
@16
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC eq
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP1
D;JEQ
D=0
@VMLOOPEND1
0;JMP
(VMLOOP1)
D=-1
(VMLOOPEND1)
@0
A=M-1
M=D
//C_PUSH constant 16
@16
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 17
@17
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC eq
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP2
D;JEQ
D=0
@VMLOOPEND2
0;JMP
(VMLOOP2)
D=-1
(VMLOOPEND2)
@0
A=M-1
M=D
//C_PUSH constant 892
@892
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 891
@891
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC lt
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP3
D;JLT
D=0
@VMLOOPEND3
0;JMP
(VMLOOP3)
D=-1
(VMLOOPEND3)
@0
A=M-1
M=D
//C_PUSH constant 891
@891
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 892
@892
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC lt
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP4
D;JLT
D=0
@VMLOOPEND4
0;JMP
(VMLOOP4)
D=-1
(VMLOOPEND4)
@0
A=M-1
M=D
//C_PUSH constant 891
@891
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 891
@891
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC lt
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP5
D;JLT
D=0
@VMLOOPEND5
0;JMP
(VMLOOP5)
D=-1
(VMLOOPEND5)
@0
A=M-1
M=D
//C_PUSH constant 32767
@32767
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 32766
@32766
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC gt
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP6
D;JGT
D=0
@VMLOOPEND6
0;JMP
(VMLOOP6)
D=-1
(VMLOOPEND6)
@0
A=M-1
M=D
//C_PUSH constant 32766
@32766
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 32767
@32767
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC gt
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP7
D;JGT
D=0
@VMLOOPEND7
0;JMP
(VMLOOP7)
D=-1
(VMLOOPEND7)
@0
A=M-1
M=D
//C_PUSH constant 32766
@32766
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 32766
@32766
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC gt
@0
AM=M-1
D=M
@0
A=M-1
D=M-D
@VMLOOP8
D;JGT
D=0
@VMLOOPEND8
0;JMP
(VMLOOP8)
D=-1
(VMLOOPEND8)
@0
A=M-1
M=D
//C_PUSH constant 57
@57
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 31
@31
D=A
@0
M=M+1
A=M-1
M=D
//C_PUSH constant 53
@53
D=A
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
//C_PUSH constant 112
@112
D=A
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
//C_ARITHMETIC neg
@0
A=M-1
M=-M
//C_ARITHMETIC and
@0
AM=M-1
D=M
@0
A=M-1
M=D&M
//C_PUSH constant 82
@82
D=A
@0
M=M+1
A=M-1
M=D
//C_ARITHMETIC or
@0
AM=M-1
D=M
@0
A=M-1
M=D|M
//C_ARITHMETIC not
@0
A=M-1
M=!M
