package com.example.healthbuddypro.ShortTermMatching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthbuddypro.Matching.MatchFragment;
import com.example.healthbuddypro.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortTermMatchFragment extends Fragment implements WritePostFragment.OnPostSubmitListener {

    private ViewPager2 monthViewPager;
    private RecyclerView dayRecyclerView;
    private RecyclerView shortTermMatchList;
    private MatchListAdapter matchListAdapter;
    private DayAdapter dayAdapter;
    private List<String> matchList;
    private Map<String, List<String>> matchData;  // 날짜별 매칭 데이터를 저장하기 위한 맵

    private final String[][] daysInMonth = {
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"}, // 1월
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"}, // 2월
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"}, // 3월
            // 나머지 월들에 대해 동일하게 추가...
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_term_match, container, false);

        // 버튼 초기화
        Button btn1To1Matching = view.findViewById(R.id.btn_1to1_matching);
        Button btnShortTermMatching = view.findViewById(R.id.btn_short_term_matching);
        Button btnWritePost = view.findViewById(R.id.btn_write_post); // 글쓰기 버튼

        // ShortTermMatchFragment로 넘어왔을 때, 단기 매칭 버튼을 기본 선택 상태로 설정
        btnShortTermMatching.setSelected(true);

        // 1:1 매칭 버튼 클릭 리스너
        btn1To1Matching.setOnClickListener(v -> {
            btn1To1Matching.setSelected(true);
            btnShortTermMatching.setSelected(false);

            // MatchFragment로 전환
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            // 애니메이션 추가
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.fragment_container, new MatchFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // 글쓰기 버튼 클릭 시 WritePostFragment로 전환
        btnWritePost.setOnClickListener(v -> {
            WritePostFragment writePostFragment = new WritePostFragment();
            writePostFragment.setOnPostSubmitListener(ShortTermMatchFragment.this);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, writePostFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // ViewPager2와 RecyclerView 초기화
        monthViewPager = view.findViewById(R.id.month_view_pager);
        dayRecyclerView = view.findViewById(R.id.day_recycler_view);
        shortTermMatchList = view.findViewById(R.id.short_term_match_list);

        // 월 선택 및 일 수 표시 설정
        setupMonths();
        setupDays(0);  // 기본적으로 1월의 일수를 표시

        // 글 목록 데이터 설정
        setupMatchList();

        return view;
    }

    private void setupMonths() {
        List<String> monthList = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        MonthAdapter monthAdapter = new MonthAdapter(monthList, this::onMonthSelected);
        monthViewPager.setAdapter(monthAdapter);
    }

    private void setupDays(int monthIndex) {
        List<String> dayList = Arrays.asList(daysInMonth[monthIndex]);
        dayAdapter = new DayAdapter(dayList);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dayRecyclerView.setAdapter(dayAdapter);

        // SnapHelper를 사용하여 중앙 고정 기능 추가
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(dayRecyclerView);
    }

    private void onMonthSelected(int position) {
        setupDays(position);
        filterMatchList(position, dayAdapter.getSelectedPosition());
    }

    private void onDaySelected(int dayPosition) {
        filterMatchList(monthViewPager.getCurrentItem(), dayPosition);
    }

    private void filterMatchList(int monthPosition, int dayPosition) {
        if (monthPosition < 0 || dayPosition < 0) {
            return;
        }

        String key = monthPosition + "-" + dayPosition;
        List<String> filteredList = matchData.getOrDefault(key, new ArrayList<>());
        matchListAdapter.updateList(filteredList);  // Update list method
    }

    private void setupMatchList() {
        shortTermMatchList.setLayoutManager(new LinearLayoutManager(getContext()));

        // 매칭 데이터 초기화
        matchData = new HashMap<>();

        // 예시 데이터 삽입 (나중에 실제 데이터로 대체 가능)
        addMatchData(0, 0, "홍길동: 열심히 할 사람!!🔥");
        addMatchData(0, 1, "방일영: 잘 부탁드려요!");
        addMatchData(1, 0, "김철수: 오늘도 화이팅!");
        addMatchData(1, 2, "이영희: 다이어트 도와주세요!");

        // 기본 데이터를 리스트에 설정
        matchListAdapter = new MatchListAdapter(new ArrayList<>());
        shortTermMatchList.setAdapter(matchListAdapter);
        filterMatchList(0, 0);
    }

    private void addMatchData(int month, int day, String match) {
        String key = month + "-" + day;
        if (!matchData.containsKey(key)) {
            matchData.put(key, new ArrayList<>());
        }
        matchData.get(key).add(match);
    }

    @Override
    public void onPostSubmitted(String title, String content) {
        // 글 작성 완료 시 호출되는 메서드, 작성된 글을 선택된 날짜에 추가
        int selectedMonth = monthViewPager.getCurrentItem();
        int selectedDay = dayAdapter.getSelectedPosition();

        if (selectedMonth >= 0 && selectedDay >= 0) {
            addMatchData(selectedMonth, selectedDay, title + ": " + content);
            filterMatchList(selectedMonth, selectedDay);
        }
    }
}
