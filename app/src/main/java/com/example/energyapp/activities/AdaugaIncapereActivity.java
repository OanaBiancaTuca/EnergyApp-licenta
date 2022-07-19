package com.example.energyapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.energyapp.R;
import com.example.energyapp.classes.Incapere;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.royrodriguez.transitionbutton.TransitionButton;

import in.dd4you.animatoo.Animatoo;


public class AdaugaIncapereActivity extends AppCompatActivity {
    TransitionButton btnAdauga;
    DatabaseReference databaseIncaperi;
    EditText etDenumire;
    Spinner spn;
    ImageButton imgButtonBack;
    FirebaseUser firebaseUser;
    String uid;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferenceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_incapere);
        initComponent();
        //userul curent
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        imgButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        btnAdauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                btnAdauga.startAnimation();

                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccessful = true;

                        // Choose a stop animation if your call was succesful or not
                        if (isSuccessful && adaugaIncapere()) {
                            btnAdauga.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    finish();
                                    Animatoo.animateDiagonal(AdaugaIncapereActivity.this);
                                }
                            });
                        } else {
                            btnAdauga.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 800);
            }
        });
    }

    private void initComponent() {
        imgButtonBack = findViewById(R.id.btnBack_adaugaIncapere);
        etDenumire = findViewById(R.id.etIncapere_adaugaIncapere);
        spn = findViewById(R.id.spnTipIncapere_adaugaIncapere);
        btnAdauga = findViewById(R.id.btnAdauga_adaugaIncapere);
        databaseIncaperi = FirebaseDatabase.getInstance().getReference("incaperi");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("utilizatori");
    }

    private boolean adaugaIncapere() {
        String denumire = etDenumire.getText().toString().trim();
        String tipIncapere = spn.getSelectedItem().toString();

        if (!TextUtils.isEmpty(denumire)) {
            etDenumire.setError(null);
            String id = databaseIncaperi.push().getKey();
            Incapere incapere = new Incapere(id, denumire, tipIncapere, uid);
            //salvam incaperea in database
            databaseIncaperi.child(uid).child(id).setValue(incapere);
            Toast.makeText(this, "Încăpere adăugată", Toast.LENGTH_SHORT).show();
            return true;

        } else {
            etDenumire.setError("Eroare");
            Toast.makeText(this, "Setează denumirea", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

}