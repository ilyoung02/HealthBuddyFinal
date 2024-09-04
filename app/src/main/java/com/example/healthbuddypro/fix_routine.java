package com.example.healthbuddypro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class fix_routine extends AppCompatActivity {

    private EditText etStartDate, etEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);

        // 시작일 클릭 리스너 설정
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));

        // 종료일 클릭 리스너 설정
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                fix_routine.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 월(month)은 0부터 시작하므로 1을 더해야 정확한 월이 표시됩니다.
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editText.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }
}
