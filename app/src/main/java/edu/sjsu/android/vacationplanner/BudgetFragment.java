package edu.sjsu.android.vacationplanner;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

    PieChart pieChart;
    Dialog budgetDialog;

    private TextView budgetDisplay;
    private static Double budgetGoalInput = 0.0;
    private TextView budgetProgressText;
    private static Double actualAmount = 0.0;
    private ProgressBar budgetProgressBar;


    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // initialize widgets
        budgetDisplay = view.findViewById(R.id.budgetDisplay);
        budgetProgressText = view.findViewById(R.id.progress_text);
        budgetProgressBar = view.findViewById(R.id.progressBar);
        
        // edit budget button
        budgetDialog = new Dialog(getContext());
        ImageButton editBudget = view.findViewById(R.id.editBudgetButton);
        editBudget.setOnClickListener(this::showEditBudget);

        // expenses chart
        pieChart = view.findViewById(R.id.pieChart_view);
        showPieChart();
        // TODO: make progress bar dynamic (not hardcoded values)

        return view;
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
                budgetDialog.dismiss();
            }
            else {
                Toast.makeText(getContext(),"Please enter in a value.", Toast.LENGTH_SHORT).show();
            }

        });

        budgetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        budgetDialog.show();
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
        //TODO: make interactive, turn into percentages (these are test values)
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Food and Drink",200);
        typeAmountMap.put("Transportation",230);
        typeAmountMap.put("Accommodation",100);
        typeAmountMap.put("Activities",500);
        typeAmountMap.put("Other",50);

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.parseColor(getString(R.string.string_teal)));
        colors.add(Color.parseColor(getString(R.string.string_tealdark)));
        colors.add(Color.parseColor(getString(R.string.string_redlight)));
        colors.add(Color.parseColor(getString(R.string.string_wheat)));
        colors.add(Color.parseColor(getString(R.string.string_melon)));

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);
        //remove description label
        pieChart.getDescription().setEnabled(false);
        //remove legend


        pieChart.setData(pieData);
        pieChart.invalidate();
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
        setProgress();
    }
}