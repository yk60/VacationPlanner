package edu.sjsu.android.vacationplanner;

import java.io.Serializable;

public class MyEvent implements Serializable {
    private String title;
    private String startTime;
    private String endTime;
    private String tripDate;

    public MyEvent(String title, String startTime, String endTime, String tripDate){
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tripDate = tripDate;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTime(){return startTime;}

    public String getEndTime() {
        return endTime;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }
}

