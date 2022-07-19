package com.example.energyapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.energyapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.dd4you.animatoo.Animatoo;

public class LoginActivity extends AppCompatActivity {
   private  TextView tvCreateAccount,tvForgotPassword;
   private ImageButton btnBack;
   private Button btnLogin;
   private EditText etPassword,etEmail;
   //firebase auth
    private FirebaseAuth firebaseAuth;
    private LottieProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                //animatie pt comutare intre activitati
                Animatoo.animateSwipeLeft(LoginActivity.this);

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
                //animatie pt comutare intre activitati
                Animatoo.animateSwipeLeft(LoginActivity.this);
            }
        });

    }

    private void initComponents(){
        tvCreateAccount=findViewById(R.id.tvNoAccount_login);
        btnBack=findViewById(R.id.btnBack_login);
        btnLogin=findViewById(R.id.btnLogin_login);
        etEmail=findViewById(R.id.etEmail_login);
        etPassword=findViewById(R.id.etPassword_login);
        tvForgotPassword=findViewById(R.id.tvForgotPassword_login);

        firebaseAuth=FirebaseAuth.getInstance();

        //setup progress dialog

        progressDialog = new LottieProgressDialog(LoginActivity.this,
                false,300, 200, null,
                null, LottieProgressDialog.SAMPLE_1, "LOGARE...",null);

    }
    private String email="",password="";
    private void validateData() {
        //Before loggin, lets do some validation
        email=etEmail.getText().toString().trim();
        password=etPassword.getText().toString().trim();
       if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email Invalid!",Toast.LENGTH_SHORT).show();
            etEmail.setError("Eroare");
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Tastează parola...!",Toast.LENGTH_SHORT).show();
            etEmail.setError(null);
            etPassword.setError("Eroare");
        }
        else{
            etPassword.setError(null);
            //data is validates, begin login
            loginUser();
       }
    }

    private void loginUser() {
        progressDialog.show();

        //login user
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //login succes
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //login failed
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        //progressDialog.setMessage("Se verifică utilizatorul... ");
        //verificam existenta userului in bd si il preluam
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        //cautam in baza de date
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Utilizatori");
        ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                } else {
                    Toast.makeText(LoginActivity.this, "Verifică emailul!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }
}