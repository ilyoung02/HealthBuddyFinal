package com.example.healthbuddypro.FitnessTab.Routine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.healthbuddypro.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddExerciseActivity extends Activity {

    private ListView categoryListView, exerciseListView;
    private LinearLayout selectedItemsLayout;
    private Button completeButton, closeButton;
    private ArrayList<Exercise> selectedExercises;
    private String selectedExerciseName;
    private Map<String, String[]> exerciseMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        categoryListView = findViewById(R.id.categoryListView);
        exerciseListView = findViewById(R.id.exerciseListView);
        selectedItemsLayout = findViewById(R.id.selectedItemsLayout);
        completeButton = findViewById(R.id.completeButton);
        closeButton = findViewById(R.id.closeButton);
        selectedExercises = new ArrayList<>();

        // 운동 카테고리와 해당 운동 데이터
        String[] categories = {"가슴", "등", "하체", "유산소", "어깨", "이두", "삼두", "복근"};
        exerciseMap = new HashMap<>();
        exerciseMap.put("가슴", new String[]{"벤치프레스", "인클라인 벤치프레스", "디클라인 벤치프레스", "덤벨 플라이", "체스트 프레스", "펙덱 플라이", "푸쉬업", "케이블 크로스오버"});
        exerciseMap.put("등", new String[]{"랫풀다운", "바벨로우", "덤벨로우", "데드리프트", "시티드로우", "풀업", "케이블 로우", "티바로우"});
        exerciseMap.put("하체", new String[]{"스쿼트", "레그프레스", "런지", "레그컬", "레그익스텐션", "카프레이즈", "힙 쓰러스트", "스티프 레그 데드리프트"});
        exerciseMap.put("유산소", new String[]{"러닝머신", "자전거", "스텝퍼", "로잉머신", "점핑잭", "버피", "마운틴 클라이머", "사이클"});
        exerciseMap.put("어깨", new String[]{"숄더프레스", "사이드 레터럴 레이즈", "프론트 레이즈", "리어 델트 플라이", "업라이트 로우", "덤벨 숄더 프레스", "케이블 레이즈", "아놀드 프레스"});
        exerciseMap.put("이두", new String[]{"바벨 컬", "덤벨 컬", "해머 컬", "프리처 컬", "컨센트레이션 컬", "케이블 컬", "머신 컬", "크로스 바디 해머 컬"});
        exerciseMap.put("삼두", new String[]{"트라이셉스 익스텐션", "덤벨 킥백", "케이블 푸쉬다운", "프렌치 프레스", "스컬 크러셔", "오버헤드 익스텐션", "딥스", "트라이앵글 푸쉬업"});
        exerciseMap.put("복근", new String[]{"크런치", "레그레이즈", "플랭크", "싯업", "러시안 트위스트", "바이시클 크런치", "힐터치", "하이 플랭크"});

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        categoryListView.setAdapter(categoryAdapter);

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);
                String[] exercises = exerciseMap.get(selectedCategory);
                ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(AddExerciseActivity.this, android.R.layout.simple_list_item_1, exercises);
                exerciseListView.setAdapter(exerciseAdapter);
            }
        });

        exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedExerciseName = (String) parent.getItemAtPosition(position);
                showInputDialog();
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("selectedExercises", selectedExercises);
                setResult(RESULT_OK, resultIntent); // 데이터를 담아서 결과로 전달
                finish(); // 액티비티 종료하고 이전 액티비티로 돌아가기
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 세트수, 횟수 정하는 팝업
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedExerciseName + " 설정");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(15, 15, 15, 15);

        final EditText setCountInput = new EditText(this);
        setCountInput.setHint("세트 수");
        setCountInput.setInputType(InputType.TYPE_CLASS_NUMBER); // 정수 입력 설정
        layout.addView(setCountInput);

        final EditText repsInput = new EditText(this);
        repsInput.setHint("반복 횟수");
        repsInput.setInputType(InputType.TYPE_CLASS_NUMBER); // 정수 입력 설정
        layout.addView(repsInput);

        builder.setView(layout);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String setCountStr = setCountInput.getText().toString();
                String repsStr = repsInput.getText().toString();

                if (!setCountStr.isEmpty() && !repsStr.isEmpty()) {
                    int setCount = Integer.parseInt(setCountStr); // 문자열을 int로 변환
                    int reps = Integer.parseInt(repsStr); // 문자열을 int로 변환
                    Exercise exercise = new Exercise(selectedExerciseName, setCount, reps);
                    selectedExercises.add(exercise);
                    displaySelectedExercise(exercise);
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void displaySelectedExercise(Exercise exercise) {
        TextView textView = new TextView(this);
        textView.setText(exercise.getName() + " - " + exercise.getSetCount() + "세트 x " + exercise.getReps() + "회");

        // 텍스트 크기와 스타일 설정
        textView.setTextSize(14); // 글자 크기 10dp
        textView.setTypeface(null, Typeface.BOLD); // bold 스타일 설정

        selectedItemsLayout.addView(textView);
    }


}
