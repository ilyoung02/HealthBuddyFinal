package com.example.healthbuddypro.Matching;

// ProfileDetailActivity의 단일 프로필을 받기 위한 Response
public class ProfileResponse {
    private UserProfile data;

    public UserProfile getData() {
        return data;
    }
    public void setData(UserProfile data) {
        this.data = data;
    }
}
