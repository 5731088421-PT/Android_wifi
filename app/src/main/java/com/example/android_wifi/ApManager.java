/**
 * Created by NOT on 10/25/17.
 */

package com.example.android_wifi;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

enum WIFI_AP_STATE {
    WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED;
}

class ApManager {

    final WifiManager mWifiManager;
    final WifiConfiguration mWifiConfig;

    BroadcastReceiver wifiReceiver;
    BroadcastReceiver wifiApReceiver;
    BroadcastReceiver connectivityReceiver;

    Boolean isAutoRun = false;
    private Timer wifiTimer = new Timer();
    private Boolean isInHotspotMode = false;
    private Context context;

    ApManager(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);

        mWifiConfig = new WifiConfiguration();
        mWifiConfig.SSID = "MANET";
//        mWifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.);
//        mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//        mWifiConfig.preSharedKey = "123456789";
//        mWifiConfig.allowedAuthAlgorithms.set(0);
//        mWifiConfig.allowedKeyManagement.set(4);
    }



    void hotspotMode(){
//        responseReceivedListener.onWifiStatusChanged("Hotspot Mode");

        if(isWifiApEnabled()){
            setWifiApEnabled(null, false);
        }

        setWifiApEnabled(mWifiConfig, true);
    }

    void clientMode(){
//        responseReceivedListener.onWifiStatusChanged("Client Mode");

        if(mWifiManager.isWifiEnabled()){
            connectToAp(mWifiConfig);
        } else {
            turnWifiOn();
        }
    }

    void startAutoSwitchWifi() {
//        responseReceivedListener.onWifiStatusChanged("Auto Mode");
        TimerTask doAsynchronousTask;
        final Handler handler = new Handler();

        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(isInHotspotMode){
                            clientMode();
                        }
                        else{
                            hotspotMode();
                        }
                        Toast.makeText(context,"running",Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        wifiTimer.schedule(doAsynchronousTask, 0,getWifiInterval()); //put the time you want
        isAutoRun = true;
    }

    private long getWifiInterval(){
        return 15000;
    }

    void stopAutoSwitchWifi() {
        wifiTimer.cancel();
        isAutoRun = false;
    }

    void turnWifiOn(){
        if(isWifiApEnabled()){
            setWifiApEnabled(null, false);
        }
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    void connectToAp(WifiConfiguration config){

        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        wifiConfiguration.SSID = "\"" + config.SSID + "\"";
//        wifiConfiguration.preSharedKey = "\"" + config.preSharedKey + "\"";

        //Config for open network
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        if (mWifiManager.isWifiEnabled()){
            List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();

            int networkId = findConfiguredNetworkId(list, config);

            if(networkId == -1){
                networkId = mWifiManager.addNetwork(wifiConfiguration);
            }
            if(networkId >= 0){
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(networkId, true);
                mWifiManager.reconnect();
            }
        }

    }

    private int findConfiguredNetworkId(List<WifiConfiguration> wifiConfigurationList, WifiConfiguration config){
        if(wifiConfigurationList != null){
            for( WifiConfiguration i : wifiConfigurationList ) {
                if(i.SSID != null && i.SSID.equals("\"" + config.SSID + "\"")) {
                    return i.networkId;
                }
            }
        }
        return -1;
    }

    void showWritePermissionSettings(boolean force) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (force || !Settings.System.canWrite(this.context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(intent);
            }
        }
    }

    private void setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        try {
            if (enabled) { // disable WiFi in any case
                mWifiManager.setWifiEnabled(false);
            }

            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            Boolean success = (Boolean) method.invoke(mWifiManager, wifiConfig, enabled);
            if(success){
                isInHotspotMode = enabled;
            }
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }

    private WIFI_AP_STATE getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(mWifiManager));

            // Fix for Android 4
            if (tmp >= 10) {
                tmp = tmp - 10;
            }

            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    private boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

}
