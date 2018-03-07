package com.example.android_wifi;

/*
 * Created by NOT on 3/5/18.
 */

import android.app.Application;
import android.content.Context;

public class AppApplication extends Application{
    private static AppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppApplication getInstance() {
        return instance;
    }

    public Context getContext() {
        return this;
    }
}