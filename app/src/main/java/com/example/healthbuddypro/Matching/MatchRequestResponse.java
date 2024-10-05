package com.example.healthbuddypro.Matching;

public class MatchRequestResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private int matchRequestId;

        // Getters and Setters
        public int getMatchRequestId() {
            return matchRequestId;
        }

        public void setMatchRequestId(int matchRequestId) {
            this.matchRequestId = matchRequestId;
        }
    }

    // Getters and Setters for MatchRequestResponse
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