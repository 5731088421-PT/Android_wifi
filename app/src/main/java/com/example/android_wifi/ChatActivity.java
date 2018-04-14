package com.example.android_wifi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

interface onAddNewMessageListener{
    void onAddNewMessageToUi(ChatMessage message);
}

public class ChatActivity extends AppCompatActivity implements onAddNewMessageListener{

    private CustomAdapter customAdapter;
    private EditText editText;
    private LinearLayoutManager llm;
    private ChatManager mChatManager;



    //debug
    private Button stopAutoButton;
    private Button sendBloomButton;
    private Button stopAuto2Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String username = intent.getExtras().getString("userName");
        boolean isRescuer = intent.getExtras().getBoolean("userRole");
        mChatManager = new ChatManager(username,isRescuer);
        mChatManager.setOnAddNewMessageListener(this);



        setupUI();
        mChatManager.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatManager.stop();
    }

    private void setupUI(){
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

        ImageButton sendButton = (ImageButton) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.messages);
        customAdapter = new CustomAdapter(this, DBManager.getInstance().fetchChatMessageList());

        recyclerView.setAdapter(customAdapter);
        recyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(false);

        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                llm.scrollToPosition(customAdapter.getItemCount()-1);
            }
        });
        stopAutoButton = (Button)findViewById(R.id.saButton);
        stopAutoButton.setOnClickListener(buttonListenner);
        stopAuto2Button = (Button)findViewById(R.id.saButton2);
        stopAuto2Button.setOnClickListener(buttonListenner);
        sendBloomButton = (Button)findViewById(R.id.sbButton);
        sendBloomButton.setOnClickListener(buttonListenner);

    }
    private View.OnClickListener buttonListenner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.saButton) {
                mChatManager.stopSwitchWifi();
            } else if (v.getId() == R.id.saButton2) {
                mChatManager.stopSwitchWifi2();
            } else {
                mChatManager.sendBeacon(null);
            }
        }
    };

    void sendMessage(){
        editText.setError(null);
        String message = editText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            editText.setError("Please enter message!");
            editText.requestFocus();
            return;
        }
        mChatManager.sendMessage(message);
        scrollToBottom();
        editText.setText("");
    }

    public void scrollToBottom(){
        llm.scrollToPosition(customAdapter.getItemCount()-1);
    }

    @Override
    public void onAddNewMessageToUi(ChatMessage message) {
        customAdapter.addNewDataToRecycler(message);
    }

}
