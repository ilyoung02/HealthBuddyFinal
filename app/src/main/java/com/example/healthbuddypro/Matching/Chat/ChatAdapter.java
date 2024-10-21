package com.example.healthbuddypro.Matching.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;

import java.util.List;

// RecyclerView의 어댑터 => 채팅 메시지 표시
// Message 객체 이용, 사용자가 보낸 메시지와 상대방이 보낸 메시지 구분
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    // ViewType 상수 정의
    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    // 메시지의 소유자가 본인인 경우와 상대방인 경우를 구분
    // 오른쪽 또는 왼쪽에 메시지가 출력되도록 ViewHolder 선택
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        // 메시지가 본인의 것인지 여부를 확인하여 뷰타입 반환
        // isSender() 메서드를 사용해 메시지의 발신자가 본인인지 구분, 이를 바탕으로 뷰타입 결정
        if (message.isSender()) {
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OTHER_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(message);
        } else if (holder instanceof OtherMessageViewHolder) {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // 내 메시지 ViewHolder (bind 메서드를 통해 메시지의 내용을 설정)
    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        MyMessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }

        // message.getContent()를 통해 Message 객체의 내용을 TextView에 적용
        void bind(Message message) {
            textMessage.setText(message.getContent());  // 메시지 내용 설정
        }
    }

    // 상대방 메시지 ViewHolder (bind 메서드를 통해 메시지의 내용을 설정)
    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        OtherMessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }

        // message.getContent()를 통해 Message 객체의 내용을 TextView에 적용
        void bind(Message message) {
            textMessage.setText(message.getContent());  // 메시지 내용 설정
        }
    }
}
