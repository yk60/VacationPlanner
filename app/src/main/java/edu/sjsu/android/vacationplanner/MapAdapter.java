package edu.sjsu.android.vacationplanner;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MapAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private final Context context;
    private final List<MyPlace> placeList;
    private int selectedPos = RecyclerView.NO_POSITION;
    private final boolean isSavesOpen;
    private final SharedViewModel sharedViewModel;
    private final UpdateSavesListener updateSavesListener;

    public MapAdapter(Context context, ArrayList<MyPlace> placeList, boolean isSavesOpen, UpdateSavesListener updateSavesListener) {
        this.context = context;
        this.placeList = placeList;
        this.isSavesOpen = isSavesOpen;
        this.sharedViewModel = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
        this.updateSavesListener = updateSavesListener;
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
        holder.bind(myPlace, isSavesOpen);
        holder.itemView.setSelected(selectedPos == position);

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
                if (isSavesOpen) {
                    Planner.getInstance().removePlace(myPlace);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, placeList.size());
                    updateSavesListener.updateTotalCost();
                }
            } else {
                holder.saveView.setImageResource(R.drawable.saved);
                Toast.makeText(context, "Saved place", Toast.LENGTH_SHORT).show();
                myPlace.setSaved(true);
                if (!Planner.getInstance().getSavedPlaces().contains(myPlace)) {
                    Planner.getInstance().addPlace(myPlace);
                    updateSavesListener.updateTotalCost();
                }
            }
            // notifyItemChanged(position);

        });

        holder.costView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myPlace.setCost(s.toString());
                sharedViewModel.setCost(s.toString());
                updateSavesListener.updateTotalCost();

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.datetimeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myPlace.setDatetime(s.toString());
                sharedViewModel.setDatetime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.startTimeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myPlace.setStartTime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.endTimeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myPlace.setEndTime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        holder.costView.setText(myPlace.getCost());
        holder.datetimeView.setText(myPlace.getDatetime());
        holder.startTimeView.setText(myPlace.getStartTime());
        holder.endTimeView.setText(myPlace.getEndTime());

    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }
}
