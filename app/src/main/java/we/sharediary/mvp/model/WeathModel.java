package we.sharediary.mvp.model;

import android.content.Context;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import we.sharediary.base.Constants;
import we.sharediary.http.HttpManager;
import we.sharediary.result.CityInfoResult;
import we.sharediary.util.MySubscriberListerner;
import we.sharediary.util.Mysubscriber;

/**
 * Created by zhanghao on 2016/11/6.
 */

public class WeathModel implements IWeathModel {
    @Override
    public void getCityInfo(String cityname, Context context, boolean isShowDialog, MySubscriberListerner listerner) {
        Subscriber<CityInfoResult> subscriber = new
                Mysubscriber<>(context, listerner, isShowDialog);
        HttpManager.getService().getCityInfo(cityname, Constants.APIKEY).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(subscriber);
    }

}
