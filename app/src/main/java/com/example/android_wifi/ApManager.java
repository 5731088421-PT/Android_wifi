package com.example.android_wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by NOT on 10/25/17.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
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

    public static void  startTethering(Context context) throws ClassNotFoundException, NoSuchMethodException {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class callback = Class.forName("ConnectivityManager.OnStartTetheringCallback");
        Method m = connectivityManager.getClass().getMethod("a", new Class[]{int.class, boolean.class, callback});

    }

    // toggle wifi hotspot on or off
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean configApState(final Context context) {

        final WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifimanager.startLocalOnlyHotspot(new LocalOnlyHotspotCallback() {
            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                Log.d(TAG, "Wifi Hotspot is on now");
                WifiConfiguration config = reservation.getWifiConfiguration();
                Log.d(TAG, isHotspotOn(context) ? "hotspot is on" : "hot spot is off");
                config.SSID = "TEST";
                wifimanager.updateNetwork(config);
            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d(TAG, "onStopped: ");
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Log.d(TAG, "onFailed: ");
            }
        } ,new Handler());



//        WifiConfiguration wificonfiguration = null;
//        try {
//            // if WiFi is on, turn it off
//            if(isApOn(context)) {
//                wifimanager.setWifiEnabled(false);
//            }
//            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
//            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
//            return true;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        return false;
    }


}
