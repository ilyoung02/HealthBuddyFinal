package com.example.healthbuddypro.FitnessTab.Routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthbuddypro.R;

import java.util.ArrayList;

public class routine_list extends AppCompatActivity {

    private ListView listView;
    private RoutineListAdapter adapter;
    private ArrayList<Routine> routineList;

    private Button btn_createRoutine;
    private ImageButton backbtn;

    // todo : 루틴 수정 버튼 + 삭제버튼 동작 구현하기
    //  헬스버디가 끝나서 진행종료 된 루틴은 수정 못하게 하기
    //  진행중인 목록은 색상 바꿔서 표시하기
    //  진행중인 목록 클릭하면 홈 화면으로 이동하게 하기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine_list);

        listView = findViewById(R.id.routineListView);
        btn_createRoutine = findViewById(R.id.btn_createRoutine);
        backbtn = findViewById(R.id.backbtn);

        // 뒤로가기
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //todo : 루틴 수정, 삭제 버튼 연결하기


        //루틴 생성 버튼
        btn_createRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), make_routine01.class);
                startActivity(intent);
            }
        });

        //TODO : 서버에서 헬스버디 이름 + 루틴 제목 + 진행상황 정보 가져와서 반영하는거 해야함
        // 루틴 생성이 완료되고 돌아왔을때 갱신되는지도 확인
        routineList = new ArrayList<>();
        routineList.add(new Routine("홍길동 헬스버디", "다이어트 운동")); // 임의로 박아둔거임

        adapter = new RoutineListAdapter(this, R.layout.activity_routine_list_item, routineList);
        listView.setAdapter(adapter);


    }
}