package com.example.healthbuddypro.FitnessTab.Routine;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthbuddypro.Fitness.FitnessFragment;
import com.example.healthbuddypro.R;

public class moved_routineFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moved_routinefragment);

        // FitnessFragment로 화면을 덮는 방식으로 수정
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FitnessFragment()) // FitnessFragment로 이동
                    .commit();
        }
    }
}
