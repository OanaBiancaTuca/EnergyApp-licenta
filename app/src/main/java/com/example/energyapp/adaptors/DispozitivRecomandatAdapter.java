package com.example.energyapp.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.energyapp.R;
import com.example.energyapp.activities.DetailsTipsActivity;
import com.example.energyapp.classes.DispozitivRecomandat;
import com.example.energyapp.classes.ItemTips;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DispozitivRecomandatAdapter extends ArrayAdapter<DispozitivRecomandat> {

    public DispozitivRecomandatAdapter(DetailsTipsActivity context, List<DispozitivRecomandat> recomandari) {
        super(context, 0, recomandari);
    }


    //Create a ViewHolder class to improve scrolling performance
    private static class ViewHolder  {
        TextView tip;
        TextView clasa;
        TextView putere;
        TextView interval;
        TextView link;

    }

    //Override getView method
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DecimalFormat formatter = new DecimalFormat("##");
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        //Get the current view
        View listItemView = convertView;

        //If listItemView is null, inflate it from list_item
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.cardview_item_dispozitiv_recomandat, parent, false);
        }

        //Get the current item by position index
        DispozitivRecomandat currentItem = getItem(position);

        //Create a view holder
        DispozitivRecomandatAdapter.ViewHolder holder = new DispozitivRecomandatAdapter.ViewHolder();

        //Get the title TextView and set its title value
        holder.tip = listItemView.findViewById(R.id.item_name);
        holder.tip.setText(currentItem.getTipDispozitiv());

        //Get the location TextView and set its location value
        holder.clasa = listItemView.findViewById(R.id.item_clasa);
        holder.clasa.setText(currentItem.getClasaDispozitiv());

        //Get the image ImageView and set its image resource ID
        holder.putere = listItemView.findViewById(R.id.item_putere);
        holder.putere.setText(String.valueOf(currentItem.getPutereDispozitiv()));

        holder.interval = listItemView.findViewById(R.id.item_pret);
        holder.interval.setText((formatter.format(currentItem.getPretMinim()))+"-"+formatter.format(currentItem.getPretMaxim()));


        holder.link = listItemView.findViewById(R.id.link_itemRecomandat);
        holder.link.setText(currentItem.getLink());
        //Return the view
        return listItemView;
    }


}
