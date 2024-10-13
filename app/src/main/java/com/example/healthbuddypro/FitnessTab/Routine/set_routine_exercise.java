package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.R;

import java.util.ArrayList;

public class set_routine_exercise extends AppCompatActivity {

    private LinearLayout exerciseContainer;
    private String routineTitle;
    private ArrayList<String> selectedDays;
    private ArrayList<Exercise> exerciseList = new ArrayList<>();
    private Button btn_makeRoutineComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_routine_exercise);

        exerciseContainer = findViewById(R.id.exerciseContainer);
        btn_makeRoutineComplete = findViewById(R.id.btn_makeRoutineComplete); // 버튼 연결

        Intent intent = getIntent();
        routineTitle = intent.getStringExtra("routineTitle");
        selectedDays = intent.getStringArrayListExtra("selectedDays");

        // 선택된 요일에 맞게 레이아웃 동적으로 추가
        if (selectedDays != null && !selectedDays.isEmpty()) {
            for (String day : selectedDays) {
                addDayLayout(day);
            }
        }

        // 생성완료 버튼
        btn_makeRoutineComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(set_routine_exercise.this, make_routine04.class);
                startActivity(intent1);
            }
        });
    }

    private void addDayLayout(final String day) {
        View dayView = getLayoutInflater().inflate(R.layout.activity_add_workout_item, null);

        TextView dayText = dayView.findViewById(R.id.selectedDate);
        dayText.setText(day);

        // 운동 추가 버튼 설정
        dayView.findViewById(R.id.addWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(set_routine_exercise.this, AddExerciseActivity.class);
                startActivityForResult(intent, selectedDays.indexOf(day));
            }
        });

        // LayoutParams 설정 (박스 간 간격 추가)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 15);  // 아래쪽 마진 15dp 추가

        dayView.setLayoutParams(params);  // 레이아웃에 적용

        exerciseContainer.addView(dayView); // 선택된 요일 추가
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<Exercise> addedExercises = data.getParcelableArrayListExtra("selectedExercises");
            if (addedExercises != null && !addedExercises.isEmpty()) {
                LinearLayout layoutForDay = (LinearLayout) exerciseContainer.getChildAt(requestCode);
                for (Exercise exercise : addedExercises) {
                    addExerciseToDay(layoutForDay, exercise);
                }
            }
        }
    }

    private void addExerciseToDay(LinearLayout dayLayout, Exercise exercise) {
        TextView workoutInfo = new TextView(this);
        workoutInfo.setText(exercise.getName() + " - " + exercise.getReps() + "회 x " + exercise.getSetCount() + "세트");

        // 글자 크기, 스타일지정
        workoutInfo.setTextSize(14);
        workoutInfo.setTypeface(null, Typeface.BOLD);

        // LayoutParams 설정
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // 운동항목 추가될때 마진 추가
        params.setMargins(0, 0, 0, 8);  // 아래쪽 마진 8dp 추가
        workoutInfo.setLayoutParams(params);  // TextView에 LayoutParams 적용
        dayLayout.addView(workoutInfo);  // 레이아웃에 TextView 추가
    }

}
