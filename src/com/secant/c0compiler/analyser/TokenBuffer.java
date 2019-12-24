package com.secant.c0compiler.analyser;

import com.secant.c0compiler.errorhandling.CompilationError;
import com.secant.c0compiler.tokenizer.Token;

import static com.secant.c0compiler.tokenizer.Tokenizer.*;
import static com.secant.c0compiler.errorhandling.ErrorCode.*;

public class TokenBuffer {
    private static Token previousToken;

    public static Token getNextToken() {
        if (previousToken != null) {
            Token tempToken = previousToken;
            previousToken = null;
            return tempToken;
        } else {
            return getSymbol();
        }
    }

    public static void unreadToken(Token token) throws CompilationError {
        if (previousToken == null) {
            previousToken = token;
        } else {
            throw new CompilationError(line, row, TOKEN_BUFFER_OVERFLOW);
        }
    }
}
