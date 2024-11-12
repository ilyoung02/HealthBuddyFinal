package com.example.healthbuddypro.FitnessTab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthbuddypro.MainActivity; // 메인 화면으로 이동
import com.example.healthbuddypro.R;
import android.widget.TextView;
import android.widget.ImageButton;


public class quit_hb03 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quit_hb03);

        // 뒤로 가기 버튼 초기화 및 클릭 시 이전 화면으로 이동 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 액티비티로 돌아가기
            }
        });

        // Intent로 전달받은 username2 가져오기
        String user2Name = getIntent().getStringExtra("user2Name");

        // username2를 TextView에 표시
        TextView nameTextView = findViewById(R.id.name_quitHB);
        if (user2Name != null) {
            nameTextView.setText(user2Name);
        } else {
            nameTextView.setText("사용자");
        }

        // 완료 버튼 초기화 및 클릭 시 메인 화면으로 이동 설정
        Button completeButton = findViewById(R.id.nextStepButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(quit_hb03.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
