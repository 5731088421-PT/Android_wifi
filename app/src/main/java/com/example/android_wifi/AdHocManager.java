/*
 * Created by NOT on 3/5/18.
 */

package com.example.android_wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.net.InetAddress;

class AdHocManager {
    private OnWifiStateChangedListener mOnWifiStateChangedListener;

    private ApManager mAPManager;
    private BroadcastManager mBroadcastManager;
    private SocketManager mSocketManager;

    BroadcastReceiver wifiReceiver;
    BroadcastReceiver apReceiver;
    BroadcastReceiver connectivityReceiver;

    AdHocManager(){
        mAPManager = new ApManager();
        initWifiStateReceiver();

        mBroadcastManager = new BroadcastManager();
        mSocketManager = new SocketManager();
    }

    void setupListener(OnDataReceiveListener listener){
        mSocketManager.setListener(listener);
        mBroadcastManager.setListener(listener);
    }

    void setOnWifiStateChangedListener(OnWifiStateChangedListener listener){
        mOnWifiStateChangedListener = listener;
    }

    void startAdHoc(){
        mAPManager.startAutoSwitchWifi();
        mBroadcastManager.listenBroadcast();
        mSocketManager.startServer();
    }

    void stopAdhoc(){
        mAPManager.stopAutoSwitchWifi();
        mBroadcastManager.stopListenBroadcast();
        mSocketManager.stopServer();
    }

    //todo
    void sendMessageViaSocket(Object object, InetAddress address){
        mSocketManager.sendObject(object, address);
    }

    //todo
    void sendMessageViaBroadcast(ChatMessage message){
        mBroadcastManager.sendBroadcast(message);
    }

    WIFI_MODE getCurrentMode(){
        return mAPManager.getCurrentMode();
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
                    mAPManager.connectToAp(mAPManager.wifiConfig);
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
                    mOnWifiStateChangedListener.onWifiStateChanged(ADHOC_STATUS.CLIENT);
                }
            }
        };
    }
}
