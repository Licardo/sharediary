package we.sharediary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import we.sharediary.R;
import we.sharediary.base.BaseActivity;
import we.sharediary.base.Constants;
import we.sharediary.util.Util;

public class
LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        initData();
    }

    private void initData() {
        int version = (int) readPreferences(Constants.VERSION_CODE, 1);
        int curVersion = Util.getVersionCode(LoadingActivity.this);
        if (curVersion > version) {
            writePreferences(Constants.VERSION_CODE, curVersion);
            Intent intent = new Intent();
            intent.setClass(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                }
            }, 1500);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            intent.setClass(LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }
    };
}
