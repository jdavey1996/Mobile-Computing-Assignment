package com.josh_davey.mobile_computing_assignment;

import android.os.Parcel;
import android.os.Parcelable;

//http://www.parcelabler.com/
//https://developer.android.com/reference/android/os/Parcelable.html
public class RecipeConstructor implements Parcelable{
    String id, title, readyInMinutes;

    public RecipeConstructor(String id, String title, String readyInMinutes)
    {
        this.id = id;
        this.title = title;
        this.readyInMinutes = readyInMinutes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReadyInMinutes() {
        return readyInMinutes;
    }



   @Override
   public void writeToParcel(Parcel dest, int flags) {
       dest.writeString(id);
       dest.writeString(title);
       dest.writeString(readyInMinutes);
   }

    private RecipeConstructor(Parcel in) {
        id = in.readString();
        title = in.readString();
        readyInMinutes = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<RecipeConstructor> CREATOR
            = new Parcelable.Creator<RecipeConstructor>() {

        @Override
        public RecipeConstructor createFromParcel(Parcel in) {
            return new RecipeConstructor(in);
        }

        @Override
        public RecipeConstructor[] newArray(int size) {
            return new RecipeConstructor[size];
        }
    };
}
