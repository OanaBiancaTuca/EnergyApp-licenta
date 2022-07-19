package com.example.energyapp.activities;

import static android.view.View.VISIBLE;
import static android.view.animation.Animation.ZORDER_BOTTOM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.example.energyapp.R;
import com.example.energyapp.classes.Dispozitiv;
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
import com.royrodriguez.transitionbutton.TransitionButton;


import org.aviran.cookiebar2.CookieBar;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import in.dd4you.animatoo.Animatoo;

public class SeteazaCostFacturaActivity extends AppCompatActivity {
    private Switch switchCostMediu;
    private Switch switchCostManual, switchCostLista;
    EditText etIntroducetiCost;
    TransitionButton btnSalveazaCostSelectat;
    TransitionButton btnSalveazaCostIntrodus;
    TextView textViewDetalii, tvConsumTotal, tvCostFactura, tvCostKWh;
    Spinner spnZona, spnOferte;
    DatabaseReference databaseReferenceIncaperi;
    DatabaseReference databaseReferenceUser;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid;
    Float consumTotalAlUserului = 0f;
    List<Dispozitiv> toateDispozitivele = new ArrayList<>();
    ValueEventListener valueEventListenerForCostAndCosnum;
    DecimalFormat formatter = new DecimalFormat("##.###");
    ImageButton imageButtonNext, btnBack;
    Animation fromBottomAnimationFaster;
    Animation fromBottomAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seteaza_cost_factura);
        //preiau userul curent
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        initComponents();
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        // initialAnimations();

        //back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateDiagonal(SeteazaCostFacturaActivity.this);
            }
        });

        tvCostKWh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arataDetaliiDespreCost(v);
            }
        });
        MyUtilsClass.preiaCost(uid, new MyCallback() {
            @Override
            public void onCallbackForCost(Float value) {
                //pentru a afisa costul care exista in bd
                if (value == 0.0) {
                    textViewDetalii.setText("Costul nu este setat !!");
                } else {
                    textViewDetalii.setText("Pretul setat este de: " + formatter.format(value) + " lei");
                }
                tvCostKWh.setText("Cost KWh: " + formatter.format(value) + " lei/lunar");
            }
        });

        //preiau toate dispozitivele userului
        valueEventListenerForCostAndCosnum = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot incaperiSnapshot : snapshot.getChildren()) {

                    Incapere incapere = incaperiSnapshot.getValue(Incapere.class);

                    if (incapere.getConsumTotal() != null && incapere.getConsumTotal() != "" && !incapere.getConsumTotal().isEmpty()) {
                        consumTotalAlUserului = consumTotalAlUserului + Float.parseFloat(incapere.getConsumTotal());
                    }
                    tvConsumTotal.setText(String.valueOf("Consum total: " + formatter.format(consumTotalAlUserului) + " kwh/lunar"));
                    //preiau costul pt kwh al userului
                    MyUtilsClass.preiaCost(uid, new MyCallback() {
                        @Override
                        public void onCallbackForCost(Float value) {
                            //pentru a afisa costul care exista in bd
                            if (value == 0.0) {
                                textViewDetalii.setText("Costul nu este setat !!");
                            } else {
                                //  textViewDetalii.setText("Pretul setat este de: " + formatter.format(value) + " lei");
                            }
                            tvCostKWh.setText("Cost KWh: " + formatter.format(value) + " lei/lunar");
                            tvCostFactura.setText(String.valueOf("Cost factura: " + formatter.format(value * consumTotalAlUserului)) + " lei/lunar ");
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReferenceIncaperi.addListenerForSingleValueEvent(valueEventListenerForCostAndCosnum);


        //pentru a pastra ce switch am ales si dupa ce ies din pagina-->pt fiecare dintre cele 3 variante
        SharedPreferences prefs = getSharedPreferences("com.project", MODE_PRIVATE);
        boolean switchState = prefs.getBoolean("service_status", false);
        boolean switchState2 = prefs.getBoolean("service_status2", false);
        //switchCostMediu.setChecked(switchState2);
        boolean switchState3 = prefs.getBoolean("service_status3", false);
        switchCostMediu.setChecked(switchState);
        switchCostManual.setChecked(switchState2);
        switchCostLista.setChecked(switchState3);

        //verificam care dintre ele a fost bifat si le dezactivam pe restul
        if (switchState == true) {
            switchCostLista.setEnabled(false);
            switchCostManual.setEnabled(false);
        } else if (switchState2 == true) {
            switchCostLista.setEnabled(false);
            switchCostMediu.setEnabled(false);
        } else if (switchState3 == true) {
            switchCostManual.setEnabled(false);
            switchCostMediu.setEnabled(false);
        }

        switchCostMediu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchCostLista.setEnabled(false);
                    switchCostManual.setEnabled(false);
                    Toast.makeText(SeteazaCostFacturaActivity.this, "Folosesti costul mediu", Toast.LENGTH_SHORT).show();
                    textViewDetalii.setText("Pretul mediu a fost selectat");
                    float consumIntermediar = 0.0f;
                    if (tvConsumTotal.getText().toString().toString().split(" ").length > 2) {
                        consumIntermediar = Float.parseFloat(tvConsumTotal.getText().toString().trim().split(" ")[2]);
                    }
                    tvCostFactura.setText("Cost factura: " + formatter.format(1.65 * consumIntermediar) + " lei/lunar");
                    tvCostKWh.setText("Cost KWh: " + formatter.format(1.65) + " lei/lunar");
                    seteazaCostulInDataBase(1.65f);
                    CookieBar.build(SeteazaCostFacturaActivity.this)
                            .setIcon(R.drawable.success_circle)
                            .setTitle("SALVAT")
                            .setTitleColor(R.color.white)
                            .setIcon(R.drawable.success_circle)
                            .setCookiePosition(CookieBar.TOP)
                            .setDuration(2000) // 2 seconds
                            .setBackgroundColor(R.color.myGreen)
                            .setSwipeToDismiss(true)
                            .setAnimationIn(android.R.anim.slide_in_left, android.R.anim.slide_in_left)
                            .setAnimationOut(android.R.anim.slide_out_right, android.R.anim.slide_out_right)
                            .show();

                } else {
                    switchCostLista.setEnabled(true);
                    switchCostManual.setEnabled(true);
                }
                SharedPreferences.Editor editor = getSharedPreferences("com.project", MODE_PRIVATE).edit();
                editor.putBoolean("service_status", isChecked);
                editor.commit();

            }
        });
        switchCostManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewDetalii.setText("Introduceti pretul aici:");
                    switchCostLista.setEnabled(false);
                    switchCostMediu.setEnabled(false);
                    etIntroducetiCost.setVisibility(VISIBLE);
                    etIntroducetiCost.setEnabled(true);
                    btnSalveazaCostIntrodus.setVisibility(VISIBLE);

                    btnSalveazaCostIntrodus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(etIntroducetiCost.getText().toString().trim()) &&
                                            Float.parseFloat(etIntroducetiCost.getText().toString()) > 0) {
                                        etIntroducetiCost.setError(null);
                                        float cost = Float.parseFloat(etIntroducetiCost.getText().toString());
                                        seteazaCostulInDataBase(cost);
                                        etIntroducetiCost.setEnabled(false);
                                        textViewDetalii.setText("Ați introdus costul de " + cost + " lei");
                                        float consumIntermediar = 0.0f;
                                        if (tvConsumTotal.getText().toString().toString().split(" ").length > 2) {
                                            consumIntermediar = Float.parseFloat(tvConsumTotal.getText().toString().trim().split(" ")[2]);
                                        }
                                        tvCostFactura.setText("Cost factură: " + formatter.format(cost * consumIntermediar) + " lei/lunar");
                                        tvCostKWh.setText("Cost KWh: " + formatter.format(cost) + " lei/lunar");
                                        etIntroducetiCost.setVisibility(View.INVISIBLE);
                                        btnSalveazaCostIntrodus.setVisibility(View.INVISIBLE);
                                        //cookie bar pentru setare
                                        CookieBar.build(SeteazaCostFacturaActivity.this)
                                                .setIcon(R.drawable.success_circle)
                                                .setTitle("SALVAT")
                                                .setTitleColor(R.color.white)
                                                .setIcon(R.drawable.success_circle)
                                                .setCookiePosition(CookieBar.TOP)
                                                .setDuration(2000) // 2 seconds
                                                .setBackgroundColor(R.color.myGreen)
                                                .setSwipeToDismiss(true)
                                                .setAnimationIn(android.R.anim.slide_in_left, android.R.anim.slide_in_left)
                                                .setAnimationOut(android.R.anim.slide_out_right, android.R.anim.slide_out_right)
                                                .show();


                                    } else {
                                        etIntroducetiCost.setError("Completează");
                                        Toast.makeText(SeteazaCostFacturaActivity.this, " Nu ați introdus costul !", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }, 100);

                        }
                    });


                } else {
                    etIntroducetiCost.setVisibility(View.INVISIBLE);
                    btnSalveazaCostIntrodus.setVisibility(View.INVISIBLE);
                    switchCostLista.setEnabled(true);
                    switchCostMediu.setEnabled(true);
                }

                SharedPreferences.Editor editor = getSharedPreferences("com.project", MODE_PRIVATE).edit();
                editor.putBoolean("service_status2", isChecked);
                editor.commit();

            }
        });

        switchCostLista.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    textViewDetalii.setText("Selectați zona de rețea, apoi apăsați săgeata");
                    switchCostManual.setEnabled(false);
                    switchCostMediu.setEnabled(false);
                    spnZona.setVisibility(VISIBLE);
                    imageButtonNext.setVisibility(VISIBLE);
                    btnSalveazaCostSelectat.setVisibility(VISIBLE);
                    imageButtonNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            textViewDetalii.setText("Selectați prețul ");
                            verificaZonaAleasasiPopuleazaOferte();
                        }
                    });
                    btnSalveazaCostSelectat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!spnZona.getSelectedItem().toString().equals("Zona")) {
                                        new KAlertDialog(SeteazaCostFacturaActivity.this, KAlertDialog.CUSTOM_IMAGE_TYPE)
                                                .setTitleText("Salvați?")
                                                .setCustomImage(R.drawable.tips, SeteazaCostFacturaActivity.this)
                                                .setContentText("Costurile furnizate sunt pentru clienții casnici cu un nivel de tensiune joasă")
                                                .setTitleTextSize(20)
                                                .setContentTextSize(20)
                                                .setConfirmText("DA")
                                                .setCancelText("NU")
                                                .confirmButtonColor(R.color.myGreen, SeteazaCostFacturaActivity.this)
                                                .cancelButtonColor(R.color.red_btn_bg_color, SeteazaCostFacturaActivity.this)
                                                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                                    @Override
                                                    public void onClick(KAlertDialog sDialog) {
                                                        String[] oferta = spnOferte.getSelectedItem().toString().split(" ");

                                                        float cost = 0.0f;
                                                        if (oferta.length == 4) {
                                                            cost = Float.parseFloat(oferta[2]);
                                                        } else if (oferta.length == 5) {
                                                            cost = Float.parseFloat(oferta[3]);
                                                        }
                                                        if (spnZona.getSelectedItem().toString().equals("Zona")) {
                                                            Toast.makeText(SeteazaCostFacturaActivity.this, " Selectati zona! ", Toast.LENGTH_SHORT);
                                                        } else {
                                                            //salvez costul in bd
                                                            seteazaCostulInDataBase(cost);
                                                            //actualizez informatiile afisate-->pt ca citirea din bd se face o zingura data la deschiderea activitatii
                                                            textViewDetalii.setText("Ați introdus costul de " + cost + " lei");
                                                            float consumIntermediar = 0.0f;
                                                            if (tvConsumTotal.getText().toString().toString().split(" ").length > 2) {
                                                                consumIntermediar = Float.parseFloat(tvConsumTotal.getText().toString().trim().split(" ")[2]);
                                                            }
                                                            tvCostFactura.setText("Cost factură: " + formatter.format(cost * consumIntermediar) + " lei/lunar");
                                                            tvCostKWh.setText("Cost KWh: " + formatter.format(cost) + " lei/lunar");
                                                            spnZona.setVisibility(View.INVISIBLE);
                                                            spnOferte.setVisibility(View.INVISIBLE);
                                                            btnSalveazaCostSelectat.setVisibility(View.INVISIBLE);
                                                            imageButtonNext.setVisibility(View.INVISIBLE);

                                                            sDialog.dismiss();
                                                            CookieBar.build(SeteazaCostFacturaActivity.this)
                                                                    .setIcon(R.drawable.success_circle)
                                                                    .setTitle("SALVAT")
                                                                    .setTitleColor(R.color.white)
                                                                    .setIcon(R.drawable.success_circle)
                                                                    .setCookiePosition(CookieBar.TOP)
                                                                    .setDuration(2000) // 2 seconds
                                                                    .setBackgroundColor(R.color.myGreen)
                                                                    .setSwipeToDismiss(true)
                                                                    .setAnimationIn(android.R.anim.slide_in_left, android.R.anim.slide_in_left)
                                                                    .setAnimationOut(android.R.anim.slide_out_right, android.R.anim.slide_out_right)
                                                                    .show();

                                                        }
                                                    }
                                                })
                                                .show();

                                    }

                                }
                            }, 100);
                        }
                    });


                } else {
                    spnZona.setVisibility(View.INVISIBLE);
                    spnOferte.setVisibility(View.INVISIBLE);
                    switchCostManual.setEnabled(true);
                    switchCostMediu.setEnabled(true);
                    btnSalveazaCostSelectat.setVisibility(View.INVISIBLE);
                    imageButtonNext.setVisibility(View.INVISIBLE);

                }
                SharedPreferences.Editor editor = getSharedPreferences("com.project", MODE_PRIVATE).edit();
                editor.putBoolean("service_status3", isChecked);
                editor.commit();

            }
        });

    }


    private void arataDetaliiDespreCost(View v) {
        Dialog dialog = new Dialog(SeteazaCostFacturaActivity.this);
        dialog.setContentView(R.layout.detaliicostkwh);
        dialog.setCancelable(true);
        Button btnInchide = dialog.findViewById(R.id.btn_close_detaliicostkwh);
        btnInchide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void verificaZonaAleasasiPopuleazaOferte() {
        if (!spnZona.getSelectedItem().toString().equals("Zona")) {
            spnOferte.setVisibility(VISIBLE);
        }
        ArrayList<String> namesList = null;
        String[] array_name = null;
        if (spnZona.getSelectedItem().toString().equals("Banat")) {
            array_name = getResources().getStringArray(R.array.Banat);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Dobrogea")) {
            array_name = getResources().getStringArray(R.array.Dobrogea);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Muntenia Sud")) {
            array_name = getResources().getStringArray(R.array.MunteniaSud);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Muntenia Nord")) {
            array_name = getResources().getStringArray(R.array.MunteniaNord);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Moldova")) {
            array_name = getResources().getStringArray(R.array.Moldova);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Oltenia")) {
            array_name = getResources().getStringArray(R.array.Oltenia);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Transilvania Sud")) {
            array_name = getResources().getStringArray(R.array.TransilvaniaSud);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Transilvania Nord")) {
            array_name = getResources().getStringArray(R.array.TransilvaniaNord);
            namesList = new ArrayList<String>(Arrays.asList(array_name));
        } else if (spnZona.getSelectedItem().toString().equals("Zona")) {
            Toast.makeText(this, " Selectați zona! ", Toast.LENGTH_SHORT).show();
        } else if (spnZona.getSelectedItem().toString().equals(null)) {
            Toast.makeText(this, " Selectați zona! ", Toast.LENGTH_SHORT).show();
        }
        if (array_name != null) {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_item, R.id.textView, namesList);
            spnOferte.setAdapter(adapter);
        }
    }


    public void seteazaCostulInDataBase(float cost) {
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    {
                        User user = userSnapshot.getValue(User.class);
                        if ((user.getUid()).equals(uid)) {
                            DatabaseReference costReference = userSnapshot.getRef().child("costKWh");
                            costReference.setValue(String.valueOf(cost));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initComponents() {
        switchCostMediu = findViewById(R.id.switch1_costMediu_seteazaCostFactura);
        switchCostManual = findViewById(R.id.switch2_costManual_seteazaCostFactura);
        switchCostLista = findViewById(R.id.switch3_costDinLista_seteazaCostFactura);
        etIntroducetiCost = findViewById(R.id.et_introduCost_seteazaCostFactura);
        btnSalveazaCostIntrodus = findViewById(R.id.btnSalveazaCost_seteazaCostFactura);
        btnSalveazaCostSelectat = findViewById(R.id.btnSalveazaCostDinListaFurnizata_seteazaCostFactura);
        textViewDetalii = findViewById(R.id.tv_detaliiMetodaAleasa_seteazaCostFactura);
        spnZona = findViewById(R.id.spinnerZona_seteazaCostFactura);
        spnOferte = findViewById(R.id.spinnerOfertePret_seteazaCostFactura);
        databaseReferenceIncaperi = FirebaseDatabase.getInstance().getReference("incaperi").child(uid);
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("utilizatori");
        tvConsumTotal = findViewById(R.id.tv_consumTotal_seteazaCostFactura);
        tvCostFactura = findViewById(R.id.tv_costFactura_seteazaCostFactura);
        tvCostFactura = findViewById(R.id.tv_costFactura_seteazaCostFactura);
        imageButtonNext = findViewById(R.id.imageButtonNext_seteazaCostFactura);
        btnBack = findViewById(R.id.btnBack_seteazaCostFactura);
        tvCostKWh = findViewById(R.id.tv_costKWh_seteazaCostFactura);
        fromBottomAnimationFaster = AnimationUtils.loadAnimation(SeteazaCostFacturaActivity.this, R.anim.from_bottom_faster);
        fromBottomAnimation = AnimationUtils.loadAnimation(SeteazaCostFacturaActivity.this, R.anim.from_bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceIncaperi.removeEventListener(valueEventListenerForCostAndCosnum);
    }

    private void initialAnimations() {
        View imageView;
        ConstraintLayout constraintLayout;
        TextView tvTitlu;
        constraintLayout = findViewById(R.id.constraintLayout2_seteazaCost);
        imageView = findViewById(R.id.imageView_seteazaCostFactura);
        tvTitlu = findViewById(R.id.tv_titlu_seteazaCostFactura);
        imageView.startAnimation(fromBottomAnimationFaster);
        constraintLayout.startAnimation(fromBottomAnimation);
        tvTitlu.startAnimation(fromBottomAnimationFaster);

    }

}