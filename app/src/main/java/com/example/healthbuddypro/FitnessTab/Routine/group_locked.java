package com.example.healthbuddypro.FitnessTab.Routine;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.R;

public class group_locked extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_locked);

        //todo : team에서 루틴 생성 여부를 판단해서 루틴 생성이 되어있지 않으면 해당 액티비티가 fragment_fitness 에 먼저 떠야함
    }
}