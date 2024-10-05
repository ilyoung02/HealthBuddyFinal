package com.example.healthbuddypro.Matching;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.ShortTermMatching.ShortTermMatchFragment;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatchFragment extends Fragment {
    private ViewPager2 viewPager;
    private ProfilePagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match, container, false);

        viewPager = view.findViewById(R.id.viewPager);

        Button btn1To1Matching = view.findViewById(R.id.btn_1to1_matching);
        Button btnShortTermMatching = view.findViewById(R.id.btn_short_term_matching);

        // 앱 시작 시 1:1 매칭 버튼을 기본 선택 상태로 설정
        btn1To1Matching.setSelected(true);

        // 1:1 매칭 버튼 클릭 리스너
        btn1To1Matching.setOnClickListener(v -> {
            btn1To1Matching.setSelected(true);
            btnShortTermMatching.setSelected(false);

            // 1:1 매칭 화면을 다시 로드 (이 경우엔 아무 동작 필요 없음)
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new MatchFragment());
            transaction.commit();
        });

        // 단기 매칭 버튼 클릭 리스너
        btnShortTermMatching.setOnClickListener(v -> {
            btn1To1Matching.setSelected(false);
            btnShortTermMatching.setSelected(true);

            // ShortTermMatchFragment로 전환
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            // 프래그먼트 전환 시 애니메이션 추가
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

            transaction.replace(R.id.fragment_container, new ShortTermMatchFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        //Retrofit으로 백엔드에서 프로필 데이터 가져오기
        fetchProfilesFromBackend();

        return view;
    }

    private void fetchProfilesFromBackend() {
        // Retrofit 설정 시 OkHttpClient와 로그 추가
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 로그 레벨 설정

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // 로그 인터셉터 추가
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://165.229.89.154:8080/") // 실제 백엔드 URL 넣기
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // OkHttpClient 추가
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<ProfileListResponse> call = apiService.getProfiles();
        call.enqueue(new Callback<ProfileListResponse>() {
            @Override
            public void onResponse(Call<ProfileListResponse> call, Response<ProfileListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    // 프로필 데이터를 불러와 뷰페이저에 설정
                    List<UserProfile> profiles = response.body().getData();
                    setupViewPager(profiles); // 데이터를 ViewPager에 설정
                } else {
                    String errorMessage = "데이터를 불러오지 못했습니다. 오류 코드: " + response.code();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MatchFragment", errorMessage); // Logcat에 오류 메시지 출력
                }
            }

            @Override
            public void onFailure(Call<ProfileListResponse> call, Throwable t) {
                String errorMessage = "데이터 로드 실패: " + t.getMessage();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("MatchFragment", errorMessage); // Logcat에 오류 메시지 출력
            } // 오류 처리
        });
    }

    // ProfileDetailActivity로 정보 넘겨줌 (ProfileId로 구분 -> 단일 프로필 정보 불러오기)
    private void setupViewPager(List<UserProfile> profiles) {
        // 받아온 profile 데이터를 ProfilePagerAdapter로 설정
        adapter = new ProfilePagerAdapter(profiles, profile -> {
            Intent intent = new Intent(getContext(), ProfileDetailActivity.class);
            intent.putExtra("profileId", profile.getProfileId()); // profileId 전달
            intent.putExtra("nickname", profile.getNickName());
            intent.putExtra("gender", profile.getGender());
            intent.putExtra("age", profile.getAge());
            intent.putExtra("image", profile.getImage());
            intent.putExtra("workoutYears", profile.getWorkoutYears() + "년");
            startActivity(intent);
        });
        viewPager.setAdapter(adapter);
    }
}
