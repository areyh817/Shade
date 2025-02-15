package com.example.shade.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.shade.R;
import com.example.shade.model.Post;
import com.example.shade.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class WritePostActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    public static String format = "yyyy/MM/dd kk:mm:ss";

    SharedPreferences sharedPreferences;

    EditText editTitle, editContent;
    Button btnFinish;
    Toolbar toolbarWrite;
    ImageButton btnBack;

    Random random = new Random();
    final String r1 = String.valueOf((char) ((int) (random.nextInt(26))+65));
    final String r2 = String.valueOf((char) ((int) (random.nextInt(26))+65));
    final String r3 = String.valueOf((char) ((int) (random.nextInt(26))+65));
    final String r4 = String.valueOf((char) ((int) (random.nextInt(26))+65));
    final String r5 = String.valueOf((char) ((int) (random.nextInt(26))+65));

    Date post_date;
    SimpleDateFormat format1;

    String intent_post_num = "";
    String intent_title = "";
    String intent_content = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_detail);

        Intent intent = getIntent();
        System.out.println(intent);
        intent_post_num = intent.getStringExtra("post_num");
        System.out.println("ii " + intent_post_num);
        intent_title = intent.getStringExtra("post_title");
        intent_content = intent.getStringExtra("post_content");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnFinish = findViewById(R.id.btn_finish);
        toolbarWrite = findViewById(R.id.toolbar_write);
        btnBack = findViewById(R.id.btnBackWrite);

        editTitle.setText(intent_title);
        editContent.setText(intent_content);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTitle.getText().toString().equals("") && editContent.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(editTitle.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if(editContent.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    String title = editTitle.getText().toString();
                    String content = editContent.getText().toString();

                    sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
                    String nick = sharedPreferences.getString("inputNickName", null);
                    String tel = sharedPreferences.getString("inputTel", null);

                    // 닉네임 가져오기 (안 된거 같음)
                    String user_nick = nick;

                    String post_num = "";
                    if(intent_post_num == null) {
                        // 글 번호 랜덤 생성 (알파벳)
                        post_num = (String.valueOf(r1 + r2 + r3 + r4 + r5)).toString();
                    }else{
                        post_num = intent_post_num;
                    }

                    // 날짜 생성
                    post_date = Calendar.getInstance().getTime();
                    format1 = new SimpleDateFormat(format, Locale.getDefault());
                    String date = format1.format(post_date);

                    // 학교가져오기
                    String finalPost_num = post_num;
                    mDatabase.child("users").child(tel).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue(User.class) != null){
                                User user = snapshot.getValue(User.class);
                                String school = user.getSchool();

                                addPost(finalPost_num, user_nick, title, content, date, school, tel);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }

    public void addPost(String post_num, String user_nick, String title, String content, String date, String school, String tel){
        Post post = new Post(post_num, user_nick, title, content, date, school, tel);

        mDatabase.child("posts").child(post_num).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(intent_post_num != null){
                    Toast.makeText(getApplicationContext(), "글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "글이 등록되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "글이 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
