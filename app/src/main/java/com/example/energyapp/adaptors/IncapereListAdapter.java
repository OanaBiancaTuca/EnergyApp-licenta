package com.example.energyapp.adaptors;


import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.energyapp.R;
import com.example.energyapp.classes.Incapere;
import com.example.energyapp.classes.MyCallback;
import com.example.energyapp.classes.MyUtilsClass;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

//adapter pentru o incapere din lista de incaperi
public class IncapereListAdapter extends BaseAdapter {
    private Activity context;
    private List<Incapere> incapereList;

    public IncapereListAdapter(Activity context, List<Incapere> incapereList) {
        this.context = context;
        this.incapereList = incapereList;
    }

    @Override
    public int getCount() {
        return incapereList.size();
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
        //prin inflater obtinem legatura cu xml
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout_incapere, null, true);

        TextView tv_denumire, tv_nrDispozitive, tv_consum, tv_cost;
        ImageView imageView,imageViewError,imageViewCost;
        //legatura intre java si xml
        tv_denumire = (TextView) listViewItem.findViewById(R.id.tv_name_incapereItemList);
        tv_nrDispozitive = (TextView) listViewItem.findViewById(R.id.tv_nrDispozitive_incapereItemList);
        tv_consum = (TextView) listViewItem.findViewById(R.id.tv_consum_incapereItemList);
        tv_cost = (TextView) listViewItem.findViewById(R.id.tv_cost_incapereItemList);
        imageView = (ImageView) listViewItem.findViewById(R.id.imgIncapere_incapereItemList);
        imageViewError=(ImageView) listViewItem.findViewById(R.id.imgView_error_incapereItemList);
        imageViewCost = (ImageView) listViewItem.findViewById(R.id.img_cost_incapereItemList);
        //obtinem incaperea pe care o afisam
        Incapere incapere = incapereList.get(position);

        tv_denumire.setText(incapere.getDenumire());

        //formatare consum doar 2 zecimale
        if(incapere.getConsumTotal()!=null && !TextUtils.isEmpty(incapere.getConsumTotal())) {
            DecimalFormat formatter = new DecimalFormat("##.###");
            formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            Float consum = Float.parseFloat(incapere.getConsumTotal());
            String consumFormatat = formatter.format(consum);
            tv_consum.setText(consumFormatat + " kWh/lunar");

            MyUtilsClass.preiaCost(incapere.getUid(),new MyCallback(){

                @Override
                public void onCallbackForCost(Float value) {
                    if(value!=0.0) {
                        tv_cost.setVisibility(View.VISIBLE);
                        imageViewCost.setVisibility(View.VISIBLE);
                        float costLunar = consum * value;
                        String costFormatat = formatter.format(costLunar);
                        tv_cost.setText(costFormatat + " lei/lunar");
                    }else{
                        tv_cost.setVisibility(INVISIBLE);
                    }
                }
            });
        }

        if(incapere.getNumardispozitive()==1){
            tv_nrDispozitive.setText(String.valueOf(incapere.getNumardispozitive()) + " dispozitiv");
        } else {
            tv_nrDispozitive.setText(String.valueOf(incapere.getNumardispozitive()) + " dispozitive");
        }
        if(incapere.getNumardispozitive()>0){
            imageViewError.setImageDrawable(null);
        }
        if (incapere.getTipIncapere().equals("Living")) {
            imageView.setImageResource(R.drawable.ic_living);
        } else if (incapere.getTipIncapere().equals("Dormitor")) {
            imageView.setImageResource(R.drawable.ic_bedroom);
        } else if (incapere.getTipIncapere().equals("Bucătărie")) {
            imageView.setImageResource(R.drawable.ic_kitchen);
        }
        else if (incapere.getTipIncapere().equals("Grădină")) {
            imageView.setImageResource(R.drawable.ic_yard);
        }  else if (incapere.getTipIncapere().equals("Garaj")) {
            imageView.setImageResource(R.drawable.ic_garage);
        } else if (incapere.getTipIncapere().equals("Baie")) {
            imageView.setImageResource(R.drawable.ic_bathroom);
        }
        else if (incapere.getTipIncapere().equals("Balcon")){
            imageView.setImageResource(R.drawable.ic_balcony);
        }

        return listViewItem;

    }

}
