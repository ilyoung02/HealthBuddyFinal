package com.example.healthbuddypro.ShortTermMatching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbuddypro.R;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    private final List<String> monthList;
    private final OnMonthSelectedListener listener;
    private int selectedPosition = -1;

    public MonthAdapter(List<String> monthList, OnMonthSelectedListener listener) {
        this.monthList = monthList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        String month = monthList.get(position);
        holder.bind(month, position, listener, selectedPosition);
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMonth;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textViewMonth);
        }

        public void bind(String month, int position, OnMonthSelectedListener listener, int selectedPosition) {
            textViewMonth.setText(month);
            if (position == selectedPosition) {
                textViewMonth.setTextColor(itemView.getResources().getColor(R.color.selected_text_color)); // 진한 색상
            } else {
                textViewMonth.setTextColor(itemView.getResources().getColor(R.color.unselected_text_color)); // 연한 색상
            }

            itemView.setOnClickListener(v -> listener.onMonthSelected(position));
        }
    }

    public interface OnMonthSelectedListener {
        void onMonthSelected(int position);
    }
}
