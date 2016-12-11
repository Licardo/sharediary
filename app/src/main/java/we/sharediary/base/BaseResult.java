package we.sharediary.base;

/**
 * Created by Jayden on 2016/1/10.
 */
public class BaseResult {
    public static final int CODE = 0;
    private int code = -1;
    private String message = "";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
