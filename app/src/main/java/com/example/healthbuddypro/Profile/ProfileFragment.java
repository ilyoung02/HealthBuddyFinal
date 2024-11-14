package com.example.healthbuddypro.Profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.FitnessTab.TeamStatusResponse;
import com.example.healthbuddypro.Login.LoginActivity;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String BASE_URL = "http://165.229.89.154:8080/";

    private Button buttonLogout, btnEdit, btnStatus;
    private TextView profileNickname, profileGender, profileAge, profileExperience, profileLocation, profileGoal, profileBio, likeCount;
    private ImageView profileImage;
    private RecyclerView favWorkoutsRecyclerView, reviewRecyclerView;
    private FavWorkoutsAdapter favWorkoutsAdapter;
    private ReviewAdapter reviewAdapter;
    private int profileId, userId, teamId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileNickname = view.findViewById(R.id.profile_nickname);
        profileGender = view.findViewById(R.id.profile_gender);
        profileAge = view.findViewById(R.id.profile_age);
        profileExperience = view.findViewById(R.id.profile_experience);
        profileLocation = view.findViewById(R.id.profile_location);
        profileGoal = view.findViewById(R.id.exercise_goal_text);
        profileBio = view.findViewById(R.id.profile_bio_text);
        profileImage = view.findViewById(R.id.profile_image);
        likeCount = view.findViewById(R.id.like_count);
        btnStatus = view.findViewById(R.id.health_buddy_status);
        favWorkoutsRecyclerView = view.findViewById(R.id.workouts_recycler_view);
        reviewRecyclerView = view.findViewById(R.id.review_recycler_view);

        favWorkoutsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        buttonLogout = view.findViewById(R.id.buttonLogout);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnStatus.setClickable(false);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences preferences = requireActivity().getSharedPreferences("localID", MODE_PRIVATE);
        profileId = preferences.getInt("profileId", -1);
        userId = preferences.getInt("userId", -1);


        SharedPreferences preferences2 = requireActivity().getSharedPreferences("user_" + userId + "_prefs", MODE_PRIVATE);
        teamId = preferences2.getInt("teamId", -1);

        if (profileId == -1) {
            Log.e("ProfileFragment", "Profile ID not found in SharedPreferences.");
            showToast("프로필 ID를 찾을 수 없습니다.");
        } else {
            fetchProfileData(profileId);
        }

        if (teamId != -1) {
            fetchTeamStatus(teamId);
        } else {
            Log.e("ProfileFragment", "Team ID not found in SharedPreferences.");
            showToast("팀 ID를 찾을 수 없습니다.");
        }

        return view;
    }

    private void fetchProfileData(int profileId) {
        ApiService profileService = RetrofitClient.getApiService();

        Call<MyProfileResponse> call = profileService.getProfileData(profileId);
        call.enqueue(new Callback<MyProfileResponse>() {
            @Override
            public void onResponse(Call<MyProfileResponse> call, Response<MyProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyProfileResponse.ProfileData profileData = response.body().getData();

                    profileNickname.setText(profileData.getNickname());
                    profileGender.setText(profileData.getGender());
                    profileAge.setText(String.valueOf(profileData.getAge()));
                    profileExperience.setText(String.valueOf(profileData.getWorkoutYears()));
                    profileLocation.setText(profileData.getRegion());
                    profileGoal.setText(profileData.getWorkoutGoal());
                    profileBio.setText(profileData.getBio());
                    likeCount.setText(String.valueOf(profileData.getLikeCount()));

                    String imageUrl = profileData.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ProfileFragment.this)
                                .load(imageUrl)
                                .error(R.drawable.default_profile_image)
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.default_profile_image);
                    }

                    List<String> favWorkouts = profileData.getFavWorkouts();
                    favWorkoutsAdapter = new FavWorkoutsAdapter(favWorkouts);
                    favWorkoutsRecyclerView.setAdapter(favWorkoutsAdapter);

                    List<MyProfileResponse.Review> reviews = profileData.getProfileReviewResponses();
                    if (reviews != null && !reviews.isEmpty()) {
                        reviewAdapter = new ReviewAdapter(reviews);
                        reviewRecyclerView.setAdapter(reviewAdapter);
                    } else {
                        Log.d("ProfileFragment", "No reviews found for this profile.");
                    }

                } else {
                    Log.e("ProfileFragment", "Failed to fetch profile data.");
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching profile data", t);
            }
        });
    }

    private void fetchTeamStatus(int teamId) {
        ApiService apiService = RetrofitClient.getApiService();

        Call<TeamStatusResponse> call = apiService.getTeamStatus(teamId);
        call.enqueue(new Callback<TeamStatusResponse>() {
            @Override
            public void onResponse(Call<TeamStatusResponse> call, Response<TeamStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getData().getTeamStatus();

                    // Update the button text based on status
                    if ("진행".equals(status)) {
                        btnStatus.setText("헬스버디 ON");
                    } else if ("종료".equals(status)) {
                        btnStatus.setText("헬스버디 OFF");
                    }
                } else {
                    Log.e("ProfileFragment", "Failed to fetch team status.");
                }
            }

            @Override
            public void onFailure(Call<TeamStatusResponse> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching team status", t);
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("아니오", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void logoutUser() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
