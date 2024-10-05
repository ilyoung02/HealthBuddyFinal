package com.example.healthbuddypro.Matching;

public class LikeResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        // You can include additional data if needed
    }

    // Getters and Setters for LikeResponse
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
}
