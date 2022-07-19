package com.example.energyapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.energyapp.R;


public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnSkip;
    private TextView tvGhid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        //handle loginBtn_main click, start login Screen
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                //animatie pt comutare intre activitati
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });
        //handle skipBtn_main click, start without login Screen
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,FaraContActivity.class));


            }
        });
        tvGhid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GhidActivity.class));
            }
        });


    }
    private void initComponents(){
        btnLogin = findViewById(R.id.btnLogin_main);
        btnSkip = findViewById(R.id.btnSkip_main);
        tvGhid = findViewById(R.id.tvGhid_main);
    }



}