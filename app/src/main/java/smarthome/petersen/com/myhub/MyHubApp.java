package smarthome.petersen.com.myhub;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by mull12 on 05.02.2018.
 */

public class MyHubApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        LeakCanary.install(this);
        JodaTimeAndroid.init(this);
    }
}
