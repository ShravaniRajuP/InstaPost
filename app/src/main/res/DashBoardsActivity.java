package com.example.instapost;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashBoardsActivity extends AppCompatActivity {

    Intent intent;
    String emailId="";
    String hashTag="";
    String data;
    private List<com.example.instapost.Upload> uploadList;
    DatabaseReference mUserRef;
    private com.example.instapost.ImageAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_boards);
        intent = getIntent();
        data = intent.getStringExtra("data");
        Log.d("FromDashBoard", emailId);
        mRecyclerView = findViewById(R.id.rec_view);
        mUserRef=FirebaseDatabase.getInstance().getReference("users");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(com.example.instapost.DashBoardsActivity.this));
        uploadList=new ArrayList<>();
        if(data.contains("@") || data.contains(".")) {
            emailId=data;
        } else {
                hashTag=data;
            }
        DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    com.example.instapost.Upload upload=snap.getValue(com.example.instapost.Upload.class);
                    if(emailId.length()!=0) {
                        if(Objects.equals(upload.mName,emailId)) {
                            uploadList.add(upload);
                        }
                    } else {
                            String hash = upload.mHashtag;
                            if(hash.contains(hashTag))
                            {
                                uploadList.add(upload);
                            }
                        }
                }
                if(uploadList.size()>0) {
                    mAdapter=new com.example.instapost.ImageAdapter(com.example.instapost.DashBoardsActivity.this,uploadList);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    Toast.makeText(com.example.instapost.DashBoardsActivity.this,
                            "There are no posts available right now" + String.valueOf(uploadList.size()),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(com.example.instapost.DashBoardsActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }

        });
    }
}
