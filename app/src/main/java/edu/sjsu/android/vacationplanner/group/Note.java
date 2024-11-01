package edu.sjsu.android.vacationplanner.group;

import java.util.ArrayList;
import java.util.Date;

public class Note {

    public static ArrayList<Note> noteArrayList = new ArrayList<>();

    private int id;
    private String title;
    private String description;
    private Date deleted;
    private int color;

    public Note(int id, String title, String description, int color, Date deleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deleted = deleted;
        this.color = color;
    }

    public Note(int id, String title, String description, int color) {
        this.id = id;
        this.title = title;
        this.description = description;
        deleted = null;
        this.color = color;
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

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
