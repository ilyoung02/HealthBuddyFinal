package com.example.healthbuddypro.Matching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;

import java.util.List;

public class MatchingReviewAdapter extends RecyclerView.Adapter<MatchingReviewAdapter.ReviewViewHolder> {

    private List<UserProfile.Review> reviewList;

    public MatchingReviewAdapter(List<UserProfile.Review> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        UserProfile.Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewerName;
        private TextView reviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.review_writer);
            reviewContent = itemView.findViewById(R.id.review_content);
        }

        public void bind(UserProfile.Review review) {
            reviewerName.setText(review.getWriter());
            reviewContent.setText(review.getContent());
        }
    }
}
