package com.example.healthbuddypro.FitnessTab.Routine;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthbuddypro.R;

public class timer extends AppCompatActivity {

    private TextView timer_text;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);

        timer_text = findViewById(R.id.timer_text);
        progressBar = findViewById(R.id.progress_bar);

        startTimer();
    }

    private void startTimer() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                timer_text.setText(secondsRemaining + "s");
                progressBar.setProgress(60 - secondsRemaining);
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }
}