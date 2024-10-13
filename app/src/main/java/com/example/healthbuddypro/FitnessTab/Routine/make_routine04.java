package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthbuddypro.R;

public class make_routine04 extends AppCompatActivity {

    private Button completeMakeRoutine;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_make_routine04);

        backButton = findViewById(R.id.backButton1);
        completeMakeRoutine = findViewById(R.id.completeMakeRoutine);

        //뒤로가기
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // 시작하기 버튼
        //todo : 시작하기 버튼 누르면 RoutineFragment 로 이동이 안됨 -> 액티비티가 튕김
        completeMakeRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(make_routine04.this, routine_list.class);
                startActivity(intent);
                finish();
            }
        });





    }
}