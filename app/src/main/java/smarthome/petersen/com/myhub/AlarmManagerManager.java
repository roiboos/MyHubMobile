package smarthome.petersen.com.myhub;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mull12 on 06.02.2018.
 */

public class AlarmManagerManager
{
    private static HashMap<String, PendingIntent> _pendingIntents = new HashMap<String, PendingIntent>();

    public static void addAlarmManager(Context context, String id, PendingIntent pi)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(_pendingIntents.containsKey(id))
        {
            alarmManager.cancel(pi);
        }

        _pendingIntents.put(id, pi);
    }

    public static void cancelAllAlarmManager(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for(String key : _pendingIntents.keySet())
        {
            PendingIntent pi = _pendingIntents.get(key);
            alarmManager.cancel(pi);
        }

        _pendingIntents.clear();
    }

    public static void cancelAlarm(Context context, String key)
    {
        if(_pendingIntents.containsKey(key))
        {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pi = _pendingIntents.get(key);
            alarmManager.cancel(pi);
            _pendingIntents.remove(key);
        }
    }
}
