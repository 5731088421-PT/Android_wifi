package com.example.android_wifi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
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

/**
 * Created by NOT on 10/25/17.
 */

public class ApManager {

    final WifiManager mWifiManager;
    final WifiConfiguration mWifiConfig;

    private Timer wifiTimer = new Timer();
    private Boolean isInHotspotMode = false;
    ResponseReceivedListener responseReceivedListener;

    private Context context;


    ApManager(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);

        mWifiConfig = new WifiConfiguration();
        mWifiConfig.SSID = "MANET";
        mWifiConfig.preSharedKey = "123456789";
        mWifiConfig.allowedAuthAlgorithms.set(0);
        mWifiConfig.allowedKeyManagement.set(4);
    }

    void hotspotMode(){
        if(isWifiApEnabled()){
            if(setWifiApEnabled(null, false)) {
                responseReceivedListener.onWifiStatusChanged("Hotspot disabled");
            }
        }
        if(setWifiApEnabled(mWifiConfig, true)){
            responseReceivedListener.onWifiStatusChanged("Hotspot Started");
        }
    }

    void clientMode(){
        if(mWifiManager.isWifiEnabled()){
            connectToAp(mWifiConfig);
        } else {
            turnWifiOn();
        }
    }

    void startAutoSwitchWifi() {
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
                    }
                });
            }
        };
        wifiTimer.schedule(doAsynchronousTask, 0,10000); //put the time you want
    }

    void stopAutoSwitchWifi() {
        wifiTimer.cancel();
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
        wifiConfiguration.preSharedKey = "\"" + config.preSharedKey + "\"";

        if (mWifiManager.isWifiEnabled()){
            List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();

            int networkId = findConfiguredNetworkId(list, config);

            if(networkId == -1){
                mWifiManager.addNetwork(wifiConfiguration);
            }
            if(networkId >= 0){
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(networkId, true);
                mWifiManager.reconnect();
            }
        }

    }

    int findConfiguredNetworkId(List<WifiConfiguration> wifiConfigurationList, WifiConfiguration config){
        if(wifiConfigurationList != null){
            for( WifiConfiguration i : wifiConfigurationList ) {
                if(i.SSID != null && i.SSID.equals("\"" + config.SSID + "\"")) {
                    return i.networkId;
                }
            }
        }
        return -1;
    }

    /**
     * Show write permission settings page to user if necessary or forced
     * @param force show settings page even when rights are already granted
     */
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

    /**
     * Start AccessPoint mode with the specified
     * configuration. If the radio is already running in
     * AP mode, update the new configuration
     * Note that starting in access point mode disables station
     * mode operation
     *
     * @param wifiConfig SSID, security and channel details as part of WifiConfiguration
     * @return {@code true} if the operation succeeds, {@code false} otherwise
     */
    public boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        try {
            if (enabled) { // disable WiFi in any case
                mWifiManager.setWifiEnabled(false);
            }

            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            Boolean success = (Boolean) method.invoke(mWifiManager, wifiConfig, enabled);
            if(success){
                isInHotspotMode = enabled;
            }
            return success;
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
        return false;
    }

    /**
     * Gets the Wi-Fi enabled state.
     *
     * @return {@link WIFI_AP_STATE}
     * @see #isWifiApEnabled()
     */
    public WIFI_AP_STATE getWifiApState() {
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

    /**
     * Return whether Wi-Fi AP is enabled or disabled.
     *
     * @return {@code true} if Wi-Fi AP is enabled
     * @hide Dont open yet
     * @see #getWifiApState()
     */
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    /**
     * Gets the Wi-Fi AP Configuration.
     *
     * @return AP details in {@link WifiConfiguration}
     */
    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return null;
        }
    }

    /**
     * Sets the Wi-Fi AP Configuration.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            return (Boolean) method.invoke(mWifiManager, wifiConfig);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }

    /**
     * Gets a list of the clients connected to the Hotspot, reachable timeout is 300
     *
     * @param onlyReachables  {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param finishListener, Interface called when the scan method finishes
     */
    public void getClientList(boolean onlyReachables, FinishScanListener finishListener) {
        getClientList(onlyReachables, 300, finishListener);
    }

    /**
     * Gets a list of the clients connected to the Hotspot
     *
     * @param onlyReachables   {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param reachableTimeout Reachable Timout in miliseconds
     * @param finishListener,  Interface called when the scan method finishes
     */
    public void getClientList(final boolean onlyReachables, final int reachableTimeout, final FinishScanListener finishListener) {
        Runnable runnable = new Runnable() {
            public void run() {

                BufferedReader br = null;
                final ArrayList<ClientScanResult> result = new ArrayList<ClientScanResult>();

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");

                        if ((splitted != null) && (splitted.length >= 4)) {
                            // Basic sanity check
                            String mac = splitted[3];

                            if (mac.matches("..:..:..:..:..:..")) {
                                boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

                                if (!onlyReachables || isReachable) {
                                    result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().toString(), e.toString());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(this.getClass().toString(), e.getMessage());
                    }
                }

                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finishListener.onFinishScan(result);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }
}
