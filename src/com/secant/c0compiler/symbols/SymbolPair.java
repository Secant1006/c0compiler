package com.secant.c0compiler.symbols;

public class SymbolPair {
    private Symbol symbol;
    private int level_diff;

    public SymbolPair(Symbol symbol, int level_diff) {
        this.symbol = symbol;
        this.level_diff = level_diff;
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public int getLevel_diff() {
        return level_diff;
    }
}
