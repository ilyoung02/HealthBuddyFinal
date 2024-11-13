package com.example.healthbuddypro.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;

    // 생성자에서 기본값으로 빈 리스트를 할당
    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList != null ? commentList : new ArrayList<>(); // commentList가 null이면 빈 리스트로 초기화
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvCommentAuthor.setText(comment.getAuthor());
        holder.tvCommentContent.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentAuthor, tvCommentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentAuthor = itemView.findViewById(R.id.tvCommentAuthor);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }

    // 댓글 목록을 설정하는 메서드
    public void setComments(List<Comment> comments) {
        this.commentList = comments != null ? comments : new ArrayList<>(); // comments가 null이면 빈 리스트로 설정
        notifyDataSetChanged();
    }
}
