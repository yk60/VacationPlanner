package edu.sjsu.android.vacationplanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavesFragment extends Fragment implements UpdateSavesListener{
    private RecyclerView recyclerView;
    private MapAdapter adapter;
    private SharedViewModel sharedViewModel;
    private TextView totalCostView;
    private Button saveButton;


    public SavesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        
        // add sample places to planner
        Planner planner = Planner.getInstance();
        Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_place_image);
        if (!Planner.isSampleDataAdded){
            planner.addPlace(new MyPlace("Sample Place 1", "address", "5.0", "9-5", true, defaultImage));
            planner.addPlace(new MyPlace("Sample Place 2", "address", "4.0", "9-5", true, defaultImage));
            planner.addPlace(new MyPlace("Sample Place 3", "address", "3.0", "9-5", true, defaultImage));
            Planner.isSampleDataAdded = true;
        }

       

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saves, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        totalCostView = view.findViewById(R.id.expected_cost);
        adapter = new MapAdapter(getContext(), Planner.getInstance().getSavedPlaces(), true, this);
        recyclerView.setAdapter(adapter);
        saveButton = view.findViewById(R.id.SaveButton);

        sharedViewModel.getSelectedPlace().observe(getViewLifecycleOwner(), new Observer<MyPlace>() {
            @Override
            public void onChanged(MyPlace place) {
                adapter.notifyDataSetChanged();
                updateTotalCost();
            }
        });

        saveButton.setOnClickListener(v -> {
            List<MyPlace> savedPlaces = Planner.getInstance().getSavedPlaces();
            sharedViewModel.updatePieChart(savedPlaces);
            updateTotalCost();
        });

        updateTotalCost();
        return view;
    }
    
    public void editPlace(MyPlace place) {
        sharedViewModel.selectPlace(place);
    }

    @Override
    public void updateTotalCost() {
        double totalCost = 0.0;
        for (MyPlace place : Planner.getInstance().getSavedPlaces()) {
            String cost = place.getCost();
            if (cost != null) {
                try {
                    totalCost += Double.parseDouble(cost);
                } catch (NumberFormatException e) {
                    totalCostView.setText("Invalid input");
                }
            }
            totalCostView.setText(String.format("Total Cost: $%.2f", totalCost));
        }
    }
}