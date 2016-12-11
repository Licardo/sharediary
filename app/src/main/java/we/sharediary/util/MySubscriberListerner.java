package we.sharediary.util;

/**
 * Created by zhanghao on 16/3/22.
 * 处理Subscriber中的各种结果
 */
public interface MySubscriberListerner<T> {
    void onNext(T t);
    void onErr(Throwable e);
    void onComplete();
}
