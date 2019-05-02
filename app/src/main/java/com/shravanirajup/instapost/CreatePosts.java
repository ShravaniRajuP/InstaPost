package com.shravanirajup.instapost;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreatePosts extends Fragment {

    private Button mUpload;
    int PICK_IMAGE_REQUEST=1;
    private ImageView mImage;
    private Uri mImageUri;
    EditText mfileName;
    EditText mComments;
    View rootView;
    Boolean isLoaded=false;
    Upload upload;
    String downloadURL;
    String filename;
    StorageReference mStorageRef;
    DatabaseReference mDataRef;
    FirebaseAuth mAuth;
    Context applicationContext;
    DatabaseReference mUserRef;
    User users;
    ProgressBar progressBar;
    String emailId;
    List<User> userList;
    HashTags hashTag;
    String mNickName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Button mButtonChooseImage;
        rootView = inflater.inflate(R.layout.tab_create_posts, container, false);

        applicationContext=rootView.getContext();
        mButtonChooseImage=rootView.findViewById(R.id.browse);
        mUpload=rootView.findViewById(R.id.upload_post);
        mImage=rootView.findViewById(R.id.imageView);
        mfileName=rootView.findViewById(R.id.email_login);
        userList=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference("/uploads/");
        mDataRef= FirebaseDatabase.getInstance().getReference("uploads");
        mUserRef=FirebaseDatabase.getInstance().getReference("users");
        emailId=mAuth.getCurrentUser().getEmail();
        users=new User();
        hashTag=new HashTags();

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap:dataSnapshot.getChildren() ) {
                    User user = snap.getValue(User.class);
                    if(Objects.equals(user.Email,emailId)) {
                        mNickName=user.NickName;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        progressBar=rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        mComments=rootView.findViewById(R.id.name_login);
        mButtonChooseImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                openChosenImage();
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        return rootView ;
    }

    public void openChosenImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    public  void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_IMAGE_REQUEST&&resultCode==-1&&data!=null && data.getData()!=null) {
            mImageUri=data.getData();
            mImage.setImageURI(mImageUri);
            Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_LONG).show();
            mUpload.setEnabled(true);
            isLoaded=true;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr= applicationContext.getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void uploadImage() {
        if(mImageUri!=null && !(mfileName.getText().toString().trim().isEmpty()) && !(mComments.getText().toString().isEmpty()) ) {
            progressBar.setVisibility(View.VISIBLE);
            filename = System.currentTimeMillis()+"."+getFileExtension(mImageUri);
            StorageReference fileReference = mStorageRef.child(filename);
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStorageRef.child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURL = uri.toString();
                            upload = new Upload(mAuth.getCurrentUser().getEmail(),
                                    downloadURL,
                                    mfileName.getText().toString().trim(),mComments.getText().toString(),mNickName);
                            String UploadId= mDataRef.push().getKey();
                            mDataRef.child(UploadId).setValue(upload);
                            mUpload.setEnabled(false);
                            Toast.makeText(getContext(),"Uploaded Successfully",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            mComments.setText("");
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            if (mfileName.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Write Some #HashTags along with this Image upload", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Image is not Selected", Toast.LENGTH_LONG).show();
            }
        }
    }
}