package com.example.instapost;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    EditText SName;
    EditText SNickName;
    EditText SEmail;
    LayoutInflater flater;
    View sign_up;
    DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private EditText MName;
    EditText MEmail;
    private ProgressBar progressBar;
    Boolean isSignup=true;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.progressBar_cyclic);
        progressBar.setVisibility(View.INVISIBLE);
        dbRef=FirebaseDatabase.getInstance().getReference("users");
        flater=LayoutInflater.from(getApplicationContext());
        sign_up=flater.inflate(R.layout.sign_up_layout,null,false);
        SEmail =sign_up.findViewById(R.id.email_register);
        SName =sign_up.findViewById(R.id.name_register);
        SNickName =sign_up.findViewById(R.id.nickname_register);
        MEmail=findViewById(R.id.email_input);
        MName=findViewById(R.id.name_input);
        mAuth=FirebaseAuth.getInstance();
    }

    public void SignUp(View view) {
        if (sign_up != null) {
            ViewGroup parent = (ViewGroup) sign_up.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
        if (isSignup) {
            Builder builder = new Builder(this);
            builder.setTitle("Sign Up");
            builder.setMessage("Please fill up the Infomation");
            builder.setCancelable(true);
            builder.setView(sign_up);
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    signUp();
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }


    public void SignIn(View view) {
        if (MEmail.getText().toString().isEmpty() || MName.getText().toString().isEmpty()) {
            Toast.makeText(com.example.instapost.MainActivity.this, "Please Signup before logging", Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(MEmail.getText().toString(),MName.getText().toString()).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()) {
                           FirebaseUser user = mAuth.getCurrentUser();
                           intent=new Intent(getApplicationContext(), com.example.instapost.InstaActivity.class);
                           startActivity(intent);
                       } else {
                           Toast.makeText(com.example.instapost.MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                       }
                    }
                });
        }
    }

    public void signUp() {
        progressBar.setVisibility(View.VISIBLE);
        if (SEmail.getText().toString().isEmpty() || SName.getText().toString().isEmpty() || SNickName.getText().toString().isEmpty()) {
            Toast.makeText(com.example.instapost.MainActivity.this, "Please fill all the details to SignUp", Toast.LENGTH_SHORT).show();
            sign_up.findViewById(R.id.email_register).findFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(SEmail.getText().toString(), SName.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(com.example.instapost.MainActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        Toast.makeText(com.example.instapost.MainActivity.this, FirebaseAuth.getInstance().getUid(), Toast.LENGTH_SHORT).show();
                        Users user = new Users(
                            SEmail.getText().toString(),
                            SName.getText().toString(),
                            SNickName.getText().toString()
                        );
                        dbRef.child(FirebaseAuth.getInstance().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(com.example.instapost.MainActivity.this, "User addedd successfully", Toast.LENGTH_LONG).show();
                                    MEmail.setText(SEmail.getText().toString());
                                    MName.setText(SName.getText().toString());
                                    progressBar.setVisibility(View.INVISIBLE);
                                    sign_up.setEnabled(false);
                                } else {
                                    Toast.makeText(com.example.instapost.MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(com.example.instapost.MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                    }
                });
        }
    }

}
