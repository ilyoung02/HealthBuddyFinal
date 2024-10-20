package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fix_routine extends AppCompatActivity {

    private LinearLayout exerciseContainer;
    private ArrayList<Exercise> exerciseList = new ArrayList<>(); // 수정할 운동 정보 리스트
    private static final String BASE_URL = "http://165.229.89.154:8080/";

    //todo : 서버에서 등록한 운동 리스트 전부 받아와서 동적추가해서 띄우는거 + 수정버튼 누르면 put 해서 수정하는거 추가
    // 아직 api 업데이트 안되서 못함 -> 등록한 루틴 전체가 정의된 테이블을 참조해야 하는데 그게 아직 없음
    // 수정완료 버튼 누르면 다시 루틴목록으로 돌아가고 갱신되는지 확인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_routine);

        exerciseContainer = findViewById(R.id.exercise_fixList_container);

        // 서버에서 운동 정보를 가져오기
//        fetchRoutineDataFromServer();

        // 운동 정보 동적으로 추가
        for (Exercise exercise : exerciseList) {
            addExerciseToLayout(exercise);
        }
    }

//    private void fetchRoutineDataFromServer() {
//        ApiService apiService = RetrofitClient.getApiService(BASE_URL);
//        Call<RoutineResponse> call = apiService.getRoutineData(1);  // teamId 예시 사용
//
//        call.enqueue(new Callback<RoutineResponse>() {
//            @Override
//            public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // 받아온 운동 정보 리스트 설정
//                    RoutineResponse routineResponse = response.body();
//                    exerciseList.clear();
//                    for (RoutineResponse.DayRoutine dayRoutine : routineResponse.getDays()) {
//                        exerciseList.addAll(dayRoutine.getWorkouts());
//                    }
//                    // 운동 정보 화면에 추가
//                    for (Exercise exercise : exerciseList) {
//                        addExerciseToLayout(exercise);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RoutineResponse> call, Throwable t) {
//                Toast.makeText(fix_routine.this, "운동 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    // 운동 정보를 동적으로 추가하는 메서드
    private void addExerciseToLayout(Exercise exercise) {
        View exerciseView = LayoutInflater.from(this).inflate(R.layout.activity_exercise_item, null, false);

        TextView exerciseName = exerciseView.findViewById(R.id.exerciseName);
        TextView exerciseInfo = exerciseView.findViewById(R.id.exerciseInfo);
        ImageButton btn_fixRoutineEx = exerciseView.findViewById(R.id.btn_fixRoutineEx);

        exerciseName.setText(exercise.getName());
        exerciseInfo.setText(exercise.getSetCount() + "세트 x " + exercise.getReps() + "회");

        // 수정 버튼 클릭 시 운동 정보 수정
        btn_fixRoutineEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fix_routine.this, fixing_routineExercise.class);
                startActivityForResult(intent, 100);
            }
        });

        exerciseContainer.addView(exerciseView);
    }

    // 수정한 운동 정보를 서버에 업데이트 (PUT)
//    private void updateRoutineDataOnServer() {
//        ApiService apiService = RetrofitClient.getApiService();
//        RoutineResponse routineToUpdate = new RoutineResponse();  // 수정된 루틴 데이터
//        routineToUpdate.setTeamId(1);  // 팀 ID 예시
//        routineToUpdate.setTitle("수정된 루틴 제목");
//        routineToUpdate.setDays(exerciseList);  // 수정된 운동 데이터 설정
//
//        Call<Void> call = apiService.updateRoutineData(routineToUpdate);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(fix_routine.this, "운동 정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(fix_routine.this, "수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(fix_routine.this, "네트워크 오류로 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
