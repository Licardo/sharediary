package we.sharediary.util;

/**
 * Created by zhanghao on 16/3/22.
 * 网络请求完成后取消订阅 以防内存泄漏
 */
public interface OnCancleUnSubscriberListener {
    void onUnSubcriber();
}
