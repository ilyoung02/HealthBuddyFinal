package com.example.healthbuddypro.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<MyProfileResponse.Review> reviewList;

    public ReviewAdapter(List<MyProfileResponse.Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        MyProfileResponse.Review review = reviewList.get(position);
        holder.writer.setText(review.getWriter());
        holder.content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView writer, content;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            writer = itemView.findViewById(R.id.review_writer);
            content = itemView.findViewById(R.id.review_content);
        }
    }
}
