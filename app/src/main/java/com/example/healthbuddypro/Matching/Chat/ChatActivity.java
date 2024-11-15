package com.example.healthbuddypro.Matching.Chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private SharedPreferences sharedPreferences;
    private ListenerRegistration matchRequestListener;
    private String chatRoomId; // 고유한 채팅방 ID

    private Set<Integer> processedMatchRequests = new HashSet<>(); // 이미 처리된 매칭 요청 ID 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // SharedPreferences 파일에서 userId 가져오기
        SharedPreferences mainPrefs = getSharedPreferences("localID", MODE_PRIVATE);
        userId = mainPrefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "UserId가 설정되지 않았습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            // 필요 시 로그인 화면으로 이동
            return;
        }

        // ProfileDetailActivity에서 넘겨준 profileId 받기
        Intent intent = getIntent();
        profileId = intent.getIntExtra("profileId", -1);

        Log.d("ChatActivity", "userId: " + userId);

        // userId와 profileId를 조합해 고유한 chatRoomId 생성
        chatRoomId = "chat_" + Math.min(userId, profileId) + "_" + Math.max(userId, profileId);
        Log.d("ChatActivity", "Generated chatRoomId: " + chatRoomId);

        // userId에 따라 구분된 SharedPreferences 파일 설정
        sharedPreferences = getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);

        apiService = RetrofitClient.getApiService();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();

        //Adapter 초기화 시 사용자 ID 전달
        chatAdapter = new ChatAdapter(messageList, userId);
        recyclerView.setAdapter(chatAdapter);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonMatch = findViewById(R.id.match_send);

        // FirebaseFirestore 초기화 및 채팅방 참조
        firebaseFirestore = FirebaseFirestore.getInstance();
        chatRef = firebaseFirestore.collection("chats").document(chatRoomId).collection("messages");

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessageToFirebase(messageText);
            }
        });

        buttonMatch.setOnClickListener(v -> sendMatchRequest());

        receiveMessagesFromFirebase();

        // 초기화 코드
        if (matchRequestListener == null) {
            receiveMatchRequests();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 리스너 등록 해제
        if (matchRequestListener != null) {
            matchRequestListener.remove();
            matchRequestListener = null;
        }
    }

    private void sendMessageToFirebase(String messageText) {
        String senderName = "User" + userId;

        // 메시지 객체 생성 (senderName과 userId 추가)
        Message message = new Message(messageText, true, senderName, userId);

        // Firestore에 메시지를 추가하고 성공적으로 추가되었을 때만 로컬 리스트에 추가
        chatRef.add(message).addOnSuccessListener(documentReference -> {
            editTextMessage.setText("");
            Toast.makeText(ChatActivity.this, "메시지 전송 성공", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ChatActivity.this, "메시지 전송 실패", Toast.LENGTH_SHORT).show();
        });
    }

    private void receiveMessagesFromFirebase() {
        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(ChatActivity.this, "메시지 수신 오류", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        messageList.clear();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            if (message != null) {
                                Log.d("ChatActivity", "Received message from: " + message.getSenderName()); // 로그 추가
                                messageList.add(message);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

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

    private void saveMatchRequestToFirestore(int matchRequestId, int senderId, int receiverId) {
        firebaseFirestore.collection("match_requests").document(String.valueOf(matchRequestId))
                .set(new MatchRequestData(matchRequestId, senderId, receiverId, "REQUESTED"))
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "매칭 요청 Firestore에 저장 성공"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "매칭 요청 Firestore에 저장 실패: " + e.getMessage()));
    }

    private void receiveMatchRequests() {
        if (matchRequestListener == null) {
            matchRequestListener = firebaseFirestore.collection("match_requests")
                    .whereEqualTo("receiverId", userId)
                    .whereEqualTo("status", "REQUESTED")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Log.e("ChatActivity", "매칭 요청 수신 오류", e);
                            return;
                        }
                        if (snapshots != null && !snapshots.isEmpty()) {
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                MatchRequestData matchRequest = doc.toObject(MatchRequestData.class);
                                int matchRequestId = matchRequest.getMatchRequestId();

                                if (!processedMatchRequests.contains(matchRequestId)) {
                                    processedMatchRequests.add(matchRequestId);
                                    showAcceptRejectDialog(matchRequestId);
                                }
                            }
                        }
                    });
        }
    }

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

    private void acceptMatchRequest(int matchRequestId) {
        MatchRequestStatus requestStatus = new MatchRequestStatus("ACCEPTED");
        apiService.respondMatchRequest(matchRequestId, requestStatus).enqueue(new Callback<MatchResponse>() {
            @Override
            public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int teamId = response.body().getData().getTeamId();
                    Toast.makeText(ChatActivity.this, "매칭 수락 성공! 팀 ID: " + teamId, Toast.LENGTH_SHORT).show();

                    // Firestore에서 팀 정보를 저장
                    saveMatchInfoToFirestore(teamId, userId, profileId);  // 현재 사용자에 대해 저장
                    saveMatchInfoToFirestore(teamId, profileId, userId);  // 상대방에 대해 저장

                    // Firestore에서 최신 팀 정보를 동기화
                    syncTeamInfoFromFirebase(userId);  // 현재 사용자
                    syncTeamInfoFromFirebase(profileId);  // 상대방

                    // 팀 정보를 SharedPreferences에 저장 (내부 저장소)
                    saveTeamIdToPreferences(teamId, userId, profileId);  // 현재 사용자와 상대방 정보 저장

                    updateMatchRequestStatusInFirestore(matchRequestId, "ACCEPTED", teamId);
                    processedMatchRequests.add(matchRequestId);
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


    private void rejectMatchRequest(int matchRequestId) {
        MatchRequestStatus requestStatus = new MatchRequestStatus("REJECTED");
        apiService.respondMatchRequest(matchRequestId, requestStatus).enqueue(new Callback<MatchResponse>() {
            @Override
            public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "매칭 거절 성공", Toast.LENGTH_SHORT).show();
                    updateMatchRequestStatusInFirestore(matchRequestId, "REJECTED", -1);
                    processedMatchRequests.add(matchRequestId);
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

    private void updateMatchRequestStatusInFirestore(int matchRequestId, String status, int teamId) {
        firebaseFirestore.collection("match_requests").document(String.valueOf(matchRequestId))
                .update("status", status, "teamId", teamId)
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "매칭 상태 업데이트 성공"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "매칭 상태 업데이트 실패: " + e.getMessage()));
    }

    private void saveTeamIdToPreferences(int teamId, int userId, int targetUserId) {
        Log.d("ChatActivity", "Saving teamId: " + teamId + " for userId: " + userId + " and targetUserId: " + targetUserId);

        // 현재 사용자의 SharedPreferences 파일
        SharedPreferences sharedPreferences = getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // teamId, targetUserId (상대방 userId)를 저장
        editor.putInt("teamId", teamId);  // 팀 ID 저장
        editor.putInt("현재 사용자", userId); // 현재 사용자 userId 저장
        editor.putInt("targetUserId", targetUserId); // 상대방 userId 저장
        editor.apply();

        // 상대방의 SharedPreferences 파일에 저장
        SharedPreferences sharedPreferencesTarget = getSharedPreferences("user_" + targetUserId + "_prefs", MODE_PRIVATE);
        SharedPreferences.Editor targetEditor = sharedPreferencesTarget.edit();

        // 상대방의 teamId를 저장
        targetEditor.putInt("teamId", teamId); // 상대방의 팀 ID 저장
        targetEditor.putInt("현재 사용자", targetUserId); // 상대방 입장에서 상대방의 UserID 저장
        targetEditor.putInt("targetUserId", userId); // 상대방 입장에서 현재 사용자 ID를 저장
        targetEditor.apply();
    }

    private void saveMatchInfoToFirestore(int teamId, int userId, int targetUserId) {
        Log.d("ChatActivity", "Saving teamId: " + teamId + " for userId: " + userId + " and targetUserId: " + targetUserId);

        // Firestore에서 현재 사용자 정보 확인
        FirebaseFirestore.getInstance().collection("users").document(String.valueOf(userId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 문서가 존재하면 업데이트
                        updateTeamInfo(userId, teamId, targetUserId);
                    } else {
                        // 문서가 존재하지 않으면 새로 생성
                        createUserDocument(userId, teamId, targetUserId);
                    }
                })
                .addOnFailureListener(e -> Log.e("ChatActivity", "사용자 정보 확인 실패: " + e.getMessage()));

        // 상대방 사용자 정보 확인
        FirebaseFirestore.getInstance().collection("users").document(String.valueOf(targetUserId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 문서가 존재하면 업데이트
                        updateTeamInfo(targetUserId, teamId, userId);
                    } else {
                        // 문서가 존재하지 않으면 새로 생성
                        createUserDocument(targetUserId, teamId, userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("ChatActivity", "상대방 정보 확인 실패: " + e.getMessage()));
    }

    private void createUserDocument(int userId, int teamId, int targetUserId) {
        // 새 문서 생성
        Map<String, Object> userData = new HashMap<>();
        userData.put("teamId", teamId);
        userData.put("targetUserId", targetUserId);

        FirebaseFirestore.getInstance().collection("users").document(String.valueOf(userId))
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "사용자 문서 생성 및 팀 정보 저장 성공"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "사용자 문서 생성 실패: " + e.getMessage()));
    }

    private void updateTeamInfo(int userId, int teamId, int targetUserId) {
        // 기존 문서 업데이트
        FirebaseFirestore.getInstance().collection("users").document(String.valueOf(userId))
                .update("teamId", teamId, "targetUserId", targetUserId)
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "팀 정보 Firestore에 저장 성공: " + userId))
                .addOnFailureListener(e -> Log.e("ChatActivity", "팀 정보 Firestore에 저장 실패: " + e.getMessage()));
    }

    private void syncTeamInfoFromFirebase(int userId) {
        FirebaseFirestore.getInstance().collection("users")
                .document(String.valueOf(userId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int teamId = documentSnapshot.getLong("teamId").intValue();
                        int targetUserId = documentSnapshot.getLong("targetUserId").intValue();
                        Log.d("ChatActivity", "팀 정보 동기화 완료: teamId = " + teamId + ", targetUserId = " + targetUserId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatActivity", "팀 정보 동기화 실패: " + e.getMessage());
                });
    }
}
