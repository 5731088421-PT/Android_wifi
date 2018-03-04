/**
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

    ApManager apManager;
    BroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        apManager = new ApManager(getApplicationContext());
        broadcastManager = new BroadcastManager(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apManager.initReceiver();
        registerReceiver(apManager.wifiReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        registerReceiver(apManager.connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(apManager.wifiApReceiver, new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));
        apManager.startAutoSwitchWifi();
        broadcastManager.listenBroadcast();
        broadcastManager.startAutoBroadcast();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(apManager.wifiReceiver);
        unregisterReceiver(apManager.connectivityReceiver);
        unregisterReceiver(apManager.wifiApReceiver);
        broadcastManager.stopAutoBroadcast();
        apManager.stopAutoSwitchWifi();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}