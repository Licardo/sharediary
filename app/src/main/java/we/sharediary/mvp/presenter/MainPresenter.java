package we.sharediary.mvp.presenter;

import android.content.Context;

import we.sharediary.mvp.model.IWeathModel;
import we.sharediary.mvp.model.WeathModel;
import we.sharediary.mvp.view.IMainView;
import we.sharediary.result.CityInfoResult;
import we.sharediary.util.MySubscriberListerner;

/**
 * Created by zhanghao on 2016/11/15.
 */

public class MainPresenter{
    private IMainView mView;
    private IWeathModel mModel;
    private Context mContext;

    public MainPresenter(Context context) {
        mView = (IMainView) context;
        mModel = new WeathModel();
        mContext = context;
    }

    public void getWeatherInfo(){
        mModel.getCityInfo("hangzhou", mContext, false, new MySubscriberListerner<CityInfoResult>() {

            @Override
            public void onNext(CityInfoResult result) {
                mView.loadView(result);
            }

            @Override
            public void onErr(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
