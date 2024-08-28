package com.example.healthbuddypro.ShortTermMatching;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbuddypro.R;

import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.ViewHolder> {

    private List<String> matchList;

    public MatchListAdapter(List<String> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewMatchInfo.setText(matchList.get(position));

        holder.btnApply.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("매칭 신청")
                    .setMessage("신청하시겠습니까?")
                    .setPositiveButton("네", (dialog, which) -> {
                        // 신청 처리 코드
                        Toast.makeText(v.getContext(), "매칭 신청 완료", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("아니오", null)
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public void updateList(List<String> newMatchList) {
        matchList = newMatchList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMatchInfo;
        Button btnApply;

        ViewHolder(View itemView) {
            super(itemView);
            textViewMatchInfo = itemView.findViewById(R.id.textViewMatchInfo);
            btnApply = itemView.findViewById(R.id.btn_apply);
        }
    }
}
