package com.example.healthbuddypro.Matching.Chat;

// 채팅 메시지의 내용과 송신자/수신자 여부 저장
public class Message {
    private String messageText;
    private boolean isSender;  // true이면 사용자가 보낸 메시지, false이면 상대방이 보낸 메시지

    public Message(String messageText, boolean isSender) {
        this.messageText = messageText;
        this.isSender = isSender;
    }

    public String getMessageText() {
        return messageText;
    }

    public boolean isSender() {
        return isSender;
    }
}
