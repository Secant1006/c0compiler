package com.secant.c0compiler.symbols;

import java.util.Iterator;
import java.util.Stack;

public class SymbolTableStack {
    private static Stack<SymbolTable> stack = new Stack<>();
    private static Iterator<SymbolTable> iterator;

    public static SymbolTable newTable() {
        SymbolTable table = new SymbolTable();
        stack.push(table);
        return table;
    }

    public static void finishTable() {
        stack.pop();
    }

    public static void setIterator() {
        iterator = stack.iterator();
    }

    public static SymbolTable getNextTable() {
        return iterator.next();
    }
}
