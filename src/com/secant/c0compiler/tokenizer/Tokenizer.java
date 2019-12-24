package com.secant.c0compiler.tokenizer;

import com.secant.c0compiler.console.Arguments;
import com.secant.c0compiler.errorhandling.CompilationError;

import static java.lang.Character.*;
import static com.secant.c0compiler.tokenizer.TokenType.*;
import static com.secant.c0compiler.errorhandling.ErrorCode.*;

public class Tokenizer {
    public static int line = 1;
    public static int row = 1;
    private static ReadBuffer readBuffer;
    private static String currentToken;
    private static char currentChar;

    static {
        readBuffer = new ReadBuffer(Arguments.getInputFileName());
    }

    public static Token getSymbol() throws CompilationError {
        clearCurrentToken();
        read();
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\r' || currentChar == '\n') {
            if (currentChar == ' ' || currentChar == '\t') {
                row++;
            } else {
                row = 1;
                line++;
            }
            read();
        }
        if (isLetter(currentChar)) {
            row++;
            currentToken += currentChar;
            while (true) {
                read();
                if (isLetterOrDigit(currentChar)) {
                    currentToken += currentChar;
                    row++;
                } else {
                    unread();
                    switch (currentToken) {
                        case "const":
                            return new Token(RESERVED_WORD_CONST, null);
                        case "void":
                            return new Token(RESERVED_WORD_VOID, null);
                        case "int":
                            return new Token(RESERVED_WORD_INT, null);
                        case "char":
                            return new Token(RESERVED_WORD_CHAR, null);
                        case "double":
                            return new Token(RESERVED_WORD_DOUBLE, null);
                        case "struct":
                            return new Token(RESERVED_WORD_STRUCT, null);
                        case "if":
                            return new Token(RESERVED_WORD_IF, null);
                        case "else":
                            return new Token(RESERVED_WORD_ELSE, null);
                        case "switch":
                            return new Token(RESERVED_WORD_SWITCH, null);
                        case "case":
                            return new Token(RESERVED_WORD_CASE, null);
                        case "default":
                            return new Token(RESERVED_WORD_DEFAULT, null);
                        case "while":
                            return new Token(RESERVED_WORD_WHILE, null);
                        case "for":
                            return new Token(RESERVED_WORD_FOR, null);
                        case "do":
                            return new Token(RESERVED_WORD_DO, null);
                        case "return":
                            return new Token(RESERVED_WORD_RETURN, null);
                        case "break":
                            return new Token(RESERVED_WORD_BREAK, null);
                        case "continue":
                            return new Token(RESERVED_WORD_CONTINUE, null);
                        case "print":
                            return new Token(RESERVED_WORD_PRINT, null);
                        case "scan":
                            return new Token(RESERVED_WORD_SCAN, null);
                        default:
                            return new Token(IDENTIFIER, currentToken);
                    }
                }
            }
        } else if (isDigit(currentChar)) {
            row++;
            if (currentChar == '0') {
                read();
                if (currentChar == 'x' || currentChar == 'X') {
                    row++;
                    while (true) {
                        read();
                        if (isDigit(currentChar) || ('a' <= currentChar && currentChar <= 'f') ||
                                ('A' <= currentChar && currentChar <= 'F')) {
                            row++;
                            currentToken += toLowerCase(currentChar);
                        } else {
                            unread();
                            break;
                        }
                    }
                    try {
                        return new Token(INTEGER, Integer.valueOf(currentToken, 16));
                    } catch (NumberFormatException e) {
                        throw new CompilationError(line, row, INTEGER_OVERFLOW);
                    }
                } else {
                    unread();
                    return new Token(INTEGER, 0);
                }
            } else {
                while (true) {
                    read();
                    if (isDigit(currentChar)) {
                        row++;
                        currentToken += currentChar;
                    } else {
                        unread();
                        try {
                            return new Token(INTEGER, Integer.valueOf(currentToken));
                        } catch (NumberFormatException e) {
                            throw new CompilationError(line, row, INTEGER_OVERFLOW);
                        }
                    }
                }
            }
        } else if (currentChar == '+') {
            row++;
            return new Token(PLUS, null);
        } else if (currentChar == '-') {
            row++;
            return new Token(MINUS, null);
        } else if (currentChar == '*') {
            row++;
            return new Token(MULTIPLY, null);
        } else if (currentChar == '/') {
            row++;
            return new Token(DIVIDE, null);
        } else if (currentChar == '<') {
            row++;
            read();
            if (currentChar == '=') {
                row++;
                return new Token(LESS_OR_EQUAL, null);
            } else {
                unread();
                return new Token(LESS_THAN, null);
            }
        } else if (currentChar == '>') {
            row++;
            read();
            if (currentChar == '=') {
                row++;
                return new Token(MORE_OR_EQUAL, null);
            } else {
                unread();
                return new Token(MORE_THAN, null);
            }
        } else if (currentChar == '=') {
            row++;
            read();
            if (currentChar == '=') {
                row++;
                return new Token(EQUAL, null);
            } else {
                unread();
                return new Token(ASSIGNMENT, null);
            }
        } else if (currentChar == '!') {
            row++;
            read();
            if (currentChar == '=') {
                row++;
                return new Token(NOT_EQUAL, null);
            } else {
                throw new CompilationError(line, row, INCOMPLETE_NOT_EQUAL);
            }
        } else if (currentChar == ',') {
            row++;
            return new Token(COMMA, null);
        } else if (currentChar == '(') {
            row++;
            return new Token(LEFT_BRACKET, null);
        } else if (currentChar == ')') {
            row++;
            return new Token(RIGHT_BRACKET, null);
        } else if (currentChar == '{') {
            row++;
            return new Token(LEFT_BRACE, null);
        } else if (currentChar == '}') {
            row++;
            return new Token(RIGHT_BRACE, null);
        } else if (currentChar == ':') {
            row++;
            return new Token(COLON, null);
        } else if (currentChar == ';') {
            row++;
            return new Token(SEMICOLON, null);
        } else if (currentChar == '\0') {
            readBuffer.close();
            return null;
        } else {
            throw new CompilationError(line, row, UNKNOWN_CHARACTER);
        }
    }

    private static void clearCurrentToken() {
        currentToken = "";
    }

    private static void read() {
        currentChar = readBuffer.read();
    }

    private static void unread() {
        readBuffer.unread(currentChar);
    }
}
