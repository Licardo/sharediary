package we.sharediary.util;

import android.content.Context;
import android.util.Log;

import rx.Subscriber;

/**
 * Created by zhanghao on 16/3/22.
 * 自定义Subscriber
 */
public class Mysubscriber<T> extends Subscriber<T> implements OnCancleUnSubscriberListener{

    private MySubscriberListerner listerner;
    private MyProgressDialogHandler handler;
    private Context mContext;
    private boolean isShowDialog;

    /**
     *
     * @param context   等待框的上下文
     * @param listerner 处理各种返回值
     * @param isShowDialog  是否显示等待框 true：在等待框取消时解绑subscriber false：onComplete & onError中解绑subscriber
     */
    public Mysubscriber(Context context, MySubscriberListerner listerner, boolean isShowDialog){
        this.listerner = listerner;
        this.mContext = context;
        this.isShowDialog = isShowDialog;
        handler = new MyProgressDialogHandler(context, this, true);
    }

    @Override
    public void onCompleted() {
        listerner.onComplete();
        if (isShowDialog){
            handler.obtainMessage(MyProgressDialogHandler.DISMISS_DIALOG).sendToTarget();
        }else {
            onUnSubcriber();
        }
        Log.i("Mysubscriber", "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        listerner.onErr(e);
        if (isShowDialog){
            handler.obtainMessage(MyProgressDialogHandler.DISMISS_DIALOG).sendToTarget();
        }else {
            onUnSubcriber();
        }
        Log.i("Mysubscriber", "onError");
    }

    @Override
    public void onNext(T t) {
        Log.i("Mysubscriber", "onNext");
        listerner.onNext(t);
    }

    @Override
    public void onStart() {
        if (isShowDialog){
            handler.obtainMessage(MyProgressDialogHandler.SHOW_DIALOG).sendToTarget();
        }
        Log.i("Mysubscriber", "onStart");
    }

    @Override
    public void onUnSubcriber() {
        this.unsubscribe();
    }
}
