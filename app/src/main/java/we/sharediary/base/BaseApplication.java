package we.sharediary.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import io.fabric.sdk.android.Fabric;
import we.sharediary.im.DemoMessageHandler;


/**
 * Created by zhanghao on 2016/11/15.
 */

public class BaseApplication extends Application{
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        Fresco.initialize(this);

//        Bmob.initialize(this, Constants.BOMB_APPLICATION_ID);
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler());
            //百度云推送
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Constants.API_KEY_BAIDUYUN);
        }

    }

    public static BaseApplication getInstance(){
        return instance;
    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *写入sharedPreferences
     * @param key
     * @param value
     */
    public void writePreferences(String key, Object value){
        SharedPreferences preferences = getSharedPreferences(Constants.KEY_PRERERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof String){
            editor.putString(key, (String) value);
        }else if (value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }else if (value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }
        editor.commit();
    }

    /**
     * 读取sharedPreferences
     * @param key
     * @param flag 0:string 1:int 2:boolean
     * @return
     */
    public Object readPreferences(String key, int flag){
        SharedPreferences preferences = getSharedPreferences(Constants.KEY_PRERERENCES, MODE_PRIVATE);
        Object o = null;
        if (flag == 0){
            o = preferences.getString(key, "");
        }else if (flag == 1){
            o = preferences.getInt(key, -1);
        }else if (flag == 2){
            o = preferences.getBoolean(key, false);
        }
        return o;
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
