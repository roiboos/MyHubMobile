package smarthome.petersen.com.myhub;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import smarthome.petersen.com.myhub.datamodel.Sensor;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "MyFirebaseService";

    public MyFirebaseMessagingService()
    {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            try
            {
                for (String key : remoteMessage.getData().keySet())
                {
                    JSONObject jsonObjectSensor = new JSONObject(remoteMessage.getData().get(key));
                    if(Sensor.SENSOR_TYPE_OPENCLOSE.equalsIgnoreCase(jsonObjectSensor.getString("type")))
                    {
                        JSONObject jsonObjectSensorState = jsonObjectSensor.getJSONObject("state");
                        Log.d("MyHub", jsonObjectSensorState.getBoolean("open") ? "Open" : "Closed");
                    }
                }
            }
            catch(Exception ex)
            {
                Log.e("MyHub", Global.getExceptionString(ex));
            }

//            if (
// Check if data needs to be processed by long running job
// true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
