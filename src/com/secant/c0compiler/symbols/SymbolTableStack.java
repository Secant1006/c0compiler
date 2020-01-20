package com.secant.c0compiler.symbols;

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

    public static SymbolPair getSymbolByName(String name) {
        Symbol result = stack.peek().getSymbolByName(name);
        if (stack.size() == 0) {
            if (result != null) {
                return new SymbolPair(result, 0);
            } else {
                return null;
            }
        } else {
            if (result != null) {
                return new SymbolPair(result, 0);
            } else {
                result = stack.get(0).getSymbolByName(name);
                if (result != null) {
                    return new SymbolPair(result, 1);
                } else {
                    return null;
                }
            }
        }
    }

    public static void addSymbol(Symbol symbol) {
        stack.peek().addSymbol(symbol);
    }
}
