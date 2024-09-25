package com.example.healthbuddypro.Matching;

import java.util.List;

public class ProfileResponse {
    private int code;
    private String message;
    private List<UserProfile> data;

    public ProfileResponse(int code, String message, List<UserProfile> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // code : 응답 코드
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    // 응답 메시지 (매칭 프로필 목록 조회 성공)
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    // data : UserProfile 객체의 리스트 -> 프로필 데이터를 담음
    public List<UserProfile> getData() {
        return data;
    }
    public void setData(List<UserProfile> data) {
        this.data = data;
    }
}
