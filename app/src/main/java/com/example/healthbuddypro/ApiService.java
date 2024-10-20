package com.example.healthbuddypro;

import com.example.healthbuddypro.FitnessTab.Routine.Exercise;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineDetailsResponse;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineResponse;
import com.example.healthbuddypro.FitnessTab.Routine.WorkoutResponse;
import com.example.healthbuddypro.Login.LoginRequest;
import com.example.healthbuddypro.Login.LoginResponse;
import com.example.healthbuddypro.Matching.Chat.MessageRequest;
import com.example.healthbuddypro.Matching.MatchRequestListResponse;
import com.example.healthbuddypro.Matching.MatchRequestResponse;
import com.example.healthbuddypro.Matching.ProfileListResponse;
import com.example.healthbuddypro.Matching.ProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    // 루틴 운동종류 불러오는것
    @GET("/api/workouts")
    Call<WorkoutResponse> getWorkouts();

    // 회원가입 API
    @POST("/api/join")
    Call<com.example.healthbuddypro.SignUp.SignUpResponse> signUp(@Body com.example.healthbuddypro.SignUp.SignUpRequest signUpRequest);

    // 매칭 신청 보내기
    @POST("/api/match/request")
    Call<MatchRequestResponse> sendMatchRequest(@Body MatchRequestListResponse.Data.MatchRequest request);

    // 매칭 신청 내역 조회
    @GET("/api/match/request/user/{userId}")
    Call<MatchRequestListResponse> getMatchRequests(@Path("userId") int userId);

    // 메시지 전송
    @POST("/sendMessage") // 서버의 엔드포인트에 맞게 수정
    Call<Void> sendMessage(@Body MessageRequest messageRequest);

    //todo : 백엔드 api 수정해서 엔드포인트 아래에 있는 것들 수정하기

    // 운동 정보 GET
    @GET("/api/routines/{routineId}")
    Call<RoutineResponse> getRoutineInfo(@Path("routineId") int teamId);

    // 수정된 운동 정보 POST
    @PUT("/api/routines/team")
    Call<RoutineResponse> updateRoutine(@Body RoutineResponse routineResponse);

    //특정 요일 루틴 정보들 불러오는거
    //todo : day= 이거 이렇게 넘기는거 맞는지 테스트해보고 확인하기
    @GET("/api/routines/{routineId}/details")
    Call<RoutineDetailsResponse> getRoutineDetails(
            @Path("routineId") int routineId,
            @Query("day") String day
    );

}
