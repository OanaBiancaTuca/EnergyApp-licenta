package com.example.energyapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.energyapp.R;
import com.example.energyapp.activities.GhidActivity;
import com.example.energyapp.activities.IncaperiUtilizatorActivity;
import com.example.energyapp.activities.MainActivity;
import com.example.energyapp.activities.SeteazaCostFacturaActivity;
import com.example.energyapp.activities.SfaturiActivity;
import com.example.energyapp.activities.SimulatorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.dd4you.animatoo.Animatoo;

public class DashboardFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    Activity referenceActivity;
    View parentHolder;
    CardView cardViewCasaMea,cardViewCosturi, cardViewSimulator, cardViewSfaturi, cardViewGhid;
    ImageButton imageButtonLogOut;
    TextView tvUserDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        referenceActivity = getActivity();
        parentHolder = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //animatii
        initialAnimations(parentHolder);
        //firebase auth
        tvUserDetails=(TextView)parentHolder.findViewById(R.id.tvUserDetails_dashboard);
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser(tvUserDetails,firebaseAuth);


        cardViewCasaMea = (CardView) parentHolder.findViewById(R.id.cardView_casaMea_dashboardFragment);
        cardViewCosturi=(CardView)parentHolder.findViewById(R.id.cardView_costuri_dashboardFragment);
        cardViewSimulator=(CardView)parentHolder.findViewById(R.id.cardView_simulator_dashboardFragment);
        cardViewSfaturi = (CardView) parentHolder.findViewById(R.id.cardView_sfaturi_dashboardFragment) ;
        cardViewGhid = (CardView) parentHolder.findViewById(R.id.cardView_ghid_dashboardFragment);
        imageButtonLogOut=(ImageButton)parentHolder.findViewById(R.id.btnLogout_dashboard);
        //click pe logout
        imageButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

             checkUser(tvUserDetails,firebaseAuth);
            }
        });
        //click pe itemul CASA MEA
        cardViewCasaMea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(referenceActivity, IncaperiUtilizatorActivity.class));
                Animatoo.animateDiagonal(referenceActivity);
            }
        });
        //click pe itemul COSTURI
        cardViewCosturi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(referenceActivity, SeteazaCostFacturaActivity.class));
                Animatoo.animateDiagonal(referenceActivity);
            }
        });
        //click pe itemul SIMULATOR
        cardViewSimulator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(referenceActivity, SimulatorActivity.class));
                Animatoo.animateDiagonal(referenceActivity);
            }
        });

        cardViewSfaturi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(referenceActivity, SfaturiActivity.class));
                Animatoo.animateDiagonal(referenceActivity);
            }
        });
        cardViewGhid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(referenceActivity, GhidActivity.class));
                Animatoo.animateDiagonal(referenceActivity);
            }
        });




        return parentHolder;
    }



    //pt animatia initiala
    private void initialAnimations(View parentHolder) {
        View toolbar;
        GridLayout gridLayout;
        Animation fromBottomAnimationFaster;
        toolbar = (View) parentHolder.findViewById(R.id.toolbarRl_dashboardFragment);
        gridLayout = (GridLayout) parentHolder.findViewById(R.id.gridLayout_dashboardFragment);
        fromBottomAnimationFaster = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom_faster);
        toolbar.startAnimation(fromBottomAnimationFaster);
        gridLayout.startAnimation(fromBottomAnimationFaster);
    }
    private void checkUser(TextView textView, FirebaseAuth firebaseAuth) {
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
            String email=firebaseUser.getEmail();
            textView.setText(email);
        }
    }
}