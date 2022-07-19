package com.example.energyapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.energyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

     private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initComponents();

        //start main screen after 2seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();

            }
        }, 2000);
    }


    private void initComponents() {
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
    }

    private void checkUser() {
        //preluăm userul curent,dacaă este conectat
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //user nelogat-->start main screen
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();//finish this activity
        } else {
            //user logat, preluam userul din baza de date
            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users");
            refUser.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }
}