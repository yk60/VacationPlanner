package edu.sjsu.android.vacationplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private TextView tripName;
    private static String tripNameInput = "Name of Trip";
    private TextView tripDestination;
    private TextView tripDate;
    EditText editName;
    ImageButton editTripNameButton;
    ImageButton doneEditingButton;

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

        tripName = view.findViewById(R.id.tripName);
        tripDestination = view.findViewById(R.id.tripDestination);
        tripDate = view.findViewById(R.id.tripDate);
        editName = view.findViewById(R.id.editTripName);
        doneEditingButton = view.findViewById(R.id.doneButton);

        editTripNameButton = view.findViewById(R.id.editTripNameButton);
        editTripNameButton.setOnClickListener(this::editTripName);


        return view;
    }

    private void editTripName(View view) {
        tripName.setVisibility(View.INVISIBLE);
        editName.setVisibility(View.VISIBLE);
        editName.setText("");

        editTripNameButton.setVisibility(View.INVISIBLE);
        doneEditingButton.setVisibility(View.VISIBLE);
        doneEditingButton.setOnClickListener(this::doneEditing);
    }

    private void doneEditing(View view) {
        if (isValidInput(editName.getText().toString())) {
            tripNameInput = editName.getText().toString();
            tripName.setText(tripNameInput);
        }

        tripName.setVisibility(View.VISIBLE);
        editName.setVisibility(View.INVISIBLE);

        editTripNameButton.setVisibility(View.VISIBLE);
        doneEditingButton.setVisibility(View.INVISIBLE);
    }

    private boolean isValidInput(String string) {
        return !string.trim().isEmpty();
    }

    public static String getTripName() {
        return tripNameInput;
    }

    @Override
    public void onResume() {
        super.onResume();
        tripName.setText(tripNameInput);
    }
}