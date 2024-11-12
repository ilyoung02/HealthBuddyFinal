package com.example.healthbuddypro.FitnessTab;

import java.util.List;

public class TeamRankingListResponse {
    private int code;
    private String message;
    private List<TeamRankingData> data;

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

    public List<TeamRankingData> getData() {
        return data;
    }

    public void setData(List<TeamRankingData> data) {
        this.data = data;
    }

    public static class TeamRankingData {
        private int teamId;
        private int points;
        private int ranks;

        public int getTeamId() {
            return teamId;
        }

        public void setTeamId(int teamId) {
            this.teamId = teamId;
        }

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
