package com.example.healthbuddypro.Matching;
import java.io.Serializable;

public class UserProfile implements Serializable {
    private String name;
    private String details;
    private int imageResId;

    public UserProfile(String name, String details, int imageResId) {
        this.name = name;
        this.details = details;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public int getImageResId() {
        return imageResId;
    }
}

