package edu.sjsu.android.vacationplanner;

import java.util.ArrayList;

public class Planner {
    private static Planner instance;
    private ArrayList<MyPlace> savedPlaces;
    public static boolean isSampleDataAdded = false;

    private Planner() {
        savedPlaces = new ArrayList<>();
    }

    public static synchronized Planner getInstance() {
        if (instance == null) {
            instance = new Planner();
        }
        return instance;
    }

    public ArrayList<MyPlace> getSavedPlaces() {
        return savedPlaces;
    }

    public void addPlace(MyPlace place) {
        savedPlaces.add(place);
    }

    public void removePlace(MyPlace place) {
        savedPlaces.remove(place);
    }
}

