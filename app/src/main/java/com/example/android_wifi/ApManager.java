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

    public static void  startTethering(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Method [] ms = connectivityManager.getClass().getMethods();
            Method targetM = null;
            for (Method m : ms) {
                if (m.getName() == "startTethering" && m.getParameterTypes().length == 3) {
                    targetM = m;
                }
            }
            if (targetM == null) {
                throw new Exception("Exception");
            }

            Class[] cs = connectivityManager.getClass().getDeclaredClasses();
            Class c = null;
            for (Class cc: cs) {
                if (cc.getName().equals("android.net.ConnectivityManager$OnStartTetheringCallback")) {
                    c = cc;
                }
            }

            if (c == null) {
                throw new Exception("Exception");
            }

            Object o = Proxy.newProxyInstance(c.getClassLoader(), new Class[]{}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName() == "onTetheringStarted" ){

                        Log.d("apManager", "Start");
                    }
                    return null;
                }
            });

            targetM.invoke(connectivityManager,0,true,o);
//            m.invoke(connectivityManager,new Object[]{0,true, new Object() {
//                public void onTetheringStarted() {
//                    Log.d("apManager", "Start");
//                }
//                public void onTetheringFailed() {
//                    Log.d("apManager", "Failed");
//                }
//            }});
        }
        catch (Exception e){
            Log.d(TAG, "Exce");
        }

    }

    // toggle wifi hotspot on or off
//    @RequiresApi(api = Build.VERSION_CODES.O) c
    public boolean configApState(final Context context, final MainActivity activity) {

        final WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
//        final Looper looper = new Looper();
        final Handler handler = new Handler();

        Message message = new Message();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "Panupong";
        message.obj = wifiConfig;
        message.what = 1;
//        wifimanager.startLocalOnlyHotspot(new LocalOnlyHotspotCallback() {
//            @Override
//            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
//                super.onStarted(reservation);
//                Log.d(TAG, "Wifi Hotspot is on now");
//                Log.d(TAG, isHotspotOn(context) ? "hotspot is on" : "hot spot is off");
////                WifiConfiguration config = reservation.getWifiConfiguration();
////                config.SSID = "1234";
////                Method setConfigMethod = null;
////                try {
////                    setConfigMethod = wifimanager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
////                } catch (NoSuchMethodException e) {
////                    e.printStackTrace();
////                }
////                try {
////                    setConfigMethod.invoke(wifimanager, config);
////                } catch (IllegalAccessException e) {
////                    e.printStackTrace();
////                } catch (InvocationTargetException e) {
////                    e.printStackTrace();
////                }
////                config.SSID = "TEST";
////                wifimanager.updateNetwork(config);
////                activity.setHotSpotNameWrap("HELLO",context);
//            }
//
//            @Override
//            public void onStopped() {
//                super.onStopped();
//                Log.d(TAG, "onStopped: ");
//            }
//
//            @Override
//            public void onFailed(int reason) {
//                super.onFailed(reason);
//                Log.d(TAG, "onFailed: ");
//            }
//        } ,handler);
        handler.sendMessage(message);

//
//
//
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
//

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
