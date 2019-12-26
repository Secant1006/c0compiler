package com.secant.c0compiler.assembly;

import com.secant.c0compiler.analyser.Analyser;
import com.secant.c0compiler.errorhandling.CompilationError;
import com.secant.c0compiler.symbols.Symbol;
import com.secant.c0compiler.symbols.SymbolTable;

import java.io.*;
import java.util.ArrayList;

import static com.secant.c0compiler.assembly.OperandType.*;
import static com.secant.c0compiler.console.Arguments.*;
import static com.secant.c0compiler.errorhandling.ErrorCode.*;

public class WriteOutput {
    private static int mode;
    private static boolean writeToStdio;
    private static RandomAccessFile writer;
    private static ArrayList<ArrayList<Instruction>> programList = new ArrayList<>();
    private static ArrayList<Instruction> instructionList;

    public static void addProgram() {
        instructionList = new ArrayList<>();
        programList.add(instructionList);
    }

    public static int getOffset() {
        return instructionList.size();
    }

    public static void initializeWriter() throws CompilationError {
        mode = getMode();
        writeToStdio = isWriteToStdio();
        try {
            if (!new File(getOutputFileName()).createNewFile()) {
                throw new CompilationError(0, 0, FILE_WRITE_ERROR);
            }
            writer = new RandomAccessFile(getOutputFileName(), "rw");
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

    public static void writeInstruction(Instruction instruction) {
        instructionList.add(instruction);
    }

    public static void setInstruction(int index, Instruction instruction) {
        instructionList.set(index, instruction);
    }

    public static void writeInstructionToFile(Instruction instruction, int index) throws CompilationError {
        if (mode == 0) {
            String str;
            if (instruction.getOpCode().getOperandType() == NULL) {
                str = String.format("%d %s\n", index, instruction.getOpCode().getName());
            } else if (instruction.getOpCode().getOperandType() == INT16_AND_INT32) {
                str = String.format("%d %s %d, %d\n", index, instruction.getOpCode().getName(),
                        ((Int16_and_Int32) instruction.getOperand()).op1,
                        ((Int16_and_Int32) instruction.getOperand()).op2);
            } else {
                str = String.format("%d %s %d\n", index, instruction.getOpCode().getName(),
                        (Integer) instruction.getOperand());
            }
            try {
                if (!writeToStdio) {
                    writer.writeChars(str);
                } else {
                    System.out.print(str);
                }
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

    public static void finishWriting() {
        String str;
        SymbolTable functionTable = Analyser.functionTable;
        initializeWriter();
        int index;
        try {
            // file header
            if (mode == 1) {
                writer.write(0x43303a29);
                writer.write(0x00000001);
            }
            // constant table
            if (mode == 1) {
                writer.writeShort(functionTable.getSize());
            } else {
                str = ".constants:\n";
                if (!writeToStdio) {
                    writer.writeChars(str);
                } else {
                    System.out.print(str);
                }
            }
            index = 0;
            for (Symbol function : functionTable.getTable()) {
                if (mode == 1) {
                    writer.writeByte(0);
                    writer.writeShort(function.getName().length());
                    writer.writeChars(function.getName());
                } else {
                    str = String.format("%d S \"%s\"\n", index, function.getName());
                    if (!writeToStdio) {
                        writer.writeChars(str);
                    } else {
                        System.out.print(str);
                    }
                }
                index++;
            }
            // start code
            if (mode == 1) {
                writer.writeShort(programList.get(0).size());
            } else {
                str = ".start:\n";
                if (!writeToStdio) {
                    writer.writeChars(str);
                } else {
                    System.out.print(str);
                }
            }
            index = 0;
            for (Instruction instruction : programList.get(0)) {
                writeInstructionToFile(instruction, index);
                index++;
            }
            programList.remove(0);
            // function table
            if (mode == 1) {
                writer.writeShort(programList.size());
            } else {
                str = ".functions:\n";
                if (!writeToStdio) {
                    writer.writeChars(str);
                } else {
                    System.out.print(str);
                }
                index = 0;
                for (Symbol function : functionTable.getTable()) {
                    str = String.format("%d %d %d 1\n", index, index, (Integer) function.getValue());
                    if (!writeToStdio) {
                        writer.writeChars(str);
                    } else {
                        System.out.print(str);
                    }
                    index++;
                }
            }
            // functions
            index = 0;
            for (Symbol function : functionTable.getTable()) {
                if (mode == 1) {
                    writer.writeShort(index);
                    writer.writeShort((Integer) function.getValue());
                    writer.writeShort(1);
                    writer.writeShort(programList.get(index).size());
                }
                int instructionIndex = 0;
                for (Instruction instruction : programList.get(index)) {
                    writeInstructionToFile(instruction, instructionIndex);
                    instructionIndex++;
                }
                index++;
            }
        } catch (IOException e) {
            throw new CompilationError(0, 0, FILE_WRITE_ERROR);
        }
        closeWriter();
    }
}
