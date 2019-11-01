package ru.voskhod.excel;

import java.util.ArrayList;

class TokenizeResult {
    private ArrayList<Integer> refs;
    private ArrayList<String> tokens;

    TokenizeResult(ArrayList<Integer> refs, ArrayList<String> tokens) {
        this.refs = new ArrayList<>(refs);
        this.tokens = new ArrayList<>(tokens);
    }

    ArrayList<Integer> getRefs() {
        return refs;
    }

    ArrayList<String> getTokens() {
        return tokens;
    }

}
