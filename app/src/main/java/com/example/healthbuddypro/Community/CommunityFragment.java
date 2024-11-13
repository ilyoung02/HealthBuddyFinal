package com.example.healthbuddypro.Community;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbuddypro.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private EditText etPostContent;
    private Button btnPost;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        etPostContent = view.findViewById(R.id.etPostContent);
        btnPost = view.findViewById(R.id.btnPost);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();

        // 게시물 불러오기
        loadPosts();

        // 게시물 추가
        btnPost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();
            if (!TextUtils.isEmpty(content)) {
                // 새로운 게시물 생성
                Post newPost = new Post("익명" + (postList.size() + 1), "방금 전", content);
                postList.add(0, newPost); // 새 게시물을 리스트의 첫 번째 위치에 추가
                postAdapter.notifyItemInserted(0); // 데이터 변경 알리기
                recyclerView.scrollToPosition(0); // 스크롤을 새 게시물로 이동
                etPostContent.setText(""); // 입력 필드 초기화

                // Firestore에 게시물 저장
                firebaseFirestore.collection("posts")
                        .add(newPost)
                        .addOnSuccessListener(documentReference -> {
                            newPost.setId(documentReference.getId()); // Firestore 문서 ID 할당
                            Toast.makeText(getContext(), "게시글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "게시글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }

    // Firestore에서 게시물 목록을 불러오는 메서드
    private void loadPosts() {
        firebaseFirestore.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Post post = documentSnapshot.toObject(Post.class);
                        post.setId(documentSnapshot.getId()); // Firestore에서 불러온 ID 설정
                        postList.add(post);
                    }
                    postAdapter.notifyDataSetChanged(); // 데이터 갱신
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "게시물 불러오기 실패", Toast.LENGTH_SHORT).show();
                });
    }
}
