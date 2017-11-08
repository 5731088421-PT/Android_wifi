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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
//    Timer timer;
//    TimerTask timerTask;
    Context context;
//    int time = 0;
    final ApManager apManager = new ApManager();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
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



        if (!apManager.isHotspotOn(context)){
            apManager.configApState(context, this);
        }

//        Log.d("stat", printWifi());
    }

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setHotSpotNameWrap(String name , Context context){

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, "android.permission.OVERRIDE_WIFI_CONFIG");
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    "android.permission.OVERRIDE_WIFI_CONFIG")) {
                showExplanation("Permission Needed", "Rationale", "android.permission.OVERRIDE_WIFI_CONFIG", REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                requestPermission("android.permission.OVERRIDE_WIFI_CONFIG", REQUEST_CODE_ASK_PERMISSIONS);
            }
        } else {
            Toast.makeText(MainActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            apManager.setHotspotName(name,context);
        }




//        int hasChangeConfigPermission = ContextCompat.checkSelfPermission(context,"android.permission.OVERRIDE_WIFI_CONFIG");
//        if(hasChangeConfigPermission != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                    this
//                    , new String[] {"android.permission.OVERRIDE_WIFI_CONFIG"},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            return;
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "granttt");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG, "denied");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    private String printWifi(){
        if (wifiManager.isWifiEnabled()){
            return "yes";
        }
        return "No";

    }
}