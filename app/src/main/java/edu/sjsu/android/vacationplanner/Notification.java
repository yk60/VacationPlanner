package edu.sjsu.android.vacationplanner;


import java.util.ArrayList;


public class Notification {

    public static ArrayList<Notification> notificationsList = new ArrayList<>();

    private int id;
    private String title;
    private String description;

    public Notification(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public static Notification getNotificationForID(int notificationID) {
        for (Notification notification : notificationsList) {
            if(notification.getId() == notificationID)
                return notification;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
