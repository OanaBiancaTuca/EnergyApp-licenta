package com.example.energyapp.adaptors;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.energyapp.R;
import com.example.energyapp.classes.Dispozitiv;
import com.example.energyapp.classes.MyCallback;
import com.example.energyapp.classes.MyUtilsClass;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

//custom adapter pentru dispozitiv
public class DispozitiveListAdapter extends BaseAdapter {
    private Activity context;
    private List<Dispozitiv> dispozitiveList;

    public DispozitiveListAdapter(Activity context, List<Dispozitiv> dispozitiveList) {
        this.context = context;
        this.dispozitiveList = dispozitiveList;
    }

    @Override
    public int getCount() {
        return dispozitiveList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TextView tv_denumire, tv_nrDispozitive, tv_consum, tv_cost, tv_minuteUtilizare;
        ImageView imageView;

        //ne folosim de inflater pentru a putea obtine viewurile din item
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout_dispozitiv, null, true);

        //legare xml de java
        tv_denumire = (TextView) listViewItem.findViewById(R.id.tv_name_dispozitivItemList);
        tv_minuteUtilizare = (TextView) listViewItem.findViewById(R.id.tv_timpUtilizare_dispozitiveItemList);
        tv_nrDispozitive = (TextView) listViewItem.findViewById(R.id.tv_nrDispozitive_dispozitiveItemList);
        tv_consum = (TextView) listViewItem.findViewById(R.id.tv_consum_dispozitiveItemList);
        tv_cost = (TextView) listViewItem.findViewById(R.id.tv_cost_dispozitiveItemList);
        imageView = (ImageView) listViewItem.findViewById(R.id.imgIncapere_incapereItemList);

        //obtinem dispozitivul curent si il afisam in itemul customizat
        Dispozitiv dispozitiv = dispozitiveList.get(position);

        tv_denumire.setText(dispozitiv.getTipDispozitiv());

        //pt a afisa doar 3 zecimale ale consumului,nerotunjit
        DecimalFormat formatter = new DecimalFormat("##.###");
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        Float consum = Float.parseFloat(dispozitiv.getConsumDispozitiv());
        String consumFormatat = formatter.format(consum);
        tv_consum.setText(consumFormatat + " kWh/lunar");
        MyUtilsClass.preiaCost(dispozitiv.getUid(),new MyCallback(){

            @Override
            public void onCallbackForCost(Float value) {
                if(value!=0.0) {
                    tv_cost.setVisibility(View.VISIBLE);
                    float costLunar = consum * value;
                    String costFormatat = formatter.format(costLunar);
                    tv_cost.setText(costFormatat + " lei/lunar");
                }
            }
        });


        if (dispozitiv.getNumarDispozitive() == 1) {
            tv_nrDispozitive.setText(String.valueOf(dispozitiv.getNumarDispozitive()) + " dispozitiv");
        } else {
            tv_nrDispozitive.setText(String.valueOf(dispozitiv.getNumarDispozitive()) + " dispozitive");
        }
        int ore = dispozitiv.getMinuteFunctionareZilnic() / 60;
        int minute = dispozitiv.getMinuteFunctionareZilnic() - ore * 60;
        if (ore == 1) {
            tv_minuteUtilizare.setText(String.valueOf(ore) + " ora si " + String.valueOf(minute) + " minute ");
        } else {
            tv_minuteUtilizare.setText(String.valueOf(ore) + " ore si " + String.valueOf(minute) + " minute ");
        }

        return listViewItem;

    }


}