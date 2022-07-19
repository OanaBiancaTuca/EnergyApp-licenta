package com.example.energyapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.energyapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import in.dd4you.animatoo.Animatoo;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private LottieProgressDialog progressDialog;
    private ImageButton btnBack;
    private Button btnSubmit;
    private EditText etEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initComponents();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateSwipeRight(ForgotPasswordActivity.this);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void initComponents(){
        btnBack=findViewById(R.id.btnBack_forgotpassword);
        etEmail=findViewById(R.id.etEmail_forgotpassword);
        btnSubmit=findViewById(R.id.btnSubmit_forgotpassword);

        firebaseAuth=FirebaseAuth.getInstance();



    }

    private String email="";
    private void validateData() {
        email=etEmail.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email Invalid!",Toast.LENGTH_SHORT).show();
            etEmail.setError("Eroare");
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Tastează emailul...!",Toast.LENGTH_SHORT).show();
            etEmail.setError("Eroare");
        }
        else{
            etEmail.setError(null);
            recoverPassword();
        }
    }

    private void recoverPassword() {
        //show progress
        progressDialog = new LottieProgressDialog(ForgotPasswordActivity.this,
                false,300, 300, null,
                null, LottieProgressDialog.SAMPLE_7, "Se trimit instrucțiunile de resetare ",null);

        progressDialog.show();

        //begin sending recovery
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //send
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this,"Instrucțiunile de resetare a parolei s-au trimis pe "+email,Toast.LENGTH_LONG).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
                //failed to send
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, "Eroare : ", Toast.LENGTH_SHORT).show();
            }
        });


    }

}