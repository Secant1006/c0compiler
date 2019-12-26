package com.secant.c0compiler.analyser;

import com.secant.c0compiler.assembly.Instruction;
import com.secant.c0compiler.assembly.Int16_and_Int32;
import com.secant.c0compiler.assembly.WriteOutput;
import com.secant.c0compiler.errorhandling.CompilationError;
import com.secant.c0compiler.symbols.Symbol;
import com.secant.c0compiler.symbols.SymbolTable;
import com.secant.c0compiler.symbols.SymbolTableStack;
import com.secant.c0compiler.tokenizer.Token;
import javafx.util.Pair;

import static com.secant.c0compiler.analyser.TokenBuffer.*;
import static com.secant.c0compiler.assembly.OPCode.*;
import static com.secant.c0compiler.assembly.WriteOutput.*;
import static com.secant.c0compiler.symbols.SymbolType.*;
import static com.secant.c0compiler.tokenizer.TokenType.*;
import static com.secant.c0compiler.errorhandling.ErrorCode.*;
import static com.secant.c0compiler.tokenizer.Tokenizer.*;

public class Analyser {
    private static SymbolTable functionTable = new SymbolTable();

    private static Token currentToken;

    private static void getToken() {
        currentToken = getNextToken();
    }

    private static void unreadToken() {
        TokenBuffer.unreadToken(currentToken);
    }

    public static void analyse() {
        analyseC0Program();
    }

    private static boolean variableDeclarationFinished = false;
    private static Token typeSpecifier, identifier;

    private static void analyseC0Program() throws CompilationError {
        SymbolTableStack.newTable();
        WriteOutput.addProgram();
        while (true) {
            getToken();
            if (currentToken.getType() == RESERVED_WORD_CONST) {
                analyseConstVariableDeclaration();
            } else if (currentToken.getType() == RESERVED_WORD_INT || currentToken.getType() == RESERVED_WORD_VOID) {
                analyseVariableOrFunction();
            } else if (currentToken.getType() == NULL) {
                break;
            } else {
                throw new CompilationError(line, row, NO_TYPE_SPECIFIER);
            }
        }
        // 执行WriteOutput的完成方法
    }

    private static void analyseConstVariableDeclaration() throws CompilationError {
        getToken();
        typeSpecifier = currentToken;
        if (currentToken.getType() != RESERVED_WORD_INT) {
            throw new CompilationError(line, row, NO_TYPE_SPECIFIER);
        }
        getToken();
        identifier = currentToken;
        if (currentToken.getType() != IDENTIFIER) {
            throw new CompilationError(line, row, NO_IDENTIFIER);
        }
        getToken();
        if (currentToken.getType() != ASSIGNMENT) {
            throw new CompilationError(line, row, INVALID_CONSTANT_DECLARATION);
        }
        unreadToken();
        analyseVariableDeclaration(true);
    }

    private static void analyseVariableOrFunction() throws CompilationError {
        typeSpecifier = currentToken;
        getToken();
        if (currentToken.getType() != IDENTIFIER) {
            throw new CompilationError(line, row, NO_IDENTIFIER);
        }
        identifier = currentToken;
        getToken();
        if (currentToken.getType() == ASSIGNMENT) {
            if (typeSpecifier.getType() == RESERVED_WORD_VOID) {
                throw new CompilationError(line, row, INVALID_VARIABLE_DEFINITION);
            }
            if (!variableDeclarationFinished) {
                analyseVariableDeclaration();
            } else {
                throw new CompilationError(line, row, VARIABLE_DECLARATION_AFTER_FUNCTION_DEFINITION);
            }
        } else if (currentToken.getType() == COMMA) {
            if (!variableDeclarationFinished) {
                analyseVariableDeclaration();
            } else {
                throw new CompilationError(line, row, VARIABLE_DECLARATION_AFTER_FUNCTION_DEFINITION);
            }
        } else if (currentToken.getType() == SEMICOLON) {
            if (typeSpecifier.getType() == RESERVED_WORD_VOID) {
                throw new CompilationError(line, row, INVALID_VARIABLE_DEFINITION);
            }
            if (SymbolTableStack.getSymbolByName(identifier.getValue().toString()) != null) {
                throw new CompilationError(line, row, VARIABLE_HAS_BEEN_DECLARED);
            }
            SymbolTableStack.addSymbol(new Symbol(identifier.getValue().toString(), SYM_INTEGER, 0));
            writeInstruction(new Instruction(IPUSH, 0));
        } else if (currentToken.getType() == LEFT_BRACKET) {
            variableDeclarationFinished = true;
            analyseFunctionDefinition();
        } else {
            throw new CompilationError(line, row, NO_SEMICOLON);
        }
    }

    private static void analyseVariableDeclaration() throws CompilationError {
        analyseVariableDeclaration(false);
    }

    private static void analyseVariableDeclaration(boolean isConstant) throws CompilationError {
        if (currentToken.getType() == ASSIGNMENT) {
            if (SymbolTableStack.getSymbolByName(identifier.getValue().toString()) != null) {
                throw new CompilationError(line, row, VARIABLE_HAS_BEEN_DECLARED);
            }
            SymbolTableStack.addSymbol(new Symbol(identifier.getValue().toString(), SYM_INTEGER, 0, isConstant));
            analyseExpression();
        } else {
            if (SymbolTableStack.getSymbolByName(identifier.getValue().toString()) != null) {
                throw new CompilationError(line, row, VARIABLE_HAS_BEEN_DECLARED);
            }
            SymbolTableStack.addSymbol(new Symbol(identifier.getValue().toString(), SYM_INTEGER, 0, isConstant));
            writeInstruction(new Instruction(IPUSH, 0));
            while (true) {
                getToken();
                if (currentToken.getType() != IDENTIFIER) {
                    throw new CompilationError(line, row, NO_IDENTIFIER);
                }
                SymbolTableStack.addSymbol(new Symbol(identifier.getValue().toString(), SYM_INTEGER, 0, isConstant));
                getToken();
                if (currentToken.getType() == ASSIGNMENT) {
                    analyseExpression();
                } else {
                    writeInstruction(new Instruction(IPUSH, 0));
                }
                getToken();
                if (currentToken.getType() != COMMA) {
                    unreadToken();
                    break;
                }
            }
        }
        getToken();
        if (currentToken.getType() != SEMICOLON) {
            throw new CompilationError(line, row, NO_SEMICOLON);
        }
    }

    private static void analyseFunctionDefinition() throws CompilationError {
        SymbolTableStack.newTable();
        WriteOutput.addProgram();
        analyseParameterDeclaration();
        analyseCompoundStatement();
        writeInstruction(new Instruction(IPUSH, 0));
        writeInstruction(new Instruction(IRET));
        SymbolTableStack.finishTable();
    }

    private static void analyseParameterDeclaration() throws CompilationError {
        getToken();
        if (currentToken.getType() == RIGHT_BRACKET) {
            if (functionTable.getSymbolByName(identifier.getValue().toString()) != null) {
                throw new CompilationError(line, row, FUNCTION_HAS_BEEN_DEFINED);
            }
            if (typeSpecifier.getType() == RESERVED_WORD_INT) {
                functionTable.addSymbol(new Symbol(identifier.getValue().toString(), SYM_FUNCTION_INT, 0));
            } else {
                functionTable.addSymbol(new Symbol(identifier.getValue().toString(), SYM_FUNCTION_VOID, 0));
            }
            return;
        }
        boolean isConstant = false;
        int paramSize = 0;
        getToken();
        if (currentToken.getType() == RESERVED_WORD_CONST) {
            isConstant = true;
            getToken();
        }
        if (currentToken.getType() != RESERVED_WORD_INT) {
            throw new CompilationError(line, row, INVALID_PARAMETER);
        }
        getToken();
        if (currentToken.getType() != IDENTIFIER) {
            throw new CompilationError(line, row, NO_IDENTIFIER);
        }
        paramSize += 4;
        SymbolTableStack.addSymbol(new Symbol(currentToken.getValue().toString(), SYM_INTEGER, 0, isConstant));
        isConstant = false;
        while (true) {
            getToken();
            if (currentToken.getType() == RIGHT_BRACKET) {
                break;
            } else if (currentToken.getType() == COMMA) {
                getToken();
                if (currentToken.getType() == RESERVED_WORD_CONST) {
                    isConstant = true;
                    getToken();
                }
                if (currentToken.getType() != RESERVED_WORD_INT) {
                    throw new CompilationError(line, row, INVALID_PARAMETER);
                }
                getToken();
                if (currentToken.getType() != IDENTIFIER) {
                    throw new CompilationError(line, row, NO_IDENTIFIER);
                }
                paramSize += 4;
                SymbolTableStack.addSymbol(new Symbol(currentToken.getValue().toString(), SYM_INTEGER, 0, isConstant));
                isConstant = false;
            }
        }
        if (typeSpecifier.getType() == RESERVED_WORD_INT) {
            functionTable.addSymbol(new Symbol(identifier.getValue().toString(), SYM_FUNCTION_INT, paramSize));
        } else {
            functionTable.addSymbol(new Symbol(identifier.getValue().toString(), SYM_FUNCTION_VOID, paramSize));
        }
    }

    private static void analyseCompoundStatement() throws CompilationError {
        getToken();
        if (currentToken.getType() != LEFT_BRACE) {
            throw new CompilationError(line, row, NO_LEFT_BRACE);
        }
        while (true) {
            boolean isConstant = false;
            getToken();
            if (currentToken.getType() == RESERVED_WORD_CONST) {
                isConstant = true;
                getToken();
                if (currentToken.getType() != RESERVED_WORD_INT) {
                    throw new CompilationError(line, row, NO_TYPE_SPECIFIER);
                }
            }
            if (currentToken.getType() == RESERVED_WORD_INT) {
                typeSpecifier = currentToken;
                getToken();
                if (currentToken.getType() == IDENTIFIER) {
                    identifier = currentToken;
                    getToken();
                    analyseVariableDeclaration(isConstant);
                } else {
                    throw new CompilationError(line, row, NO_IDENTIFIER);
                }
            } else {
                break;
            }
        }
        analyseStatementSequence();
        getToken();
        if (currentToken.getType() != RIGHT_BRACE) {
            throw new CompilationError(line, row, NO_RIGHT_BRACE);
        }
    }

    private static void analyseStatementSequence() {
        while (true) {
            getToken();
            if (currentToken.getType() == LEFT_BRACE ||
                    currentToken.getType() == RESERVED_WORD_IF ||
                    currentToken.getType() == RESERVED_WORD_WHILE ||
                    currentToken.getType() == RESERVED_WORD_RETURN ||
                    currentToken.getType() == RESERVED_WORD_PRINT ||
                    currentToken.getType() == RESERVED_WORD_SCAN ||
                    currentToken.getType() == IDENTIFIER ||
                    currentToken.getType() == SEMICOLON) {
                analyseStatement();
            } else {
                unreadToken();
                break;
            }
        }
    }

    private static void analyseStatement() throws CompilationError {
        if (currentToken.getType() == LEFT_BRACE) {
            analyseStatementSequence();
            getToken();
            if (currentToken.getType() != RIGHT_BRACE) {
                throw new CompilationError(line, row, NO_RIGHT_BRACE);
            }
        } else if (currentToken.getType() == RESERVED_WORD_IF) {
            analyseConditionStatement();
        } else if (currentToken.getType() == RESERVED_WORD_WHILE) {
            analyseLoopStatement();
        } else if (currentToken.getType() == RESERVED_WORD_RETURN) {
            analyseReturnStatement();
        } else if (currentToken.getType() == RESERVED_WORD_PRINT) {
            analysePrintStatement();
        } else if (currentToken.getType() == RESERVED_WORD_SCAN) {
            analyseScanStatement();
        } else if (currentToken.getType() == IDENTIFIER) {
            identifier = currentToken;
            getToken();
            if (currentToken.getType() == ASSIGNMENT) {
                try {
                    Symbol id = SymbolTableStack.getSymbolByName(identifier.getValue().toString()).getKey();
                    if (id.getType() == SYM_INTEGER) {
                        if (id.isConstant()) {
                            throw new CompilationError(line, row, ASSIGN_TO_CONSTANT);
                        }
                        analyseAssignmentExpression();
                    } else {
                        throw new CompilationError(line, row, ASSIGN_TO_NON_INTEGER);
                    }
                } catch (NullPointerException e) {
                    throw new CompilationError(line, row, VARIABLE_OR_FUNCTION_UNDEFINED);
                }
            } else if (currentToken.getType() == LEFT_BRACKET) {
                unreadToken();
                analyseFunctionCall();
            } else {
                throw new CompilationError(line, row, INVALID_ASSIGNMENT_EXPRESSION);
            }
            getToken();
            if (currentToken.getType() != SEMICOLON) {
                throw new CompilationError(line, row, NO_SEMICOLON);
            }
        } else if (currentToken.getType() == SEMICOLON) {
            return;
        } else {
            throw new CompilationError(line, row, INVALID_STATEMENT);
        }
    }

    private static void analyseConditionStatement() throws CompilationError {
        getToken();
        if (currentToken.getType() != LEFT_BRACKET) {
            throw new CompilationError(line, row, NO_LEFT_BRACKET);
        }
        analyseCondition();
        if (currentToken.getType() != RIGHT_BRACKET) {
            throw new CompilationError(line, row, NO_RIGHT_BRACKET);
        }
        getToken();
        analyseStatement();
        getToken();
        if (currentToken.getType() == RESERVED_WORD_ELSE) {
            getToken();
            analyseStatement();
        } else {
            unreadToken();
        }
    }

    private static void analyseCondition() {
        analyseExpression();
        getToken();
        if (currentToken.getType() == LESS_THAN ||
                currentToken.getType() == LESS_OR_EQUAL ||
                currentToken.getType() == MORE_THAN ||
                currentToken.getType() == MORE_OR_EQUAL ||
                currentToken.getType() == NOT_EQUAL ||
                currentToken.getType() == EQUAL) {
            analyseExpression();
        }
    }

    private static void analyseLoopStatement() throws CompilationError {
        getToken();
        if (currentToken.getType() != LEFT_BRACKET) {
            throw new CompilationError(line, row, NO_LEFT_BRACKET);
        }
        analyseCondition();
        getToken();
        if (currentToken.getType() != RIGHT_BRACKET) {
            throw new CompilationError(line, row, NO_RIGHT_BRACKET);
        }
        getToken();
        analyseStatement();
    }

    private static void analyseReturnStatement() throws CompilationError {
        getToken();
        if (functionTable.getLastSymbol().getType() == SYM_FUNCTION_INT) {
            if (currentToken.getType() == SEMICOLON) {
                throw new CompilationError(line, row, NO_RETURN_VALUE);
            } else {
                unreadToken();
                analyseExpression();
                getToken();
                if (currentToken.getType() != SEMICOLON) {
                    throw new CompilationError(line, row, NO_SEMICOLON);
                }
            }
            writeInstruction(new Instruction(IRET));
        } else {
            if (currentToken.getType() != SEMICOLON) {
                throw new CompilationError(line, row, NO_SEMICOLON);
            }
            writeInstruction(new Instruction(RET));
        }
    }

    private static void analyseScanStatement() throws CompilationError {
        getToken();
        if (currentToken.getType() != LEFT_BRACKET) {
            throw new CompilationError(line, row, NO_LEFT_BRACKET);
        }
        getToken();
        if (currentToken.getType() != IDENTIFIER) {
            throw new CompilationError(line, row, NO_IDENTIFIER);
        }
        Pair<Symbol, Integer> symbolPair = SymbolTableStack.getSymbolByName(currentToken.getValue().toString());
        try {
            if (symbolPair.getKey().isConstant()) {
                throw new CompilationError(line, row, ASSIGN_TO_CONSTANT);
            } else {
                writeInstruction(new Instruction(LOADA, new Int16_and_Int32(symbolPair.getValue(), symbolPair.getKey().getIndex())));
                writeInstruction(new Instruction(ISCAN));
                writeInstruction(new Instruction(ISTORE));
            }
        } catch (NullPointerException e) {
            throw new CompilationError(line, row, VARIABLE_OR_FUNCTION_UNDEFINED);
        }
        getToken();
        if (currentToken.getType() != RIGHT_BRACKET) {
            throw new CompilationError(line, row, NO_RIGHT_BRACKET);
        }
        getToken();
        if (currentToken.getType() != SEMICOLON) {
            throw new CompilationError(line, row, NO_SEMICOLON);
        }
    }

    private static void analysePrintStatement() throws CompilationError {
        getToken();
        if (currentToken.getType() != LEFT_BRACKET) {
            throw new CompilationError(line, row, NO_LEFT_BRACKET);
        }
        analysePrintableList();
        writeInstruction(new Instruction(PRINTL));
        getToken();
        if (currentToken.getType() != RIGHT_BRACKET) {
            throw new CompilationError(line, row, NO_RIGHT_BRACKET);
        }
        getToken();
        if (currentToken.getType() != SEMICOLON) {
            throw new CompilationError(line, row, NO_SEMICOLON);
        }
    }

    private static void analysePrintableList() {
        analyseExpression();
        writeInstruction(new Instruction(IPRINT));
        while (true) {
            getToken();
            if (currentToken.getType() != COMMA) {
                unreadToken();
                break;
            } else {
                writeInstruction(new Instruction(BIPUSH, 32));
                analyseExpression();
                writeInstruction(new Instruction(IPRINT));
            }
        }
    }

    private static void analyseAssignmentExpression() throws CompilationError {
        Pair<Symbol, Integer> symbolPair = SymbolTableStack.getSymbolByName(identifier.getValue().toString());
        writeInstruction(new Instruction(LOADA, new Int16_and_Int32(symbolPair.getValue(), symbolPair.getKey().getIndex())));
        analyseExpression();
        writeInstruction(new Instruction(ISTORE));
    }

    private static void analyseExpression() {
        analyseAdditiveExpression();
    }

    private static void analyseAdditiveExpression() {
        analyseMultiplicativeExpression();
        getToken();
        if (currentToken.getType() == PLUS) {
            analyseMultiplicativeExpression();
            writeInstruction(new Instruction(IADD));
        } else if (currentToken.getType() == MINUS) {
            analyseMultiplicativeExpression();
            writeInstruction(new Instruction(ISUB));
        } else {
            unreadToken();
        }
    }

    private static void analyseMultiplicativeExpression() {
        analyseUnaryExpression();
        getToken();
        if (currentToken.getType() == MULTIPLY) {
            analyseUnaryExpression();
            writeInstruction(new Instruction(IMUL));
        } else if (currentToken.getType() == DIVIDE) {
            analyseUnaryExpression();
            writeInstruction(new Instruction(IDIV));
        } else {
            unreadToken();
        }
    }

    private static void analyseUnaryExpression() {
        boolean isNegative = false;
        getToken();
        if (currentToken.getType() == PLUS) {
            isNegative = false;
        } else if (currentToken.getType() == MINUS) {
            isNegative = true;
        } else {
            unreadToken();
        }
        analysePrimaryExpression();
        if (isNegative) {
            writeInstruction(new Instruction(INEG));
        }
    }

    private static void analysePrimaryExpression() throws CompilationError {
        getToken();
        if (currentToken.getType() == LEFT_BRACKET) {
            analyseExpression();
            getToken();
            if (currentToken.getType() != RIGHT_BRACKET) {
                throw new CompilationError(line, row, NO_RIGHT_BRACKET);
            }
        } else if (currentToken.getType() == IDENTIFIER) {
            if (functionTable.getSymbolByName(currentToken.getValue().toString()) != null) {
                analyseFunctionCall();
            } else {
                Pair<Symbol, Integer> symbolPair = SymbolTableStack.getSymbolByName(currentToken.getValue().toString());
                if (symbolPair == null) {
                    throw new CompilationError(line, row, VARIABLE_OR_FUNCTION_UNDEFINED);
                } else {
                    writeInstruction(new Instruction(LOADA, new Int16_and_Int32(symbolPair.getValue(), symbolPair.getKey().getIndex())));
                    writeInstruction(new Instruction(ILOAD));
                }
            }
        } else if (currentToken.getType() == INTEGER) {
            writeInstruction(new Instruction(IPUSH, currentToken.getValue()));
        } else {
            throw new CompilationError(line, row, INVALID_EXPRESSION);
        }
    }

    private static void analyseFunctionCall() throws CompilationError {
        getToken();
        if (currentToken.getType() != LEFT_BRACKET) {
            throw new CompilationError(line, row, NO_LEFT_BRACKET);
        }
        analyseExpressionList();
        getToken();
        if (currentToken.getType() != RIGHT_BRACKET) {
            throw new CompilationError(line, row, NO_RIGHT_BRACKET);
        }
        Symbol functionCall = functionTable.getSymbolByName(identifier.getValue().toString());
        if (functionCall != null) {
            writeInstruction(new Instruction(CALL, functionCall.getIndex()));
        }
    }

    private static void analyseExpressionList() {
        analyseExpression();
        while (true) {
            getToken();
            if (currentToken.getType() != COMMA) {
                unreadToken();
                break;
            } else {
                analyseExpression();
            }
        }
    }
}
