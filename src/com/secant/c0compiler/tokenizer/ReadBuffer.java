package com.secant.c0compiler.tokenizer;

import com.secant.c0compiler.errorhandling.CompilationError;
import com.secant.c0compiler.errorhandling.ErrorCode;

import java.io.*;

public class ReadBuffer {
    private String sourceFileName;
    private File file;
    private PushbackReader pushbackReader;

    public ReadBuffer(String sourceFileName) throws CompilationError {
        this.sourceFileName = sourceFileName;
        file = new File(this.sourceFileName);
        try {
            pushbackReader = new PushbackReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new CompilationError(Tokenizer.line, Tokenizer.row, ErrorCode.FILE_READ_ERROR);
        }
    }

    public char read() throws CompilationError {
        try {
            int result = pushbackReader.read();
            if (result != -1) {
                return (char) result;
            } else {
                return '\0';
            }
        } catch (IOException e) {
            throw new CompilationError(Tokenizer.line, Tokenizer.row, ErrorCode.FILE_READ_ERROR);
        }
    }

    public void unread(char c) throws CompilationError {
        try {
            pushbackReader.unread(c);
        } catch (IOException e) {
            throw new CompilationError(Tokenizer.line, Tokenizer.row, ErrorCode.FILE_READ_ERROR);
        }
    }

    public void close() throws CompilationError {
        try {
            pushbackReader.close();
        } catch (IOException e) {
            throw new CompilationError(Tokenizer.line, Tokenizer.row, ErrorCode.FILE_READ_ERROR);
        }
    }
}
