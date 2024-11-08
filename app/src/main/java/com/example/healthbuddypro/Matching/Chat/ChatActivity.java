package com.example.healthbuddypro.Matching.Chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.MainActivity;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private int userId; // 로그인된 사용자의 ID
    private SharedPreferences sharedPreferences; // SharedPreferences 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // ProfileDetailActivity에서 넘겨준 profileId 받기
        Intent intent = getIntent();
        profileId = intent.getIntExtra("profileId", -1);

        // SharedPreferences 초기화 및 userId 가져오기
        sharedPreferences = getSharedPreferences("HealthBuddyProPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // onCreate 메서드에서 apiService 초기화
        apiService = RetrofitClient.getApiService();

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
        // 특정 profileId의 채팅 컬렉션을 사용하여 다른 유저와 개별 채팅방 생성
        chatRef = firebaseFirestore.collection("chats").document(String.valueOf(profileId)).collection("messages");

        // 메시지 전송 버튼 클릭 리스너
        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessageToFirebase(profileId, messageText);
            }
        });

        // 매칭 신청 버튼 클릭 리스너
        buttonMatch.setOnClickListener(v -> sendMatchRequest());

        // Firebase에서 실시간으로 메시지 수신
        receiveMessagesFromFirebase();

        // 수락 대기 중인 매칭 요청 모니터링
        receiveMatchRequests();
    }

    // Firebase로 메시지 전송
    private void sendMessageToFirebase(int profileId, String messageText) {
        String senderName = sharedPreferences.getString("username", "알 수 없는 사용자"); // 저장된 사용자 이름을 불러옴

        // 세 개의 매개변수를 받는 생성자로 Message 객체 생성
        Message message = new Message(messageText, true, senderName);
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

    // 매칭 신청을 서버로 전송 (수락/거절 요청 포함)
    private void sendMatchRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("매칭 신청")
                .setMessage("정말로 매칭을 신청하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    MatchRequest matchRequest = new MatchRequest(userId, profileId);
                    apiService.sendMatchRequest(matchRequest).enqueue(new Callback<MatchResponse>() {
                        @Override
                        public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                int matchRequestId = response.body().getData().getMatchRequestId();
                                Toast.makeText(ChatActivity.this, "매칭 신청 성공! 요청 ID: " + matchRequestId, Toast.LENGTH_SHORT).show();

                                // Firestore에 매칭 요청 상태 저장
                                saveMatchRequestToFirestore(matchRequestId, userId, profileId);
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

    // Firestore에 매칭 요청 상태 저장
    private void saveMatchRequestToFirestore(int matchRequestId, int senderId, int receiverId) {
        firebaseFirestore.collection("match_requests").document(String.valueOf(matchRequestId))
                .set(new MatchRequestData(matchRequestId, senderId, receiverId, "REQUESTED"))
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "매칭 요청 Firestore에 저장 성공"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "매칭 요청 Firestore에 저장 실패: " + e.getMessage()));
    }

    // 수락 대기 중인 매칭 요청 모니터링 (수신자에게만 매칭 수락/거절 창을 띄우는 로직 추가)
    private void receiveMatchRequests() {
        firebaseFirestore.collection("match_requests")
                .whereEqualTo("receiverId", userId) // 현재 로그인된 사용자가 수신자인 매칭 요청만 필터링
                .whereEqualTo("status", "REQUESTED")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("ChatActivity", "매칭 요청 수신 오류", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            MatchRequestData matchRequest = doc.toObject(MatchRequestData.class);

                            // 수락/거절 창이 이미 표시된 요청이 아닌 경우에만 창을 띄웁니다.
                            showAcceptRejectDialog(matchRequest.getMatchRequestId());
                        }
                    }
                });
    }

    // 수락/거절 다이얼로그 표시 및 처리
    private void showAcceptRejectDialog(int matchRequestId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("매칭 수락/거절")
                .setMessage("매칭 신청을 수락하시겠습니까?")
                .setPositiveButton("수락", (dialog, which) -> {
                    acceptMatchRequest(matchRequestId);
                })
                .setNegativeButton("거절", (dialog, which) -> {
                    rejectMatchRequest(matchRequestId);
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 매칭 수락 요청
    private void acceptMatchRequest(int matchRequestId) {
//        // 이미 팀이 있는지 확인
//        if (sharedPreferences.getInt("teamId", -1) != -1) {
//            Toast.makeText(ChatActivity.this, "이미 팀이 존재합니다. 다른 매칭 신청을 수락할 수 없습니다.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        MatchRequestStatus requestStatus = new MatchRequestStatus("ACCEPTED");
        apiService.respondMatchRequest(matchRequestId, requestStatus).enqueue(new Callback<MatchResponse>() {
            @Override
            public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int teamId = response.body().getData().getTeamId();
                    Toast.makeText(ChatActivity.this, "매칭 수락 성공! 팀 ID: " + teamId, Toast.LENGTH_SHORT).show();

                    // Firestore에 팀 생성 상태 업데이트
                    updateMatchRequestStatusInFirestore(matchRequestId, "ACCEPTED", teamId);
                } else {
                    Toast.makeText(ChatActivity.this, "매칭 수락 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MatchResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "매칭 수락 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 매칭 거절 요청
    private void rejectMatchRequest(int matchRequestId) {
        MatchRequestStatus requestStatus = new MatchRequestStatus("REJECTED");
        apiService.respondMatchRequest(matchRequestId, requestStatus).enqueue(new Callback<MatchResponse>() {
            @Override
            public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "매칭 거절 성공", Toast.LENGTH_SHORT).show();

                    // 거절된 경우 Firestore에 상태 업데이트
                    updateMatchRequestStatusInFirestore(matchRequestId, "REJECTED", -1);
                } else {
                    Toast.makeText(ChatActivity.this, "매칭 거절 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MatchResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "매칭 거절 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Firestore에 매칭 요청 상태 업데이트
    private void updateMatchRequestStatusInFirestore(int matchRequestId, String status, int teamId) {
        firebaseFirestore.collection("match_requests").document(String.valueOf(matchRequestId))
                .update("status", status, "teamId", teamId)
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "매칭 상태 업데이트 성공"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "매칭 상태 업데이트 실패: " + e.getMessage()));
    }
}
