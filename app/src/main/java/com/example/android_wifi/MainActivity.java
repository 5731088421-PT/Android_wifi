package com.example.android_wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ResponseReceivedListener{

    ApManager mApManager;
    BroadcastManager mBroadcastManager;
    BroadcastReceiver wifiReceiver;
    BroadcastReceiver connectivityReceiver;

    Button clientButton;
    Button hotspotButton;
    Button autoWifiButton;
    Button startClientButton;
    Button startHotspotButton;
    Button autoSocketButton;
    Button listenBroadcastButton;
    Button sendBroadcastButton;
    Button autoBroadcastButton;
    Button chatButton;
    Button clearChatButton;

    TextView wifiStatusTextView;
    TextView broadcastStatusTextView;
    TextView socketStatusTextView;


    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        context = getApplicationContext();
        mApManager = new ApManager(this);
        mApManager.responseReceivedListener = this;

        mApManager.showWritePermissionSettings(false);
        mBroadcastManager = new BroadcastManager();

        bindUI();

        initReceiver();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(connectivityReceiver);
    }

    private void initReceiver(){
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getExtras().getInt(WifiManager.EXTRA_WIFI_STATE)){
                    case WifiManager.WIFI_STATE_ENABLED:
                        mApManager.connectToAp(mApManager.mWifiConfig);
                        setWifiStatus("Wifi Enabled");
                        return;
                    case WifiManager.WIFI_STATE_ENABLING:
                        setWifiStatus("Enabling Wifi");
                        return;
                    case WifiManager.WIFI_STATE_DISABLING:
                        setWifiStatus("Disabling Wifi");
                        return;
                    case WifiManager.WIFI_STATE_DISABLED:
                        setWifiStatus("Wifi Disabled");
                        return;
                }
            }
        };

        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean status = intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
                if(status){
                    setWifiStatus("Finding Hotspot..");
                } else {
                    setWifiStatus("Connected");
                }
            }
        };
    }

    private void bindUI(){
        clientButton = (Button) findViewById(R.id.clientButton);
        clientButton.setOnClickListener(buttonListenner);
        hotspotButton = (Button) findViewById(R.id.hotspotButton);
        hotspotButton.setOnClickListener(buttonListenner);
        autoWifiButton = (Button) findViewById(R.id.autoWifiButton);
        autoWifiButton.setOnClickListener(buttonListenner);

        startClientButton = (Button) findViewById(R.id.startClientButton);
        startClientButton.setOnClickListener(buttonListenner);
        startHotspotButton = (Button) findViewById(R.id.startServerButton);
        startHotspotButton.setOnClickListener(buttonListenner);
        autoSocketButton = (Button) findViewById(R.id.autoSocketButton);
        autoSocketButton.setOnClickListener(buttonListenner);

        listenBroadcastButton = (Button) findViewById(R.id.startListenBroadcastButton);
        listenBroadcastButton.setOnClickListener(buttonListenner);
        sendBroadcastButton = (Button) findViewById(R.id.startSendBroadcastButton);
        sendBroadcastButton.setOnClickListener(buttonListenner);
        autoBroadcastButton = (Button) findViewById(R.id.autoBroadcastButton);
        autoBroadcastButton.setOnClickListener(buttonListenner);

        chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(buttonListenner);
        clearChatButton = (Button) findViewById(R.id.clearChatButton);
        clearChatButton.setOnClickListener(buttonListenner);

        wifiStatusTextView = (TextView) findViewById(R.id.wifiStatus);
        broadcastStatusTextView = (TextView) findViewById(R.id.broadcastStatus);
        socketStatusTextView = (TextView) findViewById(R.id.socketStatus);
    }

    private View.OnClickListener buttonListenner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.clientButton){
                mApManager.clientMode();
                setWifiStatus("Client...");
            }
            else if(v.getId() == R.id.hotspotButton){
                mApManager.hotspotMode();
                setWifiStatus("Hotspot...");
            }
            else if(v.getId() == R.id.startClientButton){
                Intent intent = new Intent(context, ClientActivity.class);
                startActivity(intent);
                ChatManager.MODE = ChatManager.MODE_CLIENT;
            }
            else if(v.getId() == R.id.startServerButton) {
                Intent intent = new Intent(context, ServerActivity.class);
                startActivity(intent);
                ChatManager.MODE = ChatManager.MODE_SERVER;
            }
            else if(v.getId() == R.id.startListenBroadcastButton){
                mBroadcastManager.listenBroadcast();
            }
            else if(v.getId() == R.id.startSendBroadcastButton){
                mBroadcastManager.sendBroadcast("Test Hello!");
            }
            else if(v.getId() == R.id.chatButton){
                Intent intent = new Intent(context,ChatActivity.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.clearChatButton){
                MyDbHelper myDbHelper = new MyDbHelper(context);
                myDbHelper.clearDB();
            }
            else if(v.getId() == R.id.autoWifiButton){
                mApManager.startAutoSwitchWifi();
            }

        }
    };

    public void setWifiStatus(String text){
        wifiStatusTextView.setText(text);
    }

    public void setBroadcastStatus(String text){
        wifiStatusTextView.setText(text);
    }

    public void setSocketStatus(String text){
        wifiStatusTextView.setText(text);
    }



    @Override
    public void onWifiStatusChanged(String value) {
        setWifiStatus(value);
    }

    @Override
    public void onBroadcastStatusChanged(String value) {
        setBroadcastStatus(value);
    }

    @Override
    public void onSocketStatusChanged(String value) {
        setSocketStatus(value);
    }
}