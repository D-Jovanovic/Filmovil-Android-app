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
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    //Definisanje elemenata na frontu
    private TextView mNaslov;
    private EditText mName;
    private EditText mAge;
    private EditText mEmail;
    private EditText mPassword;
    private Button mRegister;

    private ProgressBar mProgressBar;

    //Firebase objekat
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Dobijanje instance Firabase objketa
        mAuth = FirebaseAuth.getInstance();
        //Metoda ya registraciju
        registration();
    }

    private void registration() {

        //Povezivanje komponneti sa frontom
        mNaslov = findViewById(R.id.naslov);
        mName = findViewById(R.id.name);
        mAge = findViewById(R.id.age);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegister = findViewById(R.id.register);
        mProgressBar = findViewById(R.id.progressBar);

        //Programiranje dugmeta za registaciju
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Izvlacenje podataka iz komponenti
                String name = mName.getText().toString().trim();
                String age = mAge.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                //Provera da li su podaci pravilni
                if(TextUtils.isEmpty(name)){
                    mName.setError("Obavezno polje...");
                    mName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(age)){
                    mAge.setError("Obavezno polje...");
                    mAge.requestFocus();
                    return;
                }
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

                //Stavljamo progresbar da se vrti
                mProgressBar.setVisibility(View.VISIBLE);

                //Programiramo kreiranje korisnika
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Proveramo da li je korinsik kreiran
                        if(task.isSuccessful()){

                            //Pravimo objekat korisnika
                            User user = new User(name,age,email);

                            //Objekat korisnika saljemo u realtime onlina bazu podataka
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user);

                            //Gasimo progresbar i ispisujemo toast poruku
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Uspesna registracija",Toast.LENGTH_SHORT).show();

                            //Prebacujemo korisnika na login prozor
                            startActivity(new Intent(getApplicationContext(),Login.class));
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Neuspesna registracija",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        //Ako korisnik klikne na ime aplikacije, prebacujemo ga na login prozor
        mNaslov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
}