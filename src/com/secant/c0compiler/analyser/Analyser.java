package com.secant.c0compiler.analyser;

import com.secant.c0compiler.assembly.Instruction;
import com.secant.c0compiler.errorhandling.CompilationError;
import com.secant.c0compiler.symbols.Symbol;
import com.secant.c0compiler.symbols.SymbolTable;
import com.secant.c0compiler.symbols.SymbolTableStack;
import com.secant.c0compiler.tokenizer.Token;

import static com.secant.c0compiler.analyser.TokenBuffer.*;
import static com.secant.c0compiler.assembly.OPCode.*;
import static com.secant.c0compiler.assembly.WriteOutput.*;
import static com.secant.c0compiler.symbols.SymbolType.*;
import static com.secant.c0compiler.tokenizer.TokenType.*;
import static com.secant.c0compiler.errorhandling.ErrorCode.*;
import static com.secant.c0compiler.tokenizer.Tokenizer.*;

public class Analyser {
    private static SymbolTable constantTable = new SymbolTable();
    private static SymbolTable functionTable = new SymbolTable();

    private static Token currentToken;

    private static void getToken() {
        currentToken = getNextToken();
    }

    private static void unreadToken() {
        TokenBuffer.unreadToken(currentToken);
    }

    public static void analyse() {
        getToken();
        analyseC0Program();
    }

    private static void analyseC0Program() throws CompilationError {
        SymbolTableStack.newTable();
        if (currentToken.getType() == RESERVED_WORD_CONST) {
            analyseConstVariableDeclaration();
        } else if (currentToken.getType() == RESERVED_WORD_VOID || currentToken.getType() == RESERVED_WORD_INT) {
            analyseVariableOrFunction();
        } else {
            throw new CompilationError(line, row, NO_TYPE_SPECIFIER);
        }
        // 执行WriteOutput的完成方法
    }

    private static void analyseConstVariableDeclaration() throws CompilationError {
        getToken();
        if (currentToken.getType() != RESERVED_WORD_INT) {
            throw new CompilationError(line, row, NO_TYPE_SPECIFIER);
        }
        typeSpecifier = currentToken;
        getToken();
        if (currentToken.getType() != IDENTIFIER) {
            throw new CompilationError(line, row, NO_IDENTIFIER);
        }
        identifier = currentToken;
        getToken();
        if (currentToken.getType() != ASSIGNMENT) {
            throw new CompilationError(line, row, INVALID_CONSTANT_DECLARATION);
        }
        getToken();
        analyseConstantExpression();
        while (true) {
            getToken();
            if (currentToken.getType() == COMMA) {
                getToken();
                if (currentToken.getType() != IDENTIFIER) {
                    throw new CompilationError(line, row, NO_IDENTIFIER);
                }
                identifier = currentToken;
                getToken();
                if (currentToken.getType() != ASSIGNMENT) {
                    throw new CompilationError(line, row, INVALID_CONSTANT_DECLARATION);
                }
                getToken();
                analyseConstVariableDeclaration();
            } else if (currentToken.getType() == SEMICOLON) {
                break;
            } else {
                throw new CompilationError(line, row, NO_SEMICOLON);
            }
        }
    }

    private static boolean variableDeclarationFinished = false;
    private static Token typeSpecifier, identifier;

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
        if (currentToken.getType() == ASSIGNMENT) {
            if (SymbolTableStack.getSymbolByName(identifier.getValue().toString()) != null) {
                throw new CompilationError(line, row, VARIABLE_HAS_BEEN_DECLARED);
            }
            SymbolTableStack.addSymbol(new Symbol(identifier.getValue().toString(), SYM_INTEGER, 0));
            analyseExpression();
        } else {
            if (SymbolTableStack.getSymbolByName(identifier.getValue().toString()) != null) {
                throw new CompilationError(line, row, VARIABLE_HAS_BEEN_DECLARED);
            }
            SymbolTableStack.addSymbol(new Symbol(identifier.getValue().toString(), SYM_INTEGER, 0));
            writeInstruction(new Instruction(IPUSH, 0));
            while (true) {
                getToken();
                if (currentToken.getType() != IDENTIFIER) {
                    throw new CompilationError(line, row, NO_IDENTIFIER);
                }
                SymbolTableStack.addSymbol(new Symbol(identifier.getValue().toString(), SYM_INTEGER, 0));
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
        analyseParameterDeclaration();
        analyseCompoundStatement();
        SymbolTableStack.finishTable();
    }

    private static void analyseParameterDeclaration() throws CompilationError {
        getToken();
        if (currentToken.getType() == RIGHT_BRACKET) {
            if (functionTable.getSymbolByName(identifier.getValue().toString()) != null) {
                throw new CompilationError(line, row, FUNCTION_HAS_BEEN_DEFINED);
            }
            functionTable.addSymbol(new Symbol(identifier.getValue().toString(), SYM_FUNCTION, 0));
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
        functionTable.addSymbol(new Symbol(identifier.getValue().toString(), SYM_FUNCTION, paramSize));
    }

    private static void analyseCompoundStatement() throws CompilationError {
        getToken();
        if (currentToken.getType() != LEFT_BRACE) {
            throw new CompilationError(line, row, NO_LEFT_BRACE);
        }
        while (true) {
            getToken();
            if (currentToken.getType() == RESERVED_WORD_CONST) {
                analyseConstantExpression();
            } else if (currentToken.getType() == RESERVED_WORD_INT) {
                typeSpecifier = currentToken;
                getToken();
                if (currentToken.getType() == IDENTIFIER) {
                    analyseVariableDeclaration();
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
            // 这里需要根据符号表判断调用分析赋值语句子程序还是分析函数调用子程序
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
        if (currentToken.getType() != SEMICOLON) {
            analyseExpression();
        } else {
            unreadToken();
        }
        getToken();
        if (currentToken.getType() != SEMICOLON) {
            throw new CompilationError(line, row, NO_SEMICOLON);
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

    private static void analyseAssignmentExpression() throws CompilationError {
        getToken();
        if (currentToken.getType() != ASSIGNMENT) {
            throw new CompilationError(line, row, INVALID_ASSIGNMENT_EXPRESSION);
        }
        analyseExpression();
    }

    private static void analyseConstantExpression() {

    }

    private static void analyseExpression() {
        analyseAdditiveExpression();
    }

    private static void analyseAdditiveExpression() {
        analyseMultiplicativeExpression();
        getToken();
        if (currentToken.getType() == PLUS || currentToken.getType() == MINUS) {
            analyseMultiplicativeExpression();
        } else {
            unreadToken();
        }
    }

    private static void analyseMultiplicativeExpression() {
        analyseUnaryExpression();
        getToken();
        if (currentToken.getType() == MULTIPLY || currentToken.getType() == DIVIDE) {
            analyseUnaryExpression();
        } else {
            unreadToken();
        }
    }

    private static void analyseUnaryExpression() {
        getToken();
        if (currentToken.getType() == PLUS) {
            // 啥也不干
        } else if (currentToken.getType() == MINUS) {
            // 整成负数
        } else {
            unreadToken();
        }
        analysePrimaryExpression();
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
            identifier = currentToken;
            // 看符号表再决定
        } else if (currentToken.getType() == INTEGER) {
            // 整挺好
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
