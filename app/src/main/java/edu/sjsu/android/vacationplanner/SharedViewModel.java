package edu.sjsu.android.vacationplanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

 // used ViewModel to allow data sharing across different fragments and activities
 // and allow data to stay saved even when the user navigates to different parts of the app
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<MyPlace> selectedPlace = new MutableLiveData<>();
    private final MutableLiveData<String> cost = new MutableLiveData<>();
    private final MutableLiveData<String> datetime = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Double>> typeAmountMap;
    private final MutableLiveData<Double> actualAmount;
    private final MutableLiveData<ArrayList<MyPlace>> voteList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<LinkedHashMap<String, Integer>> voteCountMap = new MutableLiveData<>(new LinkedHashMap<>());
    private final MutableLiveData<Map<Integer, List<MyEvent>>> eventsMap = new MutableLiveData<>(new HashMap<>());

    public SharedViewModel() {
        Map<String, Double> initialTypeAmountMap = new HashMap<>();
        initialTypeAmountMap.put("Food and Drink", 0.0);
        initialTypeAmountMap.put("Transportation", 0.0);
        initialTypeAmountMap.put("Accommodation", 0.0);
        initialTypeAmountMap.put("Activities", 0.0);
        initialTypeAmountMap.put("Other", 0.0);
        typeAmountMap = new MutableLiveData<>(initialTypeAmountMap);
        actualAmount = new MutableLiveData<>(0.0);
    }

    public void selectPlace(MyPlace place) {
        selectedPlace.setValue(place);
    }

    public LiveData<MyPlace> getSelectedPlace() {
        return selectedPlace;
    }

    public void setCost(String cost) {
        this.cost.setValue(cost);
    }

    public LiveData<String> getCost() {
        return cost;
    }

    public void setDatetime(String datetime) {
        this.datetime.setValue(datetime);
    }

    public LiveData<String> getDatetime() {
        return datetime;
    }

    public LiveData<Map<String, Double>> getTypeAmountMap() {
        return typeAmountMap;
    }

    public LiveData<Double> getActualAmount() {
        return actualAmount;
    }

    // create a new chart everytime the user presses save
    public void updatePieChart(List<MyPlace> savedPlaces) {
        Map<String, Double> newTypeAmountMap = new HashMap<>();
        double total = 0.0;
        for (MyPlace place : savedPlaces) {
            String type = place.getType();
            String costStr = place.getCost();
            if (type != null && costStr != null && !costStr.isEmpty()) {
                double cost = Double.parseDouble(costStr);
                newTypeAmountMap.put(type, newTypeAmountMap.getOrDefault(type, 0.0) + cost);
                total += cost;
            }
        }
        typeAmountMap.setValue(newTypeAmountMap);
        actualAmount.setValue(total);
    }
    public void setVoteList(ArrayList<MyPlace> newVoteList) {
        voteList.setValue(newVoteList);
    }

    public LiveData<ArrayList<MyPlace>> getVoteList() {
        return voteList;
    }
    public void setVoteCountMap(LinkedHashMap<String, Integer> newVoteCountMap) {
        voteCountMap.setValue(newVoteCountMap);
    }

    public LiveData<LinkedHashMap<String, Integer>> getVoteCountMap() {
        return voteCountMap;
    }

    public LiveData<Map<Integer, List<MyEvent>>> getEventsMap() {
        return eventsMap;
    }

    // Set the event list for the given day
    public void setEventsForDay(int day, List<MyEvent> events) {
        Map<Integer, List<MyEvent>> currentEventsMap = eventsMap.getValue();
        if (currentEventsMap != null) {
            currentEventsMap.put(day, events);
            eventsMap.setValue(currentEventsMap);
        }
    }

    // Get the event list for the given day
    public List<MyEvent> getEventsForDay(int day) {
        Map<Integer, List<MyEvent>> currentEventsMap = eventsMap.getValue();
        if (currentEventsMap != null) {
            return currentEventsMap.getOrDefault(day, new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public boolean isVotingStarted() {
        ArrayList<MyPlace> currentVoteList = voteList.getValue();
        return currentVoteList != null && !currentVoteList.isEmpty();
    }
}