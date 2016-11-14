package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Fragment2 extends Fragment{
    //https://developer.android.com/guide/components/fragments.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout,container,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Populate fragment 2 list with data from sqlite db.- this will load whenever the MainActivity is returned to.
    }
}
