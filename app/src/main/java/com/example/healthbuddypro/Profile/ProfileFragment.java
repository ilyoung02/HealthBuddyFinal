package com.example.healthbuddypro.Profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.healthbuddypro.Login.LoginActivity;
import com.example.healthbuddypro.R;

public class ProfileFragment extends Fragment {

    private Button login_btn;
    private Button buttonLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 프래그먼트 레이아웃을 inflate
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 로그인 버튼 초기화
        login_btn = view.findViewById(R.id.login_btn);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        // 로그인 버튼 클릭 리스너 설정
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LoginActivity로 이동하는 Intent 생성
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼 클릭 리스너 설정
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그아웃 확인 팝업 표시
                showLogoutConfirmationDialog();
            }
        });

        return view;
    }

    // 로그아웃 확인 팝업
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext()) // 팝업을 표시하기 위해 AlertDialog 사용
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 로그아웃 실행
                        logoutUser();
                    }
                })
                .setNegativeButton("아니오", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // 로그아웃 처리 메서드
    private void logoutUser() {
        // SharedPreferences 초기화
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 저장된 로그인 정보 삭제
        editor.clear(); // 모든 저장된 값을 삭제
        editor.apply(); // 변경사항 적용

        // 로그아웃 후 LoginActivity로 이동
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 로그인 화면으로 이동 후 이전 액티비티 스택 지움
        startActivity(intent);

        // 프래그먼트에서 finish는 호출할 수 없으므로 액티비티 종료는 하지 않음
        requireActivity().finish(); // 현재 액티비티 종료
    }
}