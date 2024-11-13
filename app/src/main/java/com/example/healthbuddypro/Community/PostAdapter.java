package com.example.healthbuddypro.Community;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postList;
    private final Context context;
    private FirebaseFirestore firebaseFirestore;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList != null ? postList : new ArrayList<>();
        this.firebaseFirestore = FirebaseFirestore.getInstance(); // Firestore 인스턴스
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvUsername.setText(post.getUsername());
        holder.tvPostTime.setText(post.getPostTime());
        holder.tvPostContent.setText(post.getContent());

        // 댓글 업데이트
        holder.commentAdapter.setComments(post.getComments());

        // 댓글을 등록할 때
        holder.btnAddComment.setOnClickListener(v -> {
            String commentContent = holder.etCommentContent.getText().toString().trim();
            if (!TextUtils.isEmpty(commentContent)) {
                // 새로운 댓글 생성 (익명 + 번호)
//                Comment newComment = new Comment("익명" + (holder.commentAdapter.getItemCount() + 1), commentContent);
                Comment newComment = new Comment("익명", commentContent);
                post.getComments().add(newComment); // 댓글 리스트에 추가
                holder.commentAdapter.notifyDataSetChanged(); // 어댑터에 변경 사항 알림
                holder.etCommentContent.setText(""); // 댓글 입력 필드 초기화

                // 댓글 Firestore에 저장
                firebaseFirestore.collection("posts").document(post.getId()) // 게시물의 ID로 접근
                        .update("comments", post.getComments()) // 댓글 목록 업데이트
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "댓글 추가 실패", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvPostTime, tvPostContent;
        EditText etCommentContent;
        Button btnAddComment;
        RecyclerView rvComments;
        CommentAdapter commentAdapter;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvPostTime = itemView.findViewById(R.id.tvPostTime);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
            etCommentContent = itemView.findViewById(R.id.etCommentContent);
            btnAddComment = itemView.findViewById(R.id.btnAddComment);
            rvComments = itemView.findViewById(R.id.rvComments);

            rvComments.setLayoutManager(new LinearLayoutManager(itemView.getContext())); // 댓글을 보여줄 RecyclerView
            commentAdapter = new CommentAdapter(itemView.getContext(), new ArrayList<>()); // 댓글 어댑터 초기화
            rvComments.setAdapter(commentAdapter); // RecyclerView에 어댑터 설정
        }
    }
}
