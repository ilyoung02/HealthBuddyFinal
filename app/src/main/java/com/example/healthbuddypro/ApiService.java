package com.example.healthbuddypro;

import com.example.healthbuddypro.FitnessTab.Routine.Exercise;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineDetailsResponse;
import com.example.healthbuddypro.FitnessTab.Routine.RoutineResponse;
import com.example.healthbuddypro.FitnessTab.Routine.WorkoutResponse;
import com.example.healthbuddypro.Login.LoginRequest;
import com.example.healthbuddypro.Login.LoginResponse;
import com.example.healthbuddypro.Matching.Chat.MatchRequest;
import com.example.healthbuddypro.Matching.Chat.MatchRequestStatus;
import com.example.healthbuddypro.Matching.Chat.MatchResponse;
import com.example.healthbuddypro.Matching.Chat.MessageRequest;
import com.example.healthbuddypro.Matching.LikeResponse;
import com.example.healthbuddypro.Matching.ProfileListResponse;
import com.example.healthbuddypro.Matching.ProfileResponse;
import com.example.healthbuddypro.ShortTermMatching.ShortMatchRequest;
import com.example.healthbuddypro.ShortTermMatching.ShortMatchRequestStatus;
import com.example.healthbuddypro.ShortTermMatching.ShortMatchResponse;
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
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

//Restrofit API 인터페이스
public interface ApiService {
    // 프로필 목록 받아오기 => MatchFragment
    @GET("/api/match/profiles") //실제 백엔드 엔드포인트 경로 넣기
    Call<ProfileListResponse> getProfiles();

    // 1:1 매칭 좋아요 기능
    @PUT("/api/match/profiles/{profileId}/likes")
    Call<LikeResponse> likeProfile(@Path("profileId") int profileId);

    // 프로필 세부 정보 받아오기 => ProfileDetailActivity, id는 프로필 고유 ID
    @GET("/api/match/profiles/{profileId}")
    Call<ProfileResponse> getProfileDetails(@Path("profileId") int profileId);

    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 루틴 운동종류 불러오는것
    @GET("/api/workouts")
    Call<WorkoutResponse> getWorkouts();

    // 회원가입 API
    @POST("/api/join")
    Call<com.example.healthbuddypro.SignUp.SignUpResponse> signUp(@Body com.example.healthbuddypro.SignUp.SignUpRequest signUpRequest);

    // 메시지 전송
    @POST("/sendMessage") // 서버의 엔드포인트에 맞게 수정
    Call<Void> sendMessage(@Body MessageRequest messageRequest);

    // 매칭 요청
    @POST("/api/match/request")
    Call<MatchResponse> sendMatchRequest(@Body MatchRequest request);
    // 매칭 요청 수락/거절 메서드
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
            @Part MultipartBody.Part file, // 이미지 파일
            @Part("profileRequest") RequestBody profileRequest // JSON 데이터
    );

    // 프로필 가져오기
    @GET("/api/match/profiles/{profileId}")
    Call<MyProfileResponse> getProfileData(
            @Path("profileId") int profileId
    );
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
