package edu.sjsu.android.vacationplanner;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private String username;
    private String firstName;
    private String lastName;
    private String passWord;
    private int profilePicID;

    public User(String username, String firstName, String lastName, String passWord, int profilePicID) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicID = profilePicID;
    }

    protected User(Parcel in) {
        username = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        profilePicID = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeInt(profilePicID);
    }
}
