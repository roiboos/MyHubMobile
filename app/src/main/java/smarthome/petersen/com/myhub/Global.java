package smarthome.petersen.com.myhub;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
    public static int i = 0;

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
