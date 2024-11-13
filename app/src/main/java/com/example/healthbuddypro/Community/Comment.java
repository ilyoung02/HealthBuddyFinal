package com.example.healthbuddypro.Community;

public class Comment {

    private String username;
    private String content;

    // 기본 생성자
    public Comment() {
    }

    // 생성자
    public Comment(String username, String content) {
        this.username = username;
        this.content = content;
    }

    // getter와 setter 추가
    public String getAuthor() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
