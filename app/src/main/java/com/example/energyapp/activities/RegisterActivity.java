package com.example.energyapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.example.energyapp.R;
import com.example.energyapp.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.regex.Pattern;

import in.dd4you.animatoo.Animatoo;

public class RegisterActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnRegister;
    TextInputLayout tilName;
    private EditText etName,etPassword,etEmail, etConfirmPassword;
    //firebase Auth
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private KAlertDialog alertDialogForProgress;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
        //back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateSwipeRight(RegisterActivity.this);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });


    }
    private void initComponents(){
        btnBack=findViewById(R.id.btnBack_register);
        btnRegister=findViewById(R.id.btnRegister_register);
        etName=findViewById(R.id.etName_register);
        etEmail=findViewById(R.id.etEmail_register);
        etPassword=findViewById(R.id.etPassword_register);
        etConfirmPassword=findViewById(R.id.etConfirmPassword_register);
        tilName=findViewById(R.id.tilName_register);

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        //alert dialog initializare
        alertDialogForProgress = new KAlertDialog(RegisterActivity.this, KAlertDialog.PROGRESS_TYPE);
        alertDialogForProgress.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        alertDialogForProgress.setTitleText("<h1>Așteptați</h1>");
        alertDialogForProgress.setCancelable(false);

    }

    private String name = "", email = "", password = "", confirmPassword = "";

    private void validateData() {
        getData();
        if (TextUtils.isEmpty(name) || name.length()<3) {
            Toast.makeText(this, "Tastează un nume...!", Toast.LENGTH_SHORT).show();
            etName.setError("Eroare");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etName.setError(null);
            etEmail.setError("Eroare");
            Toast.makeText(this, "Email Invalid!", Toast.LENGTH_SHORT).show();
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            etPassword.setError("Parola trebuie să conțină minim 6 caractere,cel puțin o lietră și o cifră!");
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Tastează parola...!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Parola trebuie să conțină minim 6 caractere," +"\n"+
                        "cel puțin o lietră și o cifră!", Toast.LENGTH_LONG).show();
            }
        } else if (TextUtils.isEmpty(confirmPassword)) {
            etPassword.setError(null);
            etConfirmPassword.setError("Eroare");
            Toast.makeText(this, "Confirmă parola...!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            etPassword.setError("Eroare");
            Toast.makeText(this, "Parolele nu se potrivesc...!", Toast.LENGTH_SHORT).show();
        } else {
            etPassword.setError(null);
            etConfirmPassword.setError(null);
            createUserAccount();
        }

    }

    private void getData() {
        name=etName.getText().toString().trim();
        email=etEmail.getText().toString().trim();
        password=etPassword.getText().toString().trim();
        confirmPassword=etConfirmPassword.getText().toString().trim();
    }
    private void createUserAccount() {
        //show progress

       alertDialogForProgress.setContentText("Se creează contul...");
        alertDialogForProgress.show();

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account creation succes,now add in firebase realtime database
                updateUserInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //account creating failed
                alertDialogForProgress.dismiss();
                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        alertDialogForProgress.setMessage("Se salvează utilizatorul...");
        //timestamp
        long timestamp=System.currentTimeMillis();
        //obțineți ID-ul utilizatorului curent, deoarece utilizatorul este înregistrat
        String uid=firebaseAuth.getUid();
        //creez utilizatorul
        User user=new User(uid,email,name,timestamp);
        //referinta pentru baza de date in care vrem sa adaugam utilizatorul
        DatabaseReference referenceUtilizatori= FirebaseDatabase.getInstance().getReference("utilizatori");
        referenceUtilizatori.child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //data added to db

                alertDialogForProgress.dismiss();
                //send user verification
              firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   KAlertDialog progressDialogEmailVerification=new KAlertDialog(RegisterActivity.this, KAlertDialog.PROGRESS_TYPE);
                   progressDialogEmailVerification.getProgressHelper().setBarColor(Color.parseColor("#ff4d4d"));
                   progressDialogEmailVerification.setTitleText("<h1>Așteptați</h1>");
                   progressDialogEmailVerification.setCancelable(false);
                   if (task.isSuccessful()) {
                     // Toast.makeText(RegisterActivity.this, "CONT CREEAT."+"\n"+"Verifică emailul pentru validare!", Toast.LENGTH_SHORT).show();
                       CookieBar.build(RegisterActivity.this)
                               .setIcon(R.drawable.success_circle)
                               .setTitle("CONT CREAT")
                               .setMessageColor(R.color.white)
                               .setMessage("Verifică emailul pentru validare!")
                               .setTitleColor(R.color.white)
                               .setIcon(R.drawable.success_circle)
                               .setCookiePosition(CookieBar.TOP)
                               .setDuration(2000) // 2 seconds
                               .setBackgroundColor(R.color.myGreen)
                               .setSwipeToDismiss(true)
                               .setAnimationIn(android.R.anim.slide_in_left, android.R.anim.slide_in_left)
                               .setAnimationOut(android.R.anim.slide_out_right, android.R.anim.slide_out_right)
                               .show();





                      progressDialogEmailVerification.showCancelButton(true)
                              .setCancelText("OK")
                              .cancelButtonColor(R.color.myGreen,RegisterActivity.this)
                              .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                                          @Override
                                                          public void onClick(KAlertDialog sDialog) {
                                                              firebaseAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void unused) {
                                                                      if (firebaseAuth.getCurrentUser() != null) {

                                                                          if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                                                              // user verificat cu succes, deschidem aplicaia
                                                                              startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                                                                              finish();

                                                                              overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                          } else {
                                                                              //emailul nu a fost verificat
                                                                              Toast.makeText(RegisterActivity.this, "Te rog, verifica emailul!", Toast.LENGTH_SHORT).show();

                                                                          }

                                                                      }
                                                                  }
                                                              });
                                                              progressDialogEmailVerification.cancel();

                                                          }
                                                      });
                       progressDialogEmailVerification.setContentText("Verifica emailul pentru a continua!");
                       progressDialogEmailVerification.show();

                   } else {   Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                         }

                      }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        //data failed adding to db
                        alertDialogForProgress.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });


    }



}