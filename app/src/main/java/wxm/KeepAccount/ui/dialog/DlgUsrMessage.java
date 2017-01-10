package wxm.KeepAccount.ui.dialog;

import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import wxm.KeepAccount.R;

/**
 * 提交用户消息
 * Created by ookoo on 2017/1/9.
 */
public class DlgUsrMessage extends DlgOKOrNOBase {
    private final static String HTTP_POST_URL =
            "http://www.wxmandriodapp.com:8080/KeepAccountServer/def/UMUtility";
    public static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");


    @BindView(R.id.et_usr_name)
    TextInputEditText mETUsrName;

    @BindView(R.id.et_usr_message)
    TextInputEditText mETUsrMessage;

    @Override
    protected View InitDlgView() {
        InitDlgTitle("编辑消息", "接受", "放弃");
        View vw = View.inflate(getActivity(), R.layout.dlg_send_message, null);
        ButterKnife.bind(this, vw);

        return vw;
    }

    @Override
    protected boolean checkBeforeOK() {
        String usr = mETUsrName.getText().toString();
        String msg = mETUsrMessage.getText().toString();

        if (UtilFun.StringIsNullOrEmpty(usr)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("用户名不能为空")
                    .setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if (UtilFun.StringIsNullOrEmpty(msg)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("消息不能为空")
                    .setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return sendMsgByHttpPost(usr, msg);
    }

    /**
     * 通过http post发送消息
     *
     * @param usr 用户
     * @param msg 消息
     * @return 发送成功返回true
     */
    private boolean sendMsgByHttpPost(String usr, String msg) {
        HttpPostTask ht = new HttpPostTask(usr, msg);
        ht.execute();
        return true;
    }


    /**
     * for send http post
     */
    public class HttpPostTask extends AsyncTask<Void, Void, Boolean> {
        private final String mSZUsr;
        private final String mSZMsg;

        HttpPostTask(String usr, String msg) {
            mSZUsr = usr;
            mSZMsg = msg;
        }

        /*
        @Override
        protected void onPreExecute()   {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
        }
        */

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            try {
                // set param
                JSONObject param = new JSONObject();
                param.put("usr", mSZUsr);
                param.put("message", mSZMsg);

                RequestBody body = RequestBody.create(JSON, param.toString());
                Request request = new Request.Builder()
                        .url(HTTP_POST_URL).post(body).build();
                client.newCall(request).execute();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean bret) {
            super.onPostExecute(bret);
        }
    }
}
