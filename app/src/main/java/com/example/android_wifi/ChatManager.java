package com.example.android_wifi;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;

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
        mAdHocManager = new AdHocManager();
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

    void sendMessage(String message){
        long time = System.currentTimeMillis();
        Timestamp tsTemp = new Timestamp(time);
        ChatMessage chatMessage = new ChatMessage(username, message, tsTemp.toString());
        if(mAdHocManager.getCurrentMode() == WIFI_MODE.CLIENT){
            mAdHocManager.sendMessageViaSocket(chatMessage,null);
        } else {
            mAdHocManager.sendMessageViaBroadcast(chatMessage);
        }
        DBManager.getInstance().addMessage(chatMessage);
        mOnAddNewMessageListener.onAddNewMessage(chatMessage);
        mBloomfilter.put(chatMessage.toBloomfilterString());
    }

    void sendBeacon(){
        BeaconData beacon = new BeaconData(isRescuer, mBloomfilter);
        mAdHocManager.sendMessageViaSocket(beacon,null);
    }

    private void checkBloomFilter(BloomFilter<String> filter, InetAddress address){
        List<ChatMessage> messages = DBManager.getInstance().fetchChatMessageList();
        for(ChatMessage chatMessage:messages){
            if(filter.mightContain(chatMessage.toBloomfilterString())){
                mAdHocManager.sendMessageViaSocket(chatMessage,address);
            }
        }
    }

    @Override
    public void onDataReceive(Object data, InetAddress address) {
        if(data instanceof BeaconData){
           BeaconData beacon = (BeaconData)data;
           checkBloomFilter(beacon.getBloomfilter(), address);
           //todo Check bloomfilter then send back beacon and data
        }
        else if(data instanceof ChatMessage){
            ChatMessage chatMessage = (ChatMessage)data;
            DBManager.getInstance().addMessage(chatMessage);
            mOnAddNewMessageListener.onAddNewMessage(chatMessage);
            mBloomfilter.put(chatMessage.toBloomfilterString());
        }
    }

    @Override
    public void onWifiStateChanged(ADHOC_STATUS state) {
        if(state == ADHOC_STATUS.CONNECTED) {
            sendBeacon();
        }
    }
}
