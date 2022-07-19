package com.example.energyapp.activities;

import static android.view.View.INVISIBLE;

import static com.example.energyapp.activities.DispozitiveIncapereActivity.calculezPuteredispozitiv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.anychart.core.annotations.Line;
import com.example.energyapp.R;
import com.example.energyapp.adaptors.DispozitivRecomandatAdapter;
import com.example.energyapp.classes.Dispozitiv;
import com.example.energyapp.classes.DispozitivRecomandat;
import com.example.energyapp.classes.Incapere;
import com.example.energyapp.classes.MyCallback;
import com.example.energyapp.classes.MyUtilsClass;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.dd4you.animatoo.Animatoo;


public class DetailsTipsActivity extends AppCompatActivity {

    Intent intent;
    TextView tvDescriere1,tvDescriere2, tvDescriere3, tvTitlu, tvREcomandari;
    ImageView image;
    Button btnSimulare, btnInchide;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid = "";
    DatabaseReference databaseReferenceIncaperi;
    List<Dispozitiv> dispozitive = new ArrayList<>();
    String descriere = "";
    ListView listViewDispozitiveVechi;
    ListView listViewRecomandari;
    DecimalFormat formatter = new DecimalFormat("##.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_tips);
        initComopnents();
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        uid = firebaseUser.getUid();
        databaseReferenceIncaperi = FirebaseDatabase.getInstance().getReference("incaperi").child(uid);
        btnInchide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateSlideLeft(DetailsTipsActivity.this);

            }
        });



        intent = getIntent();
        String title= intent.getStringExtra("TITLE");
        if(title.equals(getString(R.string.scoateIncarcatoare_title))){
            tvTitlu.setText(title);
            tvDescriere1.setText(getString(R.string.descriereScoateIncarcatoare1)+"\n");
            tvDescriere2.setText(  getString(R.string.descriereScoateIncarcatoare2)+"\n");
            tvDescriere3.setText(getString(R.string.descriereScoateIncarcatoare3));
        }
        else if(title.equals(getString(R.string.folosesteLed_title))){
            tvTitlu.setText(title);
            tvDescriere1.setText(getString(R.string.folosesteLed1));
            tvDescriere2.setVisibility(INVISIBLE);
            tvDescriere3.setVisibility(INVISIBLE);
            image.setImageResource(R.drawable.changeled);
            btnSimulare.setVisibility(View.VISIBLE);
            btnSimulare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailsTipsActivity.this, SimulatorActivity.class);
                    intent.putExtra("SIMULARE","Bec");
                    intent.putExtra("SIMULARE_VALOARE", "12.0");
                    startActivity(intent);
                    Animatoo.animateDiagonal(DetailsTipsActivity.this);
                }
            });

        }else if(title.equals(getString(R.string.folosesteEnergieVerde_title))){
            tvTitlu.setText(title);
            image.setImageResource(R.drawable.energieverde);
            tvDescriere1.setText(getString(R.string.folosesteEnergieVerde1));
            tvDescriere2.setText(getString(R.string.folosesteEnergieVerde2));
            tvDescriere3.setVisibility(INVISIBLE);
        }else if(title.equals("Sfaturi generale")){
            LinearLayout linearLayout = findViewById(R.id.LinearLayout_detailsTips);
            linearLayout.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            PDFView pdfView = findViewById(R.id.pdfView_sfaturiGenerale);
            pdfView.setVisibility(View.VISIBLE);
            tvTitlu.setVisibility(View.GONE);
            tvDescriere1.setVisibility(View.GONE);
            tvDescriere2.setVisibility(View.GONE);
            tvDescriere3.setVisibility(View.GONE);
            pdfView.fromAsset("sfaturigenerale.pdf")
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true)
                    .enableDoubletap(true)// improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(1)
                    .load();

        }
        else if(title.equals(getString(R.string.inlocuiesteElectrocasnicele_title))){
            image.setImageResource(R.drawable.inlocuireelectrocasnice);
            tvTitlu.setText(title);
            tvDescriere1.setText(getString(R.string.inlocuiesteElectrocasnicele1));
            tvDescriere2.setVisibility(View.GONE);
            tvDescriere3.setVisibility(View.GONE);
            //daca preiau toate dispozitivele pt a-i spune care consuma cel mai mult-->clasa energetica cea mai mare


        databaseReferenceIncaperi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot incaperiSnapshot: snapshot.getChildren()) {
                    Incapere incapere= incaperiSnapshot.getValue(Incapere.class);
                    //        preluam dispozitivele din fiecare incapere
                    DatabaseReference databaseReferenceDispozitive= FirebaseDatabase.getInstance().getReference("dispozitive").child(uid).child(incapere.getIncapereId());
                    databaseReferenceDispozitive.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dispozitiveSnapshot : snapshot.getChildren()) {
                                Dispozitiv dispozitiv = dispozitiveSnapshot.getValue(Dispozitiv.class);
                                //il adaugam in lista locala--: pentru fiecare dispozitiv adaugam in nume incaperea din care face parte
                                dispozitiv.setTipDispozitiv(dispozitiv.getTipDispozitiv()+" din "+incapere.getDenumire());
                                if(dispozitiv.getClasaDispozitiv().equals("E")||dispozitiv.getClasaDispozitiv().equals("F")||
                                        dispozitiv.getClasaDispozitiv().equals("D")||dispozitiv.getClasaDispozitiv().equals("G")) {
                                    dispozitive.add(dispozitiv);

                                }
                            }
                            if(dispozitive.size()!=0) {
                                tvDescriere2.setVisibility(View.VISIBLE);
                                tvDescriere2.setText("Ai putea înlocui următoarele dispozitive:");
                                listViewDispozitiveVechi.setVisibility(View.VISIBLE);
                                ArrayAdapter<Dispozitiv> arrayAdapter = new ArrayAdapter<Dispozitiv>(
                                        DetailsTipsActivity.this,
                                        android.R.layout.simple_list_item_1,
                                        dispozitive);

                                listViewDispozitiveVechi.setAdapter(arrayAdapter);


                            }else{
                                tvDescriere2.setVisibility(INVISIBLE);

                            }
                            //ce se intampla cand apas pe un item
                            listViewDispozitiveVechi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Dispozitiv dispozitivVechi = dispozitive.get(position);
                                    afiseazaRecomandari(dispozitivVechi);

                                }
                            });
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

    private void afiseazaRecomandari(Dispozitiv dispozitivVechi) {
        tvREcomandari.setVisibility(View.VISIBLE);
        listViewRecomandari.setVisibility(View.VISIBLE);
        List<DispozitivRecomandat> recomandari = new ArrayList<>();
        //citim recomandarile in functie de tipul dispozitivului
        String tipDispozitiv = dispozitivVechi.getTipDispozitiv().split("din")[0];
        String filename = tipDispozitiv.trim() + ".txt";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader
                    (this.getAssets().open(filename), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] l = line.split(",");
                recomandari.add(new DispozitivRecomandat(l[0], l[1], Float.parseFloat(l[2]), Float.parseFloat(l[3]), Float.parseFloat(l[4]), l[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        DispozitivRecomandatAdapter adapter = new DispozitivRecomandatAdapter(DetailsTipsActivity.this, recomandari);
        listViewRecomandari.setAdapter(adapter);
        if (recomandari.size() == 0) {
            tvREcomandari.setText("Nu s-au gasit recomandari");
            adapter.notifyDataSetChanged();

        } else {
            tvREcomandari.setText("Recomandări");
            listViewRecomandari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DispozitivRecomandat dispozitivRecomandat = recomandari.get(position);
                    arataComparatie(dispozitivVechi, dispozitivRecomandat.getPutereDispozitiv(), DetailsTipsActivity.this);
                }
            });
        }
    }

    private void arataComparatie(Dispozitiv dispozitiv,Float putereDispozitivNou, Context context) {
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.comparatieconsumdispozitive_dialog);
        dialog.setCancelable(true);
        Switch switchDetalii;
        switchDetalii= (Switch) dialog.findViewById(R.id.switch_simulatorDialog);

        Button closeBtn= dialog.findViewById(R.id.btn_close_dialogSimulator);
        Button inlocuiesteBtn = dialog.findViewById(R.id.btn_inlocuieste_dialogSimulator);
        inlocuiesteBtn.setVisibility(INVISIBLE);

        TextView tvInformatii= dialog.findViewById(R.id.tvInformatii_simulator);
        TextView numarDispozitive = dialog.findViewById(R.id.tvInformatii2_simulator);
        TextView oreFunctionare = dialog.findViewById(R.id.tvInformatii1_simulator);
        TextView tipFunctionare = dialog.findViewById(R.id.tvInformatii3_simulator);
        TextView tvConsum1= dialog.findViewById(R.id.tv_consumDispozitiv1_simulator);
        TextView tvPutere1= dialog.findViewById(R.id.tv_putereDispozitiv1_simulator);
        TextView tvConsum2= dialog.findViewById(R.id.tv_consumDispozitiv2_simulator);
        TextView tvPutere2= dialog.findViewById(R.id.tv_putereDispozitiv2_simulator);
        TextView tvCost1 = dialog.findViewById(R.id.tv_costDispozitiv1_simulator);
        TextView tvCost2 = dialog.findViewById(R.id.tv_costDispozitiv2_simulator);
        TextView economieLunara = dialog.findViewById(R.id.tv_EconomieLunara_simulator);
        TextView economieAnuala = dialog.findViewById(R.id.tv_EconomieAnuala_simulator);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        int numarOreFunctionare = dispozitiv.getMinuteFunctionareZilnic()/60;
        int numarMinuteFunctionare = dispozitiv.getMinuteFunctionareZilnic() - numarOreFunctionare*60;
        tvInformatii.setText("Simulare înlocuire "+dispozitiv.getTipDispozitiv());
        numarDispozitive.setText("Numar dispozitive: "+dispozitiv.getNumarDispozitive());
        tipFunctionare.setText("Mod functionare: "+dispozitiv.getModfunctionare());
        oreFunctionare.setText("Timp utilizare: "+numarOreFunctionare+" h : "+numarMinuteFunctionare+" min");


        Float consumDispozitivNou = calculeazaConsumDispozitiv( dispozitiv,putereDispozitivNou);
        Float consumDispozitivVechi = Float.parseFloat(dispozitiv.getConsumDispozitiv());
        tvConsum1.setText(formatter.format(consumDispozitivVechi)+ " kWh/luna");
        tvConsum2.setText(formatter.format(consumDispozitivNou)+ " kWh/luna");
        tvPutere1.setText(formatter.format(calculezPuteredispozitiv(dispozitiv))+" W");
            tvPutere2.setText(putereDispozitivNou.toString()+" W");


        MyUtilsClass.preiaCost(uid,new MyCallback(){

            @Override
            public void onCallbackForCost(Float value) {
                float costLunar1 = consumDispozitivVechi* value;
                float costLunar2 = consumDispozitivNou * value;
                tvCost1.setText(formatter.format(costLunar1)+" lei");
                tvCost2.setText(formatter.format(costLunar2)+" lei");

                switchDetalii.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            economieLunara.setVisibility(View.VISIBLE);
                            economieAnuala.setVisibility(View.VISIBLE);
                            if(costLunar1 > costLunar2) {
                                float economieLunarainLei =costLunar1 - costLunar2;
                                economieLunara.setText("Economie lunara: " +formatter.format(economieLunarainLei) + " lei");
                                economieAnuala.setText("Economie anuala: " +formatter.format(economieLunarainLei*12) + " lei");
                            }
                        }else{
                            economieLunara.setVisibility(View.INVISIBLE);
                            economieAnuala.setVisibility(View.INVISIBLE);
                        }
                    }
                });


            }

        });
        dialog.show();
    }
    private Float calculeazaConsumDispozitiv(Dispozitiv dispozitiv, Float putereDispozitivNou) {
        Float consum = 0.0f;
            if (dispozitiv.getModfunctionare().equals("Zilnic")) {
                consum= (putereDispozitivNou * ((float)dispozitiv.getMinuteFunctionareZilnic() / 60) * 30 / 1000) * dispozitiv.getNumarDispozitive();
            } else if (dispozitiv.getModfunctionare().equals("Saptamanal")) {
                consum = (putereDispozitivNou * ((float)(dispozitiv.getMinuteFunctionareZilnic()) / 420) * 30 / 1000) * dispozitiv.getNumarDispozitive();
            }

        return  consum;
    }

    private void initComopnents() {
        tvDescriere1 = findViewById(R.id.descriere_detailsTipsItem1);
        tvDescriere2 = findViewById(R.id.descriere_detailsTipsItem2);
        tvDescriere3 = findViewById(R.id.descriere_detailsTipsItem3);
        tvTitlu = findViewById(R.id.itemTitle_detailsTipsItem);
        image = findViewById(R.id.item_image_detailsTipsItem);
        btnSimulare = findViewById(R.id.btn_spreSimulator_detailsTipsItem);
        btnInchide = findViewById(R.id.btn_close_detailsTips);
        listViewDispozitiveVechi = findViewById(R.id.listView_dispozitiveVechi_detailsTipsItem);
        listViewRecomandari = findViewById(R.id.listView_recomandari_detailsTipsItem);
        tvREcomandari = findViewById(R.id.recomandari_detailsTipsItem2);
    }

}
