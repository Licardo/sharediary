package we.sharediary.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import we.sharediary.R;
import we.sharediary.listener.BindListener;
import we.sharediary.listener.ExitListener;

/**
 * Created by zhanghao on 2016/11/23.
 */

public class DialogUtils {

    /**
     * 显示dialog
     *
     * @param context
     */
    public static void showDialog(final Context context, final ExitListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("退出").setMessage("确定退出吗？").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onExitListener();
            }
        }).show();
    }

    /**
     * 获取加载框
     *
     * @param context
     */
    public static ProgressDialog getProgressBar(Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.dialog);
        dialog.setCancelable(true);
        return dialog;
    }

    /**
     * 获取加载框
     *
     * @param context
     */
    public static ProgressDialog getProgressBar(Context context, boolean isCancle) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.dialog);
        dialog.setCancelable(isCancle);
        return dialog;
    }

    /**
     * 显示绑定框
     * @param context
     * @param isCancle
     */
    public static void showBindDialog(Context context, final BindListener bindListener, boolean isCancle){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bind, null);
        final Button bind = (Button) view.findViewById(R.id.btn_bind);
        final EditText etPhone = (EditText) view.findViewById(R.id.et_phone);
        builder.setView(view);
        builder.setCancelable(isCancle);
        final AlertDialog alertDialog = builder.show();
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKey(etPhone);
                bindListener.onBindListener(etPhone.getText().toString().trim());
                alertDialog.cancel();
            }
        });
    }

    /**
     * 关闭键盘
     * @param etContent
     */
    public static void closeKey(EditText etContent) {
        InputMethodManager imm = (InputMethodManager) etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(etContent.getApplicationWindowToken(), 0);
        }
    }

}
