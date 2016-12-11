package we.sharediary.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.bmob.newim.event.MessageEvent;

/**
 * Created by zhanghao on 2016/11/23.
 */

public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final MessageEvent event = (MessageEvent) intent.getSerializableExtra("event");
            //开发者可以在这里发应用通知
            Log.e("MessageReceiver", event.getMessage().getContent());
        }
    }
}
