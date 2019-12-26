package com.secant.c0compiler.assembly;

import static com.secant.c0compiler.assembly.OperandType.*;

public enum OPCode {
    NOP("nop", 0x00, NULL),
    BIPUSH("bipush", 0x01, BYTE),
    IPUSH("ipush", 0x02, INT32),
    POP("pop", 0x04, NULL),
    POP2("pop2", 0x05, NULL),
    POPN("popn", 0x06, INT32),
    DUP("dup", 0x07, NULL),
    DUP2("dup2", 0x08, NULL),
    LOADC("loadc", 0x09, INT16),
    LOADA("loada", 0x0a, INT16_AND_INT32),
    NEW("new", 0x0b, NULL),
    SNEW("snew", 0x0c, INT32),
    ILOAD("iload", 0x10, NULL),
    DLOAD("dload", 0x11, NULL),
    ALOAD("aload", 0x12, NULL),
    IALOAD("iaload", 0x18, NULL),
    DALOAD("daload", 0x19, NULL),
    AALOAD("aaload", 0x1a, NULL),
    ISTORE("istore", 0x20, NULL),
    DSTORE("dstore", 0x21, NULL),
    ASTORE("astore", 0x22, NULL),
    IASTORE("iastore", 0x28, NULL),
    DASTORE("dastore", 0x29, NULL),
    AASTORE("aastore", 0x2a, NULL),
    IADD("iadd", 0x30, NULL),
    DADD("dadd", 0x31, NULL),
    ISUB("isub", 0x34, NULL),
    DSUB("dsub", 0x35, NULL),
    IMUL("imul", 0x38, NULL),
    DMUL("dmul", 0x39, NULL),
    IDIV("idiv", 0x3c, NULL),
    DDIV("ddiv", 0x3d, NULL),
    INEG("ineg", 0x40, NULL),
    DNEG("dneg", 0x41, NULL),
    ICMP("icmp", 0x44, NULL),
    DCMP("dcmp", 0x45, NULL),
    I2D("i2d", 0x60, NULL),
    D2I("d2i", 0x61, NULL),
    I2C("i2c", 0x62, NULL),
    JMP("jmp", 0x70, INT16),
    JE("je", 0x71, INT16),
    JNE("jne", 0x72, INT16),
    JL("jl", 0x73, INT16),
    JGE("jge", 0x74, INT16),
    JG("jg", 0x75, INT16),
    JLE("jle", 0x76, INT16),
    CALL("call", 0x80, INT16),
    RET("ret", 0x88, NULL),
    IRET("iret", 0x89, NULL),
    DRET("dret", 0x8a, NULL),
    ARET("aret", 0x8b, NULL),
    IPRINT("iprint", 0xa0, NULL),
    DPRINT("dprint", 0xa1, NULL),
    CPRINT("cprint", 0xa2, NULL),
    SPRINT("sprint", 0xa3, NULL),
    PRINTL("printl", 0xaf, NULL),
    ISCAN("iscan", 0xb0, NULL),
    DSCAN("dscan", 0xb1, NULL),
    CSCAN("cscan", 0xb2, NULL);

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
