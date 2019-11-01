package ru.voskhod;

import ru.voskhod.excel.ResultCell;

public class Response {
    private String name;
    private ResultCell result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResultCell getResult() {
        return result;
    }

    public void setResult(ResultCell result) {
        this.result = result;
    }
}
