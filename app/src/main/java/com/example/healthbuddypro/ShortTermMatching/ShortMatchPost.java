package com.example.healthbuddypro.ShortTermMatching;

public class ShortMatchPost {
    private int senderId;  // 글을 올린 사용자 ID
    private int receiverId;
    private String title;
    private String health;  // 운동 부위 (getHealth)
    private String content; // 사용자가 작성한 글 (getContent)
    private String location;
    private String category;

    // 기본 생성자 추가
    public ShortMatchPost() {
    }

    public ShortMatchPost(int senderId, int receiverId, String title, String health, String content, String location, String category) {
        this.senderId = senderId;  // senderId는 현재 사용자의 userId로 설정
        this.receiverId = receiverId;
        this.title = title;
        this.health = health;
        this.content = content;
        this.location = location;
        this.category = category;
    }

    // Getter와 Setter
    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHealth() {
        return health; // 운동 부위
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getContent() {
        return content; // 사용자가 작성한 글
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
