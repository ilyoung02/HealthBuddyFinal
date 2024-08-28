package com.example.healthbuddypro.Matching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        holder.profileImage.setImageResource(profile.getImageResId());
        holder.name.setText(profile.getName());
        holder.details.setText(profile.getDetails());

        holder.itemView.setOnClickListener(v -> listener.onProfileClick(profile));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name;
        TextView details;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.profile_name);
            details = itemView.findViewById(R.id.profile_details);
        }
    }

    public interface OnProfileClickListener {
        void onProfileClick(UserProfile profile);
    }
}
