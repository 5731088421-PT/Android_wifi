package com.example.android_wifi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
    public static final String SERVERIP = "192.168.43.1";
    public static final int SERVERPORT = 8080;
    private ServerSocket serverSocket;


    public ChatManager(Context context){
        this.context = context;
//        SERVERIP = getLocalIpAddress();
        myDbHelper = new MyDbHelper(context);
    }

    public void startClient(){


    }

    public void startServer(){
        socketServerThread = new SocketServerThread();
        socketServerThread.start();
        MODE = MODE_SERVER;
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

    public static class SocketServerTask extends AsyncTask<JSONObject, Void, Void> {
        private JSONObject jsonData;
        private boolean success;

        @Override
        protected Void doInBackground(JSONObject... params) {
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
                String response = dataInputStream.readUTF();
                if (response != null && response.equals("Connection Accepted")) {
                    success = true;
                } else {
                    success = false;
                }

            } catch (IOException e) {
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

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            Log.d("server", "running server");

            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
//
            try {
                Log.i(TAG, "Creating server socket");
                serverSocket = new ServerSocket(SERVERPORT);
//
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    String messageFromClient, messageToClient, request;

                    //If no message sent from client, this code will block the program
                    messageFromClient = dataInputStream.readUTF();
                    Log.d("server", "Receive Message");

                    final JSONObject jsondata;

                    try {
                        jsondata = new JSONObject(messageFromClient);
                        boolean addSuccess = myDbHelper.addMessage(ChatMessage.getMessageFrom(jsondata));
                        if(addSuccess){

                        }
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Unable to get request");
                        dataOutputStream.flush();
                    }
                }

            } catch (IOException e) {
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
    }

}
