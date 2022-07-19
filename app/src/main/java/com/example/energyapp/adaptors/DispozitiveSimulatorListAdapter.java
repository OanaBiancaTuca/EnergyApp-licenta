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
import java.util.List;
import java.util.Locale;

public class DispozitiveSimulatorListAdapter extends BaseAdapter {
    private Activity context;
    private List<Dispozitiv> dispozitiveList;

    public DispozitiveSimulatorListAdapter(Activity context, List<Dispozitiv> dispozitiveList) {
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

        TextView tv_denumire, tv_consum;
        ImageView imageView;

        //ne folosim de inflater pentru a putea obtine viewurile din item
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.listsimulator_layout_dispozitiv, null, true);

        //legare xml de java
        tv_denumire = (TextView) listViewItem.findViewById(R.id.tv_name_dispozitivItemListSimulator);
        tv_consum = (TextView) listViewItem.findViewById(R.id.tv_consum_dispozitiveItemListSimulator);

        //obtinem dispozitivul curent si il afisam in itemul customizat
        Dispozitiv dispozitiv = dispozitiveList.get(position);

        tv_denumire.setText(dispozitiv.getTipDispozitiv());

        //pt a afisa doar 3 zecimale ale consumului,nerotunjit
        DecimalFormat formatter = new DecimalFormat("##.###");
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        Float consum = Float.parseFloat(dispozitiv.getConsumDispozitiv());
        String consumFormatat = formatter.format(consum);
        tv_consum.setText(consumFormatat + " kWh/lunar");
        return listViewItem;

    }

}
