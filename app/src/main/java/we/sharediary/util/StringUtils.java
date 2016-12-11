package we.sharediary.util;

/**
 * Created by zhanghao on 2016/11/15.
 */

public class StringUtils {
    /**
     * 字符串是否为空
     * @param param
     * @return
     */
    public static boolean isEmpty(String param){
        if (param == null || "".equals(param.trim())){
            return true;
        }
        return false;
    }
}
