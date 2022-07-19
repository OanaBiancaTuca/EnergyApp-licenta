package com.example.energyapp.activities;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.developer.kalert.KAlertDialog;
import com.example.energyapp.R;
import com.example.energyapp.adaptors.IncapereListAdapter;
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

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.dd4you.animatoo.Animatoo;


public class IncaperiUtilizatorActivity extends AppCompatActivity {
    public static final String INCAPERE_OBIECT = "incapere";
    DatabaseReference databaseIncaperi;
    SwipeMenuListView listViewIncaperi;
    List<Incapere> incaperiList;
    ImageButton imgButtonAdd, imgButtonBack;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid;
    ValueEventListener valueEventListenerIncaperiUtilizator;
    IncapereListAdapter adapter;
    TextView tvConsum;
    DecimalFormat formatter = new DecimalFormat("##.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incaperi_utilizator);
        //pt a prelua id-ul utilizatorului curent
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        uid = firebaseUser.getUid();
        databaseIncaperi = FirebaseDatabase.getInstance().getReference("incaperi").child(uid);
        initComponents();
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        //initialAnimations();
        // citim incaperile din bd
        databaseIncaperi.addValueEventListener(valueEventListenerIncaperiUtilizator = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incaperiList.clear();
                float consumTotalDispozitive = 0.0F;
                for (DataSnapshot incapereSnapshot : snapshot.getChildren()) {
                    Incapere incapere = incapereSnapshot.getValue(Incapere.class);
                    incaperiList.add(incapere);
                    if(incapere.getConsumTotal()!=null&& !incapere.getConsumTotal().isEmpty())
                    consumTotalDispozitive += Float.parseFloat(incapere.getConsumTotal());
                }

                adapter = new IncapereListAdapter(IncaperiUtilizatorActivity.this, incaperiList);
                listViewIncaperi.setAdapter(adapter);
                listViewIncaperi.setClickable(true);
                tvConsum.setText(formatter.format(consumTotalDispozitive)+" kWh/lunar");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //back button
        imgButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                Animatoo.animateDiagonal(IncaperiUtilizatorActivity.this);

            }
        });
        //adauga incapere
        imgButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IncaperiUtilizatorActivity.this, AdaugaIncapereActivity.class));
                //animatie pt comutare intre activitati
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            }
        });
        //deschide incaperea
        listViewIncaperi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Incapere incapere = incaperiList.get(position);
                Intent intent = new Intent(getApplicationContext(), DispozitiveIncapereActivity.class);
                intent.putExtra(INCAPERE_OBIECT, incapere);
                startActivity(intent);
                Animatoo.animateSlideUp(IncaperiUtilizatorActivity.this);
            }
        });
        //editare incapere
        listViewIncaperi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Incapere incapere = incaperiList.get(position);
                showUpdateDialog(incapere.getIncapereId(), incapere.getDenumire(), incapere.getTipIncapere(),
                        incapere.getNumardispozitive(), incapere.getConsumTotal(), position);
                adapter.notifyDataSetChanged();

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
                deleteItem.setBackground(new ColorDrawable(Color.rgb(232,
                        0, 0)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listViewIncaperi.setMenuCreator(creator);
        listViewIncaperi.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                new KAlertDialog(IncaperiUtilizatorActivity.this, KAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Ștergeți încăperea?")
                        .setCustomImage(android.R.drawable.ic_delete, IncaperiUtilizatorActivity.this)
                        // .setContentText("Vrei să ștergi aceast dispozitiv?")
                        .setTitleTextSize(20)
                        .setContentTextSize(20)
                        .setConfirmText("DA")
                        .setCancelText("NU")
                        .confirmButtonColor(R.color.myGreen, IncaperiUtilizatorActivity.this)
                        .cancelButtonColor(R.color.red_btn_bg_color, IncaperiUtilizatorActivity.this)
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                Incapere incapere = incaperiList.get(position);
                                //stergem incaperea din bd si dispozitivele aferente
                                deleteIncapere(incapere.getIncapereId());
                                //stergem din lista locala
                                incaperiList.remove(incapere);
                                //notificam adapterul
                                adapter.notifyDataSetChanged();
                                sDialog
                                        .setTitleText("Încăpere ștearsă")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(KAlertDialog.SUCCESS_TYPE);


                            }
                        })
                        .show();
                return false;
            }
        });

    }

    private void deleteIncapere(String incapereId) {
        //sterg incaperea din baza de date
        FirebaseDatabase.getInstance().getReference("incaperi").child(uid).child(incapereId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //sterg toate dispozitivele aferente
        FirebaseDatabase.getInstance().getReference("dispozitive").child(uid).child(incapereId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initComponents() {
        imgButtonAdd = findViewById(R.id.imgBtnAdd_listViewIncaperi);
        imgButtonBack = findViewById(R.id.btnBack_listViewIncaperi);
        listViewIncaperi = findViewById(R.id.listView_listViewIncaperi);
        incaperiList = new ArrayList<>();
        tvConsum = findViewById(R.id.tv_consum_listViewIncaperi);
    }

    //atunci cand vreau sa updatez o incapere
    private void showUpdateDialog(String incapereId, String denumire, String tipIncapere, int numarDispozitive, String consumTotal, int position) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.updateincapere_dialog, null);
        dialogBuilder.setView(dialogView);
        //preluam editTextul si butonul din dialog
        final EditText etDenumire = (EditText) dialogView.findViewById(R.id.etIncapere_editeazaIncapere);
        final Spinner spnTip = (Spinner) dialogView.findViewById(R.id.spnTipIncapere_editeazaIncapere);
        final Button buttonSave = (Button) dialogView.findViewById(R.id.btnSalveaza_editeazaIncapere);
        spnTip.setEnabled(false);
        dialogBuilder.setTitle("Modifică denumirea încăperii");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        etDenumire.setText(denumire);

        //pt a seta spinnerul pe itemul corect
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipIncapere, android.R.layout.simple_spinner_item);
        int spinnerPosition = adapter.getPosition(tipIncapere);
        spnTip.setSelection(spinnerPosition);

        //ce se intampla cand apas pe butonul salveaza
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KAlertDialog(IncaperiUtilizatorActivity.this, KAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Salvați?")
                        .setCustomImage(android.R.drawable.ic_menu_edit, IncaperiUtilizatorActivity.this)
                        // .setContentText("Vrei să ștergi aceast dispozitiv?")
                        .setTitleTextSize(20)
                        .setContentTextSize(20)
                        .setConfirmText("DA")
                        .setCancelText("NU")
                        .confirmButtonColor(R.color.myGreen, IncaperiUtilizatorActivity.this)
                        .cancelButtonColor(R.color.red_btn_bg_color, IncaperiUtilizatorActivity.this)
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                String denumireNoua = etDenumire.getText().toString().trim();
                                if (verificDatele(denumireNoua)) {
                                    etDenumire.setError(null);
                                    updateIncapere(incapereId, denumireNoua, tipIncapere, consumTotal, numarDispozitive, uid, position);
                                    alertDialog.dismiss();
                                    sDialog
                                            .setTitleText("Încăpere actualizată!")
                                            .setContentText("")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(null)
                                            .changeAlertType(KAlertDialog.SUCCESS_TYPE);

                                } else {
                                    etDenumire.setError("Denumire cerută");
                                }


                            }
                        })
                        .show();

            }
        });

    }

    //updatare in bd
    private void updateIncapere(String id, String denumire, String tip, String consumTotal, int numarDispozitive, String uid, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("incaperi").child(uid).child(id);
        Incapere incapereUpdatata = new Incapere(id, denumire, tip, consumTotal, numarDispozitive, uid);
        databaseReference.setValue(incapereUpdatata);
        //updatam local
        incaperiList.set(position, incapereUpdatata);
        Toast.makeText(this, "Încăpere actualizată!", Toast.LENGTH_SHORT).show();
    }

    private boolean verificDatele(String denumire) {
        return !TextUtils.isEmpty(denumire);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseIncaperi.removeEventListener(valueEventListenerIncaperiUtilizator);
    }

    private void initialAnimations() {
        View toolbar;
        GridLayout gridLayout;
        Animation fromBottomAnimationFaster;
        Animation fromBottomAnimation;
        toolbar = findViewById(R.id.toolbarRl_incaperiUtilizator);
        fromBottomAnimationFaster = AnimationUtils.loadAnimation(IncaperiUtilizatorActivity.this, R.anim.from_bottom_faster);
        fromBottomAnimation = AnimationUtils.loadAnimation(IncaperiUtilizatorActivity.this, R.anim.from_bottom);
        toolbar.startAnimation(fromBottomAnimationFaster);
        //transBackground.animate().translationY(-1250).setDuration(500);
        listViewIncaperi.startAnimation(fromBottomAnimation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //verificam daca are costul setat si ii afisam cookie altfel
        if (uid != null) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("utilizatori").child(uid).child("costKWh");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!(snapshot.getValue(String.class).isEmpty())) {
                        float cost = Float.parseFloat(snapshot.getValue(String.class));
                    }else{
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                afiseazaCookie();

                            }
                        }, 5500);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

    }
public void afiseazaCookie(){
    CookieBar.build(IncaperiUtilizatorActivity.this)
            .setAction("SETEAZĂ ACUM", new OnActionClickListener() {
                @Override
                public void onClick() {
                    finish();
                    startActivity( new Intent(IncaperiUtilizatorActivity.this,SeteazaCostFacturaActivity.class));
                    Animatoo.animateDiagonal(IncaperiUtilizatorActivity.this);
                    CookieBar.dismiss(IncaperiUtilizatorActivity.this);
                }
            })
            .setBackgroundColor(R.color.colorAccent)
            .setTitleColor(R.color.white)
            .setMessageColor(R.color.white)
            .setActionColor(R.color.white)
            .setTitle("Atenție!")
            .setMessage("Nu ați setat prețul de achiziție pentru kWh")
            .setEnableAutoDismiss(true) // Cookie will stay on display until manually dismissed
            .setSwipeToDismiss(true)    // Deny dismiss by swiping off the view
            .setCookiePosition(CookieBar.BOTTOM)
            .setAnimationIn(android.R.anim.slide_in_left, android.R.anim.slide_in_left)
            .setAnimationOut(android.R.anim.slide_out_right, android.R.anim.slide_out_right)
            .setDuration(15000)
            .show();

}




}