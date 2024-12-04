// push constant 7
addr=segPointer+7
\*SP=\*addr
SP++
// push constant 8
addr=segPointer+8
\*SP=\*addr
SP++
// add
segPointer
@RAM[0]
