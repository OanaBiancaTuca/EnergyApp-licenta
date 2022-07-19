package com.example.energyapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.energyapp.R;
import com.example.energyapp.fragments.AccountFragment;
import com.example.energyapp.fragments.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity {

    //firebase auth
    private FirebaseAuth firebaseAuth;
    //initizalizare variabile
    MeowBottomNavigation bottomNavigation;
    private TextView tvUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initComponents();
//        checkUser();
        //pt a nu astepta mult cand primesc datele din baza mea de date
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }

    private void initComponents() {
        bottomNavigation = findViewById(R.id.bottomNavigation_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
//        tvUserDetails = findViewById(R.id.tvUserDetails_dashboard);
        //Adaug itemurile meniului
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_account));
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                //Initializare fragmente
                Fragment fragment = null;
                //check condition
                switch (item.getId()) {
                    case 1:
                        fragment = new DashboardFragment();
                        break;
                    case 2:
                        fragment = new AccountFragment();
                        break;
                }
                //load fragment
                loadFragment(fragment);
            }
        });

        //set home fragment initially selected
        bottomNavigation.show(1, true);
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        //Replcae fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_dashboard, fragment)
                .commitAllowingStateLoss();
//               .commit();
    }

}