package com.example.healthbuddypro.Community;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private String username;
    private String postTime;
    private String content;
    private String id;
    private List<Comment> comments = new ArrayList<>(); // 초기화

    // 기본 생성자
    public Post() {
    }

    // 생성자
    public Post(String username, String postTime, String content) {
        this.username = username;
        this.postTime = postTime;
        this.content = content;
    }

    // getter와 setter 추가
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // comments 리스트에 대한 getter와 setter 추가
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
