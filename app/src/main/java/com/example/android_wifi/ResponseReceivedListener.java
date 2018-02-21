package com.example.android_wifi;

public interface ResponseReceivedListener {
    void onWifiStatusChanged(String value);
    void onBroadcastStatusChanged(String value);
    void onSocketStatusChanged(String value);
}
