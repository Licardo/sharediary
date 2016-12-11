package we.sharediary.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by zhanghao on 16/4/19.
 */
public class BusProviderUtil {
    private static Bus mBus;

    public static Bus getBusInstance(){
        if (mBus == null) {
            mBus = new Bus(ThreadEnforcer.MAIN);
        }
        return mBus;
    }
}
