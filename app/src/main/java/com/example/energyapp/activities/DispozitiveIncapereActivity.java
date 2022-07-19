package com.example.energyapp.activities;

import static android.view.View.VISIBLE;
import static com.example.energyapp.activities.AdaugaDispozitivActivity.minuteDiferenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.developer.kalert.KAlertDialog;
import com.example.energyapp.R;
import com.example.energyapp.adaptors.DispozitiveListAdapter;
import com.example.energyapp.classes.Dispozitiv;
import com.example.energyapp.classes.Incapere;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.dd4you.animatoo.Animatoo;


public class DispozitiveIncapereActivity extends AppCompatActivity {
    ImageButton imgButtonBack;
    ImageButton fltAddDispozitiv;
    SwipeMenuListView listViewDispozitiveIncapere;
    TextView tv, tvConsum;
    List<Dispozitiv> dispozitive;
    //in intent pastrez incaperea pentru care vreau sa afiz=sez lista de dispozitive
    Incapere incapere;
    Intent intent;
    DatabaseReference databaseDispozitive;
    DatabaseReference databaseIncapereActuala;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid;//uid al userului curent
    String incapereId;//id-ul incaperii pentru care am deschis activitatea
    int numarTotalDispozitive = 0;
    float consumTotalDispozitive = 0.0f;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    int ora = 8, minutul;
    ValueEventListener valueEventListenerCitireDispozitive;
    DispozitiveListAdapter dispozitiveAdapter;
    DecimalFormat formatter = new DecimalFormat("##.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispozitive_incapere);
        //pt userul curent
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        uid = firebaseUser.getUid();
        initComponent();
        //initialAnimations();
        dispozitive = new ArrayList<>();
        incapereId = incapere.getIncapereId();
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));


        //setez numele incaperii atunci cand ajung pe pagina dizpozitivelor
        tv.setText(incapere.getDenumire());

        //referinta a bazei de date care ma intereseaza
        databaseDispozitive = FirebaseDatabase.getInstance().getReference("dispozitive").child(uid).child(incapere.getIncapereId());
        //citesc din bd
        databaseDispozitive.addValueEventListener(valueEventListenerCitireDispozitive = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dispozitive.clear();
                numarTotalDispozitive = 0;
                consumTotalDispozitive = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Dispozitiv dispozitiv = dataSnapshot.getValue(Dispozitiv.class);
                    dispozitive.add(dispozitiv);
                }
                dispozitiveAdapter = new DispozitiveListAdapter(DispozitiveIncapereActivity.this, dispozitive);
                listViewDispozitiveIncapere.setAdapter(dispozitiveAdapter);

                //actualizez pentru incaperea curenta numarul de dispozitive,consumul total

                for (Dispozitiv dispozitiv : dispozitive) {
                    //pt nr total de dispozitive
                    numarTotalDispozitive = numarTotalDispozitive + dispozitiv.getNumarDispozitive();
                    consumTotalDispozitive = consumTotalDispozitive + Float.parseFloat(dispozitiv.getConsumDispozitiv());

                }

                incapere.setNumardispozitive(numarTotalDispozitive);
                incapere.setConsumTotal(String.valueOf(consumTotalDispozitive));
                //scriu coinsumul incaperii
                tvConsum.setText(formatter.format(consumTotalDispozitive)+" kWh/lunar");
                //preiau incaperea din baza de date si o actualizez
                databaseIncapereActuala = FirebaseDatabase.getInstance().getReference("incaperi").child(uid).child(incapere.getIncapereId());
                databaseIncapereActuala.setValue(incapere);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imgButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateSlideDown(DispozitiveIncapereActivity.this);


            }
        });
        imgButtonBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(DispozitiveIncapereActivity.this,DashboardActivity.class));
                finish();
                Animatoo.animateDiagonal(DispozitiveIncapereActivity.this);
                return  true;
            }
        });


        //adaugare dispozitiv
        fltAddDispozitiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), AdaugaDispozitivActivity.class);
                intent1.putExtra(IncaperiUtilizatorActivity.INCAPERE_OBIECT, incapere);
                startActivity(intent1);
                //animatie pt comutare intre activitati
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        //vizualizare detalii dispozitiv curent
        listViewDispozitiveIncapere.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dispozitiv dispozitiv = dispozitive.get(position);
                arataDetalii(view, dispozitiv);

            }
        });
        //editare detalii dispozitiv curent
        listViewDispozitiveIncapere.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Dispozitiv dispozitiv = dispozitive.get(position);
                showUpdateDialog(incapere.getIncapereId(), dispozitiv);
                return true;
            }
        });

        //biblioteca externa ptr slide si delete pe listview
        //se creaza menu ptr fiecare item din listview
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(202,
                        0, 0)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        //pt stergearea unui dispozitiv
        listViewDispozitiveIncapere.setMenuCreator(creator);
        listViewDispozitiveIncapere.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                new KAlertDialog(DispozitiveIncapereActivity.this, KAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Ștergeți acest dispozitiv?")
                        .setCustomImage(android.R.drawable.ic_delete,DispozitiveIncapereActivity.this)
                       // .setContentText("Vrei să ștergi aceast dispozitiv?")
                        .setTitleTextSize(20)
                        .setContentTextSize(20)
                        .setConfirmText("DA")
                        .setCancelText("NU")
                        .confirmButtonColor(R.color.myGreen,DispozitiveIncapereActivity.this)
                        .cancelButtonColor(R.color.red_btn_bg_color,DispozitiveIncapereActivity.this)
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                Dispozitiv dispozitiv = dispozitive.get(position);
                                deleteDispozitiv(dispozitiv.getDispozitivId(), incapere.getIncapereId(), dispozitiv.getNumarDispozitive());
                                float consum = Float.parseFloat(dispozitiv.getConsumDispozitiv());
                                String consumFormatat = formatter.format(consum);

                                sDialog
                                        .setTitleText("Dispozitiv șters")
                                        .setContentText("Ați economisit " +consumFormatat + " kWh/lunar prin renunțarea la acest dispozitiv")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(KAlertDialog.SUCCESS_TYPE);


                            }
                        })
                        .show();





               return true;
           }
        });

    }

    public void arataDetalii(View view, Dispozitiv dispozitiv) {
        Dialog dialog = new Dialog(DispozitiveIncapereActivity.this);
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

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) DispozitiveIncapereActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                String informations = "Tip: " + tvTip.getText() + " Clasa: " + tvClasa.getText() + " Putere: " + tvPutere.getText();
                ClipData clip = ClipData.newPlainText("informații dispozitiv", informations);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), informations, Toast.LENGTH_SHORT).show();

            }
        });

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
                    lZilnic.setVisibility(View.VISIBLE);
                    lLunar.setVisibility(View.VISIBLE);
                    lAnual.setVisibility(View.VISIBLE);
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
        tvTip.setText(" " + dispozitiv.getTipDispozitiv());
        tvClasa.setText(" " + dispozitiv.getClasaDispozitiv());
        tvPutere.setText(formatter.format(calculezPuteredispozitiv(dispozitiv)) + " W");

        dialog.show();
    }

    private void showUpdateDialog(String incapereId, Dispozitiv dispozitiv) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.updatedispozitiv_dialog, null);
        dialogBuilder.setView(dialogView);
        final TextView tvnrOreFunctionare = (TextView) dialogView.findViewById(R.id.tvNrOreFunctionare_editeazaDispozitiv);
        final TextView tvnrMinFunctionare = (TextView) dialogView.findViewById(R.id.tvNrMinFunctionare_editeazaDispozitiv);
        Button btnPanala = (Button) dialogView.findViewById(R.id.btnPanala_editeazaDispozitiv);
        Button btnDela = (Button) dialogView.findViewById(R.id.btnDela_editeazaDispozitiv);
        final EditText etNrdispozitiveIdentice = (EditText) dialogView.findViewById(R.id.etNrDispozitiveIdentice_editeazaDispozitiv);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.rgModFunctionare_editeazaDispozitiv);
        final RadioButton rbZilnic = (RadioButton) dialogView.findViewById(R.id.rbtZilnic_editeazaDispozitiv);
        final RadioButton rbSaptamanal = (RadioButton) dialogView.findViewById(R.id.rbtSaptamanal_editeazaDispozitiv);
        final Button buttonSave = (Button) dialogView.findViewById(R.id.btnSalveaza_editeazaDispozitiv);
        final Switch switchFunctionare = (Switch) dialogView.findViewById(R.id.switch_editeazaDispozitiv);
        final TextView tvDela = (TextView) dialogView.findViewById(R.id.tvDela_editeazaDispozitiv);
        final TextView tvPanala = (TextView) dialogView.findViewById(R.id.tvPanala_editeazaDispozitiv);

        EditText editTextOre = new EditText(DispozitiveIncapereActivity.this);
        EditText editTextMin = new EditText(DispozitiveIncapereActivity.this);

        dialogBuilder.setTitle("Modificați modul de funcționare");

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        //setez detaliile dispozitivului care pot fi editate
        int minute_total = dispozitiv.getMinuteFunctionareZilnic();
        int oreFunctionare = minute_total / 60;
        int minuteFunctionare = minute_total - oreFunctionare * 60;
        tvnrOreFunctionare.setText(String.valueOf(oreFunctionare));
        tvnrMinFunctionare.setText(String.valueOf(minuteFunctionare));
        btnDela.setText(dispozitiv.getDelaora());
        btnPanala.setText(dispozitiv.getPanalaora());
        etNrdispozitiveIdentice.setText(String.valueOf(dispozitiv.getNumarDispozitive()));
        if (dispozitiv.getModfunctionare().equals("Saptamanal")) {
            rbSaptamanal.setChecked(true);
        } else {
            rbZilnic.setChecked(true);
        }
        btnDela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker(v, dialogView);
            }
        });
        btnPanala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker(v, dialogView);
            }
        });
        //cand apas pe switch
        switchFunctionare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnDela.setVisibility(VISIBLE);
                    btnPanala.setVisibility(VISIBLE);
                    tvDela.setVisibility(VISIBLE);
                    tvPanala.setVisibility(VISIBLE);
                    tvnrOreFunctionare.setTextColor(ContextCompat.getColor(DispozitiveIncapereActivity.this, R.color.gray02));
                    tvnrMinFunctionare.setTextColor(ContextCompat.getColor(DispozitiveIncapereActivity.this, R.color.gray02));
                    tvnrOreFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._15sdp));
                    tvnrMinFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._15sdp));
                } else {
                    btnDela.setVisibility(View.GONE);
                    btnPanala.setVisibility(View.GONE);
                    tvDela.setVisibility(View.GONE);
                    tvPanala.setVisibility(View.GONE);
                    tvnrOreFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._20sdp));
                    tvnrMinFunctionare.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen._20sdp));
                    tvnrOreFunctionare.setTextColor(ContextCompat.getColor(DispozitiveIncapereActivity.this, R.color.myGreen));
                    tvnrMinFunctionare.setTextColor(ContextCompat.getColor(DispozitiveIncapereActivity.this, R.color.myGreen));
                }
            }
        });

        //atunci cand vreau sa introduc direct nr de ore
        android.app.AlertDialog dialogOre = new android.app.AlertDialog.Builder(DispozitiveIncapereActivity.this).create();
        editTextOre.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextOre.setPadding(20,20,16,16);
        editTextOre.setGravity(Gravity.CENTER);
        editTextOre.setBackgroundDrawable(ContextCompat.getDrawable(DispozitiveIncapereActivity.this, R.drawable.shape_edittext01));
        dialogOre.getWindow().setLayout(600, 400);
        dialogOre.setTitle("Introduceți numărul de ore"+"\n");
        dialogOre.setIcon(R.drawable.ic_baseline_access_time_24);
        dialogOre.setView(editTextOre);
        dialogOre.setButton(DialogInterface.BUTTON_POSITIVE, "Salvați", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvnrOreFunctionare.setText(editTextOre.getText());
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
        tvnrOreFunctionare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOre.show();

            }
        });
        //atunci cand vreau sa introduc direct nr de minute
        android.app.AlertDialog dialogMin = new android.app.AlertDialog.Builder(DispozitiveIncapereActivity.this).create();
        editTextMin.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextMin.setWidth(15);
        dialogMin.setTitle("Introduceți numărul de minute"+"\n");
        editTextMin.setPadding(20,20,16,16);
        editTextMin.setGravity(Gravity.CENTER);
        editTextMin.setBackgroundDrawable(ContextCompat.getDrawable(DispozitiveIncapereActivity.this, R.drawable.shape_edittext01));
        dialogMin.getWindow().setLayout(600, 400);
        dialogMin.setIcon(R.drawable.ic_baseline_access_time_24);
        dialogMin.setView(editTextMin);
        dialogMin.setButton(DialogInterface.BUTTON_POSITIVE, "Salvați", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvnrMinFunctionare.setText(editTextMin.getText());
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
        tvnrMinFunctionare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMin.show();
            }
        });






        //ce se intampla cand apas pe butonul salveaza
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new KAlertDialog(DispozitiveIncapereActivity.this, KAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Salvați modificările?")
                        .setCustomImage(android.R.drawable.ic_menu_edit,DispozitiveIncapereActivity.this)
                        // .setContentText("Vrei să ștergi aceast dispozitiv?")
                        .setTitleTextSize(20)
                        .setContentTextSize(20)
                        .setConfirmText("DA")
                        .setCancelText("NU")
                        .confirmButtonColor(R.color.myGreen,DispozitiveIncapereActivity.this)
                        .cancelButtonColor(R.color.red_btn_bg_color,DispozitiveIncapereActivity.this)
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                int numarNouDispozitive = 0;
                                //preiau datele completate dupa updatare
                                if (TextUtils.isEmpty(etNrdispozitiveIdentice.getText().toString().trim())) {
                                    Toast.makeText(DispozitiveIncapereActivity.this, "Introduceți numarul de dispozitive", Toast.LENGTH_SHORT).show();
                                } else {

                                    numarNouDispozitive = Integer.parseInt(etNrdispozitiveIdentice.getText().toString().trim());
                                }
                                String delaOraNou = btnDela.getText().toString().trim();
                                String panalaOranou = btnPanala.getText().toString().trim();
                                String modFunctionareNou = "";
                                int modFunctionareSelectat = radioGroup.getCheckedRadioButtonId();
                                if (modFunctionareSelectat == R.id.rbtZilnic_editeazaDispozitiv) {
                                    modFunctionareNou = "Zilnic";
                                } else if (modFunctionareSelectat == R.id.rbtSaptamanal_editeazaDispozitiv) {
                                    modFunctionareNou = "Saptamanal";

                                }
                                Float oreFunctionare = Float.parseFloat(tvnrOreFunctionare.getText().toString());
                                Float minFunctionare = Float.parseFloat(tvnrMinFunctionare.getText().toString());
                                if (verificDatele(etNrdispozitiveIdentice, numarNouDispozitive, delaOraNou, panalaOranou, oreFunctionare, minFunctionare)) {
                                    try {
                                        Dispozitiv dispozitivNou = updateDispozitiv(incapereId, dispozitiv, numarNouDispozitive, delaOraNou, panalaOranou, oreFunctionare, minFunctionare, modFunctionareNou);
                                        alertDialog.dismiss();
                                        //dupa ce se actualizeaza corect datele vreau sa fac o comparatie cu modul de folosire anterior
                                        //ii facem o fereastra noua care ii apare pe ecran
                                        showAfterUpdateDialog(dispozitiv, dispozitivNou);
                                        sDialog.setCancelable(true);
                                        sDialog.dismiss();


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(dialogView.getContext(), "Completați toate campurile!", Toast.LENGTH_SHORT).show();
                                }

                            }


                        }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        alertDialog.dismiss();
                    }
                })
                        .show();

            }
        });


    }

    private void showAfterUpdateDialog(Dispozitiv dispozitiv, Dispozitiv dispozitivUpdatat) {
        String message = "";
        int icon = R.drawable.ic_money;
        float consumVechi = Float.parseFloat(dispozitiv.getConsumDispozitiv());
        float consumNou = Float.parseFloat(dispozitivUpdatat.getConsumDispozitiv());
        if (consumNou < consumVechi) {
            float consum =  consumVechi - consumNou;
            String consumFormatat = formatter.format(consum);
            message = "<p>Felicitări!     </p> <br><p> Prin modificările aduse veți economisi " + consumFormatat + " kWh/lunar </p>";
            icon = R.drawable.ic_money;
        } else if (consumNou > consumVechi) {
            float consum =  consumNou - consumVechi;
            String consumFormatat = formatter.format(consum);
            message = "<p>Prin modificarile aduse ați crescut consumul cu " + consumFormatat + " kWh/lunar.</p>" + "\n" +
                    "<br><p>Gestionați cu atenție modul de funcționare al dispozitivului!</p>";
            icon = R.drawable.ic_error;
        } else {
            message = " Nu ați adus modificări! ";
        }

        KAlertDialog pDialog = new KAlertDialog(DispozitiveIncapereActivity.this, KAlertDialog.CUSTOM_IMAGE_TYPE);
        pDialog.setTitleTextSize(20);
        pDialog.setContentTextSize(20);
        pDialog.setCancelable(true);
        pDialog.setTitleText("Detalii pentru noul consum")
                .setContentText(message)
                .setCustomImage(icon,DispozitiveIncapereActivity.this)
                .setConfirmText("OK")
                .show();
    }

    private Dispozitiv updateDispozitiv(String incapereId, Dispozitiv dispozitiv, int numarNouDispozitive, String delaOraNou, String panalaOranou, Float oreFunctionare, Float minFunctionare, String modFunctionareNou) throws ParseException {
        DatabaseReference databaseReferenceUpdate = databaseDispozitive.child(dispozitiv.getDispozitivId());
        float putere = calculezPuteredispozitiv(dispozitiv);
        Dispozitiv dispozitivUpdatat = new Dispozitiv();
        dispozitivUpdatat.setDispozitivId(dispozitiv.getDispozitivId());
        dispozitivUpdatat.setUid(dispozitiv.getUid());
        dispozitivUpdatat.setTipDispozitiv(dispozitiv.getTipDispozitiv());
        dispozitivUpdatat.setClasaDispozitiv(dispozitiv.getClasaDispozitiv());
        dispozitivUpdatat.setNumarDispozitive(numarNouDispozitive);
        dispozitivUpdatat.setModfunctionare(modFunctionareNou);
        dispozitivUpdatat.setDelaora(delaOraNou);
        dispozitivUpdatat.setPanalaora(panalaOranou);
        if (!delaOraNou.equals("Selecteaza") && !panalaOranou.equals("Selecteaza")) {
            Date startDate = null;
            startDate = simpleDateFormat.parse(delaOraNou);
            Date endDate = simpleDateFormat.parse(panalaOranou);
            dispozitivUpdatat.setMinuteFunctionareZilnic(minuteDiferenta(startDate, endDate));
        } else {
            dispozitivUpdatat.setMinuteFunctionareZilnic((int) (oreFunctionare * 60 + minFunctionare));
        }
        float consumDispozitiv = calculezConsumdispozitiv(putere, dispozitivUpdatat);
        dispozitivUpdatat.setConsumDispozitiv(String.valueOf(consumDispozitiv));
        databaseReferenceUpdate.setValue(dispozitivUpdatat);
        Toast.makeText(this, "Dispozitiv actualizat!", Toast.LENGTH_SHORT).show();
        return dispozitivUpdatat;

    }

    private float calculezConsumdispozitiv(float putere, Dispozitiv dispozitiv) {
        float consum = 0.000f;
        if (dispozitiv.getModfunctionare().equals("Zilnic")) {
            consum = (putere * ((float) dispozitiv.getMinuteFunctionareZilnic() / 60) * 30 / 1000) * dispozitiv.getNumarDispozitive();

        } else if (dispozitiv.getModfunctionare().equals("Saptamanal")) {
            consum = (putere * ((float) (dispozitiv.getMinuteFunctionareZilnic()) / 420) * 30 / 1000) * dispozitiv.getNumarDispozitive();
        }
        return consum;
    }

    public static float calculezPuteredispozitiv(Dispozitiv dispozitiv) {
        float putere = 0.000f;
        if (dispozitiv.getModfunctionare().equals("Zilnic")) {
            float minute = ((float) dispozitiv.getMinuteFunctionareZilnic()) / 60;
            putere = ((Float.parseFloat(dispozitiv.getConsumDispozitiv()) / dispozitiv.getNumarDispozitive()) * 1000 / 30) / minute;
        } else if (dispozitiv.getModfunctionare().equals("Saptamanal")) {
            float minute = ((float) dispozitiv.getMinuteFunctionareZilnic()) / 420;
            putere = ((Float.parseFloat(dispozitiv.getConsumDispozitiv()) / dispozitiv.getNumarDispozitive()) * 1000 / 30) / minute;
        }
        return putere;

    }

    private boolean verificDatele(EditText editText, int numarNouDispozitive, String delaOraNou, String panalaOranou, Float oreFunctionare, Float minFunctionare) {
        if (!delaOraNou.equals("Selecteaza")) {
            if (!panalaOranou.equals("Selecteaza")) {
                if (numarNouDispozitive <= 0 || TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Toast.makeText(this, "Completează numarul de dispozitive!", Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    return true;
                }
            } else {
                Toast.makeText(this, "Completează ora de oprire!", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else if (oreFunctionare!=0.0 ||minFunctionare !=0.0) {
            if(oreFunctionare ==24 && minFunctionare> 0)
                return false;
            else if (oreFunctionare> 24 && minFunctionare>60)
                return  false;
            else return true;
        }else{
            return false;
        }
    }

    private void deleteDispozitiv(String dispozitivId, String incapereId, int numarDispozitive) {
        DatabaseReference dbReferenceDispozitivForDelete = databaseDispozitive.child(dispozitivId);
        dbReferenceDispozitivForDelete.removeValue();
        Toast.makeText(DispozitiveIncapereActivity.this, "Dispozitivul este șters", Toast.LENGTH_SHORT).show();
        //updatez numarul de dispozitive al incaperii
        numarTotalDispozitive = 0;
        int numarNou = incapere.getNumardispozitive() - numarDispozitive;
        incapere.setNumardispozitive(numarNou);
    }


    private void initComponent() {
        imgButtonBack = findViewById(R.id.btnBack_listViewDispozitive);
        fltAddDispozitiv = findViewById(R.id.fltBtn_listViewDispozitive);
        listViewDispozitiveIncapere = findViewById(R.id.listView_listViewDispozitive);
        tv = findViewById(R.id.tv_incaperi_listViewDispozitive);
        intent = getIntent();
        incapere = intent.getParcelableExtra(IncaperiUtilizatorActivity.INCAPERE_OBIECT);
        tvConsum = findViewById(R.id.tv_consum_listViewDispozitive);

    }


    public void popTimePicker(View viewButton, View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                try {
                    Date startDate, endDate;
                    ora = selectedHour;
                    minutul = selectedMinute;
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    TextView tvOreFunctionare = (TextView) view.findViewById(R.id.tvNrOreFunctionare_editeazaDispozitiv);
                    TextView tvMinFunctionare = (TextView) view.findViewById(R.id.tvNrMinFunctionare_editeazaDispozitiv);
                    Button btnPanala = (Button) view.findViewById(R.id.btnPanala_editeazaDispozitiv);
                    Button btnDela = (Button) view.findViewById(R.id.btnDela_editeazaDispozitiv);

                    if (viewButton.getId() == R.id.btnDela_editeazaDispozitiv) {
                        btnDela.setText(String.format(Locale.getDefault(), "%02d:%02d", ora, minutul));
                    } else if (viewButton.getId() == R.id.btnPanala_editeazaDispozitiv) {
                        btnPanala.setText(String.format(Locale.getDefault(), "%02d:%02d", ora, minutul));
                    }

                    startDate = simpleDateFormat.parse(btnDela.getText().toString());
                    endDate = simpleDateFormat.parse(btnPanala.getText().toString());
                    if (startDate.equals(endDate)) {
                        tvOreFunctionare.setText("24");
                        tvMinFunctionare.setText("0");
                    } else {
                        int ore = minuteDiferenta(startDate, endDate) / 60;
                        int minute = minuteDiferenta(startDate, endDate) - ore * 60;
                        String diferenta = ore + " ore și " + minute + " minute ";
                        tvOreFunctionare.setText(String.valueOf(ore));
                        tvMinFunctionare.setText(String.valueOf(minute));
                        tvOreFunctionare.setVisibility(View.VISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        };
        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), style, onTimeSetListener, ora, minutul, true);
        timePickerDialog.setTitle("Selectează ora");
        timePickerDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseDispozitive.removeEventListener(valueEventListenerCitireDispozitive);
    }


}