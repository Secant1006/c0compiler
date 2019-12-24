package com.secant.c0compiler.symbols;

import java.util.ArrayList;
import java.util.ListIterator;

public class SymbolTable {
    private ArrayList<Symbol> table;
    private ListIterator<Symbol> iterator;
    private int currentIndex;

    public SymbolTable() {
        table = new ArrayList<>();
        currentIndex = 0;
    }

    public Symbol getSymbolByName(String name) {
        boolean foundFlag = false;
        iterator = table.listIterator(0);
        Symbol symbol = iterator.next();
        while (iterator.hasNext()) {
            symbol = iterator.next();
            if (symbol.getName().equals(name)) {
                foundFlag = true;
                break;
            }
        }
        if (foundFlag) {
            return symbol;
        } else {
            return null;
        }
    }

    public void addSymbol(Symbol symbol) {
        symbol.setIndex(currentIndex);
        table.add(symbol);
    }
}
