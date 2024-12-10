package edu.sjsu.android.vacationplanner;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import edu.sjsu.android.vacationplanner.group.ItineraryFragment;


public class MapAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private final Context context;
    private final List<MyPlace> placeList;
    private int selectedPos = RecyclerView.NO_POSITION;
    private final boolean isSavesOpen;
    private final SharedViewModel sharedViewModel;
    private final UpdateSavesListener updateSavesListener;
    private final ItineraryFragment itineraryFragment;
    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.DataProvider");


    public MapAdapter(Context context, ArrayList<MyPlace> placeList, boolean isSavesOpen, UpdateSavesListener updateSavesListener, SharedViewModel sharedViewModel, ItineraryFragment itineraryFragment) {
        this.context = context;
        this.placeList = placeList;
        this.isSavesOpen = isSavesOpen;
        this.sharedViewModel = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
        this.updateSavesListener = updateSavesListener;
        this.itineraryFragment = itineraryFragment;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_row_layout, parent, false);
        return new PlaceViewHolder(view, sharedViewModel, itineraryFragment);
      
    }
    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        MyPlace myPlace = placeList.get(position);
        holder.bind(myPlace, isSavesOpen, sharedViewModel);
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
                    deletePlace(myPlace);
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
                    addPlace(myPlace);
                    updateSavesListener.updateTotalCost();
                } else {
                    updatePlace(myPlace);
                }
            }

        });

        holder.costView.addTextChangedListener(new TextWatcher() {
            private String previousText = myPlace.getCost();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!newText.equals(previousText)) {
                    myPlace.setCost(newText);
                    sharedViewModel.setCost(newText);
                    updateSavesListener.updateTotalCost();
                    updatePlace(myPlace);
                    previousText = newText;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.dateTimeView.addTextChangedListener(new TextWatcher() {
            private String previousText = myPlace.getDatetime();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!newText.equals(previousText)) {
                    myPlace.setDatetime(newText);
                    sharedViewModel.setDatetime(newText);
                    updatePlace(myPlace);
                    previousText = newText;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.startTimeView.addTextChangedListener(new TextWatcher() {
            private String previousText = myPlace.getStartTime();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!newText.equals(previousText)) {
                    myPlace.setStartTime(newText);
                    updatePlace(myPlace);
                    previousText = newText;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.endTimeView.addTextChangedListener(new TextWatcher() {
            private String previousText = myPlace.getEndTime();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!newText.equals(previousText)) {
                    myPlace.setEndTime(newText);
                    updatePlace(myPlace);
                    previousText = newText;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    

        holder.placeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private String previousType = myPlace.getType();

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);
                if (!selectedType.equals(previousType)) {
                    myPlace.setType(selectedType);
                    updatePlace(myPlace);
                    previousType = selectedType;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        holder.costView.setText(myPlace.getCost());
        holder.dateTimeView.setText(myPlace.getDatetime());
        holder.startTimeView.setText(myPlace.getStartTime());
        holder.endTimeView.setText(myPlace.getEndTime());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    void addPlace(MyPlace place) {
        ContentValues values = new ContentValues();
        values.put("name", place.getName());
        values.put("address", place.getAddress());
        values.put("rating", place.getRating());
        values.put("business_hour", place.getBusinessHour());
        values.put("is_saved", place.isSaved() ? 1 : 0);
        values.put("image", getBytes(place.getImage()));
        values.put("cost", place.getCost());
        values.put("datetime", place.getDatetime());
        values.put("start_time", place.getStartTime());
        values.put("end_time", place.getEndTime());
        values.put("type", place.getType());

        Uri resultUri = context.getContentResolver().insert(CONTENT_URI2, values);

        if (resultUri != null) {
            Log.d("SavesFragment", "Place added: " + resultUri.toString());
            long rowID = ContentUris.parseId(resultUri);
            place.setId((int) rowID);
        } else {
            Log.e("SavesFragment", "Failed to add place");
        } 
    }
    void updatePlace(MyPlace place) {

        ContentValues values = new ContentValues();
        values.put("_id", place.getId());
        values.put("name", place.getName());
        values.put("address", place.getAddress());
        values.put("rating", place.getRating());
        values.put("business_hour", place.getBusinessHour());
        values.put("is_saved", place.isSaved() ? 1 : 0);
        values.put("image", getBytes(place.getImage()));
        values.put("cost", place.getCost());
        values.put("datetime", place.getDatetime());
        values.put("start_time", place.getStartTime());
        values.put("end_time", place.getEndTime());
        values.put("type", place.getType());

        int rowsUpdated = context.getContentResolver().update(CONTENT_URI2, values, "_id = ?", new String[]{String.valueOf(place.getId())}); 
        if (rowsUpdated > 0) {
            Log.d("MapAdapter", "Place updated: " + place.getName());
        }
    }
    void deletePlace(MyPlace place){
        int rowsDeleted = context.getContentResolver().delete(CONTENT_URI2, "_id = ?", new String[]{String.valueOf(place.getId())});
        if (rowsDeleted > 0) {
            Log.d("MapAdapter", "Place deleted: " + place.getName());
        } else {
            Log.e("MapAdapter", "Failed to delete place: " + place.getName());
        }
    }

    private byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return new byte[0]; 
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }



}
