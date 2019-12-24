package com.secant.c0compiler.errorhandling;

public class CompilationError extends RuntimeException {
    private int line;
    private int row;
    private ErrorCode errorCode;

    public CompilationError(int line, int row, ErrorCode errorCode) {
        this.line = line;
        this.row = row;
        this.errorCode = errorCode;
    }

    public int getLine() {
        return line;
    }

    public int getRow() {
        return row;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
