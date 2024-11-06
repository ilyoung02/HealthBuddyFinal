package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class routine_check extends AppCompatActivity {

    private TextView DdayView, todayDate, workoutTimerView;
    private ImageButton workoutTimer_startBtn;
    private Button btn_completeTodayWorkout;
    private LinearLayout routineCheckItemLayout;
    private ScrollView routineCheckScrollview;
    private long workoutStartTime;
    private Handler timerHandler = new Handler();
    private static final String BASE_URL = "http://165.229.89.154:8080/";
    private int routineId = 1; // 예시 루틴 ID
    //TODO
    // 1. routineId 내부저장소에서 가져오도록 해야함
    // 2. /api/routines/{routineId}/details?day= 에서 가져온 오늘 날짜에 대한 운동루틴 정보를 나타내고 세트별로 체크되도록 해야함
    // 3. 전부 체크가 된 경우 운동 완료 누르면 랭크 포인트가 증가된 것을 서버로 보내줘야함
    // 4. Dday, 오늘날짜, 운동 시간 타이머 동작 확인 -> 운동 타이머는 따로 저장은 x


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine_check);

        DdayView = findViewById(R.id.DdayView);
        todayDate = findViewById(R.id.todayDate);
        workoutTimerView = findViewById(R.id.workoutTimerView);
        workoutTimer_startBtn = findViewById(R.id.workoutTimer_startBtn);
        btn_completeTodayWorkout = findViewById(R.id.btn_completeTodayWorkout);
        routineCheckItemLayout = findViewById(R.id.routineCheck_item);
        routineCheckScrollview = findViewById(R.id.routineCheck_scrollview);

        // 오늘 날짜 설정
        setTodayDate();

        // 서버에서 루틴 정보 가져오기
        fetchRoutineData();

        // 타이머 시작
        workoutTimer_startBtn.setOnClickListener(view -> startTimer());

        // 운동 완료 버튼 클릭 시
        btn_completeTodayWorkout.setOnClickListener(view -> {
            Intent intent = new Intent(routine_check.this, complete_today_workout.class);
            startActivity(intent);
        });
    }

    private void setTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MM.dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        todayDate.setText(currentDate);
    }

    // url 에 오늘 날짜값이 들어가야되서 이렇게 해주는거임
    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "MON";
            case Calendar.TUESDAY:
                return "TUE";
            case Calendar.WEDNESDAY:
                return "WED";
            case Calendar.THURSDAY:
                return "THU";
            case Calendar.FRIDAY:
                return "FRI";
            case Calendar.SATURDAY:
                return "SAT";
            case Calendar.SUNDAY:
                return "SUN";
            default:
                return "MON";
        }
    }

    // 서버에서 루틴 정보 가져오기
    private void fetchRoutineData() {
        ApiService apiService = RetrofitClient.getApiService();
        String day = getCurrentDay(); // 현재 요일 가져오기

        Call<RoutineDetailsResponse> call = apiService.getRoutineDetails(routineId, day);
        call.enqueue(new Callback<RoutineDetailsResponse>() {
            @Override
            public void onResponse(Call<RoutineDetailsResponse> call, Response<RoutineDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RoutineExercise> serverExercises = response.body().getData();
                    displayRoutineExercises(serverExercises); // 운동 목록을 화면에 표시
                } else {
                    Toast.makeText(routine_check.this, "루틴 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RoutineDetailsResponse> call, Throwable t) {
                Toast.makeText(routine_check.this, "서버 통신 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 운동 루틴 목록을 화면에 표시하는 메서드
    private void displayRoutineExercises(List<RoutineExercise> exercises) {
        for (RoutineExercise exercise : exercises) {
            View exerciseItem = getLayoutInflater().inflate(R.layout.activity_routine_check_exercise_item, routineCheckItemLayout, false);
            TextView exerciseName = exerciseItem.findViewById(R.id.exerciseName);
            TextView setInfo = exerciseItem.findViewById(R.id.setInfo);

            exerciseName.setText(exercise.getWorkout());
            setInfo.setText(exercise.getCounts() + "세트 x " + exercise.getRep() + "회");

            // 세트 체크 다이얼로그 생성
            exerciseItem.setOnClickListener(view -> showSetCheckDialog(exercise));

            routineCheckItemLayout.addView(exerciseItem);
        }
    }

    // 세트 체크 다이얼로그 생성
    private void showSetCheckDialog(RoutineExercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(exercise.getWorkout() + " 세트 체크");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // 세트별 체크박스 생성
        for (int i = 0; i < exercise.getCounts(); i++) {
            final TextView setCheckView = new TextView(this);
            setCheckView.setText((i + 1) + "세트 x " + exercise.getRep() + "회");
            setCheckView.setBackground(getDrawable(R.drawable.custom_checkbox_unchecked));
            setCheckView.setPadding(20, 20, 20, 20);
            setCheckView.setOnClickListener(v -> {
                // 체크 상태 토글
                if (setCheckView.getBackground().equals(getDrawable(R.drawable.custom_checkbox_checked))) {
                    setCheckView.setBackground(getDrawable(R.drawable.custom_checkbox_unchecked));
                } else {
                    setCheckView.setBackground(getDrawable(R.drawable.custom_checkbox_checked));
                }
            });
            layout.addView(setCheckView);
        }

        builder.setView(layout);
        builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    // 타이머 시작
    private void startTimer() {
        workoutStartTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    // 타이머 실행
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - workoutStartTime;
            int seconds = (int) (elapsedTime / 1000) % 60;
            int minutes = (int) (elapsedTime / 1000 / 60) % 60;
            int hours = (int) (elapsedTime / 1000 / 60 / 60);

            workoutTimerView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };
}
