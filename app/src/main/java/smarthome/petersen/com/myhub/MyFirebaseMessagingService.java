package smarthome.petersen.com.myhub;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
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
                        String sensorName = jsonObjectSensor.getString("name");
                        JSONObject jsonObjectSensorState = jsonObjectSensor.getJSONObject("state");
                        boolean sensorOpen = jsonObjectSensorState.getBoolean("open");
                        if(sensorOpen)
                        {
                            String notificationText = String.format(MyHubApp.getContext().getString(R.string.ias_warning_text), sensorName);
                            Global.notifyUser(MyHubApp.getContext(), MyHubApp.getContext().getString(R.string.ias_warning), notificationText);
                            setAlarm(key, sensorName);
                        }
                        else
                        {
                            cancelAlarm(key);
                        }
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

    private void cancelAlarm(String id)
    {
        AlarmManagerManager.cancelAlarm(getApplicationContext(), id);
    }

    private void setAlarm(String id, String sensorName)
    {
        final int i = (int) System.currentTimeMillis();
        AlarmManager alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("sensorName", sensorName);
        intent.putExtra("key", id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), i, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        10 * 60 * 1000, alarmIntent);

        AlarmManagerManager.addAlarmManager(getApplicationContext(), id, alarmIntent);
    }
}
