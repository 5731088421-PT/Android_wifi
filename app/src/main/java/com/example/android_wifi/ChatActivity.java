package com.example.android_wifi;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private MyDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = getApplicationContext();
        dbHelper = new MyDbHelper(getApplicationContext());
        dbHelper.addMockData();

//        List<ChatMessage> messageList = new ArrayList<ChatMessage>();
//        messageList.add(new ChatMessage("not","Panupong","1111"));
//        messageList.add(new ChatMessage("not2","AAAA","11121"));

        recyclerView = (RecyclerView) findViewById(R.id.messages);
        customAdapter = new CustomAdapter(this, dbHelper.fetchChatMessageList());
//        customAdapter = new CustomAdapter(this, messageList);

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
