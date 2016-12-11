package we.sharediary.result;

/**
 * Created by zhanghao on 2016/11/22.
 */

public class RefreshEvent {
    private int code;
    private String msg;
    private String title;
    private String conversionId;

    public RefreshEvent(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RefreshEvent(int code, String msg, String title) {
        this.code = code;
        this.msg = msg;
        this.title = title;
    }

    public RefreshEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }
}
