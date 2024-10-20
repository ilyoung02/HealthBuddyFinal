package com.example.healthbuddypro.FitnessTab.Routine;

import java.util.List;

public class RoutineDetailsResponse {

    private int code;
    private String message;
    private List<RoutineExercise> data;

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

    public List<RoutineExercise> getData() {
        return data;
    }

    public void setData(List<RoutineExercise> data) {
        this.data = data;
    }
}
