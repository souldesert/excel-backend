package ru.voskhod;

import java.util.ArrayList;

public class TokenizeResult {
    private ArrayList<Integer> refs;
    private ArrayList<String> tokens;

    public  TokenizeResult(ArrayList<Integer> refs, ArrayList<String> tokens) {
        this.refs = new ArrayList<>(refs);
        this.tokens = new ArrayList<>(tokens);
    }

    public ArrayList<Integer> getRefs() {
        return this.refs;
    }

    public ArrayList<String> getTokens() {
        return this.tokens;
    }

}
