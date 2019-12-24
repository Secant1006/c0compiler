package com.secant.c0compiler.symbols;

import java.util.Stack;

public class SymbolTableStack {
    private static Stack<SymbolTable> stack = new Stack<>();
    private static int currentIndex;

    public static SymbolTable newTable() {
        if (!stack.empty()) {
            currentIndex = stack.peek().getCurrentIndex();
        } else {
            currentIndex = 0;
        }
        SymbolTable table = new SymbolTable(currentIndex);
        stack.push(table);
        return table;
    }

    public static void finishTable() {
        stack.pop();
    }

    public static Symbol getSymbolByName(String name) {
        for (SymbolTable table : stack) {
            Symbol result = table.getSymbolByName(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
