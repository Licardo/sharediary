package we.sharediary.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

/**
 * Created by Jayden on 2016/1/10.
 */
public class BaseFragmentActivity extends FragmentActivity {
    //在BaseActivity或BaseFragment中添加字段
    protected final String HTTP_TASK_KEY = "HttpTaskKey_" + hashCode();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        ZhugeSDK.getInstance().init(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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

    public void goNextActivitiy(Class<?> clazz){
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
    }

    public void goNextActivitiyForResult(Class<?> clazz, int requestCode){
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivityForResult(intent, requestCode);
    }
    public void goNextActivitiyForResult(Class<?> clazz, int requestCode, String key, Bundle bundle){
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        intent.putExtra(key, bundle);
        startActivityForResult(intent, requestCode);
    }
}
