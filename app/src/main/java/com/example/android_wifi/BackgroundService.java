/*
 * Created by NOT on 3/1/18.
 */

package com.example.android_wifi;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BackgroundService extends Service {

    public class LocalBinder extends Binder{
        BackgroundService getService(){
            return  BackgroundService.this;
        }
    }

    AdHocManager adHocManager;

    @Override
    public void onCreate() {
        super.onCreate();
        adHocManager = new AdHocManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(adHocManager.wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        registerReceiver(adHocManager.connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(adHocManager.apReceiver, new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));
        adHocManager.startAdHoc();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adHocManager.stopAdhoc();
        unregisterReceiver(adHocManager.wifiReceiver);
        unregisterReceiver(adHocManager.connectivityReceiver);
        unregisterReceiver(adHocManager.apReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}