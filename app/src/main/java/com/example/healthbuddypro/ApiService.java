package com.example.healthbuddypro;

import com.example.healthbuddypro.FitnessTab.HealthBuddyEndResponse;
import com.example.healthbuddypro.FitnessTab.MatchedUserResponse;
import com.example.healthbuddypro.FitnessTab.ReviewRequest;
import com.example.healthbuddypro.FitnessTab.Routine.GymLocationResponse;
import com.example.healthbuddypro.FitnessTab.Routine.RTMatchedUserResponse;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineCompletionRequest;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineCompletionResponse;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineCreationResponse;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineDetailsResponse;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineFixRequest;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineRequest;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineResponse;
import com.example.healthbuddypro.FitnessTab.Routine.WorkoutResponse;
import com.example.healthbuddypro.FitnessTab.TeamRankingListResponse;
import com.example.healthbuddypro.FitnessTab.TeamRankingResponse;
import com.example.healthbuddypro.FitnessTab.TeamStatusResponse;
import com.example.healthbuddypro.Login.LoginRequest;
import com.example.healthbuddypro.Login.LoginResponse;
import com.example.healthbuddypro.Matching.Chat.MatchRequest;
import com.example.healthbuddypro.Matching.Chat.MatchRequestStatus;
import com.example.healthbuddypro.Matching.Chat.MatchResponse;
import com.example.healthbuddypro.Matching.Chat.MessageRequest;
import com.example.healthbuddypro.Matching.LikeResponse;
import com.example.healthbuddypro.Matching.ProfileListResponse;
import com.example.healthbuddypro.Matching.ProfileResponse;
import com.example.healthbuddypro.Profile.EditProfileResponse;
import com.example.healthbuddypro.Profile.MyProfileResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.Query;

// Retrofit API 인터페이스
public interface ApiService {
    // 프로필 목록 받아오기
    @GET("/api/match/profiles")
    Call<ProfileListResponse> getProfiles();

    // 1:1 매칭 좋아요 기능
    @POST("/api/match/profiles/{profileId}/likes")
    Call<LikeResponse> likeProfile(@Path("profileId") int profileId);

    // 프로필 세부 정보 받아오기
    @GET("/api/match/profiles/{profileId}")
    Call<ProfileResponse> getProfileDetails(@Path("profileId") int profileId);

    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 루틴 운동 종류 가져오기
    @GET("/api/workouts")
    Call<WorkoutResponse> getWorkouts();

    // 회원가입 API
    @POST("/api/join")
    Call<com.example.healthbuddypro.SignUp.SignUpResponse> signUp(@Body com.example.healthbuddypro.SignUp.SignUpRequest signUpRequest);

    // 메시지 전송
    @POST("/sendMessage")
    Call<Void> sendMessage(@Body MessageRequest messageRequest);

    // 매칭 요청
    @POST("/api/match/request")
    Call<MatchResponse> sendMatchRequest(@Body MatchRequest request);

    // 매칭 요청 응답
    @PUT("/api/match/request/{matchRequestId}")
    Call<MatchResponse> respondMatchRequest(
            @Path("matchRequestId") int matchRequestId,
            @Body MatchRequestStatus requestStatus
    );

    // 프로필 수정
    @Multipart
    @PUT("/api/match/profiles/{profileId}")
    Call<EditProfileResponse> updateProfile(
            @Path("profileId") int profileId,
            @Part MultipartBody.Part file,
            @Part("profileRequest") RequestBody profileRequest
    );

    // 프로필 가져오기
    @GET("/api/match/profiles/{profileId}")
    Call<MyProfileResponse> getProfileData(@Path("profileId") int profileId);

    // 루틴 정보 가져오기
    @GET("/api/routines/{routineId}")
    Call<RoutineResponse> getRoutineInfo(@Path("routineId") int routineId);

    // 수정된 운동 정보 POST
    @PUT("/api/routines/team")
    Call<RoutineResponse> updateRoutine(@Body RoutineResponse routineResponse);

    // 루틴 상세 정보 가져오기
    @GET("/api/routines/{routineId}/details")
    Call<RoutineDetailsResponse> getRoutineDetails(
            @Path("routineId") int routineId,
            @Query("day") String day
    );

    // 루틴 정보 수정
    @PUT("/api/routines/{routineId}")
    Call<Void> updateRoutine(@Path("routineId") int routineId, @Body RoutineFixRequest routineRequest);

    // 매칭된 상대 정보 조회 API
    @GET("/api/teams/{teamId}/users/{userId}/matched")
    Call<RTMatchedUserResponse> getMatchedUserInfo(
            @Path("teamId") int teamId, @Path("userId") int userId);

    // 매칭된 헬스버디 정보 가져오기
    @GET("/api/teams/{teamId}/users/{userId}/matched")
    Call<MatchedUserResponse> getMatchedUser(
            @Path("teamId") int teamId,
            @Path("userId") int userId
    );



    // 팀 상태 조회
    @GET("/api/teams/{teamId}/status")
    Call<TeamStatusResponse> getTeamStatus(@Path("teamId") int teamId);

    // 헬스버디 종료
    @POST("/api/teams/{teamId}/terminate")
    Call<HealthBuddyEndResponse> sendReview(
            @Path("teamId") int teamId,
            @Body ReviewRequest reviewRequest
    );

    // 팀 포인트 랭킹 목록
    @GET("/api/teams/rankings")
    Call<TeamRankingListResponse> getTeamRankingList();

    // 특정 팀 랭킹 정보 조회
    @GET("/api/teams/{teamId}/rankings")
    Call<TeamRankingResponse> getTeamRanking(@Path("teamId") int teamId);

    // 루틴 생성
    @POST("/api/routines/team")
    Call<RoutineCreationResponse> createRoutine(@Body RoutineRequest routineRequest);

    // 루틴 완료 여부 확인
    @GET("/api/routines/team/{routineId}/complete")
    Call<RoutineCompletionResponse> checkRoutineCompletion(
            @Path("routineId") int routineId,
            @Query("day") String day
    );

    // 루틴 수행 완료
    @POST("/api/routines/team/{routineId}/complete")
    Call<RoutineCompletionResponse> completeRoutine(
            @Path("routineId") int routineId,
            @Query("day") String day,
            @Body RoutineCompletionRequest completionRequest
    );

    // 헬스장 위치 정보 가져오기
    @GET("/api/gymLocation/users/{userId}")
    Call<GymLocationResponse> getGymLocation(@Path("userId") int userId);
}
