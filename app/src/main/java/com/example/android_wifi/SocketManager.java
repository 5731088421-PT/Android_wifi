package com.example.android_wifi;
/*
 * Created by NOT on 3/5/18.
 */

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class SocketManager {

    private static final String HOTSPOT_ADDR = "192.168.45.1";
    private static final int SOCKET_PORT = 8080;

    private UDPServer server;
    private UDPClient client;

    public SocketManager(){
        server = new UDPServer();
        client = new UDPClient();
    }

    void startServer(){
        server.startServer();
    }

    void stopServer(){
        server.stopServer();
    }

    void sendBeaconToHotspot(BeaconData beacon){
        client.send(beacon, HOTSPOT_ADDR, SOCKET_PORT);
    }

    class UDPServer{

        AsyncTask<Void,Void,Void> asyncTask;
        private boolean isActive = true;

        @SuppressLint("StaticFieldLeak")
        public void startServer(){
            isActive = true;
            asyncTask = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    byte[] recvBuf = new byte[5000];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    DatagramSocket socket = null;

                    try{
                        socket = new DatagramSocket(SOCKET_PORT);
                        while (isActive){
                            socket.receive(packet);
                            int byteCount = packet.getLength();
                            ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                            Object o = is.readObject();

                        }
                    }
                    catch (Exception e){
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
        void stopServer(){
            isActive = false;
        }
    }

    class UDPClient{

        AsyncTask<Void,Void,Void> asyncTask;
        String message;
        InetAddress address;

        @SuppressLint("StaticFieldLeak")
        public void send(final Object o, final String hostName, final int desPort){
            asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    DatagramSocket socket = null;
                    try{
                        socket = new DatagramSocket();

                        InetAddress address = InetAddress.getByName(hostName);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
                        ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));

                        os.flush();
                        os.writeObject(o);
                        os.flush();

                        byte[] sendBuf = byteStream.toByteArray();
                        DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, desPort);
                        int byteCount = packet.getLength();
                        socket.send(packet);
                    }
                    catch (Exception e){
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
}
