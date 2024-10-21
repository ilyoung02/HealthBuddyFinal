package com.example.healthbuddypro.Matching.Chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend, buttonMatch;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference chatRef;
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
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonMatch = findViewById(R.id.match_send);  // 매칭 신청 버튼

        // Firebase Firestore 초기화
        firebaseFirestore = FirebaseFirestore.getInstance();
        chatRef = firebaseFirestore.collection("chats").document(String.valueOf(profileId)).collection("messages");

        // 메시지 전송 버튼 클릭 리스너
        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessageToFirebase(profileId, messageText);
            }
        });

        // 매칭 신청 버튼 클릭 리스너 (기존 스프링 백엔드와 연동)
        buttonMatch.setOnClickListener(v -> sendMatchRequest());

        // Firebase에서 실시간으로 메시지 수신
        receiveMessagesFromFirebase();
    }

    // Firebase로 메시지 전송
    private void sendMessageToFirebase(int profileId, String messageText) {
        Message message = new Message(messageText, true);  // Message 객체 생성
        chatRef.add(message).addOnSuccessListener(documentReference -> {
            messageList.add(message);
            chatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageList.size() - 1);
            editTextMessage.setText("");
            Toast.makeText(ChatActivity.this, "메시지 전송 성공", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ChatActivity.this, "메시지 전송 실패", Toast.LENGTH_SHORT).show();
        });
    }

    // Firebase에서 실시간으로 메시지를 수신
    private void receiveMessagesFromFirebase() {
        chatRef.addSnapshotListener((EventListener<QuerySnapshot>) (snapshots, e) -> {
            if (e != null) {
                Toast.makeText(ChatActivity.this, "메시지 수신 오류", Toast.LENGTH_SHORT).show();
                return;
            }

            // 메시지 리스트 업데이트
            if (snapshots != null) {
                messageList.clear();
                snapshots.getDocuments().forEach(document -> {
                    Message message = document.toObject(Message.class);
                    messageList.add(message);
                });
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    // 매칭 신청을 서버로 전송 (기존 스프링 백엔드와 연동)
    private void sendMatchRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("매칭 신청")
                .setMessage("정말로 매칭을 신청하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    int senderId = 1; // 로그인된 사용자의 ID로 대체
                    int receiverId = profileId; // 채팅 상대의 ID

                    MatchRequest matchRequest = new MatchRequest(senderId, receiverId);
                    apiService.sendMatchRequest(matchRequest).enqueue(new Callback<MatchResponse>() {
                        @Override
                        public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                int teamId = response.body().getData().getTeamId();
                                Toast.makeText(ChatActivity.this, "매칭 신청 성공! 팀 ID: " + teamId, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChatActivity.this, "매칭 신청 실패", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MatchResponse> call, Throwable t) {
                            Toast.makeText(ChatActivity.this, "매칭 신청 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("아니오", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
