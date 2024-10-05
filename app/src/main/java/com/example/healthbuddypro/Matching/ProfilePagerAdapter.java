package com.example.healthbuddypro.Matching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthbuddypro.R;
import java.util.List;

public class ProfilePagerAdapter extends RecyclerView.Adapter<ProfilePagerAdapter.ProfileViewHolder> {

    private final List<UserProfile> profiles;
    private final OnProfileClickListener listener;

    public ProfilePagerAdapter(List<UserProfile> profiles, OnProfileClickListener listener) {
        this.profiles = profiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        UserProfile profile = profiles.get(position);

        // 닉네임과 성별을 텍스트뷰에 설정
//        holder.nickname.setText(profile.getNickName());
//        holder.gender.setText(profile.getGender());
//        holder.age.setText(profile.getGender());
        holder.nicknameTextView.setText(profile.getNickName());
        holder.genderTextView.setText(profile.getGender());
        holder.ageTextView.setText(String.valueOf(profile.getAge()));

        // 이미지 로딩 (Glide 사용)
        Glide.with(holder.itemView.getContext())
                .load(profile.getImage()) // 이미지 URL 로드
                .placeholder(R.drawable.user1) // 기본 이미지
                .into(holder.profileImageView);

        // 프로필 항목 클릭 리스너
        holder.itemView.setOnClickListener(v -> listener.onProfileClick(profile));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    // ViewHolder 클래스
    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
//        TextView nickname;
//        TextView gender;
//        TextView age;

        TextView nicknameTextView, genderTextView, ageTextView;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_profile.xml 파일 참고
            profileImageView = itemView.findViewById(R.id.profile_image);
//            nickname = itemView.findViewById(R.id.profile_name);
//            gender = itemView.findViewById(R.id.profile_gender);
//            age = itemView.findViewById(R.id.profile_age);

            nicknameTextView = itemView.findViewById(R.id.profile_name);
            genderTextView = itemView.findViewById(R.id.profile_gender);
            ageTextView = itemView.findViewById(R.id.profile_age);
        }
    }

    public interface OnProfileClickListener {
        void onProfileClick(UserProfile profile);
    }
}
