package com.filmovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    private EditText mEmail;
    private Button mResetPassword;

    private ProgressBar mProgressBar;

    //Firebase...
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mEmail = findViewById(R.id.email);
        mResetPassword = findViewById(R.id.resetPassword);
        mProgressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {

        String email = mEmail.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Obavezno polje...");
            mEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Unesite pravilan email!");
            mEmail.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Proverite Vas email!",Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Greska!",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}