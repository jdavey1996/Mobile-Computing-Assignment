package com.josh_davey.mobile_computing_assignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Fragment1 extends Fragment{
    //https://developer.android.com/guide/components/fragments.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout,container,false);

        Button getBtn = (Button)view.findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData test = new GetData(getContext());
                if (test.getStatus() != AsyncTask.Status.RUNNING) {
                    //http://stackoverflow.com/questions/30618600/asynctask-takes-a-long-time-before-entering-doinbackground
                    test.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
                    Toast.makeText(getContext(), "running", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
