package com.example.android_wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

/*
 * Created by NOT on 3/5/18.
 */

public class AdHocManager {
    private ApManager apManager;
    private BroadcastManager broadcastManager;
    private SocketManager socketManager;

    BroadcastReceiver wifiReceiver;
    BroadcastReceiver apReceiver;
    BroadcastReceiver connectivityReceiver;

    public AdHocManager(){
        apManager = new ApManager();
        initWifiStateReceiver();

        broadcastManager = new BroadcastManager();
        broadcastManager.listenBroadcast();

        socketManager = new SocketManager();
    }

    public void startAdHoc(){
        apManager.startAutoSwitchWifi();
//        broadcastManager.startAutoBroadcast();
    }

    public void stopAdhoc(){
        apManager.stopAutoSwitchWifi();
        broadcastManager.stopAutoBroadcast();
    }

    private void initWifiStateReceiver(){
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getExtras().getInt(WifiManager.EXTRA_WIFI_STATE)){
                    case WifiManager.WIFI_STATE_ENABLING:
//                        responseReceivedListener.onWifiStatusChanged("Wifi Enabling...");
                        return;
                    case WifiManager.WIFI_STATE_ENABLED:
                        apManager.connectToAp(apManager.wifiConfig);
//                        responseReceivedListener.onWifiStatusChanged("Wifi Enabled");
                        return;
                    case WifiManager.WIFI_STATE_DISABLING:
//                        responseReceivedListener.onWifiStatusChanged("Wifi Disabling...");
                        return;
                    case WifiManager.WIFI_STATE_DISABLED:
//                        responseReceivedListener.onWifiStatusChanged("Wifi Disabled");
                        return;
                }
            }
        };

        apReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getExtras().getInt("wifi_state")){
                    case 11:
//                        responseReceivedListener.onWifiStatusChanged("Hotspot Disabled");
                        return;
                    case 13:
//                        responseReceivedListener.onWifiStatusChanged("Hotspot Enabled");
                        return;
                    case 14:
//                        responseReceivedListener.onWifiStatusChanged("Hotspot Failed");
                        return;
                }
            }
        };

        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean status = intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
                if(status){
//                    responseReceivedListener.onWifiStatusChanged("Connecting...");
                } else {
//                    responseReceivedListener.onWifiStatusChanged("Connected");
                    broadcastManager.sendBroadcast("Send on connected!".getBytes());
                }
            }
        };

    }
}
