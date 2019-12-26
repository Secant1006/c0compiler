package com.secant.c0compiler.assembly;

public class Instruction {
    private OPCode opCode;
    private Object operand;

    public Instruction(OPCode opCode) {
        this.opCode = opCode;
        this.operand = 0;
    }

    public Instruction(OPCode opCode, Object operand) {
        this.opCode = opCode;
        this.operand = operand;
    }

    public OPCode getOpCode() {
        return this.opCode;
    }

    public Object getOperand() {
        return operand;
    }
}
