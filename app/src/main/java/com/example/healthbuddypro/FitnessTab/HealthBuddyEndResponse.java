package com.example.healthbuddypro.FitnessTab;

public class HealthBuddyEndResponse {

    private int code;
    private String message;
    private Data data;

    public HealthBuddyEndResponse() {
    }

    public HealthBuddyEndResponse(int code, String message, Data data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
    }
}
