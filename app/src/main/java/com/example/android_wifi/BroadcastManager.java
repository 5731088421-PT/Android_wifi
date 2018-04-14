/*
*  Created by NOT on 2/12/18.
*/

package com.example.android_wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;

import static android.content.ContentValues.TAG;

class BroadcastManager {

    private boolean isServerActive = false;
    private final int BROADCAST_PORT = 8099;

    private BroadcastServer server;
    private BroadcastClient client;
    private OnDataReceiveListener mOnDataReceiveListener;

    BroadcastManager() {
        server = new BroadcastServer();
        client = new BroadcastClient();
    }

    void startServer(){
        server.startServer();
    }

    void stopServer(){
        server.stopServer();
    }

    void sendObject(Object object){
        client.send(object);
    }

    void setListener(OnDataReceiveListener listener){
        mOnDataReceiveListener = listener;
    }


    class BroadcastServer {

        AsyncTask<Void, Void, Void> asyncTask;
        private boolean isActive;

        @SuppressLint("StaticFieldLeak")
        void startServer() {
            isActive = true;
            asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    WifiManager wifi;
                    wifi = (WifiManager) AppApplication.getInstance().getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiManager.MulticastLock mLock = wifi.createMulticastLock("lock");
                    mLock.acquire();
                    byte[] recvBuf = new byte[5000];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    DatagramSocket socket = null;
                    try{
                        socket = new DatagramSocket(null);
                        socket.setReuseAddress(true);
                        socket.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"),BROADCAST_PORT));
//                        socket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
                        socket.setBroadcast(true);
                        while (isActive) {
                            socket.receive(packet);
                            ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                            Object o = is.readObject();
                            if (mOnDataReceiveListener != null && !packet.getAddress().getHostAddress().equals(getIpAddress().getHostAddress())) {
                                mOnDataReceiveListener.onDataReceive(o, packet.getAddress());
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        if(socket != null){
                            socket.close();
                        }
                        if(mLock != null){
                            mLock.release();
                        }
                    }
                    return  null;
                }
            };
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        void stopServer(){
            isActive = false;
        }
    }

    class BroadcastClient {

        AsyncTask<Void, Void, Void> asyncTask;

        @SuppressLint("StaticFieldLeak")
        void send(final Object o) {
            asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    DatagramSocket socket = null;
                    try{
                        socket = new DatagramSocket();
                        socket.setBroadcast(true);
//                        InetAddress broadcastAddr = getBroadcast(getIpAddress());
                        InetAddress broadcastAddr = InetAddress.getByName("192.168.43.255");
//                        InetAddress.

                        if (broadcastAddr != null) {
                            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
                            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));

                            os.flush();
                            os.writeObject(o);
                            os.flush();

                            byte[] sendBuf = byteStream.toByteArray();
                            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddr, BROADCAST_PORT);
                            socket.send(packet);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        if(socket != null){
                            socket.close();
                        }
                    }
                    return null;
                }

            };
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    // Send
//    void startAutoBroadcast() {
//        TimerTask doAsynchronousTask;
//        final Handler handler = new Handler();
//        doAsynchronousTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        sendBroadcast("Hello from wifiTimer!".getBytes());
//                    }
//                });
//            }
//        };
//        timer.schedule(doAsynchronousTask, 0,getBroadCastInterval());
//        isAutoRun = true;
//    }
//
//    void stopAutoBroadcast(){
//        timer.cancel();
//        isAutoRun = false;
//    }
//
//    private long getBroadCastInterval(){
//        return  1000;
//    }


//    @SuppressLint("StaticFieldLeak")
//    void sendBroadcast(final Object object) {
//        AsyncTask<Void, Void, Void> sendTask = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                // Hack Prevent crash (sending should be done using an async task)
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//
//                try {
//                    DatagramSocket socket = new DatagramSocket();
//                    socket.setBroadcast(true);
//                    InetAddress broadcastAddr = getBroadcast(getIpAddress());
//                    if (broadcastAddr != null) {
//                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
//                        ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
//
//                        os.flush();
//                        os.writeObject(object);
//                        os.flush();
//
//                        byte[] sendBuf = byteStream.toByteArray();
//                        DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddr, BROADCAST_PORT);
//                        int byteCount = packet.getLength();
//                        socket.send(packet);
//                        System.out.println(getClass().getName() + "Broadcast packet sent to: " + broadcastAddr.getHostAddress());
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "IOException: " + e.getMessage());
//                }
//                return null;
//            }
//        };
//        sendTask.execute();
//    }

    // Listen



//    class BroadcastListenerThread implements Runnable {
//        @Override
//        public void run() {
//            try {
//                //Keep a socket open to listen to all the UDP traffic that is destined for this port
//                DatagramSocket socket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
//                socket.setBroadcast(true);
//                while (isServerActive) {
//                    Log.i(TAG,"Ready to receive broadcast packets!");
//                    //Receive a packet
//                    byte[] recvBuf = new byte[5000];
//                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
//                    socket.receive(packet);
//
//                    ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
//                    ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
//                    Object o = is.readObject();
//                    if(mOnDataReceiveListener != null){
//                        mOnDataReceiveListener.onDataReceive(o,packet.getAddress());
//                    }
//
////              Check if from same device
////                    String thisDeviceIp = getIpAddress().toString().substring(1,getIpAddress().toString().length());
////                    if(!thisDeviceIp.equals(packet.getAddress().getHostAddress())){
////                        final String text = packet.getAddress().getHostAddress();
////
////                        //Packet received
////                        Handler handler = new Handler(Looper.getMainLooper());
////                        handler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                Toast.makeText(context,"Packet received from: " + text,Toast.LENGTH_LONG).show();
////                            }
////                        });
////                    }
////
////                    Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
////                    String data = new String(packet.getData()).trim();
//////                    responseReceivedListener.onBroadcastStatusChanged("Receive " + data + " from " + packet.getAddress().getHostAddress());
////                    Log.i(TAG, "Packet received; data: " + data);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


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


}
