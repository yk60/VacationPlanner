package edu.sjsu.android.vacationplanner;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class MyPlace implements Parcelable{
    private int id;
    private String name;
    private String address;
    private String rating;
    private String businessHour;
    private boolean isSaved;
    private Bitmap image;
    private String cost;
    private String datetime;
    private String startTime;
    private String endTime;
    private String type;


    public MyPlace(String name, String address, String rating, String businessHour, boolean isSaved, Bitmap image) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.businessHour = businessHour;
        this.isSaved = isSaved;
        this.image = image;
    }
    public MyPlace(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

   protected MyPlace(Parcel in) {
        id = in.readInt(); 
        name = in.readString();
        address = in.readString();
        rating = in.readString();
        businessHour = in.readString();
        isSaved = in.readByte() != 0;
        image = in.readParcelable(Bitmap.class.getClassLoader());
        cost = in.readString();
        datetime = in.readString();
        type = in.readString();

   }

   public static final Creator<MyPlace> CREATOR = new Creator<MyPlace>() {
       @Override
       public MyPlace createFromParcel(Parcel in) {
           return new MyPlace(in);
       }

       @Override
       public MyPlace[] newArray(int size) {
           return new MyPlace[size];
       }
   };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRating() {
        return rating;
    }

    public String getBusinessHour() {
        return businessHour;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        this.isSaved = saved;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() { 
        return type;
    }

    public void setType(String type) { 
        this.type = type;
    }

   @Override
   public int describeContents() {
       return 0;
   }

   @Override
   public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id); 
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(rating);
        parcel.writeString(businessHour);
        parcel.writeByte((byte) (isSaved ? 1 : 0));
        parcel.writeParcelable(image, i);
        parcel.writeString(cost);
        parcel.writeString(datetime);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(type);
   }


}
