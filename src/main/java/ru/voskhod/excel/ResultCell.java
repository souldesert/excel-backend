package ru.voskhod.excel;

import java.math.BigDecimal;

public class ResultCell {
    private Status status;
    private String value;

    ResultCell(Status status, BigDecimal value) {
        this.status = status;
        this.value = value.toString();
    }

    ResultCell(Status status, String errorMsg) {
        this.status = status;
        this.value = errorMsg;
    }

    public Status getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }
}
