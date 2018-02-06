package smarthome.petersen.com.myhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mull12 on 06.02.2018.
 */

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("key");
        String sensorName = intent.getStringExtra("sensorName");
        int duration = 10;
        String notificationText = String.format(context.getString(R.string.ias_warning_encore_text), sensorName, duration);
        Global.notifyUser(context, context.getString(R.string.ias_warning), notificationText);
        AlarmManagerManager.cancelAlarm(context, id);
    }
}
