package com.example.healthbuddypro.ShortTermMatching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbuddypro.R;

public class WritePostFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button btnSubmit;
    private OnPostSubmitListener onPostSubmitListener;

    public interface OnPostSubmitListener {
        void onPostSubmitted(String title, String content);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_post, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextContent = view.findViewById(R.id.editTextContent);
        btnSubmit = view.findViewById(R.id.btn_submit);

        Spinner spinnerTopic = view.findViewById(R.id.spinner_topic);
        Spinner spinnerLocation = view.findViewById(R.id.spinner_location);
        Spinner spinnerCategory = view.findViewById(R.id.spinner_category);

        // 스피너 데이터 설정
        ArrayAdapter<CharSequence> adapterTopic = ArrayAdapter.createFromResource(getContext(), R.array.topic_array, android.R.layout.simple_spinner_item);
        adapterTopic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTopic.setAdapter(adapterTopic);

        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(getContext(), R.array.location_array, android.R.layout.simple_spinner_item);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapterLocation);

        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(getContext(), R.array.category_array, android.R.layout.simple_spinner_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

        // 글 작성 완료 버튼 클릭 리스너
        btnSubmit.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String content = editTextContent.getText().toString();

            if (!title.isEmpty() && !content.isEmpty()) {
                // 글 작성 완료 후 부모 프래그먼트로 데이터 전달
                if (onPostSubmitListener != null) {
                    onPostSubmitListener.onPostSubmitted(title, content);
                }
                // 이전 프래그먼트로 돌아가기
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "제목과 내용을 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void setOnPostSubmitListener(OnPostSubmitListener listener) {
        this.onPostSubmitListener = listener;
    }
}
