package com.example.healthbuddypro.FitnessTab;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.Profile.MyProfileResponse;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankFragment extends Fragment {

    private int userId, teamId, teamUserId, profileId1, profileId2, user1Id, user2Id;
    private TextView user1NameTextView, user2NameTextView;
    private ImageView user1Image, user2Image;
    private TextView groupPointsTextView, groupRankTextView; // 추가된 UI 요소
    private boolean isTeamEnded = false;
    private RecyclerView rankingRecyclerView;
    private TeamRankingAdapter rankingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        // UI 요소 초기화
        user1NameTextView = view.findViewById(R.id.user1_name);
        user2NameTextView = view.findViewById(R.id.user2_name);
        user1Image = view.findViewById(R.id.user1_icon);
        user2Image = view.findViewById(R.id.user2_icon);
        groupPointsTextView = view.findViewById(R.id.group_points);
        groupRankTextView = view.findViewById(R.id.group_rank);
        rankingRecyclerView = view.findViewById(R.id.recycler_view_rankings);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // SharedPreferences에서 userId 및 teamId 가져오기
        SharedPreferences preferences1 = requireActivity().getSharedPreferences("localID", MODE_PRIVATE);
        userId = preferences1.getInt("userId", -1);
        profileId1 = userId;

        SharedPreferences preferences2 = requireActivity().getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);
        teamId = preferences2.getInt("teamId", -1);

        // team 상태 가져오기
        fetchTeamStatus(teamId);

        // teamId와 userId를 사용하여 팀원 정보 가져오기
        fetchTeamUserIdAndName(teamId, userId);

        // user1의 프로필 데이터 불러오기
        fetchProfileData(userId);

        // 그룹 포인트 및 랭킹 데이터를 가져옴
        fetchTeamRankingData(teamId);

        // 랭킹 리스트 가져오기
        fetchRankingData();

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

                    if (!isTeamEnded) {
                        user2NameTextView.setText(user2Name);
                        fetchProfileData(profileId2);  // user2의 프로필 데이터 불러오기
                    }
                }
            }

            @Override
            public void onFailure(Call<MatchedUserResponse> call, Throwable t) {
                // API 호출 실패 처리
                Log.e("RankFragment", "API call failed", t);
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
                    Log.d("RankFragment", "Fetched profile data for profileId: " + profileId);
                } else {
                    Log.e("RankFragment", "Failed to fetch profile data.");
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                // API 호출 실패 처리
                Log.e("RankFragment", "Error fetching profile data", t);
            }
        });
    }

    // 프로필 이미지를 불러오는 메서드
    private void loadProfileImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(RankFragment.this)
                    .load(imageUrl)
                    .error(R.drawable.default_image)  // 오류 시 기본 이미지 표시
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.default_image);
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
                    Log.d("RankFragment", "Team Status: " + status);

                    // 팀 상태가 "종료"인 경우 user2의 정보를 숨김
                    if ("종료".equals(status)) {
                        isTeamEnded = true;
                        user2NameTextView.setText("없음");
                        user2Image.setImageResource(R.drawable.icon_person);
                        groupPointsTextView.setText("0");
                        groupRankTextView.setText("0");
                    }
                } else {
                    Log.e("RankFragment", "Failed to fetch team status.");
                    Toast.makeText(getActivity(), "Failed to fetch team status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamStatusResponse> call, Throwable t) {
                Log.e("RankFragment", "API call failed", t);
                Toast.makeText(getActivity(), "Error fetching team status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 팀 랭킹 데이터 가져오기
    private void fetchTeamRankingData(int teamId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<TeamRankingResponse> call = apiService.getTeamRanking(teamId);

        call.enqueue(new Callback<TeamRankingResponse>() {
            @Override
            public void onResponse(Call<TeamRankingResponse> call, Response<TeamRankingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeamRankingResponse rankingResponse = response.body();

                    int points = rankingResponse.getData().getPoints();
                    int rank = rankingResponse.getData().getRanks();

                    if (!isTeamEnded) {
                        // 그룹 포인트 및 랭킹 값 설정
                        groupPointsTextView.setText(String.valueOf(points));
                        groupRankTextView.setText(String.valueOf(rank));
                    }
                } else {
                    Log.e("RankFragment", "Failed to fetch team ranking data.");
                    Toast.makeText(getActivity(), "Failed to fetch team ranking data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamRankingResponse> call, Throwable t) {
                Log.e("RankFragment", "API call failed", t);
                Toast.makeText(getActivity(), "Error fetching team ranking data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRankingData() {
        ApiService apiService = RetrofitClient.getApiService();

        Call<TeamRankingListResponse> call = apiService.getTeamRankingList();
        call.enqueue(new Callback<TeamRankingListResponse>() {
            @Override
            public void onResponse(Call<TeamRankingListResponse> call, Response<TeamRankingListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API로부터 랭킹 데이터 리스트 가져오기
                    List<TeamRankingListResponse.TeamRankingData> rankingList = response.body().getData();

                    // RecyclerView에 Adapter 설정
                    rankingAdapter = new TeamRankingAdapter(getContext(), rankingList);
                    rankingRecyclerView.setAdapter(rankingAdapter);

                    Log.d("RankFragment", "Fetched ranking data successfully");
                } else {
                    Log.e("RankFragment", "Failed to fetch ranking data.");
                    Toast.makeText(getActivity(), "Failed to load rankings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamRankingListResponse> call, Throwable t) {
                Log.e("RankFragment", "Error fetching ranking data", t);
                Toast.makeText(getActivity(), "Error fetching rankings", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // 랭킹에 나타낼 유저들 가져오기
    private void fetchTeamUsers(int teamId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<TeamUsersResponse> call = apiService.getTeamUsers(teamId);

        call.enqueue(new Callback<TeamUsersResponse>() {
            @Override
            public void onResponse(Call<TeamUsersResponse> call, Response<TeamUsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeamUsersResponse.TeamUsersData data = response.body().getData();

                    // user1Id와 user2Id 설정
                    user1Id = data.getUser1Id();
                    user2Id = data.getUser2Id();

                } else {
                    Log.e("RankFragment", "Failed to fetch team users.");
                    Toast.makeText(getActivity(), "Failed to fetch team users.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamUsersResponse> call, Throwable t) {
                Log.e("RankFragment", "API call failed", t);
                Toast.makeText(getActivity(), "Error fetching team users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 사용자 이름 가져오기
    private void fetchUserName(int profileId, UserNameCallback callback) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<MyProfileResponse> call = apiService.getProfileData(profileId);

        call.enqueue(new Callback<MyProfileResponse>() {
            @Override
            public void onResponse(Call<MyProfileResponse> call, Response<MyProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nickname = response.body().getData().getNickname();
                    callback.onUserNameFetched(nickname); // 콜백 호출
                } else {
                    Log.e("RankFragment", "Failed to fetch user name for profileId: " + profileId);
                    callback.onUserNameFetched("Unknown");
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                Log.e("RankFragment", "Error fetching user name", t);
                callback.onUserNameFetched("Error");
            }
        });
    }

}
