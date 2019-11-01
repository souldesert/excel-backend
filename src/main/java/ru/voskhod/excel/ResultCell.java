package ru.voskhod.excel;

public class ResultCell {
    private Status status;
    private String value;

    ResultCell(Status status, double value) {
        this.status = status;

        int valueInt = (int) value;
        this.value = (value == valueInt) ? String.valueOf(valueInt) : String.valueOf(value);
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
