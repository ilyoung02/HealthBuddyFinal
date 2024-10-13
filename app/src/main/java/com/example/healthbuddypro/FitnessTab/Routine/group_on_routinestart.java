package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.R;

public class group_on_routinestart extends AppCompatActivity {

    private Button btn_startRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_on_routinestart);

        btn_startRoutine = findViewById(R.id.btn_startRoutine);

        // 루틴 시작 버튼
        btn_startRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(group_on_routinestart.this, check_location.class);
                startActivity(intent);
            }
        });

    }
}
