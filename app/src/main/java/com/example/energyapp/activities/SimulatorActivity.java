package com.example.energyapp.activities;

import static android.view.View.VISIBLE;
import static com.example.energyapp.activities.DispozitiveIncapereActivity.calculezPuteredispozitiv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.developer.kalert.KAlertDialog;
import com.example.energyapp.R;
import com.example.energyapp.adaptors.DispozitiveSimulatorListAdapter;
import com.example.energyapp.classes.Dispozitiv;
import com.example.energyapp.classes.Incapere;
import com.example.energyapp.classes.MyCallback;
import com.example.energyapp.classes.MyUtilsClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.royrodriguez.transitionbutton.TransitionButton;
import org.aviran.cookiebar2.CookieBar;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.dd4you.animatoo.Animatoo;

public class SimulatorActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid = "";
    ImageButton btnBack, btnAfiseazaLista;
    Spinner spnTipDispozitiv, spnPutere;
    EditText etPutere;
    TextView tvUnitati;
    EditText  etTipDispozitiv;
    List<Dispozitiv> dispozitive;
    DispozitiveSimulatorListAdapter dispozitiveAdapter;
    ListView listViewDispozitive;
    DatabaseReference databaseReferenceIncaperi;
    List<Dispozitiv> dispozitiveCautate;
    DecimalFormat formatter = new DecimalFormat("##.##");
    Intent intent ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        uid = firebaseUser.getUid();
        initComponents();
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        //preluam din baza de date dispozitivele utilizatorului si le adaugam intr-o lista locala
        preiaToateDispozitivele();

        //referinta a bazei de date care ma intereseaza-->de unde preiau dispozitivele



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateDiagonal(SimulatorActivity.this);

            }
        });

        //spinner tipDispozitiv-->daca utilizatorul alege Căutați după descriere atunci poate introduce de la tastatura tipul
        spnTipDispozitiv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_val = spnTipDispozitiv.getSelectedItem().toString();
                if (selected_val.equals("Căutați după descriere"))
                    etTipDispozitiv.setVisibility(VISIBLE);
                else{
                    etTipDispozitiv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //spiner putere/conusm-->schimb textul in functie de ce este ales de utilizator
        spnPutere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_val = spnPutere.getSelectedItem().toString();
                if (selected_val.equals("Putere")) {
                    tvUnitati.setText("W");
                    etPutere.setHint("Putere");
                } else if (selected_val.equals("Consum")) {
                    tvUnitati.setText("kWh/an");
                    etPutere.setHint("Consum");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvUnitati.setText("Unitati");
            }
        });


        btnAfiseazaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispozitiveCautate = new ArrayList<>();
                    listViewDispozitive.setVisibility(VISIBLE);
                    String tipDispozitiv = spnTipDispozitiv.getSelectedItem().toString();
                    if(tipDispozitiv.equals("Căutați după descriere")){
                        if(etTipDispozitiv.getText().toString()!=""&& etTipDispozitiv.getText().toString()!=null && !etTipDispozitiv.getText().toString().isEmpty()) {
                            tipDispozitiv = etTipDispozitiv.getText().toString();
                            Log.i("hei",tipDispozitiv);
                            etTipDispozitiv.setError(null);
                        }
                        else {
                            Toast.makeText(SimulatorActivity.this, "Nu ati introdus tipul! ", Toast.LENGTH_SHORT).show();
                            etTipDispozitiv.setError("Completați");
                        }
                    }
                    if(tipDispozitiv.equals("Selectați")){
                    Toast.makeText(SimulatorActivity.this, "Nu ati selectat tipul! ", Toast.LENGTH_SHORT).show();
                     }
                    else {
                        for (Dispozitiv dispozitiv : dispozitive) {
                            if (dispozitiv.getTipDispozitiv().toLowerCase().contains(tipDispozitiv.toLowerCase())) {
                                dispozitiveCautate.add(dispozitiv);
                            }
                        }
                        if (dispozitiveCautate.size() == 0) {
                            new AlertDialog.Builder(SimulatorActivity.this)
                                    .setIcon(R.drawable.tips)
                                    .setTitle("Nu aveti niciun aparat pentru tipul introdus")
                                    .setMessage(" Doriti sa comparati cu orice alt dispozitiv? ")
                                    .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            for (Dispozitiv dispozitiv : dispozitive) {
                                                Log.i("heil", dispozitiv.getTipDispozitiv());
                                                dispozitiveCautate.add(dispozitiv);
                                            }
                                            dispozitiveAdapter.notifyDataSetChanged();

                                        }
                                    }).setNegativeButton("NU", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dispozitiveCautate = new ArrayList<>();
                                }
                            }).show();

                        }
                        dispozitiveAdapter = new DispozitiveSimulatorListAdapter(SimulatorActivity.this, dispozitiveCautate);
                        listViewDispozitive.setAdapter(dispozitiveAdapter);
                        dispozitiveAdapter.notifyDataSetChanged();
                    }



            }

        });
        //ce se intampla cand dau click lung pe un item din listview--vad detaliile despre el
        //vizualizare detalii dispozitiv curent
        listViewDispozitive.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Dispozitiv dispozitiv = dispozitiveCautate.get(position);
                arataDetalii(view,dispozitiv);
                return true;
            }
        });
        listViewDispozitive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Float putereDispozitivNou = 0.0f;
                Dispozitiv dispozitiv = dispozitiveCautate.get(position);
                if(verificare()) {
                    putereDispozitivNou = Float.parseFloat(etPutere.getText().toString());
                    if(spnTipDispozitiv.getSelectedItem().equals("Căutați după descriere")){
                        if(etTipDispozitiv.getText().toString()!=""&& etTipDispozitiv.getText().toString()!=null && !etTipDispozitiv.getText().toString().isEmpty()) {
                            etTipDispozitiv.setError(null);
                            arataComparatie(dispozitiv, putereDispozitivNou,SimulatorActivity.this);
                        }
                        else {
                            Toast.makeText(SimulatorActivity.this, "Nu ati introdus tipul! ", Toast.LENGTH_SHORT);
                            etTipDispozitiv.setError("Completați");
                        }
                    }else {
                        arataComparatie(dispozitiv, putereDispozitivNou, SimulatorActivity.this);
                    }
                } else{
                    Toast.makeText(SimulatorActivity.this, "Nu ati introdus puterea!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        intent = getIntent();
        if(intent != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String tipDispozitivDeSimulare = intent.getStringExtra("SIMULARE");
                    String putere = intent.getStringExtra("SIMULARE_VALOARE");
                    String tipDispozitiv = null;
                    if (tipDispozitivDeSimulare != null) {
                        tipDispozitivDeSimulare = tipDispozitivDeSimulare.trim();
                        spnTipDispozitiv.setSelection(((ArrayAdapter<String>) spnTipDispozitiv.getAdapter()).getPosition(tipDispozitivDeSimulare));
                        etPutere.setText(putere);
                        dispozitiveCautate = new ArrayList<>();
                        listViewDispozitive.setVisibility(VISIBLE);
                        if(spnTipDispozitiv.getSelectedItem() != null) {
                             tipDispozitiv = spnTipDispozitiv.getSelectedItem().toString();

                        }
                        if(tipDispozitiv ==null ){
                            tipDispozitiv="Căutați după descriere";
                            spnTipDispozitiv.setSelection(((ArrayAdapter<String>) spnTipDispozitiv.getAdapter()).getPosition(tipDispozitiv));
                            etTipDispozitiv.setVisibility(VISIBLE);
                            etTipDispozitiv.setText(tipDispozitivDeSimulare);
                        }
                        if (tipDispozitiv.equals("Căutați după descriere")) {
                            if (etTipDispozitiv.getText().toString() != "" && etTipDispozitiv.getText().toString() != null && !etTipDispozitiv.getText().toString().isEmpty()) {
                                tipDispozitiv = etTipDispozitiv.getText().toString();
                                etTipDispozitiv.setError(null);
                            } else {
                                Toast.makeText(SimulatorActivity.this, "Nu ati introdus tipul! ", Toast.LENGTH_SHORT).show();
                                etTipDispozitiv.setError("Completați");
                            }
                        } else if (tipDispozitiv.equals("Selectați")) {
                            Toast.makeText(SimulatorActivity.this, "Nu ati selectat tipul! ", Toast.LENGTH_SHORT).show();
                        } else {
                            for (Dispozitiv dispozitiv : dispozitive) {
                                if (dispozitiv.getTipDispozitiv().toLowerCase().contains(tipDispozitiv.toLowerCase())) {
                                    dispozitiveCautate.add(dispozitiv);
                                }
                            }

                        }
                        dispozitiveAdapter = new DispozitiveSimulatorListAdapter(SimulatorActivity.this, dispozitiveCautate);
                        listViewDispozitive.setAdapter(dispozitiveAdapter);
                    }
                }

            }, 1000);
        }

    }

    private void arataComparatie(Dispozitiv dispozitiv,Float putereDispozitivNou, Context context) {
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.comparatieconsumdispozitive_dialog);
        dialog.setCancelable(true);
        Switch switchDetalii;
        switchDetalii= (Switch) dialog.findViewById(R.id.switch_simulatorDialog);

        Button closeBtn= dialog.findViewById(R.id.btn_close_dialogSimulator);

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

        //adaugat
        Switch switchInvestitie = dialog.findViewById(R.id.switch_investitie_simulatorDialog);
        ImageButton imgBtn = dialog.findViewById(R.id.arrow_simulatorDialog);
        TextView etValoare = dialog.findViewById(R.id.et_valoareDispozitiv_simulatorDialog);
        TextView tvRecuperareInvestitie = dialog.findViewById(R.id.tv_recuperareInvestitie_simulatorDialog);

        TransitionButton btnInlocuieste = dialog.findViewById(R.id.btn_inlocuieste_dialogSimulator);
        btnInlocuieste.setVisibility(View.INVISIBLE);


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
        if (spnPutere.getSelectedItem().toString().equals("Putere")) {
            tvPutere2.setText(putereDispozitivNou.toString()+" W");
        } else if (spnPutere.getSelectedItem().toString().equals("Consum")) {
            tvPutere2.setTextSize(14);
            tvPutere2.setText(putereDispozitivNou.toString()+" kWh/an");
        }

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
                            economieLunara.setVisibility(VISIBLE);
                            economieAnuala.setVisibility(VISIBLE);
                            if(costLunar1 > costLunar2) {
                                float economieLunarainLei =costLunar1 - costLunar2;
                                economieLunara.setText("Economie lunara: " +formatter.format(economieLunarainLei) + " lei");
                                economieAnuala.setText("Economie anuala: " +formatter.format(economieLunarainLei*12) + " lei");
                            }else {
                                float crestereLunaraInLei =costLunar2 - costLunar1;
                                economieLunara.setText("Măriți costurile lunare cu: " +formatter.format(crestereLunaraInLei) + " lei");
                                economieAnuala.setText("Măriți costurile anuale cu: " +formatter.format(crestereLunaraInLei*12) + " lei");
                                if(crestereLunaraInLei == 0){
                                    economieLunara.setText("Nicio modificare a consumului");
                                    economieAnuala.setText("Nicio modificare a consumului");
                                }
                            }
                        }else{
                            economieLunara.setVisibility(View.GONE);
                            economieAnuala.setVisibility(View.GONE);
                        }
                    }
                });
                if(costLunar1>costLunar2 )
                    switchInvestitie.setVisibility(VISIBLE);
                switchInvestitie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            imgBtn.setVisibility(VISIBLE);
                            tvRecuperareInvestitie.setVisibility(VISIBLE);
                            etValoare.setVisibility(VISIBLE);
                            etValoare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog dialog1 = new AlertDialog.Builder(SimulatorActivity.this).create();
                                    EditText editText = new EditText(SimulatorActivity.this);
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editText.setWidth(15);
                                    dialog1.setTitle("Introduceți valoarea"+"\n");
                                    editText.setGravity(Gravity.CENTER);
                                    editText.setBackgroundDrawable(ContextCompat.getDrawable(SimulatorActivity.this, R.drawable.shape_edittext01));
                                    dialog1.getWindow().setLayout(600, 400);
                                    dialog1.setIcon(R.drawable.ic_money);
                                    dialog1.setView(editText);
                                    dialog1.setButton(DialogInterface.BUTTON_POSITIVE, "Salvați", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            etValoare.setText("Dispozitivul costa "+ editText.getText()+" lei");
                                        }
                                    });
                                    dialog1.setButton(DialogInterface.BUTTON_NEGATIVE, "Anulați", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog1.dismiss();
                                        }
                                    });
                                    dialog1.show();
                                }
                            });
                            //recuperare investitie
                            imgBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!etValoare.getText().equals("Tastați valoarea dispozitivului")&&!etValoare.getText().toString().isEmpty()
                                            && etValoare.getText() != null && Float.parseFloat(etValoare.getText().toString().trim().split(" ")[2]) > 0) {
                                        etValoare.setError(null);
                                        float valoare = Float.parseFloat(etValoare.getText().toString().trim().split(" ")[2]);
                                        float economLun = (costLunar1 - costLunar2)/30;//impartim la 30 sa aflam nr de zile
                                        float recuperareInvestitie = valoare/economLun;
                                        Log.i("hei",String.valueOf(recuperareInvestitie));
                                        int ani =0, luni=0, zile=0 ;float rest =0;
                                        if(recuperareInvestitie > 365){
                                            ani = (int) (recuperareInvestitie/365);
                                            rest = recuperareInvestitie - ani*365;
                                            if(rest >30){
                                                luni = (int) (rest/30);
                                                rest = rest - luni*30;
                                                zile = (int) rest;
                                            }else{
                                                zile =(int) rest;
                                            }
                                        }else if(recuperareInvestitie >30){
                                            luni = (int) (recuperareInvestitie/30);
                                            rest = recuperareInvestitie - luni*30;
                                            zile = (int) rest;
                                        }else{
                                            zile =(int) recuperareInvestitie;
                                        }

                                        tvRecuperareInvestitie.setText("Veți recupera investiția în aproximativ "+"\n" +ani+" ani "+luni+" luni "+zile+" zile");
                                    }else{
                                        etValoare.setError("Completați");
                                    }
                                }
                            });


                        } else {
                            imgBtn.setVisibility(View.GONE);
                            tvRecuperareInvestitie.setVisibility(View.GONE);
                            etValoare.setVisibility(View.GONE);

                        }
                    }
                });


            }

        });
        dialog.show();



        //ce se intampla cand apas pe butonul inlocuieste-->updatez dispozitivul in bd
        btnInlocuieste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnInlocuieste.startAnimation();
                KAlertDialog dialogInlocuieste =new KAlertDialog(SimulatorActivity.this, KAlertDialog.CUSTOM_IMAGE_TYPE);
                 dialogInlocuieste.setTitleText("Înlocuiți dispozitivul?")
                        .setCustomImage(android.R.drawable.ic_menu_edit,SimulatorActivity.this)
                        // .setContentText("Vrei să ștergi aceast dispozitiv?")
                        .setTitleTextSize(20)
                        .setContentTextSize(20)
                        .setConfirmText("DA")
                        .setCancelText("NU")
                        .confirmButtonColor(R.color.myGreen,SimulatorActivity.this)
                        .cancelButtonColor(R.color.red_btn_bg_color,SimulatorActivity.this)
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                updateDispozitiv(dispozitiv,consumDispozitivNou);
                                CookieBar.build(SimulatorActivity.this)
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
                                dialogInlocuieste.dismiss();
                                dialog.dismiss();
                                finish();

                                Intent intent = new Intent(SimulatorActivity.this, SimulatorActivity.class);
                                String tip = dispozitiv.getTipDispozitiv().split("-")[0];
                                Log.i("hei",tip);
                                intent.putExtra("SIMULARE",tip);
                                intent.putExtra("SIMULARE_VALOARE",formatter.format(putereDispozitivNou));
                                startActivity(intent);
                               // Animatoo.animateZoom(SimulatorActivity.this);


                            }
                        }).show();

            }
        });
    }

    private void updateDispozitiv(Dispozitiv dispozitiv, Float consumDispozitivNou) {
        dispozitiveCautate = new ArrayList<>();
        dispozitiveAdapter.notifyDataSetChanged();
         DatabaseReference databaseDispozitive = FirebaseDatabase.getInstance().getReference("dispozitive").child(uid).child(dispozitiv.getIncapereId());
         DatabaseReference databaseReferenceUpdate = databaseDispozitive.child(dispozitiv.getDispozitivId()).child("consumDispozitiv");
         databaseReferenceUpdate.setValue(String.valueOf(consumDispozitivNou));

    }

    private Float calculeazaConsumDispozitiv(Dispozitiv dispozitiv, Float putereDispozitivNou) {
        Float consum = 0.0f;
        if (spnPutere.getSelectedItem().toString().equals("Putere")) {
            if (dispozitiv.getModfunctionare().equals("Zilnic")) {
                consum= (putereDispozitivNou * ((float)dispozitiv.getMinuteFunctionareZilnic() / 60) * 30 / 1000) * dispozitiv.getNumarDispozitive();
            } else if (dispozitiv.getModfunctionare().equals("Saptamanal")) {
                consum = (putereDispozitivNou * ((float)(dispozitiv.getMinuteFunctionareZilnic()) / 420) * 30 / 1000) * dispozitiv.getNumarDispozitive();
            }
        } else if (spnPutere.getSelectedItem().toString().equals("Consum")) {
            if (dispozitiv.getModfunctionare().equals("Zilnic"))  {
                consum= (((putereDispozitivNou * 1000) / 365 / 24)  * ((float)dispozitiv.getMinuteFunctionareZilnic() / 60) * 30 / 1000) * dispozitiv.getNumarDispozitive();
            } else if (dispozitiv.getModfunctionare().equals("Saptamanal")) {
                consum = (((putereDispozitivNou * 1000) / 365 / 24) * ((float)(dispozitiv.getMinuteFunctionareZilnic()) / 420) * 30 / 1000) * dispozitiv.getNumarDispozitive();

            }
        }

        return  consum;
    }

    public void arataDetalii(View view, Dispozitiv dispozitiv) {
        Dialog dialog = new Dialog(SimulatorActivity.this);
        dialog.setContentView(R.layout.detaliidispozitiv_dialog);
        dialog.setCancelable(true);

        Button copyBtn = dialog.findViewById(R.id.btn_copy_dialogDetaliiDispozitiv);
        Button closeBtn = dialog.findViewById(R.id.btn_close_dialogDetaliiDispozitiv);

        TextView tvTip = dialog.findViewById(R.id.tv_tipDispozitiv_dialogDetaliiDispozitiv);
        TextView tvClasa = dialog.findViewById(R.id.tv_clasaDispozitiv_dialogDetaliiDispozitiv);
        TextView tvPutere = dialog.findViewById(R.id.tv_putereDispozitiv_dialogDetaliiDispozitiv);
        TextView tvZilnic = dialog.findViewById(R.id.tv_zilnic_dialogDetaliiDispozitiv);
        TextView tvLunar = dialog.findViewById(R.id.tv_lunar_dialogDetaliiDispozitiv);
        TextView tvAnual = dialog.findViewById(R.id.tv_anual_dialogDetaliiDispozitiv);
        SwitchCompat switchDetalii = (SwitchCompat) dialog.findViewById(R.id.switch_detaliiDispozitiv);
        LinearLayout lZilnic = dialog.findViewById(R.id.linearLayoutZilnic_dialogDetaliiDispozitiv);
        LinearLayout lLunar = dialog.findViewById(R.id.linearLayoutLunar_dialogDetaliiDispozitiv);
        LinearLayout lAnual = dialog.findViewById(R.id.linearLayoutAnual_dialogDetaliiDispozitiv);

        copyBtn.setVisibility(View.INVISIBLE);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        switchDetalii.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lZilnic.setVisibility(VISIBLE);
                    lLunar.setVisibility(VISIBLE);
                    lAnual.setVisibility(VISIBLE);
                    Float consumZilnic = Float.parseFloat(dispozitiv.getConsumDispozitiv())/30;
                    Float consumLunar = Float.parseFloat(dispozitiv.getConsumDispozitiv());
                    Float consumAnual = consumLunar* 12;
                    tvZilnic.setText(formatter.format(consumZilnic)+" kWh");
                    tvLunar.setText(formatter.format(consumLunar)+" kWh");
                    tvAnual.setText(formatter.format(consumAnual)+" kWh");


                }else{
                    lZilnic.setVisibility(View.GONE);
                    lLunar.setVisibility(View.GONE);
                    lAnual.setVisibility(View.GONE);
                }
            }
        });
        String tipDispozitiv = dispozitiv.getTipDispozitiv().toString().split("-")[0];
        tvTip.setText(" " + tipDispozitiv);
        tvClasa.setText(" " + dispozitiv.getClasaDispozitiv());
        tvPutere.setText(formatter.format(calculezPuteredispozitiv(dispozitiv)) + " W");

        dialog.show();
    }


    private boolean verificare() {
       if (etPutere.getText().toString().isEmpty() || etPutere.getText().toString().equals(null) || etPutere.getText().toString().equals("") ||
       Float.parseFloat(etPutere.getText().toString())<=0) {
           etPutere.setError("Completati");
           return false;
       }
       else {
           etPutere.setError(null);
           return true;

       }
    }

    private void initComponents() {
        btnBack = findViewById(R.id.btnBack_simulator);
        btnAfiseazaLista = findViewById(R.id.arrow_simulator);
        spnTipDispozitiv = findViewById(R.id.spnTipDispozitiv_simulator);
        spnPutere = findViewById(R.id.spnPutere_simulator);
        etPutere = findViewById(R.id.etPutere_simulator);
        listViewDispozitive = findViewById(R.id.listView_simulator);
        databaseReferenceIncaperi = FirebaseDatabase.getInstance().getReference("incaperi").child(uid);
        dispozitive = new ArrayList<>();
        etTipDispozitiv = findViewById(R.id.etTipDispozitiv_simulator);
        tvUnitati = findViewById(R.id.tvUnitate_simulator);


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
                                dispozitiv.setTipDispozitiv(dispozitiv.getTipDispozitiv()+" -> "+incapere.getDenumire());
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