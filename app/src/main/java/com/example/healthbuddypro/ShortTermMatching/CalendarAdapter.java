package com.example.healthbuddypro.ShortTermMatching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.healthbuddypro.R;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<String> dateList;
    private ViewPager2 viewPager2;

    public CalendarAdapter(List<String> dateList, ViewPager2 viewPager2) {
        this.dateList = dateList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String date = dateList.get(position);
        holder.textViewDate.setText(date);

        // 날짜를 클릭하면 해당 날짜로 이동 (RecyclerView로 데이터를 새로고침)
        holder.itemView.setOnClickListener(v -> viewPager2.setCurrentItem(position));
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;

        CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}
