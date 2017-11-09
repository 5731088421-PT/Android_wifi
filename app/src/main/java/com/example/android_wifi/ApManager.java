package com.example.android_wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import static android.content.ContentValues.TAG;

/**
 * Created by NOT on 10/25/17.
 */

public class ApManager {


    //check whether wifi hotspot on or off
    public static boolean isHotspotOn(Context context) {
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
    public boolean configApState(final Context context) {

        final WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wifiCon = new WifiConfiguration();
        wifiCon.SSID = "ssid";
        wifiCon.preSharedKey = "Sharedpassword";
        wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try
        {
            Method setWifiApMethod = wm.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus = (Boolean) setWifiApMethod.invoke(wm, wifiCon,true);
        }
        catch (Exception e)
        {
            Log.e(this.getClass().toString(), "", e);
        }


        return false;
    }

    public static boolean setHotspotName(String newName, Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            wifiConfig.SSID = newName;

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
