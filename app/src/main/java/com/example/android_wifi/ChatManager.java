package com.example.android_wifi;

import android.os.Looper;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

/*
 * Created by NOT on 3/13/18.
 */

interface OnDataReceiveListener {
    void onDataReceive(Object data, InetAddress address);
}

interface OnWifiStateChangedListener{
    void onWifiStateChanged(ADHOC_STATUS state);
}

public class ChatManager implements OnDataReceiveListener, OnWifiStateChangedListener{

    private BloomFilter<String> mBloomfilter;
    private AdHocManager mAdHocManager;
    private String username;
    private boolean isRescuer;
    private onAddNewMessageListener mOnAddNewMessageListener;

    ChatManager(String username, boolean isRescuer) {
        this.username = username;
        this.isRescuer = isRescuer;
        mAdHocManager = new AdHocManager(isRescuer);
        mAdHocManager.setupListener(this);
        mAdHocManager.setOnWifiStateChangedListener(this);
        mBloomfilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()),1000,0.001);
        setupBloomFilter();
    }

    private void setupBloomFilter(){
        List<ChatMessage> messages = DBManager.getInstance().fetchChatMessageList();
        for(ChatMessage chatMessage:messages){
            mBloomfilter.put(chatMessage.toBloomfilterString());
        }
    }

    void setOnAddNewMessageListener(onAddNewMessageListener listener){
        mOnAddNewMessageListener = listener;
    }

    void start(){
        mAdHocManager.startAdHoc();
    }

    void stop(){
        mAdHocManager.stopAdhoc();
    }

    void stopSwitchWifi(){
        mAdHocManager.stopSwitchWifi();
    }

    void stopSwitchWifi2(){
        mAdHocManager.stopSwitchWifi2();
    }

    void sendMessage(String message){
        long time = System.currentTimeMillis();
        Timestamp ts = new Timestamp(time);
        ChatMessage chatMessage = new ChatMessage(username, message, ts);
        mBloomfilter.put(chatMessage.toBloomfilterString());
        mAdHocManager.sendViaBroadcast(chatMessage);
        DBManager.getInstance().addMessage(chatMessage);
        mOnAddNewMessageListener.onAddNewMessageToUi(chatMessage);
    }

    void sendBeacon(InetAddress address){
        BeaconData beacon = new BeaconData(isRescuer, mBloomfilter);
        mAdHocManager.sendViaSocket(beacon,address);
    }

    private void checkBloomFilter(BloomFilter<String> filter, InetAddress address){
        List<ChatMessage> messages = DBManager.getInstance().fetchChatMessageList();
        for(ChatMessage chatMessage:messages){
            if(!filter.mightContain(chatMessage.toBloomfilterString())){
                mAdHocManager.sendViaSocket(chatMessage,address);
            }
        }
    }

    @Override
    public void onDataReceive(Object data, InetAddress address) {
        if(data instanceof BeaconData){
           BeaconData beacon = (BeaconData)data;
           showToast("Rec Beacon from " + address.getHostAddress());
           checkBloomFilter(beacon.getBloomfilter(), address);
           //todo Check bloomfilter then send back beacon and data
           if(!address.getHostAddress().equals("192.168.43.1")){
               sendBeacon(address);
               showToast("Sendback Beacon to " + address.getHostAddress());
           }
        }
        else if(data instanceof ChatMessage){
            showToast("Rec Message from " + address.getHostAddress());
            ChatMessage chatMessage = (ChatMessage)data;
            if(!mBloomfilter.mightContain(chatMessage.toBloomfilterString())){
                DBManager.getInstance().addMessage(chatMessage);
                mOnAddNewMessageListener.onAddNewMessageToUi(chatMessage);
                mBloomfilter.put(chatMessage.toBloomfilterString());
                showToast("insert new message");
                if(!address.getHostAddress().equals("192.168.43.1")){
                    mAdHocManager.sendViaBroadcast(chatMessage);
                }
            } else {
                showToast("message exist");
            }
        }
    }

    private void showToast(final String text){
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppApplication.getInstance().getContext(),text,Toast.LENGTH_SHORT).show();
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onWifiStateChanged(ADHOC_STATUS state) {
        if(state == ADHOC_STATUS.CONNECTED) {
            sendBeacon(null);
        }
    }
}