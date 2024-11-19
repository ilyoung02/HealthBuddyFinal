package com.example.healthbuddypro.FitnessTab;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.Profile.MyProfileResponse;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamRankingAdapter extends RecyclerView.Adapter<TeamRankingAdapter.ViewHolder> {

    private final List<TeamRankingListResponse.TeamRankingData> rankingDataList;
    private final Context context;

    public TeamRankingAdapter(Context context, List<TeamRankingListResponse.TeamRankingData> rankingDataList) {
        this.rankingDataList = rankingDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamRankingListResponse.TeamRankingData rankingData = rankingDataList.get(position);

        holder.rankTextView.setText(String.valueOf(rankingData.getRanks()));
        holder.pointsTextView.setText(String.valueOf(rankingData.getPoints()));

        // teamId를 사용해 사용자 이름 가져오기
        fetchTeamUsers(rankingData.getTeamId(), holder);
    }

    @Override
    public int getItemCount() {
        return rankingDataList.size();
    }

    private void fetchTeamUsers(int teamId, ViewHolder holder) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<TeamUsersResponse> call = apiService.getTeamUsers(teamId);

        call.enqueue(new Callback<TeamUsersResponse>() {
            @Override
            public void onResponse(Call<TeamUsersResponse> call, Response<TeamUsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeamUsersResponse.TeamUsersData data = response.body().getData();

                    if (data != null) {
                        // user1Id와 user2Id로 사용자 이름 가져오기
                        fetchUserName(data.getUser1Id(), holder.user1NameTextView);
                        fetchUserName(data.getUser2Id(), holder.user2NameTextView);
                    } else {
                        // data가 null인 경우 처리
                        Log.e("TeamRankingAdapter", "Data is null in response.");
                        Toast.makeText(context, "Failed to fetch team users: Data is null.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 응답이 실패하거나 body가 null인 경우 처리
                    Log.e("TeamRankingAdapter", "Response is unsuccessful or body is null.");
                    Toast.makeText(context, "Failed to fetch team users.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamUsersResponse> call, Throwable t) {
                // API 호출 실패 시 처리
                Log.e("TeamRankingAdapter", "API call failed", t);
                Toast.makeText(context, "Error fetching team users.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchUserName(int profileId, TextView textView) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<MyProfileResponse> call = apiService.getProfileData(profileId);

        call.enqueue(new Callback<MyProfileResponse>() {
            @Override
            public void onResponse(Call<MyProfileResponse> call, Response<MyProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 닉네임 설정
                    String nickname = response.body().getData().getNickname();
                    textView.setText(nickname);
                } else {
                    Log.e("TeamRankingAdapter", "Failed to fetch user name for profileId: " + profileId);
                    textView.setText("Unknown");
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                Log.e("TeamRankingAdapter", "Error fetching user name", t);
                textView.setText("Error");
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rankTextView;
        private final TextView user1NameTextView;
        private final TextView user2NameTextView;
        private final TextView pointsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.group_rank);
            user1NameTextView = itemView.findViewById(R.id.user1_name);
            user2NameTextView = itemView.findViewById(R.id.user2_name);
            pointsTextView = itemView.findViewById(R.id.group_points);
        }
    }
}
