package edu.sjsu.android.vacationplanner;

import java.util.ArrayList;
// A singleton class to save the saved places
public class Planner {
    private static Planner instance;
    private static ArrayList<MyPlace> savedPlaces;
    public boolean isDataLoaded = false;

    public Planner() {
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

