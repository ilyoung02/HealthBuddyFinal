package com.example.healthbuddypro.Matching.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    private List<Message> messageList;
    private int currentUserId;; // 현재 로그인한 사용자 ID

    public ChatAdapter(List<Message> messageList, int currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        // 현재 사용자가 보낸 메시지이면 오른쪽에 표시
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_MY_MESSAGE; // 현재 사용자가 보낸 메시지
        } else {
            return VIEW_TYPE_OTHER_MESSAGE; // 상대방이 보낸 메시지
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            View view = inflater.inflate(R.layout.item_message_right, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_left, parent, false);
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

    // ViewHolder for my messages
    public class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderNameTextView;

        MyMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textMessage1);
            senderNameTextView = itemView.findViewById(R.id.textSenderName1);
        }

        void bind(Message message) {
            messageTextView.setText(message.getContent());
            senderNameTextView.setText(message.getSenderName());
        }
    }

    // ViewHolder for other messages
    public class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderNameTextView;

        OtherMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textMessage2);
            senderNameTextView = itemView.findViewById(R.id.textSenderName2);
        }

        void bind(Message message) {
            messageTextView.setText(message.getContent());
            senderNameTextView.setText(message.getSenderName());
        }
    }
}