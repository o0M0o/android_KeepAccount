package wxm.KeepAccount.ui.usr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.define.EMsgType;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.util.UtilFun;
import wxm.androidutil.util.WRMsgHandler;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.UsrItem;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * add user
 * Created by WangXM on 2016/11/29.
 */
public class FrgUsrAdd extends FrgSupportBaseAdv
        implements TextView.OnEditorActionListener {
    // for ui
    @BindView(R.id.et_usr_name)
    EditText mETUsrName;
    @BindView(R.id.et_pwd)
    EditText mETPwd;
    @BindView(R.id.et_repeat_pwd)
    EditText mETRepeatPwd;
    // fro res
    @BindString(R.string.error_no_usrname)
    String mRSErrorNoUsrName;

    @BindString(R.string.error_invalid_password)
    String mRSErrorInvalidPWD;
    @BindString(R.string.error_repeatpwd_notmatch)
    String mRSErrorRepeatPwdNotMatch;
    // for data
    private LocalMsgHandler mMHHandler;

    @Override
    protected int getLayoutID() {
        return R.layout.vw_usr_add;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle) {
        mMHHandler = new LocalMsgHandler(this);

        mETUsrName.setOnEditorActionListener(this);
        mETPwd.setOnEditorActionListener(this);
        mETRepeatPwd.setOnEditorActionListener(this);
    }


    @OnClick({R.id.bt_confirm, R.id.bt_giveup})
    void onSelfClick(View v) {
        switch (v.getId()) {
            case R.id.bt_confirm: {
                if (checkInput()) {
                    Intent data = new Intent();
                    data.putExtra(UsrItem.FIELD_NAME, mETUsrName.getText().toString());
                    data.putExtra(UsrItem.FIELD_PWD, mETPwd.getText().toString());

                    Message m = Message.obtain(ContextUtil.Companion.getMsgHandler(),
                            EMsgType.USR_ADD.getId());
                    m.obj = new Object[]{data, mMHHandler};
                    m.sendToTarget();
                }
            }
            break;

            case R.id.bt_giveup: {
                int ret_data = GlobalDef.INTRET_GIVEUP;

                Activity ac = getActivity();
                Intent data = new Intent();
                ac.setResult(ret_data, data);
                ac.finish();
            }
            break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.et_usr_name: {
                Log.d(LOG_TAG, "now usr name : " + mETUsrName.getText().toString());
            }
            break;

            case R.id.et_pwd: {
                Log.d(LOG_TAG, "now pwd : " + mETPwd.getText().toString());
            }
            break;

            case R.id.et_repeat_pwd: {
                Log.d(LOG_TAG, "now repeatPWD : " + mETRepeatPwd.getText().toString());
            }
            break;
        }

        return false;
    }


    /// PRIVATE BEGIN
    /**
     * clean UI
     */
    private void repeatInput() {
        mETUsrName.setText("");
        mETPwd.setText("");
        mETRepeatPwd.setText("");

        mETUsrName.requestFocus();
    }

    /**
     * check input data then give prompting message
     * @return  true if data legal else false
     */
    private boolean checkInput() {
        String usr_name = mETUsrName.getText().toString();
        String usr_pwd = mETPwd.getText().toString();
        String usr_r_pwd = mETRepeatPwd.getText().toString();

        boolean bret = true;
        if (UtilFun.StringIsNullOrEmpty(usr_name)) {
            mETUsrName.setError(mRSErrorNoUsrName);
            mETUsrName.requestFocus();
            bret = false;
        }

        if (bret && 4 > usr_pwd.length()) {
            mETPwd.setError(mRSErrorInvalidPWD);
            mETPwd.requestFocus();
            bret = false;
        }

        if (bret && !usr_pwd.equals(usr_r_pwd)) {
            mETRepeatPwd.setError(mRSErrorRepeatPwdNotMatch);
            mETRepeatPwd.requestFocus();
            bret = false;
        }

        return bret;
    }
    /// PRIVATE END

    private static class LocalMsgHandler extends WRMsgHandler<FrgUsrAdd> {
        LocalMsgHandler(FrgUsrAdd ac) {
            super(ac);
            TAG = "LocalMsgHandler";
        }

        @Override
        protected void processMsg(Message m, FrgUsrAdd home) {
            EMsgType et = EMsgType.Companion.getEMsgType(m.what);
            if(null == et)
                return;

            if(EMsgType.REPLAY == et)   {
                EMsgType et_inner = EMsgType.Companion.getEMsgType(m.arg1);
                if(null != et_inner)    {
                    if(EMsgType.USR_ADD == et_inner)    {
                        afterAddUsr(m, home);
                    }
                }
            } else  {
                Log.e(TAG, String.format("msg(%s) can not process", m.toString()));
            }
        }

        private void afterAddUsr(Message m, FrgUsrAdd home) {
            Activity ac = home.getActivity();
            Object[] arr = UtilFun.cast_t(m.obj);
            boolean ret = (boolean) arr[0];
            if (ret) {
                Intent data = UtilFun.cast_t(arr[1]);

                int ret_data = GlobalDef.INTRET_USR_ADD;
                ac.setResult(ret_data, data);
                ac.finish();
            } else {
                String sstr = "添加用户失败!";
                if (2 < arr.length)
                    sstr = UtilFun.cast(arr[2]);

                Toast.makeText(ContextUtil.Companion.getInstance(), sstr,
                        Toast.LENGTH_LONG).show();
                home.repeatInput();
            }
        }
    }
}
