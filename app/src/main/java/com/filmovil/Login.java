package com.filmovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mSignIn;
    private TextView mForgotPassword;
    private TextView mRegister;

    private ProgressBar mProgressBar;

    //Firebase...
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        login();
    }

    private void login() {

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mSignIn = findViewById(R.id.signIn);
        mForgotPassword = findViewById(R.id.forgotPassword);
        mRegister = findViewById(R.id.register);
        mProgressBar = findViewById(R.id.progressBar);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

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
                if(TextUtils.isEmpty(pass)){
                    mPassword.setError("Obavezno polje...");
                    mPassword.requestFocus();
                    return;
                }


                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user.isEmailVerified()){
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Uspesno logovanje",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                user.sendEmailVerification();
                                Toast.makeText(getApplicationContext(),"Proverite Vas email!",Toast.LENGTH_LONG).show();
                            }


                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Neuspesno logovanje",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        //Pokretanje prozora za registraciju
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        //Pokretanje prozora za reset lozinke
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgotPass.class));
            }
        });

    }
}