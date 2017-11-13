package com.example.android_wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by NOT on 10/25/17.
 */

public class ApManager {

    public static boolean checkHotspotOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {

        }
        return false;
    }

    // toggle wifi hotspot on or off
    public static void setHotspot(final Context context, final boolean isTurnOn) {
        final WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wifiCon = new WifiConfiguration();
        wifiCon.SSID = "TEST1";
        wifiCon.preSharedKey  = "0824129941" ;
        wifiCon.status = WifiConfiguration.Status.ENABLED;
        wifiCon.allowedAuthAlgorithms.set(0);
        wifiCon.allowedKeyManagement.set(4);
        try
        {
//
//
//            Method wifiApConfigurationMethod = wm.getClass().getMethod("getWifiApConfiguration",null);
//            WifiConfiguration getConfig = (WifiConfiguration)wifiApConfigurationMethod.invoke(wm, null);
//            String ssid = getConfig.SSID;

            Method setWifiApMethod = wm.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setWifiApMethod.invoke(wm, wifiCon,isTurnOn);

        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }
    }

    public static void scanAndConnect(final Context context, final WifiManager wifiManager){
        int wifiStage = wifiManager.getWifiState();

        if (wifiStage != WifiManager.WIFI_STATE_ENABLED){
            wifiManager.setWifiEnabled(true);
        }

        wifiManager.startScan();
        List<ScanResult> resultScan = wifiManager.getScanResults();


        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\""+"MANET"+"\"";
//        config.preSharedKey = "\""+"MANETPASS"+"\"";
//        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiManager.addNetwork(config);
//        for (ScanResult result : resultScan) {
//            if(result.SSID == "\""+"MANET"+"\""){
                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for( WifiConfiguration i : list ) {
                    if(i.SSID != null && i.SSID.equals("\"MANET\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();

                        break;
                    }
//                }
//            }
        }
    }
}
