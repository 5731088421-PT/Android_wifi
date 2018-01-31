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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.sql.Time;
import java.util.ArrayList;
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
    Button startClientButton;
    Button startHotspotButton;
    Button chatButton;
    Button clearChatButton;
    TextView statusText;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apManager = new ApManager(this);
        apManager.showWritePermissionSettings(false);
        context = getApplicationContext();

        clientButton = (Button) findViewById(R.id.clientButton);
        clientButton.setOnClickListener(buttonListenner);
        hotspotButton = (Button) findViewById(R.id.hotspotButton);
        hotspotButton.setOnClickListener(buttonListenner);
        startClientButton = (Button) findViewById(R.id.startClientButton);
        startClientButton.setOnClickListener(buttonListenner);
        startHotspotButton = (Button) findViewById(R.id.startServerButton);
        startHotspotButton.setOnClickListener(buttonListenner);
        chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(buttonListenner);
        clearChatButton = (Button) findViewById(R.id.clearChatButton);
        clearChatButton.setOnClickListener(buttonListenner);
        statusText = (TextView) findViewById(R.id.statustext);

        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "MANET";
        wifiConfig.preSharedKey = "123456789";
        wifiConfig.allowedAuthAlgorithms.set(0);
        wifiConfig.allowedKeyManagement.set(4);
    }

    private View.OnClickListener buttonListenner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.clientButton){
                clientMode();
                setStatus("This device is Client");
            }
            else if(v.getId() == R.id.hotspotButton){
                hotspotMode();
                setStatus("This device is Server");
            }
            else if(v.getId() == R.id.startClientButton){
//                Intent intent = new Intent(context, ClientActivity.class);
//                startActivity(intent);
                ChatManager.MODE = ChatManager.MODE_CLIENT;

            }
            else if(v.getId() == R.id.startServerButton){
//                Intent intent = new Intent(context, ServerActivity.class);
//                startActivity(intent);
                ChatManager.MODE = ChatManager.MODE_SERVER;
            }else if(v.getId() == R.id.chatButton){
                Intent intent = new Intent(context,LoginActivity.class);
                startActivity(intent);
            }else if(v.getId() == R.id.clearChatButton){
                MyDbHelper myDbHelper = new MyDbHelper(context);
                myDbHelper.clearDB();
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

    public void setStatus(String text){
        statusText.setText(text);
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);

        ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }
}