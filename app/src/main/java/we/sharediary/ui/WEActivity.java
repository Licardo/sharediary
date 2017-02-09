package we.sharediary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import we.sharediary.R;
import we.sharediary.base.BaseActivity;
import we.sharediary.base.Constants;
import we.sharediary.listener.ExitListener;
import we.sharediary.result.RefreshEvent;
import we.sharediary.table.WEUser;
import we.sharediary.util.BusProviderUtil;
import we.sharediary.util.DialogUtils;
import we.sharediary.util.Util;

public class WEActivity extends BaseActivity implements View.OnClickListener{

    private WEMainFragment mWEMainFragment;
    private DiaryListFragment mDiaryListFragment;
    private ChatListFragment mChatListFragment;
    private RadioButton rbHome;
    private RadioGroup rgNavigation;
    private FloatingActionButton mActionButton;
    private FragmentManager mManager;
    private boolean isShowExitIcon = false;
    private boolean isShowRefresh = false;
    private boolean isShowBind = false;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we);

        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));//设置Toolbar标题
//        toolbar.setTitle("插件你TM终于成功显示了");
        setSupportActionBar(toolbar);
        rbHome = (RadioButton) findViewById(R.id.rb_home);
        rgNavigation = (RadioGroup) findViewById(R.id.tabs);
        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        mManager = getSupportFragmentManager();
        showFragment(R.id.rb_home);
        rbHome.setChecked(true);
        rgNavigation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                showFragment(id);
            }
        });
        mActionButton.setOnClickListener(this);
        userid = (String) readPreferences(Constants.USER_OBJECTID, 0);
//        connectChat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectChat();
    }

    private void connectChat(){
        BmobIM.connect(userid, new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    //连接成功
                    writePreferences(Constants.USER_ID, uid);
                    if (mChatListFragment != null) {
                        mChatListFragment.refresh(new RefreshEvent(Constants.REFRESH_CHAT_LIST_CODE));
                    }
                    Log.e("connect", "连接成功");
                }else {
                    //链接失败
                    Snackbar.make(rgNavigation, e.getErrorCode()+e.getMessage(), Snackbar.LENGTH_LONG).show();
                    connectChat();
                }
            }
        });
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                Log.e("ERROR", status.getCode()+status.getMsg());
            }
        });
    }

    /**
     * 隐藏所有fragment
     * @param ft
     */
    private void hideFragment(FragmentTransaction ft){
        if (mWEMainFragment != null){
            ft.hide(mWEMainFragment);
        }
        if (mDiaryListFragment != null) {
            ft.hide(mDiaryListFragment);
        }
        if (mChatListFragment != null) {
            ft.hide(mChatListFragment);
        }
    }

    /**
     * 显示选中fragment
     * @param id
     */
    private void showFragment(int id){
        FragmentTransaction ft = mManager.beginTransaction();
        hideFragment(ft);
        switch (id){
            case R.id.rb_home:
                mActionButton.setVisibility(View.VISIBLE);
                isShowExitIcon = false;
                isShowRefresh = false;
                isShowBind = true;
                if (mWEMainFragment != null) {
                    ft.show(mWEMainFragment);
                }else {
                    mWEMainFragment = new WEMainFragment();
                    ft.add(R.id.fl_content, mWEMainFragment);
                }
                break;
            case R.id.rb_record:
                mActionButton.setVisibility(View.GONE);
                isShowExitIcon = false;
                isShowRefresh = true;
                isShowBind = false;
                if (mDiaryListFragment != null) {
                    ft.show(mDiaryListFragment);
                }else {
                    mDiaryListFragment = new DiaryListFragment();
                    ft.add(R.id.fl_content, mDiaryListFragment);
                }
                break;
            case R.id.rb_chat:
                mActionButton.setVisibility(View.GONE);
                isShowExitIcon = true;
                isShowRefresh = false;
                isShowBind = false;
                if (mChatListFragment != null) {
                    ft.show(mChatListFragment);
                }else {
                    mChatListFragment = new ChatListFragment();
                    ft.add(R.id.fl_content, mChatListFragment);
                }
                break;
        }
        ft.commit();
        //重新调用onCreateOptionsMenu
        invalidateOptionsMenu();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Intent intent = new Intent(WEActivity.this, AddDiaryActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_exit);
        MenuItem itemRefresh = menu.findItem(R.id.menu_refresh);
        MenuItem itemBind = menu.findItem(R.id.menu_bind);
        if (isShowExitIcon) {
            item.setVisible(true);
        }else {
            item.setVisible(false);
        }
        if (isShowRefresh) {
            itemRefresh.setVisible(true);
        }else {
            itemRefresh.setVisible(false);
        }
        if (isShowBind) {
            itemBind.setVisible(true);
        }else {
            itemBind.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_exit:
                //umeng
                MobclickAgent.onEvent(WEActivity.this, "Exit");

                MyExitListener listener = new MyExitListener();
                DialogUtils.showDialog(WEActivity.this, listener);
                break;
            case R.id.menu_refresh:
                //umeng
                MobclickAgent.onEvent(WEActivity.this, "Refresh_List");

                mDiaryListFragment.refresh();
                break;
            case R.id.menu_bind:
                //umeng
                loadPatch();
                Log.e("WE", Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                MobclickAgent.onEvent(WEActivity.this, "Bind_Phone");
                /*DialogUtils.showBindDialog(WEActivity.this, new BindListener() {
                    @Override
                    public void onBindListener(String phone) {
                        if (StringUtils.isEmpty(phone) || !Util.isMobileNO(phone)) {
                            Snackbar.make(rbHome, "请输入正确手机号", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        bindLoverAccount(phone);
                    }
                }, true);*/
                break;
        }
        return true;
    }

    /**
     * 加载热补丁插件
     */
    public void loadPatch() {
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
    }

    /**
     * 绑定账号
     */
    private void bindLoverAccount(final String phone){

        WEUser user = new WEUser();
        user.setLoverPhone(phone);
        user.update(this, userid, new UpdateListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(rbHome, "绑定成功", Snackbar.LENGTH_SHORT).show();
                writePreferences(Constants.LOVER_USER_PHONE, phone);
                BusProviderUtil.getBusInstance().post(new RefreshEvent(Constants.REFRESH_CODE));
            }

            @Override
            public void onFailure(int i, String s) {
                Snackbar.make(rbHome, i+s, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    class MyExitListener implements ExitListener{

        @Override
        public void onExitListener() {
            Util.writePreferences(Constants.USER_PHONE, "");
            Util.writePreferences(Constants.USER_OBJECTID, "");
            Util.writePreferences(Constants.USER_NAME, "");
            Util.writePreferences(Constants.LOVER_USER_PHONE, "");
            BmobIM.getInstance().disConnect();
            Intent intent = new Intent(WEActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private boolean isExit = false;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (isExit){
            finish();
        }
        isExit = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isExit = false;
            }
        },2000);
        Snackbar.make(rbHome, "点击两次退出", Snackbar.LENGTH_SHORT).show();
    }


}
