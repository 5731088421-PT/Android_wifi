/**
 * Created by NOT on 3/1/18.
 */

package com.example.android_wifi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class BackgroundService extends Service {

    ApManager apManager;
    BroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        apManager = new ApManager(getApplicationContext());
        broadcastManager = new BroadcastManager();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apManager.startAutoSwitchWifi();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apManager.stopAutoSwitchWifi();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
