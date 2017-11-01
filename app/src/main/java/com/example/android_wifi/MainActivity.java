package com.example.android_wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    Timer timer;
    TimerTask timerTask;
    Context context;
    int time = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        ApManager.startTethering(context);
//        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//            }
//        }, new IntentFilter());

//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                Log.d("Timer", Integer.toString(time));
//                time++;
//            }
//        };
//        timer.scheduleAtFixedRate(timerTask,0,5000);


        ApManager apManager = new ApManager();
        if (!apManager.isHotspotOn(context)){
            apManager.configApState(context);
        }

//        Log.d("stat", printWifi());
    }


    private String printWifi(){
        if (wifiManager.isWifiEnabled()){
            return "yes";
        }
        return "No";

    }
}