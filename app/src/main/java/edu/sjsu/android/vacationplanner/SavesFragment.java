package edu.sjsu.android.vacationplanner;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import edu.sjsu.android.vacationplanner.group.ItineraryFragment;


public class SavesFragment extends Fragment implements UpdateSavesListener{

    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.GroupProvider");
    private final Uri CONTENT_URI_notifications = CONTENT_URI2.buildUpon().appendPath("notifications").build();

    private RecyclerView recyclerView;
    private MapAdapter adapter;
    private SharedViewModel sharedViewModel;
    private TextView totalCostView;
    private Button saveButton;
    private AppDB appDB;
    private Planner planner;
    private ImageButton deleteAllButton;
    private Button doneButton;
    private ItineraryFragment itineraryFragment;


    public SavesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDB = AppDB.getInstance(getContext());
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        planner = Planner.getInstance();
        if(!planner.isDataLoaded){
            loadPlacesFromDatabase();
            planner.isDataLoaded = true;
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saves, container, false);
        // Initialize ItineraryFragment
        itineraryFragment = (ItineraryFragment) getParentFragmentManager().findFragmentByTag("ITINERARY_FRAGMENT");
        if (itineraryFragment == null) {
            itineraryFragment = new ItineraryFragment();
            getParentFragmentManager().beginTransaction()
                .add(itineraryFragment, "ITINERARY_FRAGMENT")
                .commit();
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        totalCostView = view.findViewById(R.id.expected_cost);
        adapter = new MapAdapter(getContext(), Planner.getInstance().getSavedPlaces(), true, this, sharedViewModel, itineraryFragment);
        recyclerView.setAdapter(adapter);
        saveButton = view.findViewById(R.id.SaveButton);
        deleteAllButton = view.findViewById(R.id.delete_all_button);
        doneButton = view.findViewById(R.id.done_button);

        // show message if saves list is empty
        TextView emptyText = view.findViewById(R.id.emptySavesPage);
        TextView instructions = view.findViewById(R.id.votingInstructions);
        if (Planner.getInstance().getSavedPlaces().isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            instructions.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            instructions.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.VISIBLE);
        }

        sharedViewModel.getSelectedPlace().observe(getViewLifecycleOwner(), new Observer<MyPlace>() {
            @Override
            public void onChanged(MyPlace place) {
                adapter.notifyDataSetChanged();
                updateTotalCost();
            }
        });

        deleteAllButton.setOnClickListener(v -> {
            if(appDB != null){
                appDB.deleteAllData();
                planner.getSavedPlaces().clear();
                adapter.notifyDataSetChanged();
                updateTotalCost();
            }

        });

        saveButton.setOnClickListener(v -> {
            List<MyPlace> savedPlaces = Planner.getInstance().getSavedPlaces();
            sharedViewModel.updatePieChart(savedPlaces);
            updateTotalCost();
        });

        doneButton.setOnClickListener(v -> {
            ArrayList<MyPlace> voteList = sharedViewModel.getVoteList().getValue();
            if (voteList != null && voteList.size() == 4) {
                sharedViewModel.setVoteList(voteList);
                // create a new map and set count to be 0
                LinkedHashMap<String, Integer> voteCountMap = new LinkedHashMap<>();
                for (MyPlace place : voteList) {
                    voteCountMap.put(place.getName(), 0);
                }
                sharedViewModel.setVoteCountMap(voteCountMap);
                Toast.makeText(getContext(), "Voting page updated", Toast.LENGTH_SHORT).show();
                Log.d("done_votelist", String.valueOf(sharedViewModel.getVoteList().getValue().size()));

                // notify group that voting has begun, and call MainActivity.checkNotifications() to update notifications list
                createNotification();
                MainActivity.checkNotifications();
                
                // open the planning page
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_voting);

            } else {
                Toast.makeText(getContext(), "Select 4 places to start vote", Toast.LENGTH_SHORT).show();
            }
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

    private void loadPlacesFromDatabase() {
        Log.d("dbload", "data loaded");
        Planner.getInstance().getSavedPlaces().clear();
        
        Cursor cursor = appDB.getAllPlaces();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"));
                String businessHour = cursor.getString(cursor.getColumnIndexOrThrow("business_hour"));
                boolean isSaved = cursor.getInt(cursor.getColumnIndexOrThrow("is_saved")) == 1;
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                String cost = cursor.getString(cursor.getColumnIndexOrThrow("cost"));
                String datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));

                MyPlace place = new MyPlace(name, address, rating, businessHour, isSaved, image);
                place.setId(id);
                place.setCost(cost);
                place.setDatetime(datetime);
                place.setStartTime(startTime);
                place.setEndTime(endTime);
                place.setType(type);

                planner.addPlace(place);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void createNotification() {
        Notification.notificationsList.add(new Notification(Notification.notificationsList.size(), "Voting has Begun!", "Go to the Planning Page to cast your vote for the group's activities."));

        ContentValues notificationVals = new ContentValues();
        notificationVals.put("notifTitle", "Voting has Begun!");
        notificationVals.put("notifDescription", "Go to the Planning Page to cast your vote for the group's activities.");
        notificationVals.put("notifID", Notification.notificationsList.size());
        notificationVals.put("groupID", MainActivity.getGroupID());

        Context context = this.getContext();
        assert context != null;
        ContentResolver resolver = context.getContentResolver();
        resolver.insert(CONTENT_URI_notifications, notificationVals);
    }

    

}