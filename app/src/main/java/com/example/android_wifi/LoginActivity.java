package com.example.android_wifi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameView;
    private boolean isRescuer = false;

    private SharedPreferences mSharePreferences;
    private SharedPreferences.Editor mSharePreferenceEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mUsernameView = (EditText) findViewById(R.id.username_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });
//        mUsernameView.setCursorVisible(false);

        mSharePreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String username = mSharePreferences.getString("USER-NAME","");
        if(!username.equals("")){
            mUsernameView.setText(username);
        }
        mSharePreferenceEditor = mSharePreferences.edit();

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void onRadioClick(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.radio_rescuer:
               isRescuer = checked;
               return;
            case  R.id.radio_victim:
                isRescuer = !checked;
        }
    }

    public void login(){
        mUsernameView.setError(null);
        String username = mUsernameView.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError("This field is required");
            mUsernameView.requestFocus();
            return;
        }

        mSharePreferenceEditor.putString("USER-NAME",username);
        mSharePreferenceEditor.putBoolean("USER-ROLE",isRescuer);
        mSharePreferenceEditor.commit();

        Intent intent = new Intent(getApplication().getApplicationContext(),ChatActivity.class);
        intent.putExtra("userName",username);
        intent.putExtra("userRole",isRescuer);
        startActivity(intent);
        finish();
    }
}