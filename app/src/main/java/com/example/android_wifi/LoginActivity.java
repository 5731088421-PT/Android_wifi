package com.example.android_wifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
//        mUsernameView = (EditText) findViewById(R.id.username_input);
//        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
////                if (id == R.id.login || id == EditorInfo.IME_NULL) {
////                    attemptLogin();
////                    return true;
////                }
////                return false;
//            }
//        });
    }
}
