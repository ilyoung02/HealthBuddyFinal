// RoutineFragment 파일 -> 그날 루틴 수행 완료여부 내부저장소로해서 프래그먼트 변경되는거 추가한 부분임 마지막에 추가하자
package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutineFragment extends Fragment {

    private int routineId;
    private int userId;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("RoutinePrefs", Context.MODE_PRIVATE);
        userId = getUserIdFromStorage();

        // 1. userId로 Firestore에서 teamId 조회
        fetchTeamIdFromFirestore(userId);

        return view;
    }

    // userId를 통해 Firestore에서 teamId를 조회하는 메서드
    private void fetchTeamIdFromFirestore(int userId) {
        if (userId == -1) {
            Log.e("RoutineFragment", "유효하지 않은 userId입니다.");
            saveTeamIdToSharedPreferences(-1); // teamId를 -1로 저장
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(String.valueOf(userId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long fetchedTeamId = documentSnapshot.getLong("teamId");
                        if (fetchedTeamId != null) {
                            int teamId = fetchedTeamId.intValue();
                            Log.d("RoutineFragment", "Firestore에서 teamId 조회 성공: " + teamId);

                            // teamId를 내부 저장소에 저장
                            saveTeamIdToSharedPreferences(teamId);

                            // teamId로 Firestore에서 routineId 조회
                            fetchRoutineIdFromFirestore(teamId);
                        } else {
                            Log.e("RoutineFragment", "Firestore 문서에 teamId가 없습니다.");
                            saveTeamIdToSharedPreferences(-1); // teamId를 -1로 저장
                        }
                    } else {
                        Log.e("RoutineFragment", "Firestore 문서가 존재하지 않습니다.");
                        saveTeamIdToSharedPreferences(-1); // teamId를 -1로 저장
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RoutineFragment", "Firestore teamId 조회 실패: " + e.getMessage());
                    saveTeamIdToSharedPreferences(-1); // teamId를 -1로 저장
                });
    }

    // teamId를 SharedPreferences에 저장하는 메서드
    private void saveTeamIdToSharedPreferences(int teamId) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_" + userId + "_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("teamId", teamId);
        editor.apply();
        Log.d("RoutineFragment", "teamId를 내부 저장소에 저장: " + teamId);
    }


    // teamId를 통해 Firestore에서 routineId를 확인하고 저장하는 메서드
    private void fetchRoutineIdFromFirestore(int teamId) {
        if (teamId == -1) {
            Log.e("RoutineFragment", "유효하지 않은 teamId입니다.");
            saveRoutineIdToSharedPreferences(-1); // routineId를 -1로 저장
            decideFragmentToShow(-1); // 루틴이 없음을 Fragment에 전달
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("routineIdShare")
                .document(String.valueOf(teamId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long fetchedRoutineId = documentSnapshot.getLong("routineId");
                        if (fetchedRoutineId != null) {
                            int routineId = fetchedRoutineId.intValue();
                            Log.d("RoutineFragment", "Firestore에서 routineId 조회 성공: " + routineId);

                            // routineId를 내부 저장소에 저장
                            saveRoutineIdToSharedPreferences(routineId);
                            // 루틴이 있음을 Fragment에 전달
                            decideFragmentToShow(routineId);

                        } else {
                            Log.d("RoutineFragment", "Firestore 문서에 routineId가 없습니다.");
                            decideFragmentToShow(-1); // 루틴이 없음을 Fragment에 전달
                            saveRoutineIdToSharedPreferences(-1); // routineId를 -1로 저장
                        }
                    } else {
                        Log.d("RoutineFragment", "teamId에 해당하는 Firestore 문서가 존재하지 않습니다.");
                        saveRoutineIdToSharedPreferences(-1); // routineId를 -1로 저장
                        decideFragmentToShow(-1); // 루틴이 없음을 Fragment에 전달
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RoutineFragment", "Firestore routineId 조회 실패: " + e.getMessage());
                    saveRoutineIdToSharedPreferences(-1); // routineId를 -1로 저장
                    decideFragmentToShow(-1); // 루틴이 없음을 Fragment에 전달
                });
    }

    // routineId를 SharedPreferences에 저장하는 메서드
    private void saveRoutineIdToSharedPreferences(int routineId) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("routineId" + userId + "_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("routineId", routineId);
        editor.apply();
        Log.d("RoutineFragment", "routineId를 내부 저장소에 저장: " + routineId);
    }


    // routineId와 오늘 요일에 따라 적절한 Fragment를 표시하는 메서드
    private void decideFragmentToShow(int routineId) {
        if (routineId == -1) {
            // 1. 루틴이 없으면 GroupLockedFragment 표시
            replaceFragment(new GroupLockedFragment());
        } else {
            // 2. 루틴이 있으면 오늘의 루틴 정보 확인
            checkTodayRoutine(routineId);
        }
    }

    // 오늘 요일을 가져와 /api/routines/{routineId}/details?day= 요청을 통해 루틴 정보를 확인하고, Fragment를 전환하는 메서드
    private void checkTodayRoutine(int routineId) {
        String todayDay = getTodayDay();
        ApiService apiService = RetrofitClient.getApiService();

        // 오늘 요일에 대한 루틴 정보 확인
        Call<RoutineDetailsResponse> call = apiService.getRoutineDetails(routineId, todayDay);
        call.enqueue(new Callback<RoutineDetailsResponse>() {
            @Override
            public void onResponse(Call<RoutineDetailsResponse> call, Response<RoutineDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RoutineDetailsResponse routineDetails = response.body();
                    if (routineDetails.getCode() == 200) {
                        // 2. 오늘 요일에 해당하는 루틴이 있고, 수행 완료 전이면 RoutineStartFragment 표시
                        if (!isRoutineCompleted()) {
                            replaceFragment(new RoutineStartFragment());
                        } else {
                            // 루틴이 완료된 상태라면 RoutineCompletedFragment 표시
                            replaceFragment(new RoutineCompletedFragment());
                        }
                    } else {
                        // 3. 오늘 요일에 해당하는 루틴 정보가 없으면 NoRoutineFragment 표시
                        replaceFragment(new NoRoutineFragment());
                    }
                } else {
                    // 3. 오늘 요일에 해당하는 루틴 정보가 없으면 NoRoutineFragment 표시
                    replaceFragment(new NoRoutineFragment());
                }
            }

            @Override
            public void onFailure(Call<RoutineDetailsResponse> call, Throwable t) {
                // 4. 오류 발생 시 ErrorFragment 표시
                Log.e("RoutineFragment", "Error fetching today's routine", t);
                replaceFragment(new ErrorFragment());
            }
        });
    }

    // 루틴 수행 완료 상태를 저장하는 메서드
    public void saveRoutineCompletionStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRoutineComplete_" + userId, true);
        editor.putString("completionDate", getTodayDateString());
        editor.apply();
    }

    // 루틴 완료 여부를 확인하는 메서드
    private boolean isRoutineCompleted() {
        String TAG = "루틴 완료 여부";
        Log.d(TAG, "isRoutineCompleted: Checking routine completion status");

        SharedPreferences routinePrefs = requireActivity().getSharedPreferences("RoutinePrefs", Context.MODE_PRIVATE);
        String lastCompletionDay = routinePrefs.getString("routineCompleteDay_" + userId, ""); // 마지막 완료된 요일
        boolean isComplete = routinePrefs.getBoolean("isRoutineComplete_" + userId, false); // 완료 여부

        String todayDay = getTodayDay(); // 오늘의 요일

        Log.d(TAG, "isRoutineCompleted: lastCompletionDay = " + lastCompletionDay + ", todayDay = " + todayDay + ", isComplete = " + isComplete);

        // 오늘의 요일과 저장된 완료된 요일을 비교
        if (!lastCompletionDay.equals(todayDay)) {
            Log.d(TAG, "isRoutineCompleted: Completion day mismatch, resetting completion status");
            clearRoutineCompletionStatus(); // 요일이 다르면 완료 상태 초기화
            return false;
        }

        return isComplete;
    }



    // 루틴 완료 상태 초기화
    private void clearRoutineCompletionStatus() {
        Log.d("루틴 완료 여부 초기화", "루틴 완료 여부 초기화: Clearing routine completion status");
        SharedPreferences routinePrefs = requireActivity().getSharedPreferences("RoutinePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = routinePrefs.edit();
        editor.remove("isRoutineComplete_" + userId); // 완료 여부 초기화
        editor.remove("routineCompleteDay_" + userId); // 완료된 날짜 초기화
        editor.apply();
    }


    // 오늘의 날짜 문자열을 반환하는 메서드 (yyyyMMdd 형식)
    private String getTodayDateString() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }

    // 오늘의 요일을 대문자 형식으로 반환하는 메서드 (예: "MON")
    private String getTodayDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E", Locale.getDefault());
        return dateFormat.format(new Date()).toUpperCase();
    }

    // 내부 저장소에서 routineId를 가져오는 메서드
    private int getRoutineIdFromStorage() {
        Context context = getActivity();
        if (context != null && userId != -1) {
            return context.getSharedPreferences("routineId" + userId + "_prefs", Context.MODE_PRIVATE)
                    .getInt("routineId", -1);
        }
        return -1;
    }

    // userId를 내부 저장소에서 가져오는 메서드
    private int getUserIdFromStorage() {
        Context context = getActivity();
        if (context != null) {
            return context.getSharedPreferences("localID", Context.MODE_PRIVATE)
                    .getInt("userId", -1);
        }
        return -1;
    }

    // Fragment를 동적으로 교체하는 메서드
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
