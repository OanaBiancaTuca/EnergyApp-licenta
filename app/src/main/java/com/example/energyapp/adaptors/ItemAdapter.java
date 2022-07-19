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
import com.example.energyapp.classes.ItemTips;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<ItemTips> {

    //Create a ViewHolder class to improve scrolling performance
    private static class ViewHolder {
        TextView title;
        TextView details;
        ImageView image;
    }

    //ItemAdapter constructor
    public ItemAdapter(Context context, ArrayList<ItemTips> items) {
        super(context, 0, items);
    }

    //Override getView method
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Get the current view
        View listItemView = convertView;

        //If listItemView is null, inflate it from list_item
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_sfaturi, parent, false);
        }

        //Get the current item by position index
        ItemTips currentItem = getItem(position);

        //Create a view holder
        ViewHolder holder = new ViewHolder();

        //Get the title TextView and set its title value
        holder.title = listItemView.findViewById(R.id.title_tipsItem);
        holder.title.setText(currentItem.getItemTitle());

        //Get the location TextView and set its location value
        holder.details = listItemView.findViewById(R.id.descriere_tipsItem);
        holder.details.setText(currentItem.getItemDetailEconomy());

        //Get the image ImageView and set its image resource ID
        holder.image = listItemView.findViewById(R.id.image_tipsItem);
        holder.image.setImageResource(currentItem.getItemImageResourceId());

        //Call function showReviewStar and pass number of ReviewStart
        showReviewStar(listItemView, currentItem.getReviewStar());

        //Return the view
        return listItemView;
    }

    //Function to display numbers of review star
    private void showReviewStar(View listview, int star) {
        //Init all ImageView vars
        ImageView imageViewStart1 = listview.findViewById(R.id.review_star1_tipsItem);
        ImageView imageViewStart2 = listview.findViewById(R.id.review_star2_tipsItem);
        ImageView imageViewStart3 = listview.findViewById(R.id.review_star3_tipsItem);
        ImageView imageViewStart4 = listview.findViewById(R.id.review_star4_tipsItem);
        ImageView imageViewStart5 = listview.findViewById(R.id.review_star5_tipsItem);

        //Set switch statement, and assign number of filled stars
        switch (star) {
            case 1: //If 1, displays one filled star
                imageViewStart1.setImageResource(R.drawable.cercplin);
                imageViewStart2.setImageResource(R.drawable.cercgol);
                imageViewStart3.setImageResource(R.drawable.cercgol);
                imageViewStart4.setImageResource(R.drawable.cercgol);
                imageViewStart5.setImageResource(R.drawable.cercgol);
                break;

            case 2: //If 2, displays two filled stars
                imageViewStart1.setImageResource(R.drawable.cercplin);
                imageViewStart2.setImageResource(R.drawable.cercplin);
                imageViewStart3.setImageResource(R.drawable.cercgol);
                imageViewStart4.setImageResource(R.drawable.cercgol);
                imageViewStart5.setImageResource(R.drawable.cercgol);
                break;
            case 3: //If 3, displays three filled stars
                imageViewStart1.setImageResource(R.drawable.cercplin);
                imageViewStart2.setImageResource(R.drawable.cercplin);
                imageViewStart3.setImageResource(R.drawable.cercplin);
                imageViewStart4.setImageResource(R.drawable.cercgol);
                imageViewStart5.setImageResource(R.drawable.cercgol);
                break;
            case 4: //If 4, displays four filled stars
                imageViewStart1.setImageResource(R.drawable.cercplin);
                imageViewStart2.setImageResource(R.drawable.cercplin);
                imageViewStart3.setImageResource(R.drawable.cercplin);
                imageViewStart4.setImageResource(R.drawable.cercplin);
                imageViewStart5.setImageResource(R.drawable.cercgol);
                break;
            case 5: //If 5, displays all filled stars
                imageViewStart1.setImageResource(R.drawable.cercplin);
                imageViewStart2.setImageResource(R.drawable.cercplin);
                imageViewStart3.setImageResource(R.drawable.cercplin);
                imageViewStart4.setImageResource(R.drawable.cercplin);
                imageViewStart5.setImageResource(R.drawable.cercplin);
                break;
            default: //Any other condition will displays bordered stars
                imageViewStart1.setImageResource(R.drawable.cercgol);
                imageViewStart2.setImageResource(R.drawable.cercgol);
                imageViewStart3.setImageResource(R.drawable.cercgol);
                imageViewStart4.setImageResource(R.drawable.cercgol);
                imageViewStart5.setImageResource(R.drawable.cercgol);
                break;
        }
    }

}
