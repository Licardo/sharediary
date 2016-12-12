package we.sharediary.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.facebook.drawee.view.SimpleDraweeView;

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
        initView();
        initData();
    }

    private void initView(){
        SimpleDraweeView ivBj = (SimpleDraweeView) findViewById(R.id.iv_bj);
        SimpleDraweeView ivGz = (SimpleDraweeView) findViewById(R.id.iv_gz);
        SimpleDraweeView ivHz = (SimpleDraweeView) findViewById(R.id.iv_hz);
        ivBj.setImageURI(Uri.parse("http://bmob-cdn-7671.b0.upaiyun.com/2016/12/11/1bf9447240ca5fc38003a14870b5a03c.png"));
        ivGz.setImageURI(Uri.parse("http://bmob-cdn-7671.b0.upaiyun.com/2016/12/11/d7fafa1c409d3dff8080193bd628bdce.png"));
        ivHz.setImageURI(Uri.parse("http://bmob-cdn-7671.b0.upaiyun.com/2016/12/11/fe9738634014e9008050a0d8776405d1.png"));
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
