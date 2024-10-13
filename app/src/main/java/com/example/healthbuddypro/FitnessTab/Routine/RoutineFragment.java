package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbuddypro.R;

public class RoutineFragment extends Fragment {

    private ImageButton btnRoutineList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_group_on_routinestart, container, false);

        // ImageButton 초기화
        btnRoutineList = view.findViewById(R.id.btn_routineList);

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
