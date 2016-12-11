package we.sharediary.mvp.model;

import android.content.Context;

import we.sharediary.util.MySubscriberListerner;


/**
 * Created by zhanghao on 2016/11/5.
 */

public interface IWeathModel {
    void getCityInfo(String cityname, Context context, boolean isShowDialog, MySubscriberListerner listerner);
}
