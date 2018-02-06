package smarthome.petersen.com.myhub;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by mull12 on 05.02.2018.
 */

public class MyHubApp extends Application
{
    private static MyHubApp _instance;

    public MyHubApp()
    {
        _instance = this;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        LeakCanary.install(this);
        JodaTimeAndroid.init(this);
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }

    public static MyHubApp getContext()
    {
        return _instance;
    }
}
