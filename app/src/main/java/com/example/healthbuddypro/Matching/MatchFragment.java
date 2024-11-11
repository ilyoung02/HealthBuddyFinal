package com.example.healthbuddypro.Matching;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.Matching.Chat.ChatActivity;
import com.example.healthbuddypro.Matching.Chat.MatchRequestData;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.example.healthbuddypro.ShortTermMatching.ShortTermMatchFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MatchFragment extends Fragment {
    private ViewPager2 viewPager;
    private ProfilePagerAdapter adapter;

    private static final String CHANNEL_ID = "MATCH_REQUEST_CHANNEL";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private FirebaseFirestore firebaseFirestore;
    private int userId; // 로그인된 사용자의 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match, container, false);

        // FirebaseFirestore 초기화
        firebaseFirestore = FirebaseFirestore.getInstance();

        // 예시로 SharedPreferences에서 userId를 가져오는 방법
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localID", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // 알림 권한 요청
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
        } else {
            createNotificationChannel();
            checkPendingMatchRequests();
        }

        // 초기화 및 버튼 리스너 설정
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
        ApiService apiService = RetrofitClient.getApiService();

        // 백엔드에서 모든 사용자의 프로필 데이터 가져오는 부분
        Call<ProfileListResponse> call = apiService.getProfiles();
        call.enqueue(new Callback<ProfileListResponse>() {
            @Override
            public void onResponse(Call<ProfileListResponse> call, Response<ProfileListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    List<UserProfile> profiles = response.body().getData();
                    if (profiles != null && !profiles.isEmpty()) {
                        setupViewPager(profiles); // 데이터를 ViewPager에 설정
                    } else {
                        Toast.makeText(getContext(), "프로필 목록이 비어 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "데이터를 불러오지 못했습니다. 오류 코드: " + response.code();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MatchFragment", errorMessage);
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

    // 데이터를 item_profile로 보여주는 부분
    // ProfileDetailActivity로 정보 넘겨줌 (ProfileId로 구분 -> 단일 프로필 정보 불러오기)
    private void setupViewPager(List<UserProfile> profiles) {
        adapter = new ProfilePagerAdapter(profiles, profile -> {
            Intent intent = new Intent(getContext(), ProfileDetailActivity.class);
            intent.putExtra("profileId", profile.getProfileId());
            intent.putExtra("nickname", profile.getNickName() != null ? profile.getNickName() : "Unknown");
            intent.putExtra("gender", profile.getGender() != null ? profile.getGender() : "Not specified");
            intent.putExtra("age", profile.getAge());
            intent.putExtra("image", profile.getImageUrl() != null ? profile.getImageUrl() : "");
            intent.putExtra("workoutYears", profile.getWorkoutYears() + "년");
            intent.putExtra("workoutGoal", profile.getWorkoutGoal());
            intent.putExtra("bio", profile.getBio());
            intent.putExtra("region", profile.getRegion());
            intent.putExtra("likeCount", profile.getLikeCount());
            startActivity(intent);
        });
        viewPager.setAdapter(adapter);
    }

    private void requestNotificationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
    }

    private void checkPendingMatchRequests() {
        firebaseFirestore.collection("match_requests")
                .whereEqualTo("receiverId", userId)
                .whereEqualTo("status", "REQUESTED")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("MatchFragment", "매칭 요청 수신 오류", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            MatchRequestData matchRequest = doc.toObject(MatchRequestData.class);
                            sendNotification(matchRequest.getMatchRequestId(), matchRequest.getSenderId());
                        }
                    }
                });
    }

    private void sendNotification(int matchRequestId, int senderId) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("matchRequestId", matchRequestId);
        intent.putExtra("profileId", senderId);

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("새로운 매칭 요청")
                .setContentText("채팅방으로 이동하여 매칭 요청을 확인하세요.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(matchRequestId, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "매칭 요청 알림";
            String description = "새로운 매칭 요청이 있을 때 알림을 보냅니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
