package com.example.healthbuddypro.FitnessTab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.R;

public class quit_hb01 extends AppCompatActivity {

    private TextView nameQuitHB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_quit_hb01);


        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 액티비티로 돌아가기
            }
        });

        // 사용자 이름을 표시할 TextView 초기화
        nameQuitHB = findViewById(R.id.name_quitHB);

        // Intent로 전달된 user2Name을 받기
        String user2Name = getIntent().getStringExtra("user2Name");

        // user2Name이 null이 아닐 경우 TextView에 사용자 이름 설정
        if (user2Name != null) {
            nameQuitHB.setText(user2Name);
        }

        Button yesButton = findViewById(R.id.yesButton);
        Button noButton = findViewById(R.id.noButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "Yes" 버튼 클릭 시 quit_hb02로 이동
                Intent intent = new Intent(quit_hb01.this, quit_hb02.class);

                // Pass user2Name to quit_hb02
                intent.putExtra("user2Name", user2Name);  // Passing user2Name

                startActivity(intent);
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "No" 버튼 클릭 시 종료
                finish();
            }
        });
    }
}
