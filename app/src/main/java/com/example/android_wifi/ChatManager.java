package com.example.android_wifi;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Created by NOT on 11/16/17.
 */

public class ChatManager {

    public String userName;
    public Timestamp latestReceive;
    public long messageSeq;
    public Context context;

    public ChatManager(){

    }



}
