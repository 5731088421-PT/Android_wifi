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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    ApManager apManager;
    WifiConfiguration wifiConfig;
    Timer timer;

    Button clientButton;
    Button hotspotButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apManager.showWritePermissionSettings(false);
        context = getApplicationContext();

        clientButton = (Button) findViewById(R.id.clientButton);
        clientButton.setOnClickListener(buttonListenner);
        hotspotButton = (Button) findViewById(R.id.hotspotButton);
        hotspotButton.setOnClickListener(buttonListenner);

        apManager = new ApManager(this);
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "MANET";
        wifiConfig.preSharedKey = "123456789";
        wifiConfig.allowedAuthAlgorithms.set(0);
        wifiConfig.allowedKeyManagement.set(4);
        clientMode();
    }

    private View.OnClickListener buttonListenner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.clientButton){
                clientMode();
                Intent intent = new Intent(context, ClientActivity.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.hotspotButton){
                hotspotMode();
                Intent intent = new Intent(context, ServerActivity.class);
                startActivity(intent);
            }
        }
    };


    private void hotspotMode(){
        if(apManager.isWifiApEnabled()){
            apManager.setWifiApEnabled(null, false);
        }
        apManager.setWifiApEnabled(wifiConfig, true);
    }

    private void clientMode(){
        apManager.connectToAp(wifiConfig);
    }

}