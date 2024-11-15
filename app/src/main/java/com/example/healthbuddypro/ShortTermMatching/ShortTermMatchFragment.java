package com.example.healthbuddypro.ShortTermMatching;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.Matching.Chat.ChatActivity;
import com.example.healthbuddypro.Matching.MatchFragment;
import com.example.healthbuddypro.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortTermMatchFragment extends Fragment implements WritePostFragment.OnPostSubmitListener {

    private RecyclerView dayRecyclerView;
    private RecyclerView shortTermMatchList;
    private MatchListAdapter matchListAdapter;
    private DayOfWeekAdapter dayAdapter;
    private Map<String, List<ShortMatchPost>> matchData;

    private FirebaseFirestore db;
    private String selectedDay;  // 선택된 요일을 저장할 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_term_match, container, false);

        db = FirebaseFirestore.getInstance();

        Button btn1To1Matching = view.findViewById(R.id.btn_1to1_matching);
        Button btnShortTermMatching = view.findViewById(R.id.btn_short_term_matching);
        Button btnWritePost = view.findViewById(R.id.btn_write_post);

        btnShortTermMatching.setSelected(true);

        btn1To1Matching.setOnClickListener(v -> {
            btn1To1Matching.setSelected(true);
            btnShortTermMatching.setSelected(false);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
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

        // 요일 선택 RecyclerView와 매칭 리스트 RecyclerView 초기화
        dayRecyclerView = view.findViewById(R.id.day_of_week_recycler_view);
        shortTermMatchList = view.findViewById(R.id.short_term_match_list);

        setupDays();  // 요일 선택 RecyclerView 설정
        setupMatchList();  // 매칭 목록 설정

        return view;
    }

    private void setupDays() {
        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("월");
        daysOfWeek.add("화");
        daysOfWeek.add("수");
        daysOfWeek.add("목");
        daysOfWeek.add("금");
        daysOfWeek.add("토");
        daysOfWeek.add("일");

        dayAdapter = new DayOfWeekAdapter(daysOfWeek, this::onDaySelected);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dayRecyclerView.setAdapter(dayAdapter);
    }

    private void setupMatchList() {
        shortTermMatchList.setLayoutManager(new LinearLayoutManager(getContext()));

        matchData = new HashMap<>();

        matchListAdapter = new MatchListAdapter(new ArrayList<>());
        shortTermMatchList.setAdapter(matchListAdapter);

        filterMatchList("월"); // 기본적으로 월요일 데이터 불러오기
    }

    private void onDaySelected(String day) {
        selectedDay = day;  // 사용자가 선택한 요일 저장
        filterMatchList(day);  // 선택된 요일에 맞는 매칭 목록 표시
    }

    @Override
    public void onPostSubmitted(String title, String health, String content, String location, String category) {
        int senderId = 1; // 임시로 사용자 ID 설정
        int receiverId = 2; // 임시로 매칭 대상자 ID 설정

        // 새로운 매칭 데이터를 선택된 요일에 추가
        ShortMatchPost newPost = new ShortMatchPost(senderId, receiverId, title, health, content, location, category);

        // 선택된 요일에 맞게 Firestore에 저장
        savePostToFirestore(selectedDay, newPost);

        // 매칭 목록 갱신
        filterMatchList(selectedDay);  // 선택된 요일로 매칭 목록을 다시 불러옴
    }

    public void savePostToFirestore(String day, ShortMatchPost post) {
        Map<String, Object> postData = new HashMap<>();
        postData.put("senderId", post.getSenderId());
        postData.put("receiverId", post.getReceiverId());
        postData.put("title", post.getTitle());
        postData.put("health", post.getContent());
        postData.put("content", post.getHealth());
        postData.put("location", post.getLocation());
        postData.put("category", post.getCategory());

        db.collection("shortTermMatches")
                .document(day)  // 선택된 요일에 문서 저장
                .collection("posts")
                .add(postData)
                .addOnSuccessListener(documentReference -> {
                    filterMatchList(day); // 성공적으로 저장 후 해당 요일에 맞는 매칭 목록 갱신
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving post", e);
                });
    }

    private void filterMatchList(String day) {
        db.collection("shortTermMatches")
                .document(day)
                .collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ShortMatchPost> posts = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ShortMatchPost post = documentSnapshot.toObject(ShortMatchPost.class);
                        if (post != null) {
                            posts.add(post);
                        }
                    }
                    matchListAdapter.updateList(posts);  // 가져온 데이터로 매칭 목록 갱신
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting posts", e);
                });
    }
}
