package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/*REFERENCES:
    https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
 */
public class NetworkStatus {
    //Variables.
    Context ctx;

    public NetworkStatus(Context ctx)
    {
        this.ctx=ctx;
    }

    //Get boolean value for current network state availability.
    public Boolean checkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //Status of network connection, true if connected.
        boolean status = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //If status isn't true, display error message.
        if(!status)
        {
            Toast.makeText(ctx, "Network error, please check your internet connection. ", Toast.LENGTH_SHORT).show();
        }
        return status;
    }
}
