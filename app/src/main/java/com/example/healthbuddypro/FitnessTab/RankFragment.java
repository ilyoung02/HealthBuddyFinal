package com.example.healthbuddypro.FitnessTab;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class RankFragment extends Fragment {

    private int userId, teamId, teamUserId, profileId1, profileId2;
    private TextView user1NameTextView, user2NameTextView;
    private ImageView user1Image, user2Image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        // UI 요소 초기화
        user1NameTextView = view.findViewById(R.id.user1_name);
        user2NameTextView = view.findViewById(R.id.user2_name);
        user1Image = view.findViewById(R.id.user1_icon);
        user2Image = view.findViewById(R.id.user2_icon);

        // SharedPreferences에서 userId 및 teamId 가져오기
        SharedPreferences preferences1 = requireActivity().getSharedPreferences("localID", MODE_PRIVATE);
        userId = preferences1.getInt("userId", -1);
        profileId1 = userId;

        SharedPreferences preferences2 = requireActivity().getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);
        teamId = preferences2.getInt("teamId", -1);

        // teamId와 userId를 사용하여 팀원 정보 가져오기
        fetchTeamUserIdAndName(teamId, userId);
        fetchProfileData(userId);  // user1의 프로필 데이터 불러오기

        return view;
    }

    private void fetchTeamUserIdAndName(int teamId, int userId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<MatchedUserResponse> call = apiService.getMatchedUser(teamId, userId);

        call.enqueue(new Callback<MatchedUserResponse>() {
            @Override
            public void onResponse(Call<MatchedUserResponse> call, Response<MatchedUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teamUserId = response.body().getData().getUserId();
                    String user2Name = response.body().getData().getNickname();

                    profileId2 = teamUserId;
                    user2NameTextView.setText(user2Name);

                    fetchProfileData(profileId2);  // user2의 프로필 데이터 불러오기
                }
            }

            @Override
            public void onFailure(Call<MatchedUserResponse> call, Throwable t) {
                // API 호출 실패 처리
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
                    } else if (profileId == profileId2) {
                        user2NameTextView.setText(profileData.getNickname());
                        loadProfileImage(profileData.getImageUrl(), user2Image);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                // API 호출 실패 처리
            }
        });
    }

    // 프로필 이미지를 불러오는 메서드
    private void loadProfileImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(RankFragment.this)
                    .load(imageUrl)
                    .error(R.drawable.default_profile_image)  // 오류 시 기본 이미지 표시
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.default_profile_image);
        }
    }
}
