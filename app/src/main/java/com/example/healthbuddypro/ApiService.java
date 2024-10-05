package com.example.healthbuddypro;

import com.example.healthbuddypro.Login.LoginRequest;
import com.example.healthbuddypro.Login.LoginResponse;
import com.example.healthbuddypro.Matching.ProfileListResponse;
import com.example.healthbuddypro.Matching.ProfileResponse;
import com.example.healthbuddypro.Matching.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

//Restrofit API 인터페이스
public interface ApiService {
    // 프로필 목록 받아오기 => MatchFragment
    @GET("/api/workouts") //실제 백엔드 엔드포인트 경로 넣기
    Call<ProfileListResponse> getProfiles();

    // 프로필 세부 정보 받아오기 => ProfileDetailActivity, id는 프로필 고유 ID
    @GET("/api/match/profile/{profileId}")
    Call<ProfileResponse> getProfileDetails(@Path("profileId") int profileId);

    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
