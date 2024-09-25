package com.example.healthbuddypro.Matching;
import java.io.Serializable;

public class UserProfile implements Serializable {
    private String nickname;
//    private int imageResId;
    private String image;
    private String gender;
    private int age;


    public UserProfile(String nickname, String gender, int age, String image) {
        this.nickname = nickname;
        this.image = image;
        this.gender = gender;
        this.age = age;
//        this.imageResId = imageResId;
    }

    // 닉네임
    public String getNickName() {
        return nickname;
    }
    public void setNickName(String nickname) {
        this.nickname = nickname;
    }

    // 이미지
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = this.image;
    }

    // 성별
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    // 나이
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}

