package we.sharediary.result;


import we.sharediary.base.BaseResult;

/**
 * Created by zhanghao on 16/3/22.
 */
public class Result<T> extends BaseResult {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
