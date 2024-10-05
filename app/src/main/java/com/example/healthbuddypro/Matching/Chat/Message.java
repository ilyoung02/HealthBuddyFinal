package com.example.healthbuddypro.Matching.Chat;

// 채팅 메시지의 내용과 송신자/수신자 여부 저장
public class Message {
    private String text;
    private boolean isMine; // 메시지가 내 것인지 여부

    public Message(String text, boolean isMine) {
        this.text = text;
        this.isMine = isMine;
    }

    public String getText() {
        return text;
    }

    public boolean isMine() {
        return isMine;
    }
}

