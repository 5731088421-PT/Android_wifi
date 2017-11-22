package com.example.android_wifi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by NOT on 11/16/17.
 */

public class ChatManager {

    public static final int MODE_CLIENT = 0;
    public static final int MODE_SERVER = 1;

    public static String USERNAME = "Unnamed User";
    public static int MODE = MODE_CLIENT;
    public Timestamp latestReceive;
    public long messageSeq;
    public Context context;
    public MyDbHelper myDbHelper;

    public SocketServerThread socketServerThread;
    public SocketClientThread socketClientThread;
    public static final String SERVERIP = "192.168.43.1";
    public static final int SERVERPORT = 8080;
    private ServerSocket serverSocket;
    private CustomAdapter adapter;


    public ChatManager(Context context, CustomAdapter adapter){
        this.context = context;
//        SERVERIP = getLocalIpAddress();
        myDbHelper = new MyDbHelper(context);
        this.adapter = adapter;

    }

    public void startClient(){
        socketClientThread = new SocketClientThread();
        socketClientThread.start();
    }

    public void stopClient(){
        if(socketClientThread != null){
            socketClientThread.stopClient();
        }
    }

    public void startServer(){
        socketServerThread = new SocketServerThread();
        socketServerThread.start();
    }

    public void stopServer(){
        if(socketServerThread != null){
            socketServerThread.stopServer();
        }
    }
    private String getLocalIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                        return ip;
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return null;
    }

    public void saveMessageFromArray(JSONArray array){

        try {
            for (int i = 0; i < array.length(); i++) {

                JSONArray arrayTemp = array.getJSONArray(i);
                JSONObject object = arrayTemp.getJSONObject(0);
                ChatMessage message = ChatMessage.getMessageFrom(object);
                if (message != null) {
                    if (myDbHelper.addMessage(message)) {
                        adapter.addNewDataToRecycler(message);
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Task called by client to server
    public class SocketServerTask extends AsyncTask<JSONArray, Void, Void> {
        private JSONArray jsonData;
        private boolean success;

        @Override
        protected Void doInBackground(JSONArray... params) {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            jsonData = params[0];

            try {
                // Create a new Socket instance and connect to host
                socket = new Socket(SERVERIP, SERVERPORT);

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                // transfer JSONObject as String to the server
                dataOutputStream.writeUTF(jsonData.toString());
                Log.i(TAG, "waiting for response from host");

                // Thread will wait till server replies
                String messageFromServer = dataInputStream.readUTF();
                try {
                    final JSONArray array = new JSONArray(messageFromServer);
                    saveMessageFromArray(array);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            } finally {

                // close socket
                if (socket != null) {
                    try {
                        Log.i(TAG, "closing the socket");
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // close input stream
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // close output stream
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (success) {
                Log.d(TAG,"Connection Done");
//                Toast.makeText(PlayListTestActivity.this, "Connection Done", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG,"Unable to connect");
//                Toast.makeText(PlayListTestActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // server wait for incoming message
    private class SocketServerThread extends Thread {
        boolean isRun = true;
        @Override
        public void run() {
            Log.d("server", "running server");

            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                Log.i(TAG, "Creating server socket");
                serverSocket = new ServerSocket(SERVERPORT);

                while (isRun) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    //If no message sent from client, this code will block the program
                    String messageFromClient = dataInputStream.readUTF();
                    Log.d("server", "Receive Message");

                    try {
                        final JSONArray array = new JSONArray(messageFromClient);
                        saveMessageFromArray(array);
                        dataOutputStream.writeUTF(myDbHelper.fetchChatMessageJSON().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Unable to get request");
                        dataOutputStream.flush();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void stopServer(){
            isRun = false;
        }
    }

    private class SocketClientThread extends Thread {
        MyDbHelper myDbHelper = new MyDbHelper(context);
        boolean isRun = true;
        @Override
        public void run() {
            Log.d("client", "running client");

            while (isRun){
                new SocketServerTask().execute(myDbHelper.fetchChatMessageJSON());
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopClient(){
            isRun = false;
        }
    }
}
