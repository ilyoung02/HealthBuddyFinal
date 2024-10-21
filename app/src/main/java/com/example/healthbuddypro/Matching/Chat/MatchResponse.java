package com.example.healthbuddypro.Matching.Chat;

public class MatchResponse {
    private int code;
    private String message;
    private MatchData data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public MatchData getData() {
        return data;
    }

    public class MatchData {
        private int teamId;

        public int getTeamId() {
            return teamId;
        }
    }
}

