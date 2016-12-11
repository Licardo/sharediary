package we.sharediary.im;

import android.util.Log;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import we.sharediary.base.Constants;
import we.sharediary.result.RefreshEvent;
import we.sharediary.util.BusProviderUtil;

/**
 * Created by zhanghao on 2016/11/23.
 */

public class DemoMessageHandler extends BmobIMMessageHandler {
    @Override
    public void onMessageReceive(final MessageEvent event) {
        //当接收到服务器发来的消息时，此方法被调用
        Log.e("服务器发来的消息", event.getMessage().getContent());
        RefreshEvent refreshEvent = new RefreshEvent(Constants.REFRESH_CHAT_CODE,
                event.getMessage().getContent(), event.getMessage().getFromId());
        refreshEvent.setConversionId(event.getConversation().getConversationId());
        BusProviderUtil.getBusInstance().post(refreshEvent);
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
        Log.e("离线消息数量", event.getTotalNumber()+"");
    }
}
