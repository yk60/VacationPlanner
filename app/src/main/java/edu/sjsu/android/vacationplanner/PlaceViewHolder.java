package edu.sjsu.android.vacationplanner;

import android.app.TimePickerDialog;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    // create reference to items in map_row_layout
    TextView nameView;
    TextView addressView;
    TextView ratingView;
    TextView businessHourView;
    ImageButton saveView;
    ImageView imageView;
    EditText costView;
    EditText datetimeView;
    EditText startTimeView;
    EditText endTimeView;
    MaterialButton datePickerButton;
//    TimePicker timePickerView;


    public PlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.place_name);
        addressView = itemView.findViewById(R.id.place_address);
        ratingView = itemView.findViewById(R.id.place_rating);
        businessHourView = itemView.findViewById(R.id.place_business_hour);
        saveView = itemView.findViewById(R.id.save_button);
        imageView = itemView.findViewById(R.id.place_image);
        costView = itemView.findViewById(R.id.place_cost);
        datetimeView = itemView.findViewById(R.id.place_datetime);
        startTimeView = itemView.findViewById(R.id.startTime);
        endTimeView = itemView.findViewById(R.id.endTime);
        datePickerButton = itemView.findViewById(R.id.date_picker);
//        timePickerView = itemView.findViewById(R.id.time_picker);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        startTimeView.setText(timeFormat.format(calendar.getTime()));
        endTimeView.setText(timeFormat.format(calendar.getTime()));

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        // .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
                        String date = sdf.format(new Date(selection));
                        datetimeView.setText(date); 
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
    
    public void bind(MyPlace myPlace,  boolean isSavesOpen) {
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
            datetimeView.setVisibility(View.VISIBLE);
            datePickerButton.setVisibility(View.VISIBLE);
            startTimeView.setVisibility(View.VISIBLE);
            endTimeView.setVisibility(View.VISIBLE);
//            timePickerView.setVisibility(View.VISIBLE);

        } else {
            costView.setVisibility(View.GONE);
            datetimeView.setVisibility(View.GONE);
            datePickerButton.setVisibility(View.GONE);
            startTimeView.setVisibility(View.GONE);
            endTimeView.setVisibility(View.GONE);
//            timePickerView.setVisibility(View.GONE);
        }
        costView.setText(myPlace.getCost());
        datetimeView.setText(myPlace.getDatetime());
        startTimeView.setText(myPlace.getStartTime());
        endTimeView.setText(myPlace.getEndTime());

    }
}
