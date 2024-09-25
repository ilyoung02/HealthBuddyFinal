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

import com.example.healthbuddypro.ProfileDetailActivity;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.ShortTermMatching.ShortTermMatchFragment;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatchFragment extends Fragment {

    private ViewPager2 viewPager;
    private ProfilePagerAdapter adapter;
    
    // Retrofit API 인터페이스에서 받아올 정보
    private List<UserProfile> profiles = new ArrayList<>();

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

        // 샘플 데이터 추가
//        List<UserProfile> profiles = new ArrayList<>();
//        profiles.add(new UserProfile("홍길동", "구력 6년, 벤치프레스", R.drawable.user1));
//        profiles.add(new UserProfile("이순신", "구력 2년, 스쿼트", R.drawable.default_image));
//        profiles.add(new UserProfile("유관순", "구력 4년, 데드리프트", R.drawable.default_image));

//        adapter = new ProfilePagerAdapter(profiles, profile -> {
//            Intent intent = new Intent(getContext(), ProfileDetailActivity.class);
//            intent.putExtra("name", profile.getName());
//            intent.putExtra("details", profile.getDetails());
//            intent.putExtra("imageResId", profile.getImageResId());
//            startActivity(intent);
//        });

        //Retrofit으로 백엔드에서 프로필 데이터 가져오기
        fetchProfilesFromBackend();

        return view;
    }

    private void fetchProfilesFromBackend() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://165.229.89.156:8080/") // 실제 백엔드 URL 넣기
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // API 호출
        Call<ProfileResponse> call = apiService.getProfiles();
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    profiles = response.body().getData(); // 프로필 데이터를 불러옴
                    setupViewPager(profiles); // 데이터를 받아온 후 뷰페이저 설정
                } else {
                    Toast.makeText(getContext(), "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(getContext(), "데이터 로드 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            } // 오류 처리
        });
    }

    private void setupViewPager(List<UserProfile> profiles) {
            // 받아온 profile 데이터를 사용하여 ProfilePagerAdapter 설정
            adapter = new ProfilePagerAdapter(profiles, profile -> {
                Intent intent = new Intent(getContext(), ProfileDetailActivity.class);
                intent.putExtra("nickname", profile.getNickName());
                intent.putExtra("gender", profile.getGender());
                intent.putExtra("age", profile.getAge());
                intent.putExtra("image", profile.getImage());
                startActivity(intent);
            });
            viewPager.setAdapter(adapter);
    }
}
