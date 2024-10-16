package com.example.healthbuddypro;

import com.example.healthbuddypro.Login.LoginRequest;
import com.example.healthbuddypro.Login.LoginResponse;
import com.example.healthbuddypro.Matching.Chat.MessageRequest;
import com.example.healthbuddypro.Matching.ProfileListResponse;
import com.example.healthbuddypro.Matching.ProfileResponse;
import com.example.healthbuddypro.Profile.EditProfileResponse;
import com.example.healthbuddypro.ShortTermMatching.MatchRequest;
import com.example.healthbuddypro.ShortTermMatching.MatchRequestStatus;
import com.example.healthbuddypro.ShortTermMatching.MatchResponse;

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

//Restrofit API 인터페이스
public interface ApiService {
    // 프로필 목록 받아오기 => MatchFragment
    @GET("/api/match/profiles") //실제 백엔드 엔드포인트 경로 넣기
    Call<ProfileListResponse> getProfiles();

    // 1:1 매칭 좋아요 기능
    @PUT("/api/match/profiles/{profileId}/likes")
    Call<com.example.healthbuddypro.Matching.Chat.LikeResponse> likeProfile(@Path("profileId") int profileId);

    // 프로필 세부 정보 받아오기 => ProfileDetailActivity, id는 프로필 고유 ID
    @GET("/api/match/profiles/{profileId}")
    Call<ProfileResponse> getProfileDetails(@Path("profileId") int profileId);

    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 회원가입 API
    @POST("/api/join")
    Call<com.example.healthbuddypro.SignUp.SignUpResponse> signUp(@Body com.example.healthbuddypro.SignUp.SignUpRequest signUpRequest);

    // 매칭 신청 보내기
//    @POST("/api/match/request")
//    Call<MatchRequestResponse> sendMatchRequest(@Body MatchRequestListResponse.Data.MatchRequest request);

    // 매칭 신청 내역 조회
//    @GET("/api/match/request/user/{userId}")
//    Call<MatchRequestListResponse> getMatchRequests(@Path("userId") int userId);

    // 메시지 전송
    @POST("/sendMessage") // 서버의 엔드포인트에 맞게 수정
    Call<Void> sendMessage(@Body MessageRequest messageRequest);

    // 단기 매칭 요청
    @POST("/api/match/request")
    Call<MatchResponse> sendMatchRequest(@Body MatchRequest request);

    @PUT("/api/match/request/{matchRequestId}")
    Call<MatchResponse> respondMatchRequest(
            @Path("matchRequestId") int matchRequestId,
            @Body MatchRequestStatus requestStatus
    );

    @Multipart
    @PUT("/api/match/profiles/{profileId}")
    Call<EditProfileResponse> updateProfile(
            @Path("profileId") int profileId,
            @Part MultipartBody.Part file, // 이미지 파일
            @Part("profileRequest") RequestBody profileRequest // JSON 데이터
    );
}
