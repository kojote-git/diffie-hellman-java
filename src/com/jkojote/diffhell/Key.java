package com.jkojote.diffhell;

public class Key {
    private int p;
    private int g;

    public Key(int p, int g) {
        this.p = p;
        this.g = g;
    }

    public int getG() {
        return g;
    }

    public int getP() {
        return p;
    }

    @Override
    public String toString() {
        return "Key{p=" + p +  ", g=" + g + "}";
    }
}
