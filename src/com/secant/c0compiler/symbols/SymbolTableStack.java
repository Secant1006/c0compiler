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
        int index = 0;
        for (SymbolTable table : stack) {
            Symbol result = table.getSymbolByName(name);
            if (result != null) {
                int diff_level = stack.size() - index - 1;
                return new SymbolPair(result, diff_level);
            }
            index++;
        }
        return null;
    }

    public static void addSymbol(Symbol symbol) {
        stack.peek().addSymbol(symbol);
    }
}
