package com.example.healthbuddypro.FitnessTab.TeamChat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private int userId; // 로그인된 사용자 ID
    private int teamId; // 팀 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_chat, container, false);

        // 로그인된 사용자 ID 가져오기 (SharedPreferences 또는 Bundle을 통해)
        userId = getActivity().getIntent().getIntExtra("userId", -1);

        // 팀 ID를 SharedPreferences에서 가져옴
        SharedPreferences preferences = getActivity().getSharedPreferences("user_" + userId + "_prefs", Context.MODE_PRIVATE);
        teamId = preferences.getInt("teamId", -1);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        messageList = new ArrayList<>();
        chatAdapter = new TeamChatAdapter(messageList, userId);
        recyclerView.setAdapter(chatAdapter);

        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        firebaseFirestore = FirebaseFirestore.getInstance();

        // 팀 채팅방 ID 설정 (teamId를 기준으로 채팅방을 설정)
        teamChatRoomId = "team_chat_" + teamId;  // teamId를 기준으로 설정

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

    private void sendMessageToFirebase(String messageText) {
        String senderName = "익명" + userId; // 예시로 익명 사용자로 설정

        // 메시지 객체 생성
        Message message = new Message(messageText, true, senderName, userId);

        // Firestore에 메시지를 추가하고 성공적으로 추가되었을 때만 로컬 리스트에 추가
        firebaseFirestore.collection("team_chats").document(teamChatRoomId)
                .collection("messages").add(message)
                .addOnSuccessListener(documentReference -> {
                    editTextMessage.setText("");
                    Toast.makeText(getContext(), "메시지 전송 성공", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "메시지 전송 실패", Toast.LENGTH_SHORT).show();
                });
    }

    private void receiveMessagesFromFirebase() {
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
    }
}
