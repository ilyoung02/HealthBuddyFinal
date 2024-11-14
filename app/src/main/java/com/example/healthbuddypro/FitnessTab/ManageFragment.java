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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.Profile.MyProfileResponse;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageFragment extends Fragment {

    private int userId, teamId, teamUserId, profileId1, profileId2;
    private TextView user1NameTextView, user2NameTextView, matchingSuffix;
    private ImageView user1Image, user2Image;
    private boolean isTeamEnded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        // TextView 및 ImageView 초기화
        user1NameTextView = view.findViewById(R.id.user1_name);
        user2NameTextView = view.findViewById(R.id.user2_name);
        user1Image = view.findViewById(R.id.user1_icon);
        user2Image = view.findViewById(R.id.user2_icon);
        matchingSuffix = view.findViewById(R.id.matching_suffix);

        // 종료 버튼 설정
        ImageButton quitButton = view.findViewById(R.id.btn_hbdQuit);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user2Name을 quit_hb01으로 전달
                String user2Name = user2NameTextView.getText().toString();
                Intent intent = new Intent(getActivity(), quit_hb01.class);
                intent.putExtra("user2Name", user2Name);
                startActivity(intent);
            }
        });

        // SharedPreferences에서 userId 및 teamId 가져오기
        SharedPreferences preferences1 = requireActivity().getSharedPreferences("localID", MODE_PRIVATE);
        userId = preferences1.getInt("userId", -1);
        profileId1 = userId;
        Log.d("ManageFragment", "Retrieved userId from SharedPreferences: " + userId);

        SharedPreferences preferences2 = requireActivity().getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);
        teamId = preferences2.getInt("teamId", -1);
        Log.d("ManageFragment", "Retrieved teamId from SharedPreferences: " + teamId);

        // 팀 상태를 가져오는 API 호출
        fetchTeamStatus(teamId);

        // teamId와 userId를 사용하여 팀원 정보 가져오기
        fetchTeamUserIdAndName(teamId, userId);
        fetchProfileData(userId);  // user1의 프로필 데이터 불러오기



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
                    profileId2 = teamUserId;

                    if (!isTeamEnded) {
                        user2NameTextView.setText(response.body().getData().getNickname());
                        fetchProfileData(profileId2);  // user2의 프로필 데이터 불러오기
                    }

                    Log.d("ManageFragment", "Fetched teamUserId: " + teamUserId);
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

    private void fetchProfileData(int profileId) {
        ApiService profileService = RetrofitClient.getApiService();

        Call<MyProfileResponse> call = profileService.getProfileData(profileId);
        call.enqueue(new Callback<MyProfileResponse>() {
            @Override
            public void onResponse(Call<MyProfileResponse> call, Response<MyProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyProfileResponse.ProfileData profileData = response.body().getData();

                    // 프로필 데이터 UI 설정
                    if (profileId == profileId1) {
                        user1NameTextView.setText(profileData.getNickname());
                        loadProfileImage(profileData.getImageUrl(), user1Image);
                    } else if (profileId == profileId2 && !isTeamEnded) {
                        user2NameTextView.setText(profileData.getNickname());
                        loadProfileImage(profileData.getImageUrl(), user2Image);
                    }
                    Log.d("ManageFragment", "Fetched profile data for profileId: " + profileId);
                } else {
                    Log.e("ManageFragment", "Failed to fetch profile data.");
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                Log.e("ManageFragment", "Error fetching profile data", t);
            }
        });
    }

    private void loadProfileImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(ManageFragment.this)
                    .load(imageUrl)
                    .error(R.drawable.default_profile_image)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.default_profile_image);
        }
    }

    // 팀 상태를 가져오는 메서드
    private void fetchTeamStatus(int teamId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<TeamStatusResponse> call = apiService.getTeamStatus(teamId);

        call.enqueue(new Callback<TeamStatusResponse>() {
            @Override
            public void onResponse(Call<TeamStatusResponse> call, Response<TeamStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 팀 상태를 받아와서 matchingSuffix에 설정
                    String status = response.body().getData().getTeamStatus();
                    matchingSuffix.setText(status);
                    Log.d("ManageFragment", "Team Status: " + status);

                    // 팀 상태가 "종료"인 경우 user2의 정보를 숨김
                    if ("종료".equals(status)) {
                        isTeamEnded = true;
                        user2NameTextView.setText("없음");
                        user2Image.setImageResource(R.drawable.default_profile_image);
                    }
                } else {
                    Log.e("ManageFragment", "Failed to fetch team status.");
                    Toast.makeText(getActivity(), "Failed to fetch team status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamStatusResponse> call, Throwable t) {
                Log.e("ManageFragment", "API call failed", t);
                Toast.makeText(getActivity(), "Error fetching team status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
