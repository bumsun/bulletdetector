package com.partymaker.roadsign;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

//@ReportsCrashes(formKey = "", mailTo = "bumsun@yandex.ru")
public class UILApplication extends Application {
    public static Context context;

    public enum TrackerName{
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}