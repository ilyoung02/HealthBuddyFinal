package com.example.healthbuddypro;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthbuddypro.Community.CommunityFragment;
import com.example.healthbuddypro.Fitness.FitnessFragment;
import com.example.healthbuddypro.Gym.GymFragment;
import com.example.healthbuddypro.Matching.MatchFragment;
import com.example.healthbuddypro.Profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private int currentTabIndex = 0; // 현재 선택된 탭의 인덱스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 기본 화면 설정 (매칭 화면)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_match); // 매칭 메뉴를 선택 상태로 설정
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MatchFragment())
                    .commit();
        }

        // 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int newTabIndex = 0; // 새로 선택된 탭의 인덱스

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_match) {
                selectedFragment = new MatchFragment();
                newTabIndex = 2;
            } else if (itemId == R.id.navigation_fitness) {
                selectedFragment = new FitnessFragment();
                newTabIndex = 0;
            } else if (itemId == R.id.navigation_gym) {
                selectedFragment = new GymFragment();
                newTabIndex = 1;
            } else if (itemId == R.id.navigation_community) {
                selectedFragment = new CommunityFragment();
                newTabIndex = 3;
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
                newTabIndex = 4;
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // 애니메이션 설정
                if (newTabIndex > currentTabIndex) {
                    // 오른쪽으로 이동 (오른쪽에서 왼쪽으로 슬라이드)
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (newTabIndex < currentTabIndex) {
                    // 왼쪽으로 이동 (왼쪽에서 오른쪽으로 슬라이드)
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                }

                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();

                // 현재 탭 인덱스 업데이트
                currentTabIndex = newTabIndex;
            }

            return true;
        });
    }
}