package ru.voskhod.excel;

import java.util.ArrayList;

class Cell {
    private ArrayList<Integer> refs;
    private ArrayList<String> expr;
    private ArrayList<String> exprPRN;
    private Status status;
    private String result;
    private String errorMsg;

    Cell() {
        this.refs = new ArrayList<>();
        this.expr = new ArrayList<>();
        this.exprPRN = new ArrayList<>();
        this.status = Status.PROCESSING;
        this.errorMsg = null;
        this.result = null;
    }

    ArrayList<Integer> getRefs() {
        return refs;
    }

    void setRefs(ArrayList<Integer> refs) {
        this.refs = new ArrayList<>(refs);
    }


    ArrayList<String> getExpr() {
        return expr;
    }

    void setExpr(ArrayList<String> expr) {
        this.expr = new ArrayList<>(expr);
    }

    ArrayList<String> getExprPRN() {
        return exprPRN;
    }

    void setExprPRN(ArrayList<String> exprPRN) {
        this.exprPRN = new ArrayList<>(exprPRN);
    }

    Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    String getResult() {
        return result;
    }

    void setResult(String result) {
        this.result = result;
    }

    String getErrorMsg() {
        return errorMsg;
    }

    void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
