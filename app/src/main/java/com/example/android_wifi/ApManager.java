/**
 * Created by NOT on 10/25/17.
 */

package com.example.android_wifi;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
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

enum WIFI_MODE{
    CLIENT, HOTSPOT
}

class ApManager {

    final WifiManager wifiManager;
    final WifiConfiguration wifiConfig;

    Boolean isAutoActive = false;
    private WIFI_MODE currentMode;

    private Context context;
    private Timer wifiTimer = new Timer();

    ApManager() {
        context = AppApplication.getInstance().getContext();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "MANET";
        currentMode = Math.random() <= 0.5 ? WIFI_MODE.CLIENT : WIFI_MODE.HOTSPOT;
    }

    void hotspotMode(){
        if(isWifiApEnabled()){
            setWifiApEnabled(null, false);
        }
        setWifiApEnabled(wifiConfig, true);
        currentMode = WIFI_MODE.HOTSPOT;
    }

    void clientMode(){
        if(isWifiApEnabled()){
            setWifiApEnabled(null, false);
        }
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        currentMode = WIFI_MODE.CLIENT;
    }

    private long getWifiInterval(){
        return 10000 + (int)(Math.random()*15000);
    }

    void startAutoSwitchWifi() {
        TimerTask timerTask;
        final Handler handler = new Handler();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(currentMode == WIFI_MODE.HOTSPOT){
                            clientMode();
                        }
                        else{
                            hotspotMode();
                        }
                        Toast.makeText(context,"Changing to mode = " + (currentMode == WIFI_MODE.HOTSPOT ? "Hotspot" : "Client"),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        wifiTimer.schedule(timerTask, 0,getWifiInterval());
        isAutoActive = true;
    }

    void stopAutoSwitchWifi() {
        wifiTimer.cancel();
        wifiManager.setWifiEnabled(false);
        isAutoActive = false;
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

        if (wifiManager.isWifiEnabled()){
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

            int networkId = findConfiguredNetworkId(list, config);

            if(networkId == -1){
                networkId = wifiManager.addNetwork(wifiConfiguration);
            }
            if(networkId >= 0){
                wifiManager.disconnect();
                wifiManager.enableNetwork(networkId, true);
                wifiManager.reconnect();
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
                wifiManager.setWifiEnabled(false);
            }
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            Boolean success = (Boolean) method.invoke(wifiManager, wifiConfig, enabled);

        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }

    private WIFI_AP_STATE getWifiApState() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(wifiManager));

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