package com.example.healthbuddypro.Matching.Chat;

public class MessageRequest {
    private int profileId; // 채팅 대상 프로필 ID
    private String message; // 전송할 메시지

    public MessageRequest(int profileId, String message) {
        this.profileId = profileId;
        this.message = message;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

