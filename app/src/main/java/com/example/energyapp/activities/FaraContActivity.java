package com.example.energyapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energyapp.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FaraContActivity extends AppCompatActivity {

    EditText etNume,etNumarDispozitive, etPutere, etTimp, etCostkWh;
    TextView tvConsumCalculat, tvCostCalculat,tvConsumCalculatZilnic, tvCostCalculatZilnic, tvConsumCalculatAnual, tvCostCalculatAnual;
    ImageButton btnBack;
    Button btnCalculeaza, btnReseteaza;
    CardView cardViewLunar, cardViewZilnic, cardViewAnual;
    DecimalFormat formatter = new DecimalFormat("##.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fara_cont);
        initComponents();
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        //buton inapoi
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //animatie pt comutare intre activitati
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });
        btnCalculeaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificaDateComplete()){
                    int numarDispozitive = Integer.parseInt(etNumarDispozitive.getText().toString());
                    float putere = Float.parseFloat(etPutere.getText().toString());
                    int oreUtilizare = Integer.parseInt(etTimp.getText().toString());
                    float cost = Float.parseFloat(etCostkWh.getText().toString());
                    float consumCalculat = 0.0f;
                    float costCalculat = 0.0f;
                    //calculez consumul si costul zilnic
                    consumCalculat= ((putere * oreUtilizare)/1000) *numarDispozitive;
                    cardViewZilnic.setVisibility(View.VISIBLE);
                    tvConsumCalculatZilnic.setText(formatter.format(consumCalculat)+" kWh");
                    costCalculat = consumCalculat * cost;
                    tvCostCalculatZilnic.setText(formatter.format(costCalculat)+" lei");

                    //calculez consumul si costul lunar

                    consumCalculat= ((putere * oreUtilizare)/1000 *30) *numarDispozitive;
                    cardViewLunar.setVisibility(View.VISIBLE);
                    tvConsumCalculat.setText(formatter.format(consumCalculat)+" kWh");
                    costCalculat = consumCalculat * cost;
                    tvCostCalculat.setText(formatter.format(costCalculat)+" lei");
                    //calculez consumul si costul anual

                    consumCalculat= ((putere * oreUtilizare)/1000 *30)*12 *numarDispozitive;
                    cardViewAnual.setVisibility(View.VISIBLE);
                    tvConsumCalculatAnual.setText(formatter.format(consumCalculat)+" kWh");
                    costCalculat = consumCalculat * cost;
                    tvCostCalculatAnual.setText(formatter.format(costCalculat)+" lei");


                }else {
                    Toast.makeText(FaraContActivity.this, "Nu ati completat toate campurile",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnReseteaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNume.setText(null);
                etNumarDispozitive.setText(null);
                etPutere.setText(null);
                etTimp.setText(null);
                etCostkWh.setText(null);
                cardViewZilnic.setVisibility(View.INVISIBLE);
                cardViewLunar.setVisibility(View.INVISIBLE);
                cardViewAnual.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean verificaDateComplete() {
        if( etNume.getText().toString()!=null && ! etNume.getText().toString().isEmpty()){
            etNume.setError(null);
//            if(!etCostkWh.getText().toString().isEmpty()&& Float.parseFloat(etCostkWh.getText().toString())>0){
//                etCostkWh.setError(null);
               if(!etNumarDispozitive.getText().toString().isEmpty()&&Integer.parseInt(etNumarDispozitive.getText().toString())>0){
                   etNumarDispozitive.setError(null);
                   if(!etPutere.getText().toString().isEmpty()&& Float.parseFloat(etPutere.getText().toString())>0){
                       etPutere.setError(null);
                       if(!etTimp.getText().toString().isEmpty() && Integer.parseInt(etTimp.getText().toString())>0) {
                           etTimp.setError(null);
                           if (!etCostkWh.getText().toString().isEmpty() && Float.parseFloat(etCostkWh.getText().toString()) > 0) {
                               etCostkWh.setError(null);
                               return true;
                           } else {
                               etCostkWh.setError("Completează");
                               return false;
                           }
                       } else {
                           etTimp.setError("Completează");
                           return false;
                       }
                   }else{
                       etPutere.setError("Completează");
                       return false;
                   }

               }else{
                   etNumarDispozitive.setError("Completează");
                   return  false;
               }
//            }else{
//                etCostkWh.setError("Completează");
//                return  false;
//            }

        }
        else {
            etNume.setError("Completează");
            return false;
        }
    }

    private void initComponents() {
        etNume = findViewById(R.id.etNume_faraContActivity);
        etCostkWh = findViewById(R.id.etCost_faraContActivity);
        etNumarDispozitive = findViewById(R.id.etNumar_faraContActivity);
        etPutere = findViewById(R.id.etPutere_faraContActivity);
        etTimp = findViewById(R.id.etUtilizare_faraContActivity);
        tvConsumCalculat = findViewById(R.id.tv_cosumCalculat_faraContActivity);
        tvCostCalculat = findViewById(R.id.tv_costCalculat_faraContActivity);
        btnBack = findViewById(R.id.btnBack_faraContActivity);
        btnCalculeaza = findViewById(R.id.btnCalculeaza_faraContActivity);
        cardViewLunar = findViewById(R.id.cardView_lunar_faraContActivity);
        tvConsumCalculatZilnic = findViewById(R.id.tv_cosumCalculatZilnic_faraContActivity);
        tvCostCalculatZilnic = findViewById(R.id.tv_costCalculatZilnic_faraContActivity);
        cardViewZilnic = findViewById(R.id.cardView_zilnic_faraContActivity);
        btnReseteaza = findViewById(R.id.btnReseteaza_faraContActivity);
        cardViewAnual = findViewById(R.id.cardView_anual_faraContActivity);
        tvConsumCalculatAnual = findViewById(R.id.tv_cosumCalculatAnual_faraContActivity);
        tvCostCalculatAnual = findViewById(R.id.tv_costCalculatAnual_faraContActivity);

    }
}