package edu.sjsu.android.vacationplanner;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    // create reference to items in map_row_layout
    TextView nameView;
    TextView addressView;
    TextView ratingView;
    TextView businessHourView;
    ImageButton saveView;
    ImageView imageView;
    EditText costView;
    EditText datetimeView;

    public PlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.place_name);
        addressView = itemView.findViewById(R.id.place_address);
        ratingView = itemView.findViewById(R.id.place_rating);
        businessHourView = itemView.findViewById(R.id.place_business_hour);
        saveView = itemView.findViewById(R.id.save_button);
        imageView = itemView.findViewById(R.id.place_image);
        costView = itemView.findViewById(R.id.place_cost);
        datetimeView = itemView.findViewById(R.id.place_datetime);

    }
    
    public void bind(MyPlace myPlace,  boolean isSavesOpen) {
        nameView.setText(myPlace.getName());
        addressView.setText(myPlace.getAddress());
        ratingView.setText(myPlace.getRating());
        businessHourView.setText(myPlace.getBusinessHour());
        if (myPlace.getImage() != null) {
            imageView.setImageBitmap(myPlace.getImage());
        } else {
            imageView.setImageResource(R.drawable.default_place_image);
        }
        
        if (isSavesOpen) {
            costView.setVisibility(View.VISIBLE);
            datetimeView.setVisibility(View.VISIBLE);
        } else {
            costView.setVisibility(View.GONE);
            datetimeView.setVisibility(View.GONE);
        }
        costView.setText(myPlace.getCost());
        datetimeView.setText(myPlace.getDatetime());
    }
}
