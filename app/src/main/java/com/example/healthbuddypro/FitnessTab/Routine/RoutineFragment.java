package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbuddypro.R;

public class RoutineFragment extends Fragment {

    private ImageButton btnRoutineList;
    private Button btn_startRoutine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_group_on_routinestart, container, false);

        //TODO : 루틴 설정을 다하고 RoutineFragment 로 넘어왔을 때 설정한 정보를 오늘의 운동에 표시되도록 서버로부터 받아와서
        // 그 정보들이 갱신되어 화면에 나타나도록 해야함 -> 설정 안했을때는 group_locked 화면이 떠야하고 / 설정이 되면 그 정보가 떠야함
        // moved_routineFragment 은 화면 덧씌우기용이라 여기에 동작 코드를 추가해야함

        // ImageButton 초기화
        btnRoutineList = view.findViewById(R.id.btn_routineList);

        btn_startRoutine = view.findViewById(R.id.btn_startRoutine);

        // 루틴 시작 버튼 -> 위치 인증으로 감
        btn_startRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), check_location.class);
                startActivity(intent);
            }
        });

        // 버튼 클릭 리스너 설정
        btnRoutineList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), routine_list.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
