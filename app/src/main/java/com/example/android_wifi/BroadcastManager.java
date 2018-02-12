package com.example.android_wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.content.ContentValues.TAG;

/**
 * Created by NOT on 2/12/18.
 */

public class BroadcastManager {

    private Context mContext;

    public BroadcastManager(Context ctx){
        this.mContext = ctx;
    }

    // Send

    public void sendBroadcast(String messageStr) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] sendData = messageStr.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), 1234);
            socket.send(sendPacket);
//            System.out.println(getClass().getName() + "Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // handle null somehow
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if(dhcp == null) return InetAddress.getByName("255.255.255.255");
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        int ipAddress = getCodecIpAddress(wifi, cm.getActiveNetworkInfo());
        int broadcast = (ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    public static int getCodecIpAddress(WifiManager wm, NetworkInfo wifi){
        WifiInfo wi = wm.getConnectionInfo();
        if(wifi != null && wifi.isConnected())
            return wi.getIpAddress(); //normal wifi
        Method method = null;
        try {
            method = wm.getClass().getDeclaredMethod("getWifiApState");
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(method != null)
            method.setAccessible(true);
        int actualState = -1;
        try {
            if(method!=null)
                actualState = (Integer) method.invoke(wm, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if(actualState==13){  //if wifiAP is enabled
            try {
                InetAddress ip = InetAddress.getByName("192.168.43.1");
                byte[] bytes = ip.getAddress();
                return convertIP2Int(bytes);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private static int convertIP2Int(byte[] ipAddress){
        return (int) (Math.pow(256, 3)*Integer.valueOf(ipAddress[3] & 0xFF)+Math.pow(256, 2)*Integer.valueOf(ipAddress[2] & 0xFF)+256*Integer.valueOf(ipAddress[1] & 0xFF)+Integer.valueOf(ipAddress[0] & 0xFF));
    }

    // Listen

    public void listenBroadcast(){
        Thread mThread = new Thread(new BroadcastListenerThread());
        mThread.start();
    }

    class BroadcastListenerThread implements Runnable {

        @Override
        public void run() {

            try {
                //Keep a socket open to listen to all the UDP trafic that is destined for this port
                DatagramSocket socket = new DatagramSocket(1234, InetAddress.getByName("0.0.0.0"));
                socket.setBroadcast(true);

                while (true) {
                    Log.i(TAG,"Ready to receive broadcast packets!");

                    //Receive a packet
                    byte[] recvBuf = new byte[15000];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(packet);

                    //Packet received
                    Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                    String data = new String(packet.getData()).trim();
                    Log.i(TAG, "Packet received; data: " + data);

                }
            } catch (IOException ex) {
                Log.i(TAG, "Oops" + ex.getMessage());
            }
        }
    }
}
