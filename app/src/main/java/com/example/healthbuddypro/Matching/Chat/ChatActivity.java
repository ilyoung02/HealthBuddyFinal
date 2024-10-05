package com.example.healthbuddypro.Matching.Chat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;

import java.util.ArrayList;
import java.util.List;

// 1:1 매칭 프로필 상세보기에서 채팅 신청 시
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // ProfileDetailActivity에서 넘겨준 profileId 받기
        Intent intent = getIntent();
        int profileId = intent.getIntExtra("profileId", -1);

        // profileId를 사용하여 채팅 상대를 표시하거나 로드
        if (profileId != -1) {
            // 예: 채팅 상대 이름을 상단에 표시
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

                    // 서버로 메시지 전송 로직 추가 가능
                }
            }
        });

        // 예시 메시지 추가
        messageList.add(new Message("안녕하세요!", true));
        messageList.add(new Message("안녕하세요. 만나서 반갑습니다!", false));

        // 데이터가 변경되었음을 어댑터에 알림
        chatAdapter.notifyDataSetChanged();
    }
}
