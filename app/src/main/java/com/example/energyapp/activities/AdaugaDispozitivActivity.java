package com.example.energyapp.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.energyapp.R;
import com.example.energyapp.adaptors.CustomExpandableListAdapter;
import com.example.energyapp.classes.Dispozitiv;
import com.example.energyapp.classes.ExpandableListDataPump;
import com.example.energyapp.classes.Incapere;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.dd4you.animatoo.Animatoo;


public class AdaugaDispozitivActivity extends AppCompatActivity {

    //format pentru data
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    Button btnDela, btnPanala;
    TransitionButton btnAdauga;
    TextView tvOreFunctionare, tvUnitati, tvMinFunctionare, tvDela, tvPanala;
    Spinner spnPutere, spnTipDispozitv, spnClasaEnergetica;
    EditText etPutere, etTipDispozitiv, etDispozitiveIdentice;
    DatabaseReference databaseDispozitive;
    RadioButton rbZilnic, rbSaptamanal;
    RadioGroup rgTipUtilizare;
    ImageButton imgButtonBack;
    int ora=8, minutul=0;
    //pentru baza de date
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid;
    String incapereId;
    DatabaseReference databaseReferenceUser;
    ImageView btnSelecteazaDinListaFurnizata;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_dispozitiv);

        //preiau userul curent
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        uid=firebaseUser.getUid();
        initComponents();

        EditText editTextOre = new EditText(AdaugaDispozitivActivity.this);
        EditText editTextMin = new EditText(AdaugaDispozitivActivity.this);

        //buton inapi
        imgButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });

        //preiau intentul primit
        Intent intent = getIntent();
        Incapere incapere = intent.getParcelableExtra(IncaperiUtilizatorActivity.INCAPERE_OBIECT);
        incapereId=incapere.getIncapereId();

        //initializez baza de date
        // am acelasi id pt dispozitiv si incapere-->dar trebuie sa verific sa fie al userului curent
        databaseDispozitive = FirebaseDatabase.getInstance().getReference("dispozitive").child(uid).child(incapere.getIncapereId());


        //switch listener
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnDela.setVisibility(VISIBLE);
                    btnPanala.setVisibility(VISIBLE);
                    tvDela.setVisibility(VISIBLE);
                    tvPanala.setVisibility(VISIBLE);
                    tvOreFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.gray02));
                    tvMinFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.gray02));
                    tvOreFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._15sdp));
                    tvMinFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._15sdp));
                } else {
                    btnDela.setVisibility(GONE);
                    btnPanala.setVisibility(GONE);
                    tvDela.setVisibility(GONE);
                    tvPanala.setVisibility(GONE);
                    btnDela.setText("Selecteaza");
                    btnPanala.setText("Selecteaza");
                    btnPanala.setEnabled(false);
                    tvOreFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._20sdp));
                    tvMinFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._20sdp));
                    tvOreFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.myGreen));
                    tvMinFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.myGreen));
                }
            }
        });
        //clickListener pe butonulDeAduaga
        btnAdauga.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
    // Start the loading animation when the user tap the button
    btnAdauga.startAnimation();

    // Do your networking task or background work here.
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            boolean isSuccessful = true;

            // Choose a stop animation if your call was succesful or not
            if (isSuccessful && aIntrodusCorect()) {
                btnAdauga.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                    @Override
                    public void onAnimationStopEnd() {
                        try {
                            saveDispozitiv();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        finish();
                        Animatoo.animateDiagonal(AdaugaDispozitivActivity.this);
                    }
                });
            } else {
                btnAdauga.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
            }
        }
    }, 800);
}


        });
        //listener pentru click pe imageView selecteaza dispozitiv din lista furnizata
        btnSelecteazaDinListaFurnizata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectItemDialog();
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
        //spinner tipDispozitiv-->daca utilizatorul alege alt tip atunci poate introduce de la tastatura tipul
        spnTipDispozitv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_val = spnTipDispozitv.getSelectedItem().toString();
                if (selected_val.equals("Alt tip"))
                    etTipDispozitiv.setVisibility(VISIBLE);
                else{
                    etTipDispozitiv.setVisibility(GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //atunci cand vreau sa introduc direct nr de ore
        AlertDialog dialogOre = new AlertDialog.Builder(AdaugaDispozitivActivity.this).create();
        editTextOre.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextOre.setPadding(20,20,16,16);
        editTextOre.setGravity(Gravity.CENTER);
        editTextOre.setBackgroundDrawable(ContextCompat.getDrawable(AdaugaDispozitivActivity.this, R.drawable.shape_edittext01));
        dialogOre.getWindow().setLayout(600, 400);
        dialogOre.setTitle("Introduceți numărul de ore"+"\n");
        dialogOre.setIcon(R.drawable.ic_baseline_access_time_24);
        dialogOre.setView(editTextOre);
         dialogOre.setButton(DialogInterface.BUTTON_POSITIVE, "Salvați", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 tvOreFunctionare.setText(editTextOre.getText());
                 btnDela.setText("Selecteaza");
                 btnPanala.setText("Selecteaza");
             }
         });
        dialogOre.setButton(DialogInterface.BUTTON_NEGATIVE, "Anulați", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialogOre.dismiss();
            }
        });
        tvOreFunctionare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOre.show();

            }
        });
        //atunci cand vreau sa introduc direct nr de minute
        AlertDialog dialogMin = new AlertDialog.Builder(AdaugaDispozitivActivity.this).create();
        editTextMin.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextMin.setWidth(15);
        dialogMin.setTitle("Introduceți numărul de minute"+"\n");
        editTextMin.setPadding(20,20,16,16);
        editTextMin.setGravity(Gravity.CENTER);
        editTextMin.setBackgroundDrawable(ContextCompat.getDrawable(AdaugaDispozitivActivity.this, R.drawable.shape_edittext01));
        dialogMin.getWindow().setLayout(600, 400);
        dialogMin.setIcon(R.drawable.ic_baseline_access_time_24);
        dialogMin.setView(editTextMin);
        dialogMin.setButton(DialogInterface.BUTTON_POSITIVE, "Salvați", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvMinFunctionare.setText(editTextMin.getText());
                btnDela.setText("Selecteaza");
                btnPanala.setText("Selecteaza");
            }
        });
        dialogMin.setButton(DialogInterface.BUTTON_NEGATIVE, "Anulați", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogMin.dismiss();
            }
        });
        tvMinFunctionare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMin.show();
            }
        });


    }
    private void showSelectItemDialog() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.select_item_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Selectati dispozitivul");
        dialogBuilder.setCancelable(true);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        final ExpandableListView expandableListView = (ExpandableListView) dialogView.findViewById(R.id.expandableListView_dialog);
        final HashMap<String, List<String>> expandableListDetail = ExpandableListDataPump.getData();
        final List<String>  expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        final ExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String tip1 = expandableListTitle.get(groupPosition);
                String details = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
                Log.i("hei",details);
                String tip=details.split(" ")[0];
                ArrayAdapter<String> adapterTip = (ArrayAdapter<String>) spnTipDispozitv.getAdapter();
                int spinnerPositionTip = adapterTip.getPosition(tip1);
                spnTipDispozitv.setSelection(spinnerPositionTip);
                String clasaEnergetica = details.split(" ")[3];
                ArrayAdapter<String> adapterClasa = (ArrayAdapter<String>) spnClasaEnergetica.getAdapter();
                int spinnerPositionClasa = adapterClasa.getPosition(clasaEnergetica);
                spnClasaEnergetica.setSelection(spinnerPositionClasa);
                String putereSauConsum = details.split(" ")[5];
                if(putereSauConsum.equals("W")){
                    String putere = details.split(" ")[4];
                    etPutere.setText(putere);
                    spnPutere.setSelection(0);
                    tvUnitati.setText("W");
                    etPutere.setHint("Putere");
                } else{
                    String consum = details.split(" ")[4];
                    etPutere.setText(consum);
                    spnPutere.setSelection(1);
                    tvUnitati.setText("kWh/an");
                    etPutere.setHint("Consum");

                }

                alertDialog.dismiss();
                return false;


            }
        });


        alertDialog.show();
    }
    //initializare componente
    private void initComponents() {
        imgButtonBack=findViewById(R.id.btnBack_adaugaDispozitiv);
        btnDela = findViewById(R.id.btnDela_adaugaDispozitiv);
        btnPanala = findViewById(R.id.btnPanala_adaugaDispozitiv);
        tvOreFunctionare = findViewById(R.id.tvNrOreFunctionare_adaugaDispozitiv);
        spnPutere = findViewById(R.id.spnPutere_adaugaDispozitiv);
        tvUnitati = findViewById(R.id.tvUnitate_adaugaDispozitiv);
        etPutere = findViewById(R.id.etPutere_adaugaDispozitiv);
        spnTipDispozitv = findViewById(R.id.spnTipDispozitiv_adaugaDispozitiv);
        etTipDispozitiv = findViewById(R.id.etTipDispozitiv_adaugaDispozitiv);
        spnClasaEnergetica = findViewById(R.id.spnClasa_adaugaDispozitiv);
        btnAdauga = findViewById(R.id.btnAdauga_adaugaDispozitiv);
        etDispozitiveIdentice = findViewById(R.id.etNrDispozitiveIdentice_adaugaDispozitiv);
        rbZilnic = findViewById(R.id.rbtZilnic_adaugaDispozitiv);
        rbSaptamanal = findViewById(R.id.rbtSaptamanal_adaugaDispozitiv);
        rgTipUtilizare = findViewById(R.id.rgModFunctionare_adaugaDispozitiv);
        databaseReferenceUser=  FirebaseDatabase.getInstance().getReference("utilizatori");
        tvMinFunctionare = findViewById(R.id.tvNrMinFunctionare_adaugaDispozitiv);
        btnSelecteazaDinListaFurnizata = findViewById(R.id.imageview_pick_item_adaugaDispozitiv);
        aSwitch = findViewById( R.id.switch_adaugaDispozitiv);
        tvDela = findViewById(R.id.tvDela_adaugaDispozitiv);
        tvPanala = findViewById(R.id.tvPanala_adaugaDispozitiv);


    }

    //pt cele 2 butoane de la si pana la -->fereastra de dialog si setez in textView nr de ore corespunzator
    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                try {
                    Date startDate, endDate;
                    ora = selectedHour;
                    minutul = selectedMinute;

                    if (view.getId() == R.id.btnDela_adaugaDispozitiv) {
                        btnDela.setText(String.format(Locale.getDefault(), "%02d:%02d", ora, minutul));
                        tvMinFunctionare.setText("0");
                        tvOreFunctionare.setText("0");
                        btnPanala.setEnabled(true);

                    } else if (view.getId() == R.id.btnPanala_adaugaDispozitiv) {
                        btnPanala.setText(String.format(Locale.getDefault(), "%02d:%02d", ora, minutul));
                    }

                    startDate = simpleDateFormat.parse(btnDela.getText().toString());
                    endDate = simpleDateFormat.parse(btnPanala.getText().toString());
                    assert startDate != null;
                    if (startDate.equals(endDate)) {
                        tvOreFunctionare.setText("24");
                        tvMinFunctionare.setText("0");
                        tvOreFunctionare.setVisibility(VISIBLE);
                    } else {
                        assert endDate != null;
                        int ore = minuteDiferenta(startDate, endDate) / 60;
                        int minute = minuteDiferenta(startDate, endDate) - ore * 60;
                        tvOreFunctionare.setText(String.valueOf(ore));
                        tvMinFunctionare.setText(String.valueOf(minute));
                        tvOreFunctionare.setVisibility(VISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        };

        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(AdaugaDispozitivActivity.this, style, onTimeSetListener, ora, minutul, true);
        timePickerDialog.setTitle("Selecteaza ora ");
        timePickerDialog.show();
    }

    // functie care calculeaza diferenta dintre 2Timpuri de utilizare in minute
    public static int minuteDiferenta(Date startDate, Date endDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            long difference = endDate.getTime() - startDate.getTime();
            if (difference < 0) {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                assert dateMin != null;
                assert dateMax != null;
                difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
            }
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            return hours * 60 + min;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void saveDispozitiv() throws ParseException {
        String idDispozitiv = databaseDispozitive.push().getKey();
        float consumAnual;
        float putere;
        float consumDispozitivLunar = 0.000000f;
        String modFunctionare = "";
        //pentru modfunctionare ne luam un int unde pastram id-ul butonului selectat din radiogrup
        int modFunctionareSelectat = rgTipUtilizare.getCheckedRadioButtonId();

        float nrMinuteFunctionare = 0.0f;
        if(!btnDela.getText().toString().equals("Selecteaza") && !btnPanala.getText().toString().equals("Selecteaza"))
        {
            Date startDate = simpleDateFormat.parse(btnDela.getText().toString());
            Date endDate = simpleDateFormat.parse(btnPanala.getText().toString());

            assert startDate != null;
            assert endDate != null;
             nrMinuteFunctionare = (float) minuteDiferenta(startDate, endDate);
            if (nrMinuteFunctionare == 0)
                nrMinuteFunctionare = 1440;//adica avem o zi intre cele 2 ore selectate
        } else {
            nrMinuteFunctionare = Float.parseFloat(tvOreFunctionare.getText().toString())* 60 +
                    Float.parseFloat(tvMinFunctionare.getText().toString());
        }

        String tipDispozitiv = spnTipDispozitv.getSelectedItem().toString();
        if (tipDispozitiv.equals("Alt tip")) {
            tipDispozitiv = etTipDispozitiv.getText().toString();
        }

        String clasaEnergetica = spnClasaEnergetica.getSelectedItem().toString();

        int numarDispozitive = Integer.parseInt(etDispozitiveIdentice.getText().toString());

        //verificam daca utilizatorul a ales sa introduca puterea sau consumul anual
        //dupa care calculam consumulLunar in functie de modul de utilizare:zilnic/saptamanal
        if (spnPutere.getSelectedItem().toString().equals("Putere")) {
            putere = Float.parseFloat(etPutere.getText().toString());
            if (modFunctionareSelectat == R.id.rbtZilnic_adaugaDispozitiv) {
                consumDispozitivLunar = (putere *((float) nrMinuteFunctionare / 60) * 30 / 1000) * numarDispozitive;
                modFunctionare = "Zilnic";
            } else if (modFunctionareSelectat == R.id.rbtSaptamanal_adaugaDispozitiv) {
                consumDispozitivLunar = (putere * ((float)(nrMinuteFunctionare) / 420) * 30 / 1000) * numarDispozitive;
                modFunctionare = "Saptamanal";
            }
        } else if (spnPutere.getSelectedItem().toString().equals("Consum")) {
            consumAnual = Float.parseFloat(etPutere.getText().toString());
            if (modFunctionareSelectat == R.id.rbtZilnic_adaugaDispozitiv) {
                consumDispozitivLunar = (((consumAnual * 1000) / 365 / 24) * ((float)nrMinuteFunctionare / 60) * 30) / 1000 * numarDispozitive;
                modFunctionare = "Zilnic";
            } else if (modFunctionareSelectat == R.id.rbtSaptamanal_adaugaDispozitiv) {
                consumDispozitivLunar =  (((consumAnual * 1000) / 365 / 24) * ((float)(nrMinuteFunctionare) / 420) * 30) / 1000 * numarDispozitive;
                modFunctionare = "Saptamanal";

            }
        }

        //cream noul dispozitiv
        Dispozitiv dispozitiv = new Dispozitiv(idDispozitiv, tipDispozitiv, clasaEnergetica, String.valueOf(consumDispozitivLunar),
                (int) nrMinuteFunctionare, btnDela.getText().toString(),
                btnPanala.getText().toString(), numarDispozitive, modFunctionare,incapereId,uid);
        databaseDispozitive.child(idDispozitiv).setValue(dispozitiv);
        Toast.makeText(this, "Salvat cu succes!", Toast.LENGTH_SHORT).show();
    }

    //verific daca a introdus toate datele corect
    private boolean aIntrodusCorect() {
        if(spnTipDispozitv.getSelectedItem().toString().equals("Selectați")){
            Toast.makeText(this, "Selectează tipul dispozitivului!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(etPutere.getText().toString().trim()) ||
                Double.parseDouble(etPutere.getText().toString()) <= 0.0) {
            Toast.makeText(this, "Completează puterea/consumul", Toast.LENGTH_SHORT).show();
            etPutere.setError("Completează");
        }
        else if(btnDela.getText().toString().equals("Selecteaza")&& btnPanala.getText().toString().equals("Selecteaza") &&
                tvOreFunctionare.getText().toString().equals("0")&& tvMinFunctionare.getText().toString().equals("0")){
            etPutere.setError(null);
            Toast.makeText(this, "Selectează timpul de functionare", Toast.LENGTH_SHORT).show();
        }else if(!btnDela.getText().toString().equals("Selecteaza")&& btnPanala.getText().toString().equals("Selecteaza") &&
                tvOreFunctionare.getText().toString().equals("0")&& tvMinFunctionare.getText().toString().equals("0")){
            etPutere.setError(null);
            Toast.makeText(this, "Selectează pana la ce ora functioneaza", Toast.LENGTH_SHORT).show();
        } else if(Integer.parseInt(tvOreFunctionare.getText().toString())==24 && Integer.parseInt(tvMinFunctionare.getText().toString())>0){
            etPutere.setError(null);
            Toast.makeText(this, "Formatul timpului de functionare invalid", Toast.LENGTH_SHORT).show();
            tvMinFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.error_stroke_color));
        } else if (Integer.parseInt(tvOreFunctionare.getText().toString())>24) {
            etPutere.setError(null);
            tvOreFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.error_stroke_color));
            tvMinFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.myGreen));
            Toast.makeText(this, "Formatul timpului de functionare invalid", Toast.LENGTH_SHORT).show();
        }else if( Integer.parseInt(tvMinFunctionare.getText().toString())>60){
            tvOreFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.myGreen));
            tvMinFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.error_stroke_color));
            etPutere.setError(null);
        } else if (TextUtils.isEmpty(etDispozitiveIdentice.getText().toString().trim()) ||
                Integer.parseInt(etDispozitiveIdentice.getText().toString().trim()) == 0) {
            tvOreFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.myGreen));
            tvMinFunctionare.setTextColor(ContextCompat.getColor(AdaugaDispozitivActivity.this, R.color.myGreen));
             etDispozitiveIdentice.setError("Completează");
            Toast.makeText(this, "Trebuie să aveți minim un dispozitiv", Toast.LENGTH_SHORT).show();
        } else {
             etDispozitiveIdentice.setError(null);
            return true;
        }
        return false;
    }

}