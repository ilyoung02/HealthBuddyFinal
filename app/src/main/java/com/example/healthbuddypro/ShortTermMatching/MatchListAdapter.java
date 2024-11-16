package com.example.healthbuddypro.ShortTermMatching;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.Matching.Chat.ChatActivity;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.ViewHolder> {

    private List<ShortMatchPost> matchList;
    private int currentUserId; // 현재 사용자의 ID

    public MatchListAdapter(List<ShortMatchPost> matchList, int currentUserId) {
        this.matchList = matchList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShortMatchPost currentPost = matchList.get(position);

        // 제목 설정
        holder.textViewMatchInfo.setText(currentPost.getTitle());

        // 운동 부위 (health) 설정
        holder.textViewHealth.setText(currentPost.getHealth());

        // 사용자가 작성한 글 (content) 설정
        holder.textViewContent.setText(currentPost.getContent());

        // 지역 설정
        holder.textViewLocation.setText(currentPost.getLocation());

        // 성별 카테고리 설정
        holder.textViewCategory.setText(currentPost.getCategory());

        // "함께하기" 버튼 클릭 리스너
        holder.btnApply.setOnClickListener(v -> {
            showApplyDialog(v.getContext(), currentPost);
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    private void showApplyDialog(Context context, ShortMatchPost post) {
        // "함께하기" 버튼을 클릭했을 때 다이얼로그를 띄운다
        new AlertDialog.Builder(context)
                .setTitle("함께 하기")
                .setMessage(post.getTitle() + "에 함께 하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    // "예"를 선택하면 채팅방으로 이동
                    openChatRoom(context, post.getSenderId());  // senderId를 가지고 채팅방으로 이동
                })
                .setNegativeButton("아니오", null)
                .show();
    }

    // 채팅방으로 이동하는 메서드
    private void openChatRoom(Context context, int targetUserId) {
        // "targetUserId"를 가지고 채팅방을 여는 액티비티로 이동
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("profileId", targetUserId);  // 채팅할 상대의 userId를 전달
        context.startActivity(intent);
    }

    public void updateList(List<ShortMatchPost> newMatchList) {
        matchList = newMatchList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMatchInfo;
        TextView textViewHealth;
        TextView textViewLocation;
        TextView textViewCategory;
        TextView textViewContent; // content를 표시할 TextView
        Button btnApply;

        ViewHolder(View itemView) {
            super(itemView);
            textViewMatchInfo = itemView.findViewById(R.id.textViewMatchInfo);
            textViewHealth = itemView.findViewById(R.id.textViewHealth);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewContent = itemView.findViewById(R.id.textViewContent);  // content를 위한 TextView
            btnApply = itemView.findViewById(R.id.btn_apply);
        }
    }
}
