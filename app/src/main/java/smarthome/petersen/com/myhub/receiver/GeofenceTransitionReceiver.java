package smarthome.petersen.com.myhub.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import smarthome.petersen.com.myhub.Global;
import smarthome.petersen.com.myhub.dataaccess.DataAccess;

/**
 * Created by mull12 on 13.02.2018.
 */

public class GeofenceTransitionReceiver extends BroadcastReceiver
{
    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError())
        {
            Log.e(TAG, "Geofence error: " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Log.d(TAG, "Geofence action " + geofenceTransition);
            Global.notifyUser(context, "Geofence", "At home " + (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? "yes" : "no"));
            try
            {
                String currentUserid = Global.getCurrentUserid();
                if (currentUserid != null)
                {
                    DataAccess.setAtHome(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER, Global.getCurrentUserid());
                }
            }
            catch (Exception ex)
            {
                Log.e(TAG, Global.getExceptionString(ex));
            }
        } else
        {
            // Log the error.
            Log.e(TAG, "Invalid geofence transition " + geofenceTransition);
        }
    }
}
