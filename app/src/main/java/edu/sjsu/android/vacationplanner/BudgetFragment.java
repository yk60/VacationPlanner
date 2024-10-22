package edu.sjsu.android.vacationplanner;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BudgetFragment extends Fragment {

    PieChart pieChart;

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

        // expenses chart
        pieChart = view.findViewById(R.id.pieChart_view);
        showPieChart();
        // TODO: make progress bar dynamic (not hardcoded values), make button lead to budget settings

        return view;
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
        typeAmountMap.put("Accomodation",100);
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
        // write text in center
        pieChart.setCenterText("Expenses");

        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}