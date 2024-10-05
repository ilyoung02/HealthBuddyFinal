package com.example.healthbuddypro.Matching;
import java.io.Serializable;

public class UserProfile implements Serializable {
    private int profileId;
    private String nickname;
    private String image;
    private String gender;
    private int age;
    private int workoutYears;  // 구력(운동 경력)
    private int likeCount;

    public UserProfile(int profileId, String nickname, String image, String gender, int age, int workoutYears, int likeCount) {
        this.profileId = profileId;
        this.nickname = nickname;
        this.image = image;
        this.gender = gender;
        this.age = age;
        this.workoutYears = workoutYears;
        this.likeCount = likeCount;
    }

    // 프로필 ID
    public int getProfileId() {
        return profileId;
    }
    public void setProfileId(int profileId) {
        this.profileId = profileId;
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

    public int getWorkoutYears() {
        return workoutYears;  // 구력(운동 경력) 반환 메서드
    }
    public void setWorkoutYears(int workoutYears) {
        this.workoutYears = workoutYears;
    }

    // 좋아요 수
    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}

