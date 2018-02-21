package com.example.android_wifi;

import android.content.Context;

import java.util.UUID;


/**
 * Created by NOT on 2/15/18.
 */

public class BeaconData {

    private String id;

    public BeaconData(Context context){
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }


}
