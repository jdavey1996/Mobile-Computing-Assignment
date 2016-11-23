package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/*References:
    https://developers.google.com/android/guides/setup
    http://stackoverflow.com/questions/22493465/check-if-correct-google-play-service-available-unfortunately-application-has-s*/

public class GooglePlayServices {
    //Variables.
    Context ctx;
    Activity activity;

    public GooglePlayServices(Context ctx, Activity activity)
    {
        this.ctx = ctx;
        this.activity = activity;
    }

    public boolean checkAvailable()
    {
        //Get result code for GooglePlayServices availability.
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int availability = googleApiAvailability.isGooglePlayServicesAvailable(ctx);

        /*If is available, return true, if not return false, check if it's resolvable and show corresponding dialog if so.
             If it's not resolvable, toast error.*/
        if (availability == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else
        {
            if(googleApiAvailability.isUserResolvableError(availability))
            {
                googleApiAvailability.getErrorDialog(activity,availability,1).show();
            }
            else
            {
                Toast.makeText(ctx, "Unresolvable error, certain sections of this app will not work correctly on this device.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }
}
