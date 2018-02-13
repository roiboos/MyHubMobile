package smarthome.petersen.com.myhub;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by mull12 on 06.02.2018.
 */

public class Global
{
    public static final String NOTIFICATION_CHANNEL_ID = "MyHub";
    public static final String HOME = "HOME";
    public static int i = 0;

    private static SharedPreferences _sharedPreferences;

    // Safer to check if _sharedPreferences has not been discarded by an overly
    // eager garbage collector
    public static SharedPreferences getSharedPreferences()
    {
        if (_sharedPreferences == null)
        {
            _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyHubApp.getContext());
        }

        return _sharedPreferences;
    }

    public static boolean isAtHome()
    {
        return getSharedPreferences().getBoolean("athome", false);
    }

    public static String getCurrentUserid()
    {
        return getSharedPreferences().getString("userid", null);
    }

    public static void setCurrentUserid(String userid)
    {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("userid", userid);
        editor.commit();
    }
    
    public void setAtHome(boolean value)
    {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("athome", value);
        editor.commit();
    }

    public static void notifyUser(Context context, String notificationCaption, String notificationText)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Global.NOTIFICATION_CHANNEL_ID);
            Notification notification = builder
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setAutoCancel(true)
                    .setContentTitle(notificationCaption)
                    .setContentText(notificationText)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context,
                            MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .build();
            notificationManager.notify(i++, notification);
        }
    }

    public static String getExceptionString(Exception ex)
    {
        Writer stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);

        String exceptionText = stringWriter.toString();
        return exceptionText;
    }
}
