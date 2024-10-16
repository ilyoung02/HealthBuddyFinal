package com.example.healthbuddypro.Profile;

import com.example.healthbuddypro.SignUp.SignUpResponse;

public class EditProfileResponse {
    private int code;
    private String message;
    private SignUpResponse.Data data;


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

    public SignUpResponse.Data getData() {
        return data;
    }

    public void setData(SignUpResponse.Data data) {
        this.data = data;
    }
}


