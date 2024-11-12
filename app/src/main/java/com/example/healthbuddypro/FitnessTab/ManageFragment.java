package com.example.healthbuddypro.FitnessTab;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.example.healthbuddypro.FitnessTab.quit_hb01;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageFragment extends Fragment {

    private int userId, teamId, teamUserId;
    private TextView user2NameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        user2NameTextView = view.findViewById(R.id.user2_name); // Find the TextView

        ImageButton quitButton = view.findViewById(R.id.btn_hbdQuit);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // `user2Name`을 `quit_hb01`으로 넘기기 위해 Intent에 추가
                String user2Name = user2NameTextView.getText().toString();
                Intent intent = new Intent(getActivity(), quit_hb01.class);
                intent.putExtra("user2Name", user2Name); // 데이터 전달
                startActivity(intent);
            }
        });

        // SharedPreferences에서 userId 및 teamId 가져오기
        SharedPreferences preferences1 = requireActivity().getSharedPreferences("localID", MODE_PRIVATE);
        userId = preferences1.getInt("userId", -1);
        Log.d("ManageFragment", "Retrieved userId from SharedPreferences: " + userId);

        SharedPreferences preferences2 = requireActivity().getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);
        teamId = preferences2.getInt("teamId", -1);
        Log.d("ManageFragment", "Retrieved teamId from SharedPreferences: " + teamId);

        // teamId와 userId를 사용하여 팀원 정보 가져오기
        fetchTeamUserIdAndName(teamId, userId);

        return view;
    }

    private void fetchTeamUserIdAndName(int teamId, int userId) {
        Log.d("ManageFragment", "Starting fetchTeamUserIdAndName with teamId: " + teamId + " and userId: " + userId);

        ApiService apiService = RetrofitClient.getApiService();
        Call<MatchedUserResponse> call = apiService.getMatchedUser(teamId, userId);

        call.enqueue(new Callback<MatchedUserResponse>() {
            @Override
            public void onResponse(Call<MatchedUserResponse> call, Response<MatchedUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teamUserId = response.body().getData().getUserId();
                    String user2Name = response.body().getData().getNickname();

                    user2NameTextView.setText(user2Name);

                    Log.d("ManageFragment", "Fetched teamUserId: " + teamUserId + ", user2Name: " + user2Name);
                } else {
                    Log.e("ManageFragment", "Failed to fetch data: Response unsuccessful or body is null");
                    Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MatchedUserResponse> call, Throwable t) {
                Log.e("ManageFragment", "API call failed", t);
                Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
