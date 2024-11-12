package com.example.healthbuddypro.FitnessTab;


public class TeamRankingResponse {
    private int code;
    private String message;
    private TeamRankingData data;

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

    public TeamRankingData getData() {
        return data;
    }

    public void setData(TeamRankingData data) {
        this.data = data;
    }

    public static class TeamRankingData {
        private int points;
        private int ranks;

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public int getRanks() {
            return ranks;
        }

        public void setRanks(int ranks) {
            this.ranks = ranks;
        }
    }
}
