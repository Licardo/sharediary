package we.sharediary.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;


/**
 * Created by zhanghao on 16/3/22.
 * 在Subscriber中显示／取消等待框
 */
public class MyProgressDialogHandler extends Handler {

    private Context mContext;
    private OnCancleUnSubscriberListener listener;
    private boolean cancleable;
    public static final int SHOW_DIALOG = 0;
    public static final int DISMISS_DIALOG = 1;
    private ProgressDialog dialog;

    MyProgressDialogHandler(Context context, OnCancleUnSubscriberListener listener,
                            boolean cancleable){
        this.mContext = context;
        this.listener = listener;
        this.cancleable = cancleable;
    }

    private void initProgressDialog(){
        if (dialog == null){
            dialog = new ProgressDialog(mContext);
            dialog.setCancelable(cancleable);
            if (cancleable){
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        listener.onUnSubcriber();
                    }
                });
            }
            if (!dialog.isShowing()){
                dialog.show();
            }
        }
    }

    private void dismissProgressDialog(){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case SHOW_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }
}
