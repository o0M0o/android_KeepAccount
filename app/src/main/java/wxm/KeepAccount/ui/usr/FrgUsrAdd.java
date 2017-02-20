package wxm.KeepAccount.ui.usr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import cn.wxm.andriodutillib.util.WRMsgHandler;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.data.UsrItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;

/**
 * 添加用户
 * Created by ookoo on 2016/11/29.
 */
public class FrgUsrAdd extends FrgUtilityBase implements TextView.OnEditorActionListener {
    // for data
    private  LocalMsgHandler mMHHandler;

    // for ui
    @BindView(R.id.et_usr_name)
    EditText mETUsrName;
    @BindView(R.id.et_pwd)
    EditText mETPwd;
    @BindView(R.id.et_repeat_pwd)
    EditText mETRepeatPwd;

    /*
    @BindView(R.id.bt_confirm)
    Button mBTConfirm;
    @BindView(R.id.bt_giveup)
    Button mBTGiveup;
    */

    // fro res
    @BindString(R.string.error_no_usrname)
    String  mRSErrorNoUsrName;

    @BindString(R.string.error_invalid_password)
    String  mRSErrorInvalidPWD;

    @BindString(R.string.error_repeatpwd_notmatch)
    String  mRSErrorRepeatPwdNotMatch;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgUsrAdd";
        View rootView = layoutInflater.inflate(R.layout.vw_usr_add, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        mMHHandler = new LocalMsgHandler(this);

        mETUsrName.setOnEditorActionListener(this);
        mETPwd.setOnEditorActionListener(this);
        mETRepeatPwd.setOnEditorActionListener(this);
    }

    @Override
    protected void initUiInfo() {

    }

    @OnClick({R.id.bt_confirm, R.id.bt_giveup})
    void onSelfClick(View v)    {
        switch (v.getId())  {
            case R.id.bt_confirm: {
                if(checkInput()) {
                    Intent data = new Intent();
                    data.putExtra(UsrItem.FIELD_NAME, mETUsrName.getText().toString());
                    data.putExtra(UsrItem.FIELD_PWD, mETPwd.getText().toString());

                    Message m = Message.obtain(ContextUtil.getMsgHandler(),
                            GlobalDef.MSG_USR_ADDUSR);
                    m.obj = new Object[] {data, mMHHandler};
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
        switch(v.getId())  {
            case R.id.et_usr_name: {
                Log.d(LOG_TAG, "now usr name : " + mETUsrName.getText().toString());
            }
            break;

            case R.id.et_pwd: {
                Log.d(LOG_TAG, "now pwd : " + mETPwd.getText().toString());
            }
            break;

            case R.id.et_repeat_pwd: {
                Log.d(LOG_TAG, "now repeatpwd : " + mETRepeatPwd.getText().toString());
            }
            break;
        }

        return false;
    }


    /// PRIVATE BEGIN
    /**
     * 清空已经存在的数据
     */
    private void repeatInput()   {
        mETUsrName.setText("");
        mETPwd.setText("");
        mETRepeatPwd.setText("");

        mETUsrName.requestFocus();
    }


    /**
     * 检查输入数据合法性，并设置提示信息
     * @return  如果数据合法返回true, 否则返回false
     */
    private boolean checkInput()    {
        String usr_name = mETUsrName.getText().toString();
        String usr_pwd = mETPwd.getText().toString();
        String usr_r_pwd = mETRepeatPwd.getText().toString();

        boolean bret = true;
        if(UtilFun.StringIsNullOrEmpty(usr_name))  {
            mETUsrName.setError(mRSErrorNoUsrName);
            mETUsrName.requestFocus();
            bret = false;
        }

        if(bret && 4 > usr_pwd.length())    {
            mETPwd.setError(mRSErrorInvalidPWD);
            mETPwd.requestFocus();
            bret = false;
        }

        if(bret && !usr_pwd.equals(usr_r_pwd))   {
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
            switch (m.what) {
                case GlobalDef.MSG_REPLY: {
                    switch (m.arg1) {
                        case GlobalDef.MSG_USR_ADDUSR: {
                            afterAddUsr(m, home);
                        }
                        break;

                        default:
                            Log.e(TAG, String.format("msg(%s) can not process", m.toString()));
                            break;
                    }
                }
                break;

                default:
                    Log.e(TAG, String.format("msg(%s) can not process", m.toString()));
                    break;
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

                Toast.makeText(ContextUtil.getInstance(), sstr,
                                Toast.LENGTH_LONG).show();
                home.repeatInput();
            }
        }
    }
}
