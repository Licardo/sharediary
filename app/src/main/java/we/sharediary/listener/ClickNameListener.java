package we.sharediary.listener;

import we.sharediary.table.WEUser;

/**
 * Created by zhanghao on 2016/11/24.
 * 点击用户名 用于启动聊天
 */

public interface ClickNameListener {
    void onClickNameListener(WEUser user);
}
