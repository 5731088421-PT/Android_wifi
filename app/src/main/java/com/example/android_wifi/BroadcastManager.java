/*
  Created by NOT on 2/12/18.
 */

package com.example.android_wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

class BroadcastManager {

    Boolean isAutoRun = false;

    private final int BROADCAST_PORT = 1234;
    private Timer timer = new Timer();
    private Context context;

    private AsyncTask<Void,Void,Void> sendTask;

    BroadcastManager() {
        this.context = AppApplication.getInstance().getContext();
    }

    // Send
    void startAutoBroadcast() {
        TimerTask doAsynchronousTask;
        final Handler handler = new Handler();
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        sendBroadcast("Hello from wifiTimer!".getBytes());
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0,getBroadCastInterval());
        isAutoRun = true;
    }

    void stopAutoBroadcast(){
        timer.cancel();
        isAutoRun = false;
    }

    private long getBroadCastInterval(){
        return  1000;
    }

    @SuppressLint("StaticFieldLeak")
    void sendBroadcast(final byte[]  data) {
//        responseReceivedListener.onBroadcastStatusChanged("Broadcasting");
        sendTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Hack Prevent crash (sending should be done using an async task)
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    DatagramSocket socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    InetAddress broadcastAddr = getBroadcast(getIpAddress());
                    if(broadcastAddr != null){
                        DatagramPacket sendPacket = new DatagramPacket(data, data.length, broadcastAddr, BROADCAST_PORT);
                        socket.send(sendPacket);
                        System.out.println(getClass().getName() + "Broadcast packet sent to: " + broadcastAddr.getHostAddress());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException: " + e.getMessage());
                }
                return null;
            }
        };
    }

    private InetAddress getIpAddress() {
        InetAddress inetAddress = null;
        InetAddress myAddr = null;

        try {
            for (Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces(); networkInterface.hasMoreElements();) {

                NetworkInterface singleInterface = networkInterface.nextElement();

                for (Enumeration < InetAddress > IpAddresses = singleInterface.getInetAddresses(); IpAddresses
                        .hasMoreElements();) {
                    inetAddress = IpAddresses.nextElement();

                    if (!inetAddress.isLoopbackAddress() && (singleInterface.getDisplayName()
                            .contains("wlan0") ||
                            singleInterface.getDisplayName().contains("eth0") ||
                            singleInterface.getDisplayName().contains("ap0"))) {

                        myAddr = inetAddress;
                    }
                }
            }

        } catch (SocketException ex) {
            Log.e("ERRROR GetIP", ex.toString());
        }
        return myAddr;
    }

    private InetAddress getBroadcast(InetAddress inetAddr) {

        NetworkInterface temp;
        InetAddress iAddr = null;
        try {
            if(inetAddr != null){
                temp = NetworkInterface.getByInetAddress(inetAddr);

                //null lasttime

                if(temp != null){
                    List<InterfaceAddress> addresses = temp.getInterfaceAddresses();
                    for (InterfaceAddress inetAddress: addresses)

                        iAddr = inetAddress.getBroadcast();
                    Log.d(TAG, "iAddr=" + iAddr);
                }

                return iAddr;
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Log.d(TAG, "getBroadcast" + e.getMessage());
//            responseReceivedListener.onBroadcastStatusChanged("Get broadcast ip error");
        }
        return null;
    }

    // Listen
    void listenBroadcast(){
        Thread mThread = new Thread(new BroadcastListenerThread());
        mThread.start();

//        responseReceivedListener.onBroadcastStatusChanged("Listening");
    }

    class BroadcastListenerThread implements Runnable {
        @Override
        public void run() {
            try {
                //Keep a socket open to listen to all the UDP traffic that is destined for this port
                DatagramSocket socket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
                socket.setBroadcast(true);
                while (true) {
                    Log.i(TAG,"Ready to receive broadcast packets!");

                    //Receive a packet
                    byte[] recvBuf = new byte[15000];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(packet);

                    String thisDeviceIp = getIpAddress().toString().substring(1,getIpAddress().toString().length());
                    if(!thisDeviceIp.equals(packet.getAddress().getHostAddress())){
                        final String text = packet.getAddress().getHostAddress();

                        //Packet received
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"Packet received from: " + text,Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                    String data = new String(packet.getData()).trim();
//                    responseReceivedListener.onBroadcastStatusChanged("Receive " + data + " from " + packet.getAddress().getHostAddress());
                    Log.i(TAG, "Packet received; data: " + data);
                }

            } catch (IOException ex) {
                Log.i(TAG, "Oops" + ex.getMessage());
//                responseReceivedListener.onBroadcastStatusChanged("Broadcast Listening Error!" + ex.getMessage());
            }
        }
    }
}
