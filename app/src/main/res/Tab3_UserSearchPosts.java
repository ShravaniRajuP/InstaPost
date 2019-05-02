package com.example.instapost;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Tab3_UserSearchPosts  extends Fragment {
    View rootView;
    DatabaseReference mDbRef;
    List<String> userList;
    List<String> hashList;
    List<String> userEmailList;
    private ListView userListView;
    private ListAdapter userAdapter;
    private ListView hashTagListView;
    private ListAdapter hashTagAdapter;

    HashMap<String,List<String>> hashMap;
    DatabaseReference mUpDbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab3_usersearchposts, container, false);
        mDbRef= FirebaseDatabase.getInstance().getReference("users");
        mUpDbRef=FirebaseDatabase.getInstance().getReference("uploads");
        userList=new ArrayList<>();
        hashMap=new HashMap<>();
        hashList=new ArrayList<>();
        hashTagListView=rootView.findViewById(R.id.hashtag_list);
        userListView=rootView.findViewById(R.id.user_list);
        userEmailList=new ArrayList<>();
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                    Users user = postSnapshot.getValue(Users.class);
                    userList.add(user.Name);
                    userEmailList.add(user.Email);
                }
                userAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,userList);
                userListView.setAdapter(userAdapter);
                userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String emailid=userEmailList.get((int)id);
                        Log.d("Emaild from Tab3",emailid);
                        Intent intent = new Intent(getContext(),DashBoardsActivity.class);
                        intent.putExtra("data",emailid);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Database Error, try after some time",Toast.LENGTH_LONG).show();
            }
        });

        mUpDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    com.example.instapost.Upload upload = snap.getValue(com.example.instapost.Upload.class);
                    String hashtags= upload.mHashtag;
                    List<String> hashtagList= Arrays.asList(hashtags.split("#"));
                    for (String tag : hashtagList) {
                        if(!(hashList.contains("#"+tag)) && !(tag.trim().isEmpty())) {
                            hashList.add("#"+tag);
                        }
                    }
                }
                hashTagAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,hashList);
                hashTagListView.setAdapter(hashTagAdapter);
                hashTagListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String hashTag = hashList.get((int) id);
                        Intent intent = new Intent(getContext(),DashBoardsActivity.class);
                        intent.putExtra("data",hashTag);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return rootView;
    }
}