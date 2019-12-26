package com.secant.c0compiler.symbols;

import javafx.util.Pair;

import java.util.Stack;

public class SymbolTableStack {
    private static Stack<SymbolTable> stack = new Stack<>();

    public static SymbolTable newTable() {
        SymbolTable table = new SymbolTable();
        stack.push(table);
        return table;
    }

    public static void finishTable() {
        stack.pop();
    }

    public static Pair<Symbol, Integer> getSymbolByName(String name) {
        int diff_level = 0;
        for (SymbolTable table : stack) {
            Symbol result = table.getSymbolByName(name);
            if (result != null) {
                return new Pair<>(result, diff_level);
            }
            diff_level++;
        }
        return null;
    }

    public static void addSymbol(Symbol symbol) {
        stack.peek().addSymbol(symbol);
    }
}
