package com.example.android_wifi;

import android.net.wifi.p2p.WifiP2pManager;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by NOT on 3/13/18.
 */

public class ChatManager{

    private BloomFilter<String> mBloomfilter;
    private DBManager dbManager;

    public ChatManager() {
        mBloomfilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()),1000,0.001);
        dbManager = new DBManager();
        setupBloomFilter();
    }

    private void setupBloomFilter(){
        List<ChatMessage> messages = dbManager.fetchChatMessageList();
        for(ChatMessage message:messages){
            mBloomfilter.put(message.message+message.username);
        }
    }




}
