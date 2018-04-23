package wxm.KeepAccount.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.PackageUtil;
import wxm.androidutil.util.SIMCardUtil;
import wxm.androidutil.util.UtilFun;
import wxm.androidutil.util.WRMsgHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.utility.ContextUtil;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

/**
 * submit usr msg
 * Created by WangXM on 2017/1/9.
 */
public class DlgUsrMessage extends DlgOKOrNOBase {
    // for progress dialog when send http post
    private final static int PROGRESS_DIALOG = 0x112;
    private final static int MSG_PROGRESS_UPDATE = 0x111;
    // for http post
    private static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");
    @BindString(R.string.url_post_send_message)
    String mSZUrlPost;
    @BindString(R.string.col_usr)
    String mSZColUsr;
    @BindString(R.string.col_message)
    String mSZColMsg;
    @BindString(R.string.col_app_name)
    String mSZColAppName;
    @BindString(R.string.col_val_app_name)
    String mSZColValAppName;
    @BindString(R.string.cn_usr_message)
    String mSZUsrMessage;
    @BindString(R.string.cn_accept)
    String mSZAccept;
    @BindString(R.string.cn_giveup)
    String mSZGiveUp;
    @BindView(R.id.et_usr_message)
    TextInputEditText mETUsrMessage;
    private int mProgressStatus = 0;
    private LocalMsgHandler mHDProgress;

    /*
    @BindView(R.id.et_usr_name)
    TextInputEditText mETUsrName;
    */
    private ProgressDialog mPDDlg;

    @Override
    protected View InitDlgView() {
        InitDlgTitle(mSZUsrMessage, mSZAccept, mSZGiveUp);
        View vw = View.inflate(getActivity(), R.layout.dlg_send_message, null);
        ButterKnife.bind(this, vw);

        // for progress
        mHDProgress = new LocalMsgHandler(this);
        mPDDlg = new ProgressDialog(getContext());
        return vw;
    }

    @Override
    protected boolean checkBeforeOK() {
        String msg = mETUsrMessage.getText().toString();
        if (UtilFun.StringIsNullOrEmpty(msg)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("消息不能为空")
                    .setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        String usr = null;
        Context ct = ContextUtil.Companion.getInstance();
        if(null != ct) {
            if (ContextCompat.checkSelfPermission(ct, READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(ct, READ_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                SIMCardUtil si = new SIMCardUtil(getContext());
                usr = si.getNativePhoneNumber();
            }
        }

        return sendMsgByHttpPost(UtilFun.StringIsNullOrEmpty(usr) ? "null" : usr, msg);
    }

    /**
     * use http send msg
     * @param usr       msg owner
     * @param msg       msg
     * @return          true if success
     */
    private boolean sendMsgByHttpPost(String usr, String msg) {
        mProgressStatus = 0;

        mPDDlg.setMax(100);
        // 设置对话框的标题
        mPDDlg.setTitle("发送消息");
        // 设置对话框 显示的内容
        mPDDlg.setMessage("发送进度");
        // 设置对话框不能用“取消”按钮关闭
        mPDDlg.setCancelable(true);
        mPDDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (dialogInterface, i) -> {
        });

        // 设置对话框的进度条风格
        mPDDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPDDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置对话框的进度条是否显示进度
        mPDDlg.setIndeterminate(false);

        mPDDlg.incrementProgressBy(-mPDDlg.getProgress());
        mPDDlg.show();

        ToolUtil.INSTANCE.runInBackground(this.getActivity(),
                () -> {
                    OkHttpClient client = new OkHttpClient();
                    try {
                        // set param
                        JSONObject param = new JSONObject();
                        param.put(mSZColUsr, usr);
                        param.put(mSZColMsg, msg);
                        param.put(mSZColAppName,
                                mSZColValAppName + "-"
                                        + PackageUtil.getVerName(getContext(), GlobalDef.PACKAGE_NAME));

                        RequestBody body = RequestBody.create(JSON, param.toString());

                        mProgressStatus = 50;
                        Message m = new Message();
                        m.what = MSG_PROGRESS_UPDATE;
                        mHDProgress.sendMessage(m);

                        Request request = new Request.Builder()
                                .url(mSZUrlPost).post(body).build();
                        client.newCall(request).execute();

                        mProgressStatus = 100;
                        Message m1 = new Message();
                        m1.what = MSG_PROGRESS_UPDATE;
                        mHDProgress.sendMessage(m1);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                },
                () -> mPDDlg.dismiss());

        return true;
    }

    /**
     * safe message hanlder
     */
    private static class LocalMsgHandler extends WRMsgHandler<DlgUsrMessage> {
        LocalMsgHandler(DlgUsrMessage ac) {
            super(ac);
            TAG = "LocalMsgHandler";
        }

        @Override
        protected void processMsg(Message m, DlgUsrMessage home) {
            switch (m.what) {
                case MSG_PROGRESS_UPDATE: {
                    home.mPDDlg.setProgress(home.mProgressStatus);
                }
                break;

                default:
                    Log.e(TAG, String.format("msg(%s) can not process", m.toString()));
                    break;
            }
        }
    }
}
