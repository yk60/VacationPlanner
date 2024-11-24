package edu.sjsu.android.vacationplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {

    private final List<String> hours;


    public ItineraryAdapter(List<String> hours) {
        this.hours = hours;
    }


    @NonNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itinerary_row_layout, parent, false);
        return new ItineraryViewHolder(view);
         
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {
        String hour = hours.get(position);
        holder.hourTextView.setText(hour);
       
    }


    @Override
    public int getItemCount() {
        return hours.size();
    }


    public static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        public final TextView hourTextView;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            hourTextView = itemView.findViewById(R.id.hour);

        }
    }
   
}
