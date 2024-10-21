package com.example.healthbuddypro.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbuddypro.R;
import java.util.List;

public class FavWorkoutsAdapter extends RecyclerView.Adapter<FavWorkoutsAdapter.ViewHolder> {

    private List<String> favWorkouts;

    public FavWorkoutsAdapter(List<String> favWorkouts) {
        this.favWorkouts = favWorkouts;
    }

    public void setData(List<String> newFavWorkouts) {
        this.favWorkouts.clear(); // 기존 데이터 삭제
        this.favWorkouts.addAll(newFavWorkouts); // 새로운 데이터 추가
        notifyDataSetChanged(); // RecyclerView 업데이트
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fav_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the workout name from the list and set it to the TextView
        holder.workoutName.setText(favWorkouts.get(position));
    }

    @Override
    public int getItemCount() {
        return favWorkouts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView workoutName;

        public ViewHolder(View view) {
            super(view);
            workoutName = view.findViewById(R.id.workout_name);
        }
    }
}
