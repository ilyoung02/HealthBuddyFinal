package com.example.healthbuddypro.FitnessTab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.example.healthbuddypro.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class quit_hb02 extends AppCompatActivity {

    private EditText reviewEditText;
    private ApiService apiService;
    private int writerId;
    private int teamId;
    private String user2Name; // 추가된 변수
    private static final String TAG = "quit_hb02";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quit_hb02);

        // SharedPreferences에서 userId 및 teamId 가져오기
        SharedPreferences preferences1 = getSharedPreferences("localID", MODE_PRIVATE);
        writerId = preferences1.getInt("userId", -1);
        Log.d(TAG, "Retrieved writerId from SharedPreferences: " + writerId);

        SharedPreferences preferences2 = getSharedPreferences("user_" + writerId + "_prefs", MODE_PRIVATE);
        teamId = preferences2.getInt("teamId", -1);
        Log.d(TAG, "Retrieved teamId from SharedPreferences: " + teamId);

        // UI 요소 초기화
        reviewEditText = findViewById(R.id.review_quitHB);
        Button nextStepButton = findViewById(R.id.nextStepButton);
        ImageButton backButton = findViewById(R.id.backButton);
        TextView nameQuitHB = findViewById(R.id.name_quitHB); // Retrieve the TextView for user2Name display

        // Intent로 전달받은 user2Name을 받아오기
        user2Name = getIntent().getStringExtra("user2Name"); // ManageFragment에서 전달한 값

        // Set user2Name in the TextView
        if (user2Name != null) {
            nameQuitHB.setText(user2Name); // Set user2Name to name_quitHB TextView
        } else {
            nameQuitHB.setText("사용자"); // Set a default text if user2Name is null
        }

        // RetrofitClient를 사용하여 ApiService 인스턴스 초기화
        apiService = RetrofitClient.getApiService();
        Log.d(TAG, "ApiService initialized");

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = reviewEditText.getText().toString().trim();
                Log.d(TAG, "Review entered: " + review);

                if (!review.isEmpty()) {
                    Log.d(TAG, "Sending review to API...");
                    sendReview(teamId, writerId, review);
                } else {
                    Toast.makeText(quit_hb02.this, "후기를 작성해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void sendReview(int teamId, int writerId, String review) {
        Log.d(TAG, "Preparing review request with teamId: " + teamId + ", writerId: " + writerId + ", review: " + review);

        ReviewRequest reviewRequest = new ReviewRequest(writerId, review);

        Call<HealthBuddyEndResponse> call = apiService.sendReview(teamId, reviewRequest);
        call.enqueue(new Callback<HealthBuddyEndResponse>() {
            @Override
            public void onResponse(Call<HealthBuddyEndResponse> call, Response<HealthBuddyEndResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Review sent successfully: " + response.body().getMessage());
                    Toast.makeText(quit_hb02.this, "후기 전송 성공: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    // Intent로 user2Name을 quit_hb03으로 넘기기
                    Intent intent = new Intent(quit_hb02.this, quit_hb03.class);
                    intent.putExtra("user2Name", user2Name); // user2Name을 전달
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Review sending failed: " + response.code() + " " + response.message());
                    Toast.makeText(quit_hb02.this, "후기 전송 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HealthBuddyEndResponse> call, Throwable t) {
                Log.e(TAG, "Network error while sending review", t);
                Toast.makeText(quit_hb02.this, "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
