package com.secant.c0compiler.symbols;

public class Symbol {
    int index;
    String name;
    SymbolType type;
    Object value;
    boolean constant;

    public Symbol(String name, SymbolType type) {
        this.name = name;
        this.type = type;
        this.value = 0;
        this.constant = false;
    }

    public Symbol(String name, SymbolType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.constant = false;
    }

    public Symbol(String name, SymbolType type, Object value, boolean constant) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.constant = constant;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public SymbolType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public boolean isConstant() {
        return constant;
    }
}
