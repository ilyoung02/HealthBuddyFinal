package com.example.healthbuddypro.FitnessTab.TeamChat;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TeamChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private TeamChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;
    private FirebaseFirestore firebaseFirestore;
    private String teamChatRoomId; // 팀 채팅방 ID

    private int userId = -1; // 로그인된 사용자 ID
    private int teamId = -1; // 팀 ID
    private int targetUserId = -1; // 상대방 사용자 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_chat, container, false);

        // SharedPreferences에서 userId 가져오기
        SharedPreferences mainPrefs = requireActivity().getSharedPreferences("localID", MODE_PRIVATE);
        userId = mainPrefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "로그인 정보가 올바르지 않습니다. 다시 로그인 해주세요.", Toast.LENGTH_LONG).show();
            return view;
        }

        // Firestore 인스턴스 초기화
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (firebaseFirestore == null) {
            Log.e("TeamChatFragment", "FirebaseFirestore 초기화 실패");
            return view;
        }

        // 파이어스토어에서 사용자 정보 불러오기
        fetchUserDataFromFirestore(userId);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        messageList = new ArrayList<>();
        chatAdapter = new TeamChatAdapter(messageList, userId, targetUserId);
        recyclerView.setAdapter(chatAdapter);

        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        // 채팅 메시지 보내기
        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessageToFirebase(messageText);
            }
        });

        // 채팅 메시지 수신
        receiveMessagesFromFirebase();

        return view;
    }

    private void fetchUserDataFromFirestore(int userId) {
        // Firestore에서 사용자 데이터를 가져와 팀 ID와 상대방 userId를 업데이트
        firebaseFirestore.collection("users")
                .document(String.valueOf(userId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        teamId = documentSnapshot.getLong("teamId").intValue();
                        targetUserId = documentSnapshot.getLong("targetUserId").intValue();

                        if (teamId == -1 || targetUserId == -1) {
                            Toast.makeText(getContext(), "팀 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 팀 채팅방 ID 설정
                            teamChatRoomId = "team_chat_" + teamId + "_" + Math.min(userId, targetUserId) + "_" + Math.max(userId, targetUserId);

                            // 팀 채팅방 ID가 정상적으로 설정되지 않은 경우 추가 오류 처리
                            if (teamChatRoomId == null || teamChatRoomId.isEmpty()) {
                                Log.e("TeamChatFragment", "teamChatRoomId가 유효하지 않습니다.");
                                return;
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TeamChatFragment", "Failed to fetch user data: " + e.getMessage());
                    Toast.makeText(getContext(), "사용자 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendMessageToFirebase(String messageText) {
        String senderName = "User" + userId; // 사용자 이름

        // 메시지 객체 생성
        Message message = new Message(messageText, true, senderName, userId);

        // Firestore에 메시지를 추가하고 성공적으로 추가되었을 때만 로컬 리스트에 추가
        if (teamChatRoomId != null && !teamChatRoomId.isEmpty()) {
            firebaseFirestore.collection("team_chats").document(teamChatRoomId)
                    .collection("messages").add(message)
                    .addOnSuccessListener(documentReference -> {
                        editTextMessage.setText("");
                        Toast.makeText(getContext(), "메시지 전송 성공", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "메시지 전송 실패", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("TeamChatFragment", "팀 채팅방 ID가 유효하지 않습니다.");
        }
    }

    private void receiveMessagesFromFirebase() {
        if (teamChatRoomId != null && !teamChatRoomId.isEmpty()) {
            firebaseFirestore.collection("team_chats").document(teamChatRoomId)
                    .collection("messages").orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Toast.makeText(getContext(), "메시지 수신 오류", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (snapshots != null) {
                            messageList.clear();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                Message message = document.toObject(Message.class);
                                if (message != null) {
                                    messageList.add(message);
                                }
                            }
                            chatAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    });
        } else {
            Log.e("TeamChatFragment", "팀 채팅방 ID가 유효하지 않습니다.");
        }
    }
}
