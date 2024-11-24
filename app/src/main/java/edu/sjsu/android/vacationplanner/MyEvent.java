package edu.sjsu.android.vacationplanner;

import java.io.Serializable;

public class MyEvent implements Serializable {
    private String title;
    private String startTime;
    private String endTime;

    public MyEvent(String title, String startTime, String endTime){
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTime(){return startTime;}

    public String getEndTime() {
        return endTime;
    }
}

