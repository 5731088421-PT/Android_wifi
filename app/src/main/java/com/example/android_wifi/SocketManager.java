/*
 * Created by NOT on 3/5/18.
 */

package com.example.android_wifi;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

class SocketManager {

    private static final String HOTSPOT_ADDR = "192.168.43.1";
    private static final int SOCKET_PORT = 8098;

    private UDPServer server;
    private UDPClient client;
    private OnDataReceiveListener mOnDataReceiveListener;

    SocketManager(){
        server = new UDPServer();
        client = new UDPClient();
    }

    void startServer(){
        server.startServer();
    }

    void stopServer(){
        server.stopServer();
    }

    void sendObject(Object object, InetAddress address){
        if(address != null){
            client.send(object,address.getHostAddress(),SOCKET_PORT);
        } else {
            client.send(object,HOTSPOT_ADDR,SOCKET_PORT);
        }
    }

    void setListener(OnDataReceiveListener listener){
        mOnDataReceiveListener = listener;
    }

    class UDPServer{

        AsyncTask<Void,Void,Void> asyncTask;
        private boolean isActive;

        @SuppressLint("StaticFieldLeak")
        void startServer(){
            isActive = true;
            asyncTask = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    byte[] recvBuf = new byte[5000];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    DatagramSocket socket = null;
                    try{
                        socket = new DatagramSocket(null); // <-- create an unbound socket first
                        socket.setReuseAddress(true);
                        socket.bind(new InetSocketAddress(SOCKET_PORT)); // <-- now bind it
                        while (isActive){
                            socket.receive(packet);
                            ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                            Object o = is.readObject();
                            if(mOnDataReceiveListener != null){
                                mOnDataReceiveListener.onDataReceive(o,packet.getAddress());
                            }
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

        @SuppressLint("StaticFieldLeak")
        void send(final Object o, final String hostName, final int desPort){
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
//                        int byteCount = packet.getLength();
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

    void showToast(final String text){
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppApplication.getInstance().getContext(),text,Toast.LENGTH_SHORT).show();
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }
}
