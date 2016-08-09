package com.highfaner.highfaner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.highfaner.highfaner.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends EaseBaseActivity{
    private EditText usernameView;
    private EditText pwdView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if(EMClient.getInstance().isLoggedInBefore()){
            //enter to main activity directly if you logged in before.
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        usernameView = (EditText) findViewById(R.id.et_username);
        pwdView = (EditText) findViewById(R.id.et_password);
        Button loginBtn = (Button) findViewById(R.id.btn_login);


        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount("zhangq", "123");

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!LoginActivity.this.isFinishing()){
                                int errorCode = e.getErrorCode();
                                if(errorCode== EMError.NETWORK_ERROR){
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
                                    Toast.makeText(getApplicationContext(),"registration_failed_without_permission", Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Registration_failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        }).start();


        loginBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //login
                EMClient.getInstance().login(usernameView.getText().toString(), pwdView.getText().toString(), new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {
                        
                    }
                    
                    @Override
                    public void onError(int code, String error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
        
    }
}
