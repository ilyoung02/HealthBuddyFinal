package com.example.healthbuddypro.Login;

public class LoginResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        // 로그인 후에 필요한 추가 정보가 있을 경우 여기에 추가
    }

    // Getters and Setters
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
