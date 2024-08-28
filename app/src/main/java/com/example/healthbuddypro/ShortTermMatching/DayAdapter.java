package com.example.healthbuddypro.ShortTermMatching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbuddypro.R;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private final List<String> dayList;
    private int selectedPosition = -1;

    public DayAdapter(List<String> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        String day = dayList.get(position);
        holder.bind(day, position, selectedPosition, this);
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDay;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
        }

        public void bind(String day, int position, int selectedPosition, DayAdapter adapter) {
            textViewDay.setText(day);
            if (position == selectedPosition) {
                textViewDay.setTextColor(itemView.getResources().getColor(R.color.selected_text_color)); // 진한 색상
            } else {
                textViewDay.setTextColor(itemView.getResources().getColor(R.color.unselected_text_color)); // 연한 색상
            }

            itemView.setOnClickListener(v -> {
                adapter.setSelectedPosition(position);
            });
        }
    }
}
