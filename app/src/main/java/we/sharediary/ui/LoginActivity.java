package we.sharediary.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SQLQueryListener;
import we.sharediary.R;
import we.sharediary.base.BaseActivity;
import we.sharediary.base.Constants;
import we.sharediary.table.WEUser;
import we.sharediary.util.DialogUtils;
import we.sharediary.util.Util;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button btnAccquire, btnLogin;
    private EditText etPhone, etPassword;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int time = 59;
    private boolean isExistUser = true;//是否存在用户
    private String tempPhone = "";//防止用户第一次输错手机号（上一次的号码）
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
    }

    private void initView() {
        etPassword = (EditText) findViewById(R.id.et_password);
        etPhone = (EditText) findViewById(R.id.et_phone);
        btnAccquire = (Button) findViewById(R.id.btn_accquire);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    private void initData() {
        mDialog = DialogUtils.getProgressBar(this);
        btnAccquire.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        autoLogin();
    }

    private void autoLogin() {
        String phone = (String) readPreferences(Constants.USER_PHONE, 0);
        if (!Util.isBlank(phone) && Util.isMobileNO(phone)) {
//            getUserInfo(phone);
            Intent intent = new Intent(LoginActivity.this, WEActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        closeKey(etPhone);
        String phone = etPhone.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btn_accquire:
                //umeng
                MobclickAgent.onEvent(LoginActivity.this, "Accquire_Code");
                //定义与事件相关的属性信息  zhuge
                try {
                    JSONObject eventObject = new JSONObject();
                    eventObject.put("分类", "验证码");
                    eventObject.put("名称", phone);
                    //记录事件
                    ZhugeSDK.getInstance().track(getApplicationContext(), "获取验证码",
                            eventObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isValidated(phone)) {
                    sendCode(phone);
                }
                break;
            case R.id.btn_login:
                //umeng
                MobclickAgent.onEvent(LoginActivity.this, "Login");
                //zhuge
                try {
                    JSONObject eventObject = new JSONObject();
                    eventObject.put("分类", "登录");
                    eventObject.put("名称", phone);
                    //记录事件
                    ZhugeSDK.getInstance().track(getApplicationContext(), "点击登录",
                            eventObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                closeKey(etPassword);
                if (!isValidated(phone)) {
                    return;
                }
                String code = etPassword.getText().toString().trim();
                insertLoginInfo(phone, code);
                break;
        }
        tempPhone = etPhone.getText().toString().trim();
    }

    private void sendCode(String phone) {
        mDialog.show();
        BmobSMS.requestSMSCode(this, phone, getString(R.string.sms_template), new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                mDialog.cancel();
                if (e == null) {
                    Snackbar.make(btnAccquire, "验证码发送成功", Snackbar.LENGTH_SHORT).show();
                    btnAccquire.setClickable(false);
                    startTimer();
                } else {
                    Snackbar.make(btnAccquire, e.getErrorCode() + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void closeKey(EditText etContent) {
        InputMethodManager imm = (InputMethodManager) etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(etContent.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 插入手机号码
     *
     * @param phone
     */
    private void insertLoginInfo(final String phone, String code) {
        WEUser user = new WEUser();
        user.setToken(phone);
        user.setMobilePhoneNumber(phone);
        user.setUsername(Util.filterPhone(phone));
        user.setPassword(phone);
//        user.signOrLogin(this, code, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                getUserInfo(phone);
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                Snackbar.make(btnAccquire, i + s, Snackbar.LENGTH_LONG).show();
//            }
//        });
        user.signOrLoginByMobilePhone(this, phone, code, new LogInListener<WEUser>() {
            @Override
            public void done(WEUser user, BmobException e) {
                if (user != null) {
                    getUserInfo(phone);
                }else {
                    Snackbar.make(btnAccquire, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateLoginView(boolean isShow) {
        etPassword.setVisibility(isShow ? View.GONE : View.VISIBLE);
        btnAccquire.setVisibility(isShow ? View.GONE : View.VISIBLE);
        isExistUser = isShow;
    }

    /**
     * 获取用户信息
     *
     * @param phone
     */
    private void getUserInfo(final String phone) {
        mDialog.show();
        String sql = "select * from _User where mobilePhoneNumber = '" + phone + "'";
        new BmobQuery<WEUser>().doSQLQuery(this, sql, new SQLQueryListener<WEUser>() {
                    @Override
                    public void done(BmobQueryResult<WEUser> bmobQueryResult, BmobException e) {
                        mDialog.cancel();
                        if (e == null) {
                            if (bmobQueryResult == null || bmobQueryResult.getResults() == null ||
                                    bmobQueryResult.getResults().size() <= 0) {
                                return;
                            }
                            //数据库已经存在用户
                            WEUser user = bmobQueryResult.getResults().get(0);
                            writePreferences(Constants.USER_PHONE, user.getMobilePhoneNumber());
                            writePreferences(Constants.USER_OBJECTID, user.getObjectId());
                            writePreferences(Constants.USER_NAME, user.getUsername());
                            writePreferences(Constants.LOVER_USER_PHONE, user.getLoverPhone());

                            //诸葛io
                            try {
                                //定义用户识别码
                                String userid = user.getObjectId();
                                //定义用户属性
                                JSONObject personObject = new JSONObject();
                                personObject.put("name", user.getMobilePhoneNumber());
                                personObject.put("avatar", user.getLoverPhone());
                                //标识用户
                                ZhugeSDK.getInstance().identify(getApplicationContext(), userid,
                                        personObject);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            //登录
                            Intent intent = new Intent(LoginActivity.this, WEActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            //数据库没有这个用户
                        }
                    }
                }

        );
    }

    /**
     * 验证手机号
     *
     * @param phone
     * @return
     */
    private boolean isValidated(String phone) {
        if (Util.isBlank(phone)) {
            Snackbar.make(btnAccquire, "手机号不能为空...", Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            if (Util.isMobileNO(phone)) {
                return true;
            } else {
                Snackbar.make(btnAccquire, "手机号码不正确...", Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                }
            };
        }
        if (mTimer != null && mTimerTask != null) {
            mTimer.schedule(mTimerTask, 1000, 1000);
        }
    }

    private void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        time = 59;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (time <= 0) {
                btnAccquire.setText("获取验证码");
                btnAccquire.setClickable(true);
                stopTimer();
                return;
            }
            btnAccquire.setText(time + "");
            time--;
        }
    };
}
