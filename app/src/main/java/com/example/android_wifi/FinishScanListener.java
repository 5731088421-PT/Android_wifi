package com.example.android_wifi;

import java.util.ArrayList;

/**
 * Created by NOT on 11/13/17.
 */

public interface FinishScanListener {
    /**
     * Interface called when the scan method finishes. Network operations should not execute on UI thread
     * @param clients
     */
    public void onFinishScan(ArrayList<ClientScanResult> clients);
}
