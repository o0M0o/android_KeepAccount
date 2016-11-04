package wxm.KeepAccount.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * 提供"OK"和"NO"的对话框基类
 * Created by 123 on 2016/11/1.
 */
public abstract class DlgOKAndNOBase extends DialogFragment  {
    private View        mVWDlg;
    private String      mTitle;
    private String      mOKName;
    private String      mNoName;

    // for notice interface
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }


    private NoticeDialogListener mListener;
    public void setDialogListener(NoticeDialogListener nl)  {
        mListener = nl;
    }


    /**
     * 处理NoticeDialogListener实例
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
     */

    /**
     * 在DialogFragment的show方法执行后，系统会调用此方法
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mVWDlg = InitDlgView();

        // 创建dialog并设置button的点击事件
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mVWDlg);

        builder.setMessage(UtilFun.StringIsNullOrEmpty(mTitle) ? "对话框" : mTitle)
                .setPositiveButton(UtilFun.StringIsNullOrEmpty(mOKName) ? "确认" : mOKName,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(null != mListener)
                                    mListener.onDialogPositiveClick(DlgOKAndNOBase.this);
                            }
                        })
                .setNegativeButton(UtilFun.StringIsNullOrEmpty(mNoName) ? "放弃" : mNoName,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(null != mListener)
                                    mListener.onDialogNegativeClick(DlgOKAndNOBase.this);
                            }
                        });

        return builder.create();
    }

    /**
     * 初始化对话框的视图部分
     * @return  对话框的视图部分
     */
    protected abstract View InitDlgView();

    /**
     * 获取对话框视图
     * @return  对话框视图
     */
    protected View getDlgView() {
        return mVWDlg;
    }

    /**
     * 初始化对话框辅助字符串
     * @param title         对话框title
     * @param OKName        “OK”选项名
     * @param NoName        "No"选项名
     */
    protected void InitDlgTitle(String title, String OKName, String NoName)     {
        mTitle = title;
        mOKName = OKName;
        mNoName = NoName;
    }
}
