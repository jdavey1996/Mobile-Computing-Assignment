package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/*REFERENCES:
       https://www.simplifiedcoding.net/android-tablayout-example-using-viewpager-fragments/#
       http://stackoverflow.com/questions/18747975/difference-between-fragmentpageradapter-and-fragmentstatepageradapter*/

public class FragmentAdapter extends FragmentPagerAdapter{
    //Variables.
    int count;
    Activity activity;

    public FragmentAdapter(FragmentManager fm, int count, Activity activity) {
        super(fm);
        this.count = count;
        this.activity = activity;
    }

    /*Return correct fragment based on position given. Checks the activity class name to ensure correct fragment is returned,
      as this class is used by both MainActivity Fragments and RecipeActivity Fragments.*/
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (activity.getLocalClassName().equals("MainActivity"))
                {
                    return  new Fragment1();
                }
                else if (activity.getLocalClassName().equals("RecipeActivity"))
                {
                    return new RecipeFragment1();
                }

            case 1:
                if (activity.getLocalClassName().equals("MainActivity"))
                {
                    return new Fragment2();
                }
                else if (activity.getLocalClassName().equals("RecipeActivity"))
                {
                    return new RecipeFragment2();
                }
            case 2:
                if (activity.getLocalClassName().equals("RecipeActivity"))
                {
                    return new RecipeFragment3();
                }
            default:
                return null;
        }
    }

    //Get count on amount of fragments.
    @Override
    public int getCount() {
        return count;
    }
}
