package com.example.healthbuddypro.FitnessTab;

public class TeamUsersResponse {
    private int code;
    private String message;
    private TeamUsersData data;

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

    public TeamUsersData getData() {
        return data;
    }

    public void setData(TeamUsersData data) {
        this.data = data;
    }

    public static class TeamUsersData {
        private int user1Id;
        private int user2Id;

        // Getters and Setters
        public int getUser1Id() {
            return user1Id;
        }

        public void setUser1Id(int user1Id) {
            this.user1Id = user1Id;
        }

        public int getUser2Id() {
            return user2Id;
        }

        public void setUser2Id(int user2Id) {
            this.user2Id = user2Id;
        }
    }
}
