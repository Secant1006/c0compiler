package com.secant.c0compiler.assembly;

import static com.secant.c0compiler.assembly.OperandType.*;

public enum OPCode {
    NOP("NOP", 0x00, NULL),
    BIPUSH("BIPUSH", 0x01, BYTE),
    IPUSH("IPUSH", 0x02, INT32),
    POP("POP", 0x04, NULL),
    POP2("POP2", 0x05, NULL),
    POPN("POPN", 0x06, INT32),
    DUP("DUP", 0x07, NULL),
    DUP2("DUP2", 0x08, NULL),
    LOADC("LOADC", 0x09, INT16),
    LOADA("LOADA", 0x0a, INT16_AND_INT32),
    NEW("NEW", 0x0b, NULL),
    SNEW("SNEW", 0x0c, INT32),
    ILOAD("ILOAD", 0x10, NULL),
    DLOAD("DLOAD", 0x11, NULL),
    ALOAD("ALOAD", 0x12, NULL),
    IALOAD("IALOAD", 0x18, NULL),
    DALOAD("DALOAD", 0x19, NULL),
    AALOAD("AALOAD", 0x1a, NULL),
    ISTORE("ISTORE", 0x20, NULL),
    DSTORE("DSTORE", 0x21, NULL),
    ASTORE("ASTORE", 0x22, NULL),
    IASTORE("IASTORE", 0x28, NULL),
    DASTORE("DASTORE", 0x29, NULL),
    AASTORE("AASTORE", 0x2a, NULL),
    IADD("IADD", 0x30, NULL),
    DADD("DADD", 0x31, NULL),
    ISUB("ISUB", 0x34, NULL),
    DSUB("DSUB", 0x35, NULL),
    IMUL("IMUL", 0x38, NULL),
    DMUL("DMUL", 0x39, NULL),
    IDIV("IDIV", 0x3c, NULL),
    DDIV("DDIV", 0x3d, NULL),
    INEG("INEG", 0x40, NULL),
    DNEG("DNEG", 0x41, NULL),
    ICMP("ICMP", 0x44, NULL),
    DCMP("DCMP", 0x45, NULL),
    I2D("I2D", 0x60, NULL),
    D2I("D2I", 0x61, NULL),
    I2C("I2C", 0x62, NULL),
    JMP("JMP", 0x70, INT16),
    JE("JE", 0x71, INT16),
    JNE("JNE", 0x72, INT16),
    JL("JL", 0x73, INT16),
    JGE("JGE", 0x74, INT16),
    JG("JG", 0x75, INT16),
    JLE("JLE", 0x76, INT16),
    CALL("CALL", 0x80, INT16),
    RET("RET", 0x88, NULL),
    IRET("IRET", 0x89, NULL),
    DRET("DRET", 0x8a, NULL),
    ARET("ARET", 0x8b, NULL),
    IPRINT("IPRINT", 0xa0, NULL),
    DPRINT("DPRINT", 0xa1, NULL),
    CPIRNT("CPRINT", 0xa2, NULL),
    SPRINT("SPRINT", 0xa3, NULL),
    PRINTL("PRINTL", 0xaf, NULL),
    ISCAN("ISCAN", 0xb0, NULL),
    DSCAN("DSCAN", 0xb1, NULL),
    CSCAN("CSCAN", 0xb2, NULL);

    private final String name;
    private final byte value;
    private final OperandType operandType;

    OPCode(String name, int value, OperandType operandType) {
        this.name = name;
        this.value = (byte) value;
        this.operandType = operandType;
    }

    public String getName() {
        return this.name;
    }

    public byte getValue() {
        return this.value;
    }

    public OperandType getOperandType() {
        return this.operandType;
    }
}
