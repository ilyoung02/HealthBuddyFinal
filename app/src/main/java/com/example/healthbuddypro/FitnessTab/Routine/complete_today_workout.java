package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.MainActivity;
import com.example.healthbuddypro.R;

public class complete_today_workout extends AppCompatActivity {

    // todo : 랭킹 점수 반영되서 들어가도록 수정 (서버로 부터 받아야함) -> /api/teams/{teamId}/rankings 팀id 는 api 수정해서 참조 테이블 올려준다함

    private Button completeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complete_today_workout);

        completeButton = findViewById(R.id.completeButton);

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(complete_today_workout.this, MainActivity.class);
                startActivity(intent);
                finish(); // 여기서 액티비티 한번 정리
            }
        });


    }
}