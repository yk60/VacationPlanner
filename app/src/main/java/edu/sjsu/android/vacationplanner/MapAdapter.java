package edu.sjsu.android.vacationplanner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MapAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private final Context context;
    private final List<MyPlace> placeList;

    public MapAdapter(Context context, ArrayList<MyPlace> placeList) {
        this.context = context;
        this.placeList = placeList;

    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_row_layout, parent, false);
        return new PlaceViewHolder(view);
      
    }
    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        MyPlace myPlace = placeList.get(position);
        holder.bind(myPlace);
        if (myPlace.isSaved()) {
            holder.saveView.setImageResource(R.drawable.saved);
        } else {
            holder.saveView.setImageResource(R.drawable.save);
        }

        holder.saveView.setOnClickListener(v -> {
            if (myPlace.isSaved()){
                holder.saveView.setImageResource(R.drawable.save);
                Toast.makeText(context, "Unsaved place", Toast.LENGTH_SHORT).show();
                myPlace.setSaved(false);
            } else {
                holder.saveView.setImageResource(R.drawable.saved);
                Toast.makeText(context, "Saved place", Toast.LENGTH_SHORT).show();
                myPlace.setSaved(true);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }
}
