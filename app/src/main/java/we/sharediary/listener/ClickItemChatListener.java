package we.sharediary.listener;

import cn.bmob.newim.bean.BmobIMConversation;

/**
 * Created by zhanghao on 2016/11/24.
 * 点击用户名 用于启动聊天
 */

public interface ClickItemChatListener {
    void onClickItemListener(BmobIMConversation conversation);
}
