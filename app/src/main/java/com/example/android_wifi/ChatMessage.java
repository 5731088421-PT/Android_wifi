package com.example.android_wifi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by NOT on 11/17/17.
 */

public class ChatMessage {
    private static final String USERNAME = "USERNAME";
    private static final String MESSAGE = "MESSAGE";
    private static final String TIMESTAMP = "TIMESTAMP";

    public String username;
    public String message;
    public String timeStamp;

    public ChatMessage(String username, String message, String timeStamp){
        this.username = username;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(USERNAME,this.username);
            jsonObject.put(MESSAGE,this.message);
          jsonObject.put(TIMESTAMP,this.timeStamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static ChatMessage getMessageFrom(JSONObject jsonObject){

        String userName = null;
        String message = null;
        String timeStamp = null;
        try {
           userName = jsonObject.getString(USERNAME);
           message = jsonObject.getString(MESSAGE);
           timeStamp = jsonObject.getString(TIMESTAMP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatMessage chatMessage = new ChatMessage(userName, message, timeStamp);

        return chatMessage;
    }


}
