package com.example.healthbuddypro.FitnessTab.TeamChat;

import com.google.firebase.firestore.FieldValue;

public class Message {

    private String content;
    private boolean isSender;
    private String senderName;  // 메시지 보낸 사람의 이름
    private int senderId; // 메시지 보낸 사람의 ID
    private Object timestamp;  // 타임스탬프 필드로 서버 타임스탬프를 저장

    // Firestore에서 객체를 역직렬화할 때 기본 생성자가 필요합니다.
    public Message() {}

    // 기존 생성자
    public Message(String content, boolean isSender, String senderName, int senderId) {
        this.content = content;
        this.isSender = isSender;
        this.senderName = senderName;
        this.senderId = senderId;
        this.timestamp = FieldValue.serverTimestamp();  // 서버 타임스탬프 설정
    }

    // getter와 setter 추가
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSender() {
        return isSender;
    }
    public void setSender(boolean sender) {
        isSender = sender;
    }

    public String getSenderName() {
        return senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public int getSenderId() {
        return senderId;
    }
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    public Object getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}