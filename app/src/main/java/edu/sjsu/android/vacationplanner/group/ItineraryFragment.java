package edu.sjsu.android.vacationplanner.group;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.sjsu.android.vacationplanner.EventAdapter;
import edu.sjsu.android.vacationplanner.ItineraryAdapter;
import edu.sjsu.android.vacationplanner.MyEvent;
import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.SharedViewModel;

public class ItineraryFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private static final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.EventProvider");
    private static List<String> hoursList;

    private TextView tripDateView;
    private TextView currentDateView;
    private Calendar currentCalendar;
    private Dialog createEventDialog;
    private ItineraryAdapter itineraryAdapter;
    private EventAdapter eventAdapter;
    private RecyclerView itineraryRecyclerView;
    private RecyclerView eventRecyclerView;
    private int currentDay = 1;

    public ItineraryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        // repopulate sharedviewmodel using data saved into eventDB
        loadEventsFromDatabase();
        // restore all event data for current day and update itinerary
        InitEventsList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);

        itineraryRecyclerView = view.findViewById(R.id.recycler_view_itinerary);
        itineraryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventRecyclerView = view.findViewById(R.id.recycler_view_event);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        synchronizeScroll(itineraryRecyclerView, eventRecyclerView);
        List<String> hours = setHours();

        itineraryAdapter = new ItineraryAdapter(hours);
        itineraryRecyclerView.setAdapter(itineraryAdapter);
        eventAdapter = new EventAdapter(sharedViewModel.getEventsForDay(currentDay));
        eventRecyclerView.setAdapter(eventAdapter);

        tripDateView = view.findViewById(R.id.trip_date);
        currentDateView = view.findViewById(R.id.current_date);
        currentCalendar = Calendar.getInstance();

        ImageButton prevDayButton = view.findViewById(R.id.prev_day);
        prevDayButton.setOnClickListener(v -> {
            String tripDate = tripDateView.getText().toString();
            int day = Integer.parseInt(tripDate.split(" ")[1]);
            if(day > 1){
                goPrevDay(view);
            }
        });

        ImageButton nextDayButton = view.findViewById(R.id.next_day);
        nextDayButton.setOnClickListener(v -> {
            goNextDay(view);
        });

        createEventDialog = new Dialog(getContext());
        ImageButton createEventButton = view.findViewById(R.id.create_Event);
        createEventButton.setOnClickListener(this::showCreateEvent);

        Button deleteAllEventsButton = view.findViewById(R.id.delete_all_events_button);
        deleteAllEventsButton.setOnClickListener(v -> {
            deleteAllEvents();
            Toast.makeText(getContext(), "Deleted all events", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void InitEventsList(){
        if (hoursList == null) {
            setHours();
        }
        // Recreate 24 empty time slots
        List<MyEvent> eventsList = createEmptyTimeSlots();

        // Fetch map from the SharedViewModel
        Map<Integer, List<MyEvent>> eventsMap = sharedViewModel.getEventsMap().getValue();

        if (eventsMap != null) {
            Log.d("ItineraryFragment", "Events map size(num day pages with events): " + eventsMap.size());
            List<MyEvent> savedEvents = eventsMap.get(currentDay);
            if(savedEvents != null){
                // Print the saved events for current day for debugging
                for (int i = 0; i < savedEvents.size(); i++) {
                    MyEvent event = savedEvents.get(i);
                    Log.d("ItineraryFragment", "Day: " + currentDay + ", Index: " + i + ", Event title: " + event.getTitle());

                    // Insert the restored events into the correct positions in the new events list
                    String startTime = event.getStartTime();
                    if (startTime.length() == 4) {
                        startTime = "0" + startTime;
                    }
                    int position = hoursList.indexOf(startTime);
                    if (position != -1) {
                        eventsList.set(position, event);
                    }
                }
            }

            // Update the SharedViewModel with the updated events list for the current day
            sharedViewModel.setEventsForDay(currentDay, eventsList);

        }
        // Initialize the event list for the current day if it is empty
        List<MyEvent> currentDayEvents = sharedViewModel.getEventsForDay(currentDay);
        if (currentDayEvents.isEmpty()) {
            currentDayEvents = createEmptyTimeSlots();
            sharedViewModel.setEventsForDay(currentDay, currentDayEvents);
        }
    }

    private List<String> setHours() {
        hoursList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        for (int i = currentHour; i < 24; i++) {
            hoursList.add(String.format("%02d:00", i));
        }
        for (int i = 0; i < currentHour; i++) {
            hoursList.add(String.format("%02d:00", i));
        }
        return hoursList;
    }

    private void showCreateEvent(View view) {
        createEventDialog.setContentView(R.layout.event_editor_popup);

        EditText titleInput = createEventDialog.findViewById(R.id.new_event_input);
        EditText startTimeInput = createEventDialog.findViewById(R.id.start_time_input);
        EditText endTimeInput = createEventDialog.findViewById(R.id.end_time_input);
        Button doneCreateButton = createEventDialog.findViewById(R.id.doneCreateButton);
        ImageButton closeButton = createEventDialog.findViewById(R.id.closeButton);

        startTimeInput.setOnClickListener(v -> showTimePickerDialog(startTimeInput));
        endTimeInput.setOnClickListener(v -> showTimePickerDialog(endTimeInput));
        closeButton.setOnClickListener(v -> createEventDialog.dismiss());

        doneCreateButton.setOnClickListener(view1 -> {
            String title = titleInput.getText().toString();
            String startTime = startTimeInput.getText().toString();
            String endTime = endTimeInput.getText().toString();

            if (title.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidHour(startTime, endTime)) {
                Toast.makeText(getContext(), "Invalid start or end time.", Toast.LENGTH_SHORT).show();
                return;
            }

            MyEvent newEvent = new MyEvent(title, startTime, endTime, tripDateView.getText().toString().split(" ")[1]);
            saveEvent(newEvent);
            setEventPosition(newEvent);
            Toast.makeText(getContext(), "Added event to itinerary", Toast.LENGTH_SHORT).show();

            eventAdapter.setEvents(sharedViewModel.getEventsForDay(currentDay));
            eventAdapter.notifyDataSetChanged();
            createEventDialog.dismiss();
        });

        createEventDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createEventDialog.show();
    }

    // TODO: Allow events to be longer/shorter than 1 hour
    public static boolean isValidHour(String startTime, String endTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(startTime == endTime || LocalTime.parse(endTime).isBefore(LocalTime.parse(startTime))){
                return false;
            }
        }
        if (!startTime.endsWith(":00") || !endTime.endsWith(":00")) {
            return false;
        }
        Duration duration = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            duration = Duration.between(LocalTime.parse(startTime), LocalTime.parse(endTime));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!duration.equals(Duration.ofHours(1))){
                return false;
            }
        }
        return true;
    }

    public void setEventPosition(MyEvent event) {
        String startTime = event.getStartTime();
        if (startTime.length() == 4) {
            startTime = "0" + startTime;
        }
        int position = hoursList.indexOf(startTime);
        if (position != -1) {
            List<MyEvent> eventsList = sharedViewModel.getEventsForDay(currentDay);
            eventsList.set(position, event);
            sharedViewModel.setEventsForDay(currentDay, eventsList);
        }
    }

    public void saveEvent(MyEvent event) {
        String selection = "startTime = ? AND endTime = ? AND tripDate = ?";
        String[] selectionArgs = {event.getStartTime(), event.getEndTime(), tripDateView.getText().toString().split(" ")[1]};

        Cursor cursor = getContext().getContentResolver().query(CONTENT_URI2, null, selection, selectionArgs, null);
        boolean eventExists = false;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String existingStartTime = cursor.getString(cursor.getColumnIndex("startTime"));
                @SuppressLint("Range") String existingEndTime = cursor.getString(cursor.getColumnIndex("endTime"));
                @SuppressLint("Range") String existingTripDate = cursor.getString(cursor.getColumnIndex("tripDate"));

                if (existingStartTime.equals(event.getStartTime()) && existingEndTime.equals(event.getEndTime()) && existingTripDate.equals(tripDateView.getText().toString().split(" ")[1])) {
                    // If the new event has same start time, end time, and trip date, update event
                    updateEvent(event);
                    Toast.makeText(getContext(), "Event updated", Toast.LENGTH_SHORT).show();
                    eventExists = true;
                    break;
                }
            }
            cursor.close();
        }

        if (!eventExists) {
            addEventToDB(event);
            Toast.makeText(getContext(), "Event added", Toast.LENGTH_SHORT).show();
        }
    }

    public void addEventToDB(MyEvent event) {
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("startTime", event.getStartTime());
        values.put("endTime", event.getEndTime());
        values.put("tripDate", tripDateView.getText().toString().split(" ")[1]);

        getContext().getContentResolver().insert(CONTENT_URI2, values);
        List<MyEvent> eventsList = sharedViewModel.getEventsForDay(currentDay);
        eventsList.add(event);
        sharedViewModel.setEventsForDay(currentDay, eventsList);
    }

    public void updateEvent(MyEvent event) {
        ContentValues eventUpdate = new ContentValues();
        eventUpdate.put("title", event.getTitle());
        eventUpdate.put("startTime", event.getStartTime());
        eventUpdate.put("endTime", event.getEndTime());
        eventUpdate.put("tripDate", tripDateView.getText().toString().split(" ")[1]);

        String selection = "startTime = ? AND endTime = ? AND tripDate = ?";
        String[] selectionArgs = {event.getStartTime(), event.getEndTime(), tripDateView.getText().toString().split(" ")[1]};
        getContext().getContentResolver().update(CONTENT_URI2, eventUpdate, selection, selectionArgs);

        List<MyEvent> eventsList = sharedViewModel.getEventsForDay(currentDay);
        for (int i = 0; i < eventsList.size(); i++) {
            MyEvent existingEvent = eventsList.get(i);
            if (existingEvent.getStartTime().equals(event.getStartTime()) &&
                existingEvent.getEndTime().equals(event.getEndTime()) &&
                existingEvent.getTripDate().equals(event.getTripDate())) {
                eventsList.set(i, event);
                break;
            }
        }
        sharedViewModel.setEventsForDay(currentDay, eventsList);
    }

    public void deleteEvent(MyEvent myEvent) {
        List<MyEvent> eventsList = sharedViewModel.getEventsForDay(currentDay);
        eventsList.remove(myEvent);
        sharedViewModel.setEventsForDay(currentDay, eventsList);
    }

    // deletes all events from the current day 
    private void deleteAllEvents() {
        String currentDayString = tripDateView.getText().toString().split(" ")[1];
        String selection = "tripDate = ?";
        String[] selectionArgs = {currentDayString};
        getContext().getContentResolver().delete(CONTENT_URI2, selection, selectionArgs);
    
        // Create empty time slots for the current day
        List<MyEvent> eventsList = createEmptyTimeSlots();
        sharedViewModel.setEventsForDay(currentDay, eventsList);
        eventAdapter.setEvents(eventsList);
        eventAdapter.notifyDataSetChanged();
    }
    public List<MyEvent> createEmptyTimeSlots(){
        List<MyEvent> eventsList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            eventsList.add(new MyEvent("", "", "", ""));
        }
        return eventsList;
    }

    private void loadEventsFromDatabase() {
        Map<Integer, List<MyEvent>> eventsMap = new HashMap<>();
        Cursor cursor = getContext().getContentResolver().query(CONTENT_URI2, null, null, null, null);
    
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
                @SuppressLint("Range") String tripDate = cursor.getString(cursor.getColumnIndex("tripDate"));
    
                MyEvent event = new MyEvent(title, startTime, endTime, tripDate);
                int day = Integer.parseInt(tripDate);
    
                if (!eventsMap.containsKey(day)) {
                    eventsMap.put(day, new ArrayList<>());
                }
                eventsMap.get(day).add(event);
            }
            cursor.close();
        }
    
        for (Map.Entry<Integer, List<MyEvent>> entry : eventsMap.entrySet()) {
            sharedViewModel.setEventsForDay(entry.getKey(), entry.getValue());
        }
    }

    public void goPrevDay(View view) {
        String tripDate = tripDateView.getText().toString();

        int prevDay = Integer.parseInt(tripDate.split(" ")[1]) - 1;
        tripDateView.setText("DAY " + prevDay);
        currentDay = prevDay;

        currentCalendar.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MM/dd", Locale.getDefault());
        String prevDate = dateFormat.format(currentCalendar.getTime());
        currentDateView.setText(prevDate);

        InitEventsList();
        List<MyEvent> eventsList = sharedViewModel.getEventsForDay(currentDay);
        eventAdapter.setEvents(eventsList);
        eventAdapter.notifyDataSetChanged();
    }

    public void goNextDay(View view) {
        String tripDate = tripDateView.getText().toString();
        int nextDay = Integer.parseInt(tripDate.split(" ")[1]) + 1;
        tripDateView.setText("DAY " + nextDay);
        currentDay = nextDay;

        currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MM/dd", Locale.getDefault());
        String nextDate = dateFormat.format(currentCalendar.getTime());
        currentDateView.setText(nextDate);

        InitEventsList();
        List<MyEvent> eventsList = sharedViewModel.getEventsForDay(currentDay);
        eventAdapter.setEvents(eventsList);
        eventAdapter.notifyDataSetChanged();
    }

    private void synchronizeScroll(RecyclerView recyclerView1, RecyclerView recyclerView2) {
        final RecyclerView.OnScrollListener[] scrollListeners = new RecyclerView.OnScrollListener[2];

        scrollListeners[0] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerView2.removeOnScrollListener(scrollListeners[1]);
                recyclerView2.scrollBy(dx, dy);
                recyclerView2.addOnScrollListener(scrollListeners[1]);
            }
        };
        scrollListeners[1] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerView1.removeOnScrollListener(scrollListeners[0]);
                recyclerView1.scrollBy(dx, dy);
                recyclerView1.addOnScrollListener(scrollListeners[0]);
            }
        };

        recyclerView1.addOnScrollListener(scrollListeners[0]);
        recyclerView2.addOnScrollListener(scrollListeners[1]);
    }

    public void showTimePickerDialog(EditText timeInput) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    timeInput.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.getEventsMap().observe(getViewLifecycleOwner(), eventsMap -> {
            List<MyEvent> events = sharedViewModel.getEventsForDay(currentDay);
            eventAdapter.setEvents(events);
            eventAdapter.notifyDataSetChanged();
        });
    }
}
