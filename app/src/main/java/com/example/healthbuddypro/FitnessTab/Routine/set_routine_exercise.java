package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class set_routine_exercise extends AppCompatActivity {

    private LinearLayout exerciseContainer;
    private String routineTitle;
    private ArrayList<String> selectedDays;
    private ArrayList<Exercise> exerciseList = new ArrayList<>();
    private Button btn_makeRoutineComplete;
    private ImageButton backButton;

    private Map<String, List<RoutineRequest.Workout>> workoutsByDay = new HashMap<>();

    // 한글 운동 이름을 영어로 매핑하는 해시맵
    private final HashMap<String, String> exerciseMapping = new HashMap<String, String>() {{
        // 등
        put("랫 풀 다운", "LAT_PULLDOWN");
        put("바벨 로우", "BARBELL_ROW");
        put("덤벨 로우", "DUMBBELL_ROW");
        put("데드리프트", "DEADLIFT");
        put("시티드 로우", "SEATED_ROW");
        put("풀업", "PULL_UP");
        put("케이블 로우", "CABLE_ROW");
        put("티 바 로우", "T_BAR_ROW");

        // 가슴
        put("벤치 프레스", "BENCH_PRESS");
        put("인클라인 벤치 프레스", "INCLINE_BENCH_PRESS");
        put("디클라인 벤치 프레스", "DECLINE_BENCH_PRESS");
        put("덤벨 플라이", "DUMBBELL_FLY");
        put("체스트 프레스", "CHEST_PRESS");
        put("펙덱 플라이", "PEC_DECK_FLY");
        put("푸쉬업", "PUSH_UP");
        put("케이블 크로스오버", "CABLE_CROSSOVER");

        // 하체
        put("스쿼트", "SQUAT");
        put("레그 프레스", "LEG_PRESS");
        put("런지", "LUNGE");
        put("레그 컬", "LEG_CURL");
        put("레그 익스텐션", "LEG_EXTENSION");
        put("카프 레이즈", "CALF_RAISE");
        put("힙 쓰러스트", "HIP_THRUST");
        put("스티프 레그 데드리프트", "STIFF_LEG_DEADLIFT");

        // 유산소
        put("러닝머신", "TREADMILL");
        put("자전거", "BICYCLE");
        put("스텝퍼", "STEPPER");
        put("로잉머신", "ROWING_MACHINE");
        put("점핑잭", "JUMPING_JACK");
        put("버피", "BURPEE");
        put("마운틴 클라이머", "MOUNTAIN_CLIMBER");
        put("사이클", "CYCLE");

        // 어깨
        put("숄더 프레스", "SHOULDER_PRESS");
        put("사이드 레터럴 레이즈", "SIDE_LATERAL_RAISE");
        put("프론트 레이즈", "FRONT_RAISE");
        put("리어 델트 플라이", "REAR_DELT_FLY");
        put("업라이트 로우", "UPRIGHT_ROW");
        put("덤벨 숄더 프레스", "DUMBBELL_SHOULDER_PRESS");
        put("케이블 레이즈", "CABLE_RAISE");
        put("아놀드 프레스", "ARNOLD_PRESS");

        // 이두
        put("바벨 컬", "BARBELL_CURL");
        put("덤벨 컬", "DUMBBELL_CURL");
        put("해머 컬", "HAMMER_CURL");
        put("프리처 컬", "PREACHER_CURL");
        put("컨센트레이션 컬", "CONCENTRATION_CURL");
        put("케이블 컬", "CABLE_CURL");
        put("머신 컬", "MACHINE_CURL");
        put("크로스 바디 해머 컬", "CROSS_BODY_HAMMER_CURL");

        // 삼두
        put("트라이셉스 익스텐션", "TRICEPS_EXTENSION");
        put("덤벨 킥백", "DUMBBELL_KICKBACK");
        put("케이블 푸쉬다운", "CABLE_PUSH_DOWN");
        put("프렌치 프레스", "FRENCH_PRESS");
        put("스컬 크러셔", "SKULL_CRUSHER");
        put("오버헤드 익스텐션", "OVERHEAD_EXTENSION");
        put("딥스", "DIPS");
        put("트라이앵글 푸쉬업", "TRIANGLE_PUSH_UP");

        // 복근
        put("크런치", "CRUNCH");
        put("레그레이즈", "LEG_RAISE");
        put("플랭크", "PLANK");
        put("싯업", "SIT_UP");
        put("러시안 트위스트", "RUSSIAN_TWIST");
        put("바이시클 크런치", "BICYCLE_CRUNCH");
        put("힐터치", "HEEL_TOUCH");
        put("하이 플랭크", "HIGH_PLANK");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_routine_exercise);

        exerciseContainer = findViewById(R.id.exerciseContainer);
        backButton = findViewById(R.id.backButton);
        btn_makeRoutineComplete = findViewById(R.id.btn_makeRoutineComplete);

        Intent intent = getIntent();
        routineTitle = intent.getStringExtra("routineTitle");
        selectedDays = intent.getStringArrayListExtra("selectedDays");

        if (selectedDays != null && !selectedDays.isEmpty()) {
            for (String day : selectedDays) {
                addDayLayout(day);
                workoutsByDay.put(day, new ArrayList<>());
            }
        }

        backButton.setOnClickListener(view -> finish());

        btn_makeRoutineComplete.setOnClickListener(view -> sendRoutineToServer());

        //memo : Firestore에서 routineId를 가져와 내부 저장소에 저장
        fetchRoutineIdFromFirestore();
    }

    private void addDayLayout(final String day) {
        View dayView = getLayoutInflater().inflate(R.layout.activity_add_workout_item, null);

        TextView dayText = dayView.findViewById(R.id.selectedDate);
        dayText.setText(day);

        dayView.findViewById(R.id.addWorkout).setOnClickListener(v -> {
            Intent intent = new Intent(set_routine_exercise.this, AddExerciseActivity.class);
            startActivityForResult(intent, selectedDays.indexOf(day));
        });

        exerciseContainer.addView(dayView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<Exercise> addedExercises = data.getParcelableArrayListExtra("selectedExercises");
            if (addedExercises != null && !addedExercises.isEmpty()) {
                String day = selectedDays.get(requestCode);
                List<RoutineRequest.Workout> workouts = workoutsByDay.get(day);
                if (workouts == null) workouts = new ArrayList<>();

                for (Exercise exercise : addedExercises) {
                    workouts.add(new RoutineRequest.Workout(exercise.getName(), exercise.getSetCount(), exercise.getReps()));
                }
                workoutsByDay.put(day, workouts);

                LinearLayout layoutForDay = (LinearLayout) exerciseContainer.getChildAt(requestCode);
                for (Exercise exercise : addedExercises) {
                    addExerciseToDay(layoutForDay, exercise);
                }
            }
        }
    }

    private void addExerciseToDay(LinearLayout dayLayout, Exercise exercise) {
        LinearLayout exerciseEntry = new LinearLayout(this);
        exerciseEntry.setOrientation(LinearLayout.HORIZONTAL);
        exerciseEntry.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        exerciseEntry.setPadding(0, 10, 0, 10);

        TextView workoutInfo = new TextView(this);
        workoutInfo.setText(exercise.getName() + " " + exercise.getReps() + "회 x " + exercise.getSetCount() + "세트");
        workoutInfo.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));
        workoutInfo.setTextSize(14);
        workoutInfo.setTypeface(null, Typeface.BOLD);
        workoutInfo.setTextColor(Color.BLACK);

        Button deleteButton = new Button(this);
        deleteButton.setText("X");
        deleteButton.setTextSize(16);
        deleteButton.setBackgroundColor(Color.TRANSPARENT);
        deleteButton.setOnClickListener(v -> dayLayout.removeView(exerciseEntry));

        exerciseEntry.addView(workoutInfo);
        exerciseEntry.addView(deleteButton);

        dayLayout.addView(exerciseEntry);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        ));
        divider.setBackgroundColor(Color.GRAY);
        dayLayout.addView(divider);
    }

    private String mapKoreanDayToEnglish(String koreanDay) {
        Map<String, String> dayMap = new HashMap<>();
        dayMap.put("월요일", "MON");
        dayMap.put("화요일", "TUE");
        dayMap.put("수요일", "WED");
        dayMap.put("목요일", "THU");
        dayMap.put("금요일", "FRI");
        dayMap.put("토요일", "SAT");
        dayMap.put("일요일", "SUN");

        return dayMap.getOrDefault(koreanDay, koreanDay);
    }

    private int getTeamId() {
        SharedPreferences userPreferences = getSharedPreferences("localID", MODE_PRIVATE);
        int userId = userPreferences.getInt("userId", -1);

        if (userId == -1) {
            Log.e("set_routine_exercise", "User ID not found.");
            return -1;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);
        int teamId = sharedPreferences.getInt("teamId", -1);
        Log.d("teamId 조회@@@@@", "teamId : " + teamId);

        if (teamId == -1) {
            Log.e("set_routine_exercise", "Team ID not found for userId: " + userId);
        }

        return teamId;
    }

    private void sendRoutineToServer() {
        List<RoutineRequest.DayRoutine> days = new ArrayList<>();
        for (String day : selectedDays) {
            String englishDay = mapKoreanDayToEnglish(day);
            List<RoutineRequest.Workout> workouts = new ArrayList<>();

            List<RoutineRequest.Workout> originalWorkouts = workoutsByDay.get(day);
            if (originalWorkouts != null) {
                for (RoutineRequest.Workout workout : originalWorkouts) {
                    String englishWorkoutName = exerciseMapping.getOrDefault(workout.getWorkout(), workout.getWorkout());
                    workouts.add(new RoutineRequest.Workout(englishWorkoutName, workout.getCounts(), workout.getRep()));
                }
            }

            if (!workouts.isEmpty()) {
                days.add(new RoutineRequest.DayRoutine(englishDay, workouts));
            }
        }

        RoutineRequest routineRequest = new RoutineRequest(getTeamId(), routineTitle, days);
        Call<RoutineCreationResponse> call = RetrofitClient.getApiService().createRoutine(routineRequest);

        call.enqueue(new Callback<RoutineCreationResponse>() {
            @Override
            public void onResponse(Call<RoutineCreationResponse> call, Response<RoutineCreationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int routineId = response.body().getData().getRoutineId();
                    saveRoutineIdToSharedPreferences(routineId);
                    saveRoutineIdToFirestore(routineId);
                    Toast.makeText(set_routine_exercise.this, "루틴이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(set_routine_exercise.this, make_routine04.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(set_routine_exercise.this, "루틴 생성 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RoutineCreationResponse> call, Throwable t) {
                Toast.makeText(set_routine_exercise.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRoutineIdToSharedPreferences(int routineId) {
        SharedPreferences userPreferences = getSharedPreferences("localID", MODE_PRIVATE);
        int userId = userPreferences.getInt("userId", -1);

        if (userId != -1) {
            SharedPreferences sharedPreferences = getSharedPreferences("routineId" + userId + "_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("routineId", routineId);
            editor.apply();
        } else {
            Log.e("set_routine_exercise", "User ID not found. Cannot save routineId.");
        }
    }

    private void saveRoutineIdToFirestore(int routineId) {
        int teamId = getTeamId();
        if (teamId == -1) {
            Log.e("set_routine_exercise", "teamId가 유효하지 않습니다. routineId를 저장할 수 없습니다.");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("routineIdShare")
                .document(String.valueOf(teamId))
                .set(new HashMap<String, Object>() {{
                    put("routineId", routineId);
                    put("timestamp", System.currentTimeMillis());
                }})
                .addOnSuccessListener(aVoid -> Log.d("set_routine_exercise", "routineId 저장 성공: " + routineId))
                .addOnFailureListener(e -> Log.e("set_routine_exercise", "routineId 저장 실패: " + e.getMessage()));
    }

    private void fetchRoutineIdFromFirestore() {
        int teamId = getTeamId();
        if (teamId == -1) {
            Log.e("set_routine_exercise", "teamId가 유효하지 않습니다. routineId를 조회할 수 없습니다.");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("routineIdShare")
                .document(String.valueOf(teamId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int routineId = documentSnapshot.getLong("routineId").intValue();
                        saveRoutineIdToSharedPreferences(routineId);
                        Log.d("set_routine_exercise", "routineId 조회 성공 및 저장: " + routineId);
                    } else {
                        Log.d("set_routine_exercise", "해당 teamId에 대한 routineId가 존재하지 않습니다.");
                    }
                })
                .addOnFailureListener(e -> Log.e("set_routine_exercise", "routineId 조회 실패: " + e.getMessage()));
    }
}
