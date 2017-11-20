package com.example.android_wifi;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.sql.Timestamp;

public class ChatActivity extends AppCompatActivity {

    private Context context;
    private ChatManager chatManager;
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private MyDbHelper dbHelper;
    private EditText editText;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = getApplicationContext();
        editText = (EditText) findViewById(R.id.message_input);
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == R.id.send || actionId == EditorInfo.IME_NULL) {
                            sendMessage();
                            return true;
                        }
                        return false;
                    }
                }
        );
        sendButton = (ImageButton) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        dbHelper = new MyDbHelper(getApplicationContext());
//        dbHelper.addMockData();

        recyclerView = (RecyclerView) findViewById(R.id.messages);
        customAdapter = new CustomAdapter(this, dbHelper.fetchChatMessageList());

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        chatManager = new ChatManager(context, customAdapter);

        if(ChatManager.MODE == ChatManager.MODE_SERVER){
            chatManager.startServer();
        }
        else if(ChatManager.MODE == ChatManager.MODE_CLIENT){
            chatManager.startClient();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatManager.stopClient();
        chatManager.stopServer();
    }

    public void sendMessage(){
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        String ts =  tsTemp.toString();
        String message = editText.getText().toString();
        ChatMessage chatMessage = new ChatMessage(ChatManager.USERNAME, message, ts);
//        if(ChatManager.MODE == ChatManager.MODE_CLIENT){
//            JSONArray jsonObject = chatMessage.toJSON();
//            new ChatManager.SocketServerTask().execute(jsonObject);
//        }
        dbHelper.addMessage(chatMessage);
        customAdapter.addNewDataOnTop(chatMessage);
        editText.setText("");
    }
}