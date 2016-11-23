package com.josh_davey.mobile_computing_assignment;

import android.os.Parcel;
import android.os.Parcelable;

/*References:
    http://www.parcelabler.com/
    https://developer.android.com/reference/android/os/Parcelable.html*/
//A constructor class for Recipe ingredients. This parcels up the objects so they can be sent in an ArrayList via intents.
public class RecipeIngredientsConstructor implements Parcelable{
    String ingredient, amount, unit;

    public RecipeIngredientsConstructor(String ingredient, String amount, String unit)
    {
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       dest.writeString(ingredient);
       dest.writeString(amount);
       dest.writeString(unit);
    }

    private RecipeIngredientsConstructor(Parcel in) {
        ingredient = in.readString();
        amount = in.readString();
        unit = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecipeIngredientsConstructor> CREATOR = new Creator<RecipeIngredientsConstructor>() {
        @Override
        public RecipeIngredientsConstructor createFromParcel(Parcel in) {
            return new RecipeIngredientsConstructor(in);
        }

        @Override
        public RecipeIngredientsConstructor[] newArray(int size) {
            return new RecipeIngredientsConstructor[size];
        }
    };
}
