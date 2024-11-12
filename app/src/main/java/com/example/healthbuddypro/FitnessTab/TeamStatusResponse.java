package com.example.healthbuddypro.FitnessTab;

public class TeamStatusResponse {
    private int code;
    private String message;
    private TeamData data;

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

    public TeamData getData() {
        return data;
    }

    public void setData(TeamData data) {
        this.data = data;
    }

    public static class TeamData {
        private String teamStatus; // "진행" or "종료"

        public String getTeamStatus() {
            return teamStatus;
        }

        public void setTeamStatus(String teamStatus) {
            this.teamStatus = teamStatus;
        }
    }
}
