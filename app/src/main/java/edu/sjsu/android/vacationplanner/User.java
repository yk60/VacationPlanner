package edu.sjsu.android.vacationplanner;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class User{

    public static ArrayList<User> usersList = new ArrayList<>();

    private String username;
    private String firstName;
    private String lastName;
    private String passWord;
    private int profilePicID;


    public User(String username, int profilePicID) {
        this.username = username;
        this.profilePicID = profilePicID;
    }

    public User(String username, String firstName, String lastName, String passWord, int profilePicID) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicID = profilePicID;
    }

    public static int findUserPosition(User user) {
        for (User searchUser: User.usersList) {
            if (Objects.equals(searchUser.getUsername(), user.getUsername())) {
                return usersList.indexOf(searchUser);
            }
        }
        return -1;
    }

    public int getProfilePicID() {
        return profilePicID;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {return username;}

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setUsername(String username) {
        this.username = username;
    }


}
