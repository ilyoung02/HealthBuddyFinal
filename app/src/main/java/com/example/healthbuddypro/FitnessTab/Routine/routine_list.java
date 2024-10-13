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
    private ImageButton RoutineHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine_list);

        listView = findViewById(R.id.routineListView);
        btn_createRoutine = findViewById(R.id.btn_createRoutine);
        RoutineHome = findViewById(R.id.RoutineHome);

        //루틴 프래그먼트 홈으로 가는 버튼
        //todo : 액티비티에서 프래그먼트 이동하는거 해야됨
        RoutineHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // FragmentManager를 통해 FragmentTransaction 시작
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // RoutineFragment로 교체
                RoutineFragment routineFragment = new RoutineFragment();
                fragmentTransaction.replace(R.id.fragment_container, routineFragment); // fragment_container는 대상 레이아웃 ID입니다.
                fragmentTransaction.addToBackStack(null); // 뒤로 가기 시 이전 화면으로 돌아갈 수 있도록 설정
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit(); // 변경사항 적용
            }
        });


        //루틴 생성 버튼
        btn_createRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), make_routine01.class);
                startActivity(intent);
            }
        });

        //todo 서버에서 헬스버디 이름 + 루틴 제목을 가져오는걸로 변경해야함
        routineList = new ArrayList<>();
        routineList.add(new Routine("홍길동 헬스버디", "다이어트 운동"));

        adapter = new RoutineListAdapter(this, R.layout.activity_routine_list_item, routineList);
        listView.setAdapter(adapter);


    }
}