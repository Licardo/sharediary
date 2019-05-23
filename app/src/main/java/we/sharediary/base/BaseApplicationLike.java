package we.sharediary.base;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import we.sharediary.im.DemoMessageHandler;
import we.sharediary.tinker_csdn.SampleResultService;
import we.sharediary.tinkerutil.SampleApplicationContext;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by zhanghao on 2016/11/15.
 */

@SuppressWarnings("unused")
@DefaultLifeCycle(application = "we.sharediary.base.BaseApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class BaseApplicationLike extends DefaultApplicationLike {
    private static BaseApplicationLike instance;
    private Application app;

    public BaseApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        /*SampleApplicationContext.application = getApplication();
        context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());*/
        SampleApplicationContext.application = getApplication();
        TinkerInstaller.install(this,new DefaultLoadReporter(getApplication()),new DefaultPatchReporter(getApplication()),
                new DefaultPatchListener(getApplication()),SampleResultService.class,new UpgradePatch());
        Tinker tinker = Tinker.with(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        app = getApplication();
        Fresco.initialize(app);

//        Bmob.initialize(this, Constants.BOMB_APPLICATION_ID);
        //只有主进程运行的时候才需要初始化
        if (app.getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(app);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler());
            //百度云推送
            PushManager.startWork(app.getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Constants.API_KEY_BAIDUYUN);
        }
    }

    public static ApplicationLike getInstance(){
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
        SharedPreferences preferences = app.getSharedPreferences(Constants.KEY_PRERERENCES, MODE_PRIVATE);
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
        SharedPreferences preferences = app.getSharedPreferences(Constants.KEY_PRERERENCES, MODE_PRIVATE);
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
