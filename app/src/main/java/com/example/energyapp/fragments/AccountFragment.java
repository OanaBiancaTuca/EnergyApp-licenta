package com.example.energyapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.energyapp.R;
import com.example.energyapp.activities.GraficActivity;
import com.example.energyapp.activities.MainActivity;
import com.example.energyapp.classes.Incapere;
import com.example.energyapp.classes.MyCallback;
import com.example.energyapp.classes.MyUtilsClass;
import com.example.energyapp.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import in.dd4you.animatoo.Animatoo;

public class AccountFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    Activity referenceActivity;
    View parentHolder;
    DatabaseReference databaseReferenceUtilizatorCurent, databaseReferenceIncaperiUserCurent;
    TextView textViewNume, textViewEmail, textViewCostKWh, textViewConsum, textViewCost;
    TextView textViewNrIncaperi, textViewNrDispozitive;
    FirebaseUser firebaseUser;
    String uid;
    String email = "hei";
    DecimalFormat formatter = new DecimalFormat("##.##");
    Float consumTotalAlUserului = 0.0f;
    int numarIncaperi= 0;
    int numarDispozitive = 0;
    Button buton;
    ImageButton btnLogOut;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        referenceActivity = getActivity();
        parentHolder = inflater.inflate(R.layout.fragment_account, container, false);
        initialAnimations(parentHolder);

        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        //legatura xml cu java
        textViewNume=(TextView)parentHolder.findViewById(R.id.tv_nume_accountFragment);
        textViewEmail = (TextView) parentHolder.findViewById(R.id.tv_email_accountFragment);
        textViewCostKWh = (TextView) parentHolder.findViewById(R.id.tv_costKwh_accountFragment);
        textViewCost = (TextView) parentHolder.findViewById(R.id.tv_costFactura_accountFragment);
        textViewConsum = (TextView) parentHolder.findViewById(R.id.tv_consum_accountFragment);
        textViewNrIncaperi = (TextView) parentHolder.findViewById(R.id.tv_incaperi_accountFragment);
        textViewNrDispozitive = (TextView) parentHolder.findViewById(R.id.tv_dispozitive_accountFragment);
        buton = (Button) parentHolder.findViewById(R.id.btn_accountFragment);
        btnLogOut = (ImageButton) parentHolder.findViewById(R.id.btnLogout_accountFragment);
        imageView = (ImageView) parentHolder.findViewById(R.id.imageView_fragmentAccount);



        //avem nevoie de userul curent pentru a-i afisa detaliile
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        email = firebaseUser.getEmail();
        textViewEmail.setText(email);
        //prelaum din tabela utilizatori doar userul curent, si pentru el preluam si incaperile si dispozitivele
        databaseReferenceUtilizatorCurent =  FirebaseDatabase.getInstance().getReference("utilizatori").child(uid);
        databaseReferenceIncaperiUserCurent = FirebaseDatabase.getInstance().getReference("incaperi").child(uid);



        //setez numele utilizatorului
        if (uid != null) {
            databaseReferenceUtilizatorCurent.child("nume").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!(snapshot.getValue(String.class).isEmpty())) {
                        String nume =snapshot.getValue(String.class);
                        textViewNume.setText(String.valueOf(nume));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        //setez cost kWh
            MyUtilsClass.preiaCost(uid, new MyCallback() {
                @Override
                public void onCallbackForCost(Float value) {
                    //pentru a afisa costul care exista in bd
                    if (value == 0.0) {
                        textViewCost.setText("-");
                        textViewCostKWh.setText("-");
                    } else {
                        textViewCostKWh.setText(formatter.format(value)+" lei");

                    }


                }
            });

        //preiau toate dispozitivele userului
        databaseReferenceIncaperiUserCurent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot incaperiSnapshot : snapshot.getChildren()) {
                    Incapere incapere = incaperiSnapshot.getValue(Incapere.class);
                    numarIncaperi++;
                    if (incapere.getConsumTotal() != null && incapere.getConsumTotal() != "" && !incapere.getConsumTotal().isEmpty()) {
                        consumTotalAlUserului = consumTotalAlUserului + Float.parseFloat(incapere.getConsumTotal());
                    }
                    numarDispozitive = numarDispozitive + incapere.getNumardispozitive();
                    textViewConsum.setText(String.valueOf(formatter.format(consumTotalAlUserului) + " kWh"));
                    //preiau costul pt kwh al userului
                    MyUtilsClass.preiaCost(uid, new MyCallback() {
                        @Override
                        public void onCallbackForCost(Float value) {
                            //pentru a afisa costul care exista in bd
                            if (value == 0.0) {
                                textViewCost.setText("-");
                                textViewCostKWh.setText("-");

                            } else {
                                textViewCost.setText(String.valueOf( formatter.format(value * consumTotalAlUserului)) + " lei");
                                textViewCostKWh.setText(formatter.format(value)+" lei");
                                textViewNrDispozitive.setText(numarDispozitive+ " dispozitive");
                                textViewNrIncaperi.setText(numarIncaperi+" incaperi");

                            }


                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(referenceActivity, GraficActivity.class));
                Animatoo.animateCard(referenceActivity);
            }
        });

        //buton contact
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:"
                        + "tucaoana19@stud.ase.ro"
                        + "?subject=" + "Feedback" + "&body=" + "");
                intent.setData(data);
                startActivity(intent);
            }
        });
        //buton logOut
        //click pe logout
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                checkUser(firebaseAuth);
            }
        });



        return parentHolder;
    }

    //pt animatia initiala
    private void initialAnimations(View parentHolder) {
        View toolbar;
        View cardView;
        Animation fromBottomAnimationFaster;
        toolbar = (View) parentHolder.findViewById(R.id.toolbarRl_account);
        cardView = (View) parentHolder.findViewById(R.id.CardView_account);
        fromBottomAnimationFaster = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom_faster);
        toolbar.startAnimation(fromBottomAnimationFaster);
        cardView.startAnimation(fromBottomAnimationFaster);
    }

    private void checkUser( FirebaseAuth firebaseAuth) {
        //get current user
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser==null ){
            startActivity(new Intent(getContext(), MainActivity.class));
        }
        else if(firebaseUser!=null&& !firebaseUser.isEmailVerified()){
            startActivity(new Intent(getContext(),MainActivity.class));
        }
        else  if(firebaseUser!=null&& firebaseUser.isEmailVerified()){
            //logged in, get user info

        }
    }

}