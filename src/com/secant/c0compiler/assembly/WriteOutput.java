package com.secant.c0compiler.assembly;

import com.secant.c0compiler.errorhandling.CompilationError;

import java.io.*;

import static com.secant.c0compiler.assembly.OperandType.*;
import static com.secant.c0compiler.console.Arguments.*;
import static com.secant.c0compiler.errorhandling.ErrorCode.*;

public class WriteOutput {
    private static int mode;
    private static RandomAccessFile writer;
    private static int currentIndex;
    private static int state;

    public static void initializeWriter() throws CompilationError {
        mode = getMode();
        try {
            if (!new File(getOutputFileName()).createNewFile()) {
                throw new CompilationError(0, 0, FILE_WRITE_ERROR);
            }
            writer = new RandomAccessFile(getOutputFileName(), "rw");
            writeHeader();
        } catch (Exception e) {
            throw new CompilationError(0, 0, FILE_WRITE_ERROR);
        }
    }

    public static void closeWriter() throws CompilationError {
        try {
            writer.close();
        } catch (IOException e) {
            throw new CompilationError(0, 0, FILE_WRITE_ERROR);
        }
    }

    private static void writeHeader() throws IOException {
        if (mode == 1) {
            writer.writeInt(0x43303a29);
            writer.writeInt(0x00000001);
        }
    }

    private static void resetIndex() {
        currentIndex = 0;
    }

    public static void writeInstruction(Instruction instruction) throws CompilationError {
        if (mode == 0) {
            String str;
            if (instruction.getOpCode().getOperandType() == NULL) {
                str = String.format("%d %s\n", currentIndex, instruction.getOpCode().getName());
            } else if (instruction.getOpCode().getOperandType() == INT16_AND_INT32) {
                str = String.format("%d %s %d, %d\n", currentIndex, instruction.getOpCode().getName(),
                        ((Int16_and_Int32) instruction.getOperand()).op1,
                        ((Int16_and_Int32) instruction.getOperand()).op2);
            } else {
                str = String.format("%d %s %d\n", currentIndex, instruction.getOpCode().getName(),
                        (Integer) instruction.getOperand());
            }
            try {
                writer.writeChars(str);
            } catch (IOException e) {
                throw new CompilationError(0, 0, FILE_WRITE_ERROR);
            }
        } else {
            try {
                writer.write(instruction.getOpCode().getValue());
                if (instruction.getOpCode().getOperandType() == BYTE) {
                    writer.writeByte((Integer) instruction.getOperand());
                } else if (instruction.getOpCode().getOperandType() == INT16) {
                    writer.writeShort((Integer) instruction.getOperand());
                } else if (instruction.getOpCode().getOperandType() == INT32) {
                    writer.writeInt((Integer) instruction.getOperand());
                } else if (instruction.getOpCode().getOperandType() == INT16_AND_INT32) {
                    writer.writeShort(((Int16_and_Int32) instruction.getOperand()).op1);
                    writer.writeInt(((Int16_and_Int32) instruction.getOperand()).op2);
                }
            } catch (IOException e) {
                throw new CompilationError(0, 0, FILE_WRITE_ERROR);
            }
        }
    }
}
