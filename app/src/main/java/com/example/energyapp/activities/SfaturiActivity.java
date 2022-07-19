package com.example.energyapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.energyapp.R;
import com.example.energyapp.adaptors.ItemAdapter;
import com.example.energyapp.classes.Dispozitiv;
import com.example.energyapp.classes.Incapere;
import com.example.energyapp.classes.ItemTips;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.dd4you.animatoo.Animatoo;

public class SfaturiActivity extends AppCompatActivity {

    ImageButton btnBack;
    String descriere = "";
    //create an Array LIst of items
    final ArrayList<ItemTips> items = new ArrayList<ItemTips>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sfaturi);
        //Get list view
        listView = findViewById(R.id.list);

        //initialAnimations();
        btnBack = findViewById(R.id.btnBack_sfaturiActivity);
        descriere = getString(R.string.inlocuiesteElectrocasnicele_descriere);
        //buton inapoi
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateDiagonal(SfaturiActivity.this);

            }
        });

        //Add list data
        items.add(new ItemTips(getString(R.string.scoateIncarcatoare_title),
                getString(R.string.scoateIncarcatoare_descriere),
                R.drawable.unplag,
                Integer.parseInt(getString(R.string.scoateIncarcatoare_review))));
        items.add(new ItemTips(getString(R.string.folosesteLed_title),
                getString(R.string.folosesteLed_descriere),
                R.drawable.changeled,
                Integer.parseInt(getString(R.string.folosesteLed_review))));

        items.add(new ItemTips(getString(R.string.inlocuiesteElectrocasnicele_title),
                descriere,
                R.drawable.inlocuireelectrocasnice,
                Integer.parseInt(getString(R.string.inlocuiesteElectrocasnicele_review))));
        items.add(new ItemTips(getString(R.string.folosesteEnergieVerde_title),
                getString(R.string.folosesteEnergieVerde_descriere),
                R.drawable.energieverde,
                Integer.parseInt(getString(R.string.folosesteEnergieVerde_review))));
        items.add(new ItemTips("Sfaturi generale",
               "Citiți cu atenție aceste informații! ",
                R.drawable.sfaturigenerale,
                Integer.parseInt("5")));



        final ItemAdapter adapter = new ItemAdapter(this, items);



        //Bind with adapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemTips itemCurent = items.get(position);
                Intent intent = new Intent(SfaturiActivity.this,DetailsTipsActivity.class);
                intent.putExtra("TITLE",itemCurent.getItemTitle());
                startActivity(intent);
                Animatoo.animateSlideRight(SfaturiActivity.this);
            }
        });
    }


}