package com.example.instapost;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tab1_UserPosts extends Fragment {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private String userEmail;
    TextView noPosts;
    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    private List<com.example.instapost.Upload> uploadList;
    DatabaseReference mUserRef;
    List<Users> userList;
    String mNickName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab1_userposts, container, false);
        mRecyclerView=rootView.findViewById(R.id.rec_view);
        mRecyclerView.setHasFixedSize(true);
        mAuth=FirebaseAuth.getInstance();
        userList=new ArrayList<>();
        mUserRef=FirebaseDatabase.getInstance().getReference("users");
        userEmail=mAuth.getCurrentUser().getEmail();
        mRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        uploadList=new ArrayList<>();
        String view = rootView.getContext().toString();
        mDbRef= FirebaseDatabase.getInstance().getReference("uploads");
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    Users user = snap.getValue(Users.class);
                    if(Objects.equals(user.Email,userEmail)) {
                        mNickName=user.NickName;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                    com.example.instapost.Upload upload = postSnapshot.getValue(com.example.instapost.Upload.class);
                    if(Objects.equals(upload.mName,userEmail)) {
                        upload.mName=mNickName;
                        uploadList.add(upload);
                    }
                }
                if(uploadList.size()>0) {
                    mAdapter = new ImageAdapter(rootView.getContext(), uploadList);
                    mRecyclerView.setAdapter(mAdapter);
                }
                else {
                   noPosts = rootView.findViewById(R.id.noposts);
                   noPosts.setVisibility(View.VISIBLE);
                   Toast.makeText(getContext(),"No posts",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(rootView.getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

        return rootView;
    }
}