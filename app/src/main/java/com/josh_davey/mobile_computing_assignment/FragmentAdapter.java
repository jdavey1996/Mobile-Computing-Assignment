package com.josh_davey.mobile_computing_assignment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//REFERENCES:
    //http://stackoverflow.com/questions/18747975/difference-between-fragmentpageradapter-and-fragmentstatepageradapter
    //https://developer.android.com/training/implementing-navigation/lateral.html
    //https://developer.android.com/reference/android/support/v4/app/FragmentPagerAdapter.html

public class FragmentAdapter extends FragmentPagerAdapter{
    //Count on amount of fragments.
    int count;
    public FragmentAdapter(FragmentManager fm, int count) {
        super(fm);
        this.count = count;
    }

    //Return correct fragment based on position given.
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment1 tab1 = new Fragment1();
                return tab1;
            case 1:
                Fragment2 tab2 = new Fragment2();
                return tab2;
            case 2:
                Fragment3 tab3 = new Fragment3();
                return tab3;
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
