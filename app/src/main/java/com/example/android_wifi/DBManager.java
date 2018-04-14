/*
 * Created by NOT on 11/17/17.
 */

package com.example.android_wifi;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {

    private static DBManager instance;

    private static final String DB_NAME = "chatLog";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "message";
    private static final String COL_USERNAME = "username";
    private static final String COL_MESSAGE = "message";
    private static final String COL_SENDTIME = "sendtime";

    private DBManager() {
        super(AppApplication.getInstance(), DB_NAME, null, DB_VERSION);
    }

    static DBManager getInstance(){
        if(instance == null){
            instance = new DBManager();
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT, " + COL_MESSAGE + " TEXT, "
                + COL_SENDTIME + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    boolean findMessage(ChatMessage message){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query
                (TABLE_NAME, null,  COL_SENDTIME+" =?", new String[]{message.timeStamp.toString()}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!(cursor != null && cursor.isAfterLast())) {
            String cUsername = cursor.getString(1);
            String cMessage = cursor.getString(2);

            if (cUsername.equals(message.username)
                    && cMessage.equals(message.message)){
                sqLiteDatabase.close();
                return true;
            }
            cursor.moveToNext();
        }

        sqLiteDatabase.close();
        return false;
    }

    public boolean addMessage(ChatMessage chatMessage){

        if(findMessage(chatMessage)){
            return false;
        }

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, chatMessage.username);
        values.put(COL_MESSAGE, chatMessage.message);
        values.put(COL_SENDTIME, chatMessage.timeStamp.toString());

        sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();
        return true;
    }

    public void clearDB(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,null,null);
        sqLiteDatabase.close();
    }

    List<ChatMessage> fetchChatMessageList(){
        List<ChatMessage> messages = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query
                (TABLE_NAME, null, null, null, null, null, COL_SENDTIME + " ASC");

        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!(cursor != null && cursor.isAfterLast())) {
            ChatMessage chatMessage = new ChatMessage(
                    cursor.getString(1),
                    cursor.getString(2),
                    Timestamp.valueOf(cursor.getString(3)));
            messages.add(chatMessage);
            cursor.moveToNext();
        }

        sqLiteDatabase.close();
        return messages;
    }

    public JSONArray fetchChatMessageJSON(){

        JSONArray array = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query
                (TABLE_NAME, null, null, null, null, null, COL_SENDTIME);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        while(!cursor.isAfterLast()) {
            ChatMessage chatMessage = new ChatMessage(
                    cursor.getString(1),
                    cursor.getString(2),
                    Timestamp.valueOf(cursor.getString(3)));
            array.put(chatMessage.toJSON());
            cursor.moveToNext();
        }
        sqLiteDatabase.close();
        return array;
    }

}
