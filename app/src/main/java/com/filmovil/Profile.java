package com.filmovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView mGret, mEmailAddressTitle, mEmailAddress, mNameTitle, mName, mAgeTitle, mAge;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mGret = findViewById(R.id.gret);
        mEmailAddressTitle = findViewById(R.id.emailAddressTitle);
        mEmailAddress = findViewById(R.id.emailAddress);
        mNameTitle = findViewById(R.id.nameTitle);
        mName = findViewById(R.id.name);
        mAgeTitle = findViewById(R.id.ageTitle);
        mAge = findViewById(R.id.age);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null){
                    String name = userProfile.name;
                    String age = userProfile.age;
                    String email = userProfile.email;

                    mGret.setText("Dobrodosao, " + name + "!");
                    mName.setText(name);
                    mAge.setText(age);
                    mEmailAddress.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Greska!!!",Toast.LENGTH_SHORT).show();
            }
        });

    }
    //Metoda koja kreira meni
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    //Metoda koja prati na koji ITEM je kliknuto u meniju
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}