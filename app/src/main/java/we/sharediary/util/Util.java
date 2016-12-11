package we.sharediary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Second;

import java.util.Locale;

import we.sharediary.base.BaseApplication;
import we.sharediary.base.Constants;

/**
 * Created by Jayden on 2016/1/11.
 */
public class Util {
    // 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    public static final PrettyTime sPrettyTime = new PrettyTime(Locale.CHINA);
    static {
//        sPrettyTime.removeUnit(JustNow.class);
        sPrettyTime.removeUnit(Second.class);
        sPrettyTime.removeUnit(Millisecond.class);
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication
                .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 字符串是否为空
     * @param str
     * @return
     */
    public static boolean isBlank(String str){
        if (str == null || "".equals(str) ||
                " " == str || "".equals(str.trim())){
            return true;
        }
        return false;
    }

    /**
     *写入sharedPreferences
     * @param key
     * @param value
     */
    public static void writePreferences(String key, Object value){
        SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(Constants.KEY_PRERERENCES, Context.MODE_PRIVATE);
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
    public static Object readPreferences(String key, int flag){
        SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(Constants.KEY_PRERERENCES, Context.MODE_PRIVATE);
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

    public static String filterString(String param){
        if (param == null || param.trim().equals("")){
            param = "";
        }
        return param;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 返回过滤后的手机号
     * @param phone
     * @return
     */
    public static String filterPhone(String phone){
        if (isBlank(phone)) {
            return "";
        }else {
            if (phone.length() == 11) {
                String result = phone.substring(0,3)+"****"+phone.substring(7);
                return result;
            }else {
                return phone;
            }
        }
    }

    public static int getVersionCode(Context context)//获取版本号(内部识别号)
     {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
