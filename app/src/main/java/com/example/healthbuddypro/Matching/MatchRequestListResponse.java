package com.example.healthbuddypro.Matching;

import java.util.List;

public class MatchRequestListResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private List<MatchRequest> sentRequests;
        private List<MatchRequest> receivedRequests;

        public static class MatchRequest {
            private int matchRequestId;
            private int senderId;
            private int receiverId;
            private String senderNickname;
            private String receiverNickname;
            private String status;

            // Getters and Setters
            public int getMatchRequestId() {
                return matchRequestId;
            }

            public void setMatchRequestId(int matchRequestId) {
                this.matchRequestId = matchRequestId;
            }

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

            public String getSenderNickname() {
                return senderNickname;
            }

            public void setSenderNickname(String senderNickname) {
                this.senderNickname = senderNickname;
            }

            public String getReceiverNickname() {
                return receiverNickname;
            }

            public void setReceiverNickname(String receiverNickname) {
                this.receiverNickname = receiverNickname;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }

        // Getters and Setters for sent and received requests
        public List<MatchRequest> getSentRequests() {
            return sentRequests;
        }

        public void setSentRequests(List<MatchRequest> sentRequests) {
            this.sentRequests = sentRequests;
        }

        public List<MatchRequest> getReceivedRequests() {
            return receivedRequests;
        }

        public void setReceivedRequests(List<MatchRequest> receivedRequests) {
            this.receivedRequests = receivedRequests;
        }
    }

    // Getters and Setters for MatchRequestListResponse
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}