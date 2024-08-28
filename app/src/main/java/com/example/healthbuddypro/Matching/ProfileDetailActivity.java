package com.example.healthbuddypro.Matching;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.Matching.Chat.ChatActivity;
import com.example.healthbuddypro.R;

public class ProfileDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        ImageView profileImage = findViewById(R.id.profile_image);
        TextView name = findViewById(R.id.profile_name);
        TextView details = findViewById(R.id.profile_intro);

        Intent intent = getIntent();

        if (intent != null) {
            name.setText(intent.getStringExtra("name"));
            details.setText(intent.getStringExtra("details"));
            profileImage.setImageResource(intent.getIntExtra("imageResId", R.drawable.default_image));
        }

        // 채팅 버튼 클릭 시 넘어가지 않음 => 오류? 왜지
        Button btnChat = findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileDetailActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }
}
