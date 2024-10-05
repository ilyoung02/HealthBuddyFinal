package com.example.healthbuddypro.Matching.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 1:1 매칭 프로필 상세보기에서 채팅 신청 시
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;
    private ApiService apiService;
    private int profileId; // 채팅 상대 프로필 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // ProfileDetailActivity에서 넘겨준 profileId 받기
        Intent intent = getIntent();
        profileId = intent.getIntExtra("profileId", -1);

        // profileId 사용하여 채팅 상대 표시하거나 로드
        if (profileId != -1) {
            setTitle("채팅 - 상대방 프로필 ID: " + profileId);
        }

        // RecyclerView와 Adapter 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 메시지 리스트 초기화
        messageList = new ArrayList<>();

        // 어댑터 설정
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Retrofit 설정
        setupRetrofit();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    // 메시지를 리스트에 추가
                    messageList.add(new Message(messageText, true));
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);  // 스크롤 맨 아래로 이동
                    editTextMessage.setText("");

                    // 서버로 메시지 전송
                    sendMessageToServer(profileId, messageText);
                }
            }
        });

        // 예시 메시지 추가
        messageList.add(new Message("안녕하세요!", false));
        chatAdapter.notifyDataSetChanged();
    }

    // Retrofit 설정
    private void setupRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://165.229.89.154:8080/") // 백엔드 URL 설정
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    // 서버로 메시지 전송
    private void sendMessageToServer(int profileId, String messageText) {
        MessageRequest messageRequest = new MessageRequest(profileId, messageText);
        Call<Void> call = apiService.sendMessage(messageRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "메시지 전송 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "메시지 전송 실패", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "메시지 전송 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
