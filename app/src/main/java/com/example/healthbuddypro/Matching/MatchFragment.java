package com.example.healthbuddypro.Matching;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthbuddypro.Matching.Chat.ChatActivity;
import com.example.healthbuddypro.ProfileDetailActivity;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.ShortTermMatching.ShortTermMatchFragment;
import java.util.ArrayList;
import java.util.List;

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

        // 샘플 데이터 추가
        List<UserProfile> profiles = new ArrayList<>();
        profiles.add(new UserProfile("홍길동", "구력 6년, 벤치프레스", R.drawable.user1));
        profiles.add(new UserProfile("이순신", "구력 2년, 스쿼트", R.drawable.default_image));
        profiles.add(new UserProfile("유관순", "구력 4년, 데드리프트", R.drawable.default_image));

        adapter = new ProfilePagerAdapter(profiles, profile -> {
            Intent intent = new Intent(getContext(), ProfileDetailActivity.class);
            intent.putExtra("name", profile.getName());
            intent.putExtra("details", profile.getDetails());
            intent.putExtra("imageResId", profile.getImageResId());
            startActivity(intent);
        });

        viewPager.setAdapter(adapter);

        return view;
    }
}
