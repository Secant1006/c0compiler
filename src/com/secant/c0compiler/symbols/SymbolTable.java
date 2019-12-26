package com.secant.c0compiler.symbols;

import java.util.ArrayList;

public class SymbolTable {
    private ArrayList<Symbol> table;
    private int currentIndex;

    public SymbolTable() {
        table = new ArrayList<>();
        currentIndex = 0;
    }

    public Symbol getSymbolByName(String name) {
        for (Symbol result : table) {
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    public void addSymbol(Symbol symbol) {
        symbol.setIndex(currentIndex);
        table.add(symbol);
        currentIndex++;
    }

    public Symbol getLastSymbol() {
        if (table.isEmpty()) {
            return null;
        } else {
            return table.get(currentIndex - 1);
        }
    }

    public int getSize() {
        return table.size();
    }

    public ArrayList<Symbol> getTable() {
        return table;
    }
}
