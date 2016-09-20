package wxm.KeepAccount.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * 选择关注channel的对话框
 * Created by 123 on 2016/9/20.
 */
public class DlgSelectChannel extends DialogFragment {
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private NoticeDialogListener mListener;

    /**
     * 处理NoticeDialogListener实例
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement NoticeDialogListener");
        }
    }

    /**
     * 在DialogFragment的show方法执行后，系统会调用此方法
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 创建dialog并设置button的点击事件
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("fire_missiles").setPositiveButton("fire", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(DlgSelectChannel.this);
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(DlgSelectChannel.this);
            }
        });

        return builder.create();
    }
}
