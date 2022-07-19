package com.example.energyapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.energyapp.R;
import com.example.energyapp.classes.Dispozitiv;
import com.example.energyapp.classes.Incapere;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import in.dd4you.animatoo.Animatoo;

public class GraficActivity extends AppCompatActivity {
    AnyChartView chart;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid = "";
    DatabaseReference databaseReferenceIncaperi;
    List<Dispozitiv> dispozitive;
    List<String> aparate = new ArrayList<>();
    List<Float> valori = new ArrayList<>();
    TextView textView ;
    ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);
        textView = findViewById(R.id.tv_grafic);
        btn = findViewById(R.id.btnBack_grafic);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        uid = firebaseUser.getUid();
        databaseReferenceIncaperi = FirebaseDatabase.getInstance().getReference("incaperi").child(uid);
        dispozitive = new ArrayList<>();
        chart = findViewById(R.id.chart_grafic);
        //preluam din baza de date dispozitivele utilizatorului si le adaugam intr-o lista locala
        preiaToateDispozitivele();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for(Dispozitiv dispozitiv: dispozitive){
                    aparate.add(dispozitiv.getTipDispozitiv());
                    valori.add(Float.parseFloat(dispozitiv.getConsumDispozitiv()));
                }
                Pie pie = AnyChart.pie();
                pie.animation(true,10 );
                List<DataEntry> dataEntries = new ArrayList<>();
                for (int i = 0; i < dispozitive.size(); i++) {
                    dataEntries.add(new ValueDataEntry(aparate.get(i), valori.get(i)));
                }
                pie.data(dataEntries);
                chart.setChart(pie);

            }
        }, 700);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Animatoo.animateCard(GraficActivity.this);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Dispozitiv dispozitiv: dispozitive){
                    aparate.add(dispozitiv.getTipDispozitiv());
                    valori.add(Float.parseFloat(dispozitiv.getConsumDispozitiv()));
                }
                Pie pie = AnyChart.pie();
                pie.animation(true,10 );
                List<DataEntry> dataEntries = new ArrayList<>();
                for (int i = 0; i < dispozitive.size(); i++) {
                    dataEntries.add(new ValueDataEntry(aparate.get(i), valori.get(i)));
                }
                pie.data(dataEntries);
                chart.setChart(pie);
            }
        });

    }
    private void preiaToateDispozitivele() {
        databaseReferenceIncaperi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot incaperiSnapshot: snapshot.getChildren()) {
                    Incapere incapere= incaperiSnapshot.getValue(Incapere.class);
                    //        preluam dispozitivele din fiecare incapere
                    DatabaseReference databaseReferenceDispozitive=FirebaseDatabase.getInstance().getReference("dispozitive").child(uid).child(incapere.getIncapereId());
                    databaseReferenceDispozitive.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dispozitiveSnapshot : snapshot.getChildren()) {
                                Dispozitiv dispozitiv = dispozitiveSnapshot.getValue(Dispozitiv.class);
                                //il adaugam in lista locala--: pentru fiecare dispozitiv adaugam in nume incaperea din care face parte
                                dispozitiv.setTipDispozitiv(dispozitiv.getTipDispozitiv()+" din "+incapere.getDenumire());
                                dispozitive.add(dispozitiv);
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}