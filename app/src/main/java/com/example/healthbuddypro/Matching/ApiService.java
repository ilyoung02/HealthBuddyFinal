package com.example.healthbuddypro.Matching;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

//Restrofit API 인터페이스
public interface ApiService {
    @GET("/api/match/profile") //실제 백엔드 엔드포인트 경로 넣기
    Call<ProfileResponse> getProfiles();
}
