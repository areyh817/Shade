package com.example.shade.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shade.R;
import com.example.shade.view.RecyclerDecoration;
import com.example.shade.adapter.TimeLineAdapter;
import com.example.shade.model.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MySchoolFragment extends Fragment {

    RecyclerView contents_school;

    TimeLineAdapter adapter;
    ArrayList<Post> respone = new ArrayList<>();
    String my_school = "";

    private DatabaseReference databaseReference;

    androidx.appcompat.widget.Toolbar tb;
    TextView school_title;

    RecyclerDecoration recyclerDecoration;
    androidx.recyclerview.widget.RecyclerView recyclerView;

    String num, title, content, nickname;
    long like_cnt, chat_cnt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_my_school, container, false);

        contents_school = view.findViewById(R.id.contents_school);
        tb = view.findViewById(R.id.toolbar_frg);
        school_title = view.findViewById(R.id.toolSchool);

        recyclerView = view.findViewById(R.id.contents_school);
        recyclerDecoration = new RecyclerDecoration(60);
        recyclerView.addItemDecoration(recyclerDecoration);

        // 내 학교 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        String tel = sharedPreferences.getString("inputTel", null);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = databaseReference.orderByChild("phone").equalTo(tel);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    my_school = data.child("school").getValue().toString();
                    school_title.setText(my_school);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 우리 학교 글만 보기
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("posts").orderByChild("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("firebaseData", "Key : " + snapshot.getKey());

                if(snapshot.getValue(Post.class) != null){
                    Post post = snapshot.getValue(Post.class);
                    Log.w("FirebaseData", "getMySchool" + post.toString());
                    if(my_school.equals(post.getSchool())) {
                        // 각각의 데이터
                         num = post.getPost_num();
                         title = post.getTitle();
                         content = post.getContent().split("\n")[0];
                         nickname = post.getUser_nick();
                         // like_cnt = post.getLike_cnt();
                         chat_cnt = post.getComment_cnt();

                        respone.add(0, new Post(num, title, content, nickname, chat_cnt));

                        // 리스트뷰 띄우기
                        adapter = new TimeLineAdapter(respone, getContext());
                        contents_school.setAdapter(adapter);
                        contents_school.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  view;
    }
}
