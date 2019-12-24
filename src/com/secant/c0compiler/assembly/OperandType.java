package com.secant.c0compiler.assembly;

public enum OperandType {
    NULL(0),
    BYTE(1),
    INT32(4),
    INT16(2),
    INT16_AND_INT32(6);

    private final int size;

    OperandType(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }
}
