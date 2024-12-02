package edu.sjsu.android.vacationplanner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BudgetFragment extends Fragment {

    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.GroupProvider");
    private final Uri CONTENT_URI_trips = CONTENT_URI2.buildUpon().appendPath("trips").build();

    PieChart pieChart;
    Dialog budgetDialog;

    private TextView budgetDisplay;
    private static Double budgetGoalInput = 0.0;
    private TextView budgetProgressText;
    private static Double actualAmount = 0.0;
    private ProgressBar budgetProgressBar;
    private SharedViewModel sharedViewModel;
    Map<String, Double> typeAmountMap = new HashMap<>();
    ArrayList<Integer> colors;

    private TextView emptyBudget;
    private ImageView emptyBudgetImage;


    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // get info from database
        getBudgetInfoFromDB();

        // initialize widgets
        budgetDisplay = view.findViewById(R.id.budgetDisplay);
        budgetProgressText = view.findViewById(R.id.progress_text);
        budgetProgressBar = view.findViewById(R.id.progressBar);
        emptyBudget = view.findViewById(R.id.emptyBudget);
        emptyBudgetImage = view.findViewById(R.id.emptyBudgetImage);
        
        // edit budget button
        budgetDialog = new Dialog(requireContext());
        ImageButton editBudget = view.findViewById(R.id.editBudgetButton);
        editBudget.setOnClickListener(this::showEditBudget);

        // expenses chart
        pieChart = view.findViewById(R.id.pieChart_view);

        //initializing colors for the entries
        colors = new ArrayList<>();
        colors.add(Color.parseColor(getString(R.string.string_teal)));
        colors.add(Color.parseColor(getString(R.string.string_tealdark)));
        colors.add(Color.parseColor(getString(R.string.string_redlight)));
        colors.add(Color.parseColor(getString(R.string.string_wheat)));
        colors.add(Color.parseColor(getString(R.string.string_melon)));
        
        showPieChart();

        sharedViewModel.getTypeAmountMap().observe(getViewLifecycleOwner(), new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> newTypeAmountMap) {
                typeAmountMap = newTypeAmountMap;
                Log.d("PieChart", "updated map: " + newTypeAmountMap.toString());
                updatePieChart();
            }
        });

        sharedViewModel.getActualAmount().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double newActualAmount) {
                actualAmount = newActualAmount;
                setProgress();
            }
        });

        return view;
    }

    @SuppressLint("Range")
    private void getBudgetInfoFromDB() {
        // Sort by groupID
        String selection = "Select * from users where groupID = " + MainActivity.getGroupID();
        try (Cursor c = requireContext().getContentResolver().
                query(CONTENT_URI_trips, null, selection, null, "groupID")) {

            assert c != null;
            if (c.moveToFirst()) {
                do {
                    int groupID = c.getInt(c.getColumnIndex("groupID"));
                    if (groupID == MainActivity.getGroupID()) {
                        float budgetGoal = c.getFloat(c.getColumnIndex("budgetGoal"));
                        budgetGoalInput = (double) budgetGoal;
                    }
                } while (c.moveToNext());
            } }
    }

    private void showEditBudget(View view) {
        TextView totalBudgetInput;
        Button doneButton;
        budgetDialog.setContentView(R.layout.budget_editor_popup);

        totalBudgetInput = budgetDialog.findViewById(R.id.totalBudgetInput);
        doneButton = budgetDialog.findViewById(R.id.doneButton);

        doneButton.setOnClickListener(view1 -> {
            // set total budget
            String input = totalBudgetInput.getText().toString();
            if(isValidInput(input)) {
                budgetGoalInput = Double.parseDouble(input);
                setProgress();
                saveBudgetGoalToDB(budgetGoalInput);
                budgetDialog.dismiss();
            }
            else {
                Toast.makeText(getContext(),"Please enter in a value.", Toast.LENGTH_SHORT).show();
            }
        });

        budgetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        budgetDialog.show();
    }

    private void saveBudgetGoalToDB(Double budgetGoalInput) {
        ContentValues values = new ContentValues();
        values.put("budgetGoal", budgetGoalInput);
        requireContext().getContentResolver().update(CONTENT_URI_trips, values, "groupID = ?",
                new String[] {String.valueOf(MainActivity.getGroupID())});
    }

    private boolean isValidInput(String input) {
        if (input.isEmpty()){
            return false;
        }
        else return true;
    }

    // Methods for pie chart
    // tutorial used: https://medium.com/@clyeung0714/using-mpandroidchart-for-android-application-piechart-123d62d4ddc0
    private void showPieChart(){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        typeAmountMap.put("Food and Drink",0.0);
        typeAmountMap.put("Transportation",0.0);
        typeAmountMap.put("Accommodation",0.0);
        typeAmountMap.put("Activities",0.0);
        typeAmountMap.put("Other",0.0);



        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);

        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);
        //remove description label
        pieChart.getDescription().setEnabled(false);
        //remove white in middle
        pieChart.setHoleColor(Color.TRANSPARENT);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void updatePieChart(){

        emptyBudget.setVisibility(View.GONE);
        emptyBudgetImage.setVisibility(View.GONE);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));

            if (typeAmountMap.get(type).floatValue() == 0.0 ) {
                emptyBudget.setVisibility(View.VISIBLE);
                emptyBudgetImage.setVisibility(View.VISIBLE);
            }
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }

    private void setProgress() {
        budgetDisplay.setText(String.format("$%.2f", budgetGoalInput));
        budgetProgressText.setText((String.format("$%.2f", actualAmount) + " / " + (String.format("$%.2f", budgetGoalInput))));
        int max = (int) Math.round(budgetGoalInput);
        int progress = (int) Math.round(actualAmount);
        budgetProgressBar.setMax(max);
        budgetProgressBar.setProgress(progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBudgetInfoFromDB();
        setProgress();
    }
}