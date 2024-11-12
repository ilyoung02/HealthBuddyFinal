package com.example.healthbuddypro.FitnessTab;

public class ReviewRequest {
    private int writerId;
    private String review;

    public ReviewRequest(int writerId, String review) {
        this.writerId = writerId;
        this.review = review;
    }

    public int getWriterId() {
        return writerId;
    }

    public void setWriterId(int writerId) {
        this.writerId = writerId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
