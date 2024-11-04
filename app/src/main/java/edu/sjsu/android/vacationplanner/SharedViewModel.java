package edu.sjsu.android.vacationplanner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<MyPlace> selectedPlace = new MutableLiveData<>();
    private final MutableLiveData<String> cost = new MutableLiveData<>();
    private final MutableLiveData<String> datetime = new MutableLiveData<>();

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
}