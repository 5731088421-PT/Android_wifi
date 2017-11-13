package com.example.android_wifi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private WifiManager wifiManager;
//    Timer timer;
//    TimerTask timerTask;
//    int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

//        ApManager.startTethering(context);
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
        settingPermission();
//        ApManager.scanAndConnect(context, wifiManager);

//            ApManager.setHotspot(context, false);

            ApManager.setHotspot(context, true);


    }

    public void settingPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }
    }
}