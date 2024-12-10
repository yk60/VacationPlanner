package edu.sjsu.android.vacationplanner;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import edu.sjsu.android.vacationplanner.group.ItineraryFragment;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    // create reference to items in map_row_layout
    TextView nameView;
    TextView addressView;
    TextView ratingView;
    TextView businessHourView;
    ImageButton saveView;
    ImageView imageView;
    EditText costView;
    EditText dateTimeView;
    EditText startTimeView;
    EditText endTimeView;
    TextView tripDateView;
    MaterialButton datePickerButton;
    Spinner placeTypeSpinner;
    ImageButton saveToCalendarButton;
    CheckBox checkBox;


    public PlaceViewHolder(@NonNull View itemView, SharedViewModel sharedViewModel, ItineraryFragment itineraryFragment) {
        super(itemView);
        nameView = itemView.findViewById(R.id.place_name);
        addressView = itemView.findViewById(R.id.place_address);
        ratingView = itemView.findViewById(R.id.place_rating);
        businessHourView = itemView.findViewById(R.id.place_business_hour);
        saveView = itemView.findViewById(R.id.save_button);
        imageView = itemView.findViewById(R.id.place_image);
        costView = itemView.findViewById(R.id.place_cost);
        dateTimeView = itemView.findViewById(R.id.place_datetime);
        startTimeView = itemView.findViewById(R.id.startTime);
        endTimeView = itemView.findViewById(R.id.endTime);
        tripDateView = itemView.findViewById(R.id.trip_date);
        datePickerButton = itemView.findViewById(R.id.date_picker);
        placeTypeSpinner = itemView.findViewById(R.id.place_type_spinner);
        saveToCalendarButton = itemView.findViewById(R.id.saveToCalendarButton);
        checkBox = itemView.findViewById(R.id.checkBox);


        // new 
        itemView.findViewById(R.id.saveToCalendarButton).setOnClickListener(view -> {
            String title = nameView.getText().toString();
            String startTime = startTimeView.getText().toString();
            String endTime = endTimeView.getText().toString();
            String date = dateTimeView.getText().toString();
            String tripStartDate = ItineraryFragment.getTripStartDate();

           String tripDate = convertDateToDay(date, tripStartDate);
           Log.d("tripdate", date + "  " + tripDate);


            MyEvent newEvent = new MyEvent(title, startTime, endTime, tripDate);
            Context context = itemView.getContext();
            if (!ItineraryFragment.isValidHour(startTime, endTime)) {
                Toast.makeText(context, "Invalid start or end time.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(itineraryFragment != null){
                itineraryFragment.saveEvent(newEvent);
                Toast.makeText(context, "Added event to itinerary", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Failed to add event to itinerary", Toast.LENGTH_SHORT).show();

            }

        });



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(itemView.getContext(),
        R.array.place_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeTypeSpinner.setAdapter(adapter);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        startTimeView.setText(timeFormat.format(calendar.getTime()));
        endTimeView.setText(timeFormat.format(calendar.getTime()));

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
                        String date = sdf.format(new Date(selection));
                        dateTimeView.setText(date);
                    }
                });
                materialDatePicker.show(((FragmentActivity) itemView.getContext()).getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        startTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(itemView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeView.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        endTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(itemView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTimeView.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });





    }
    // converts date in MM/DD/YYYY format to trip date number(String)
    private String convertDateToDay(String date, String tripStartDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        // if user did not set the trip start date
        if(tripStartDate == null){
            return "1";
        }
        try {
            Date parsedDate = sdf.parse(date);
            Date tripStart = sdf.parse(tripStartDate);
            if (parsedDate.before(tripStart)) {
                Toast.makeText(itemView.getContext(), "Invalid date. Date cannot be before trip start date.", Toast.LENGTH_SHORT).show();
                return "";
            }
            long diff = parsedDate.getTime() - tripStart.getTime();
            int day = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
            return String.valueOf(day);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("error", "error converting");
            return "1";
        }
    }
    
    public void bind(MyPlace myPlace,  boolean isSavesOpen, SharedViewModel sharedViewModel) {
        nameView.setText(myPlace.getName());
        addressView.setText(myPlace.getAddress());
        ratingView.setText(myPlace.getRating());
        businessHourView.setText(myPlace.getBusinessHour());
        if (myPlace.getImage() != null) {
            imageView.setImageBitmap(myPlace.getImage());
        } else {
            imageView.setImageResource(R.drawable.default_place_image);
        }
        
        if (isSavesOpen) {
            costView.setVisibility(View.VISIBLE);
            dateTimeView.setVisibility(View.VISIBLE);
            datePickerButton.setVisibility(View.VISIBLE);
            startTimeView.setVisibility(View.VISIBLE);
            endTimeView.setVisibility(View.VISIBLE);
            placeTypeSpinner.setVisibility(View.VISIBLE);
            saveToCalendarButton.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);



        } else {
            costView.setVisibility(View.GONE);
            dateTimeView.setVisibility(View.GONE);
            datePickerButton.setVisibility(View.GONE);
            startTimeView.setVisibility(View.GONE);
            endTimeView.setVisibility(View.GONE);
            placeTypeSpinner.setVisibility(View.GONE);
            saveToCalendarButton.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);

        }

        costView.setText(myPlace.getCost());
        dateTimeView.setText(myPlace.getDatetime());
        startTimeView.setText(myPlace.getStartTime());
        endTimeView.setText(myPlace.getEndTime());

        int spinnerPosition = ((ArrayAdapter) placeTypeSpinner.getAdapter()).getPosition(myPlace.getType());
        placeTypeSpinner.setSelection(spinnerPosition);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ArrayList<MyPlace> voteList = sharedViewModel.getVoteList().getValue();
            if (voteList == null) {
                return;
            }
            if (isChecked) {
                if (voteList.contains(myPlace)) {
                    Toast.makeText(itemView.getContext(), "Error: Place exists in vote list", Toast.LENGTH_SHORT).show();
                    checkBox.setChecked(false);
                } else {
                    voteList.add(myPlace);
                    Toast.makeText(itemView.getContext(), "Added place to vote list", Toast.LENGTH_SHORT).show();
                }
                sharedViewModel.setVoteList(voteList);
                Log.d("after_check_votelist", String.valueOf(sharedViewModel.getVoteList().getValue().size()));


            } else {
                voteList.remove(myPlace);
                Toast.makeText(itemView.getContext(), "Removed place from vote list", Toast.LENGTH_SHORT).show();
                sharedViewModel.setVoteList(voteList);
                Log.d("after_uncheck_votelist", String.valueOf(sharedViewModel.getVoteList().getValue().size()));
            }

        });

        


    }
}
