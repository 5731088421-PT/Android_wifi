/*
 * Created by NOT on 11/17/17.
 */
package com.example.android_wifi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



class ChatMessage implements Serializable {
    private static final String USERNAME = "USERNAME";
    private static final String MESSAGE = "MESSAGE";
    private static final String TIMESTAMP = "TIMESTAMP";

    String username;
    String message;
    Timestamp timeStamp;

    ChatMessage(String username, String message, Timestamp timeStamp){
        this.username = username;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    JSONArray toJSON(){
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            jsonObject.put(USERNAME,this.username);
            jsonObject.put(MESSAGE,this.message);
            jsonObject.put(TIMESTAMP,this.timeStamp);
            array.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

//    public static ChatMessage getMessageFrom(JSONObject jsonObject){
//
//        String userName = null;
//        String message = null;
//        String timeStamp = null;
//        ChatMessage chatMessage = null;
//        try {
//            userName = jsonObject.getString(USERNAME);
//            message = jsonObject.getString(MESSAGE);
//            timeStamp = jsonObject.getString(TIMESTAMP);
//            chatMessage = new ChatMessage(userName, message, timeStamp);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return chatMessage;
//    }

    String toBloomfilterString(){
        return message+username+timeStamp;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

}
