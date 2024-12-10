package edu.sjsu.android.vacationplanner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.GroupProvider");
    private final Uri CONTENT_URI_trips = CONTENT_URI2.buildUpon().appendPath("trips").build();

    private TextView tripName;
    private TextView tripDestination;
    private TextView tripDate;
    ImageButton editTripButton;
    private Dialog editTripInfoDialog;
    private TextView emptyTripInfo;
    private FrameLayout tripInfoLayout;

    private static String tripNameString = "";
    private static String destinationString = "Destination";
    public static String tripStartDate = "MM/DD/YYYY";
    private static String tripEndDate = "MM/DD/YYYY";
    private static String tripDateString = tripStartDate + " - " + tripEndDate; // default

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // initialize widgets
        tripName = view.findViewById(R.id.tripName);
        tripDestination = view.findViewById(R.id.tripDestination);
        tripDate = view.findViewById(R.id.tripDate);
        editTripButton = view.findViewById(R.id.editTripButton);
        editTripInfoDialog = new Dialog(requireContext());
        emptyTripInfo = view.findViewById(R.id.emptyTripInfo);
        tripInfoLayout = view.findViewById(R.id.tripInfoLayout);

        //initialize string variables w/ info from database
        getTripInfo();

        // set text
        setTripInfo();

        // set OnClickListener
        editTripButton.setOnClickListener(this::showDialog);


        return view;
    }

    private void setTripInfo() {
        if (tripNameString.isEmpty()) {
            emptyTripInfo.setVisibility(View.VISIBLE);
            tripInfoLayout.setVisibility(View.GONE);
        } else {
            tripDateString = tripStartDate + " - " + tripEndDate;

            emptyTripInfo.setVisibility(View.GONE);
            tripInfoLayout.setVisibility(View.VISIBLE);
            tripName.setText(tripNameString);
            tripDate.setText(tripDateString);
            tripDestination.setText(destinationString);
            tripDate.setText(tripDateString);
        }

    }

    private void showDialog(View view) {
        EditText tripNameInput;
        EditText tripDestinationInput;
        EditText startDayInput;
        EditText endDayInput;
        Button doneButton;
        ImageButton closeButton;

        editTripInfoDialog.setContentView(R.layout.trip_editor_popup);

        // initialize widgets
        tripNameInput = editTripInfoDialog.findViewById(R.id.editTripName);
        tripDestinationInput = editTripInfoDialog.findViewById(R.id.editTripDestination);
        startDayInput = editTripInfoDialog.findViewById(R.id.start_day_input);
        endDayInput = editTripInfoDialog.findViewById(R.id.end_day_input);
        closeButton = editTripInfoDialog.findViewById(R.id.closeButton);
        doneButton = editTripInfoDialog.findViewById(R.id.doneButton);

        // set texts to current information
        if (!tripNameString.isEmpty()) {
            tripNameInput.setText(tripNameString);
            tripDestinationInput.setText(destinationString);
            startDayInput.setText(tripStartDate);
            endDayInput.setText(tripEndDate);
        }
        else {
            tripNameInput.setText("");
            tripDestinationInput.setText("");
            startDayInput.setText("");
            endDayInput.setText("");
        }
        // set OnClickListeners
        closeButton.setOnClickListener(view1 -> {
            editTripInfoDialog.dismiss();
        });

        doneButton.setOnClickListener(view1 -> {
            tripNameString = tripNameInput.getText().toString();
            destinationString = tripDestinationInput.getText().toString();
            tripStartDate = startDayInput.getText().toString();
            tripEndDate = endDayInput.getText().toString();

            if(isValidInput(tripNameString) && isValidInput(destinationString) && isValidInput(tripStartDate) && isValidInput(tripEndDate)) {
                setTripInfo();

                // save to database
                saveTripInfoToDB(tripNameString, destinationString, tripStartDate, tripEndDate);

                editTripInfoDialog.dismiss();
            }
            else {
                Toast.makeText(getContext(),"Please enter in a value.", Toast.LENGTH_SHORT).show();
            }

        });

        editTripInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editTripInfoDialog.show();
    }

    private void saveTripInfoToDB(String tripNameString, String destinationString, String tripStartDate, String tripEndDate) {
        ContentValues values = new ContentValues();
        values.put("tripName", tripNameString);
        values.put("destination", destinationString);
        values.put("startDate", tripStartDate);
        values.put("endDate", tripEndDate);
        requireContext().getContentResolver().update(CONTENT_URI_trips, values, "groupID = ?",
                new String[] {String.valueOf(MainActivity.getGroupID())});
    }


    private boolean isValidInput(String string) {
        return !string.trim().isEmpty();
    }


    public static String getTripName() {
        return tripNameString;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTripInfo();
        setTripInfo();
    }

    @SuppressLint("Range")
    public void getTripInfo() {

        // Sort by groupID
        String selection = "Select * from users where groupID = " + MainActivity.getGroupID();

        try (Cursor c = requireContext().getContentResolver().
                query(CONTENT_URI_trips, null, selection, null, "groupID")) {

            assert c != null;
            if (c.moveToFirst()) {
                do {
                    int groupID = c.getInt(c.getColumnIndex("groupID"));
                    if (groupID == MainActivity.getGroupID()) {
                        tripNameString = c.getString(c.getColumnIndex("tripName"));
                        destinationString = c.getString(c.getColumnIndex("destination"));
                        tripStartDate = c.getString(c.getColumnIndex("startDate"));
                        tripEndDate = c.getString(c.getColumnIndex("endDate"));
                    }
                } while (c.moveToNext());
            } }

        tripDateString = tripStartDate + " - " + tripEndDate;
    }


}