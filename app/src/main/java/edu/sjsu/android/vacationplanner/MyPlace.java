package edu.sjsu.android.vacationplanner;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MyPlace implements Parcelable{
    private final String name;
    private final String address;
    private final String rating;
    private final String businessHour;
    private boolean isSaved;
    private Bitmap image;

    public MyPlace(String name, String address, String rating, String businessHour, boolean isSaved, Bitmap image) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.businessHour = businessHour;
        this.isSaved = isSaved;
        this.image = image;
    }

   protected MyPlace(Parcel in) {
       name = in.readString();
       address = in.readString();
       rating = in.readString();
       businessHour = in.readString();
       isSaved = in.readByte() != 0;
    //    image = in.readBitmap();
       image = in.readParcelable(Bitmap.class.getClassLoader());

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

   @Override
   public int describeContents() {
       return 0;
   }

   @Override
   public void writeToParcel(@NonNull Parcel parcel, int i) {
       parcel.writeString(name);
       parcel.writeString(address);
       parcel.writeString(rating);
       parcel.writeString(businessHour);
       parcel.writeByte((byte) (isSaved ? 1 : 0));
    //    parcel.writeInt(image);
       parcel.writeParcelable(image, i);
   }


}
