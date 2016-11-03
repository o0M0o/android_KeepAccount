package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppMsgDef;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;

import cn.wxm.andriodutillib.util.UtilFun;

public class ACAddUsr
        extends AppCompatActivity
        implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = "ACAddUsr";
    private ACAUMsgHandler      mMHHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_usr_add);
        mMHHandler = new ACAUMsgHandler(this);

        // init view
        Button bt_confirm = UtilFun.cast_t(findViewById(R.id.ac_nabt_confirm));
        Button bt_giveup = UtilFun.cast_t(findViewById(R.id.ac_nabt_giveup));

        bt_confirm.setOnClickListener(this);
        bt_giveup.setOnClickListener(this);

        EditText et_usrname = UtilFun.cast_t(findViewById(R.id.ac_naet_accountname));
        EditText et_pwd = UtilFun.cast_t(findViewById(R.id.ac_naet_accountpwd));
        EditText et_repeatpwd = UtilFun.cast_t(findViewById(R.id.ac_naet_repeatpwd));

        et_usrname.setOnEditorActionListener(this);
        et_pwd.setOnEditorActionListener(this);
        et_repeatpwd.setOnEditorActionListener(this);
    }


    @Override
    public void onClick(View v)    {
        switch (v.getId())  {
            case R.id.ac_nabt_confirm : {
                if(checkInput()) {
                    EditText et_usrname = (EditText)findViewById(R.id.ac_naet_accountname);
                    EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);
                    assert et_usrname != null && et_pwd != null;

                    Intent data = new Intent();
                    data.putExtra(UsrItem.FIELD_NAME,
                                    et_usrname.getText().toString());
                    data.putExtra(UsrItem.FIELD_PWD,
                                    et_pwd.getText().toString());

                    Message m = Message.obtain(ContextUtil.getMsgHandler(),
                                                AppMsgDef.MSG_USR_ADDUSR);
                    m.obj = new Object[] {data, mMHHandler};
                    m.sendToTarget();
                }
            }
            break;

            case R.id.ac_nabt_giveup : {
                int ret_data = AppGobalDef.INTRET_GIVEUP;

                Intent data=new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;
        }
    }


    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        switch(textView.getId())  {
            case R.id.ac_naet_accountname : {
                EditText et_usrname = (EditText)findViewById(R.id.ac_naet_accountname);
                assert et_usrname != null;
                Log.d(TAG, "now usr name : " + et_usrname.getText().toString());
            }
            break;

            case R.id.ac_naet_accountpwd : {
                EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);
                assert et_pwd != null;
                Log.d(TAG, "now pwd : " + et_pwd.getText().toString());
            }
            break;

            case R.id.ac_naet_repeatpwd : {
                EditText et_repeatpwd = (EditText)findViewById(R.id.ac_naet_repeatpwd);
                assert et_repeatpwd != null;
                Log.d(TAG, "now repeatpwd : " + et_repeatpwd.getText().toString());
            }
            break;
        }

        return false;
    }

    /**
     * 清空已经存在的数据
     */
    private void repeatInput()   {
        EditText et_usrname = (EditText)findViewById(R.id.ac_naet_accountname);
        EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);
        EditText et_repeatpwd = (EditText)findViewById(R.id.ac_naet_repeatpwd);
        assert et_usrname != null && et_pwd != null && et_repeatpwd != null;

        et_usrname.setText("");
        et_pwd.setText("");
        et_repeatpwd.setText("");
        et_usrname.requestFocus();
    }


    /**
     * 检查输入数据合法性，并设置提示信息
     * @return  如果数据合法返回true, 否则返回false
     */
    private boolean checkInput()    {
        EditText et_usrname = (EditText)findViewById(R.id.ac_naet_accountname);
        EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);
        EditText et_repeatpwd = (EditText)findViewById(R.id.ac_naet_repeatpwd);
        assert et_usrname != null && et_pwd != null && et_repeatpwd != null;

        String usr_name = et_usrname.getText().toString();
        String usr_pwd = et_pwd.getText().toString();
        String usr_rpwd = et_repeatpwd.getText().toString();

        Resources res = getResources();
        if(usr_name.isEmpty())  {
            et_usrname.setError(res.getString(R.string.error_no_usrname));
            et_usrname.requestFocus();
            return false;
        }

        if(4 > usr_pwd.length())    {
            et_pwd.setError(res.getString(R.string.error_invalid_password));
            et_pwd.requestFocus();
            return false;
        }

        if(!usr_pwd.equals(usr_rpwd))   {
            et_repeatpwd.setError(res.getString(R.string.error_repeatpwd_notmatch));
            et_repeatpwd.requestFocus();
            return false;
        }

        return true;
    }

    public static class ACAUMsgHandler extends Handler {
        private static final String TAG = "ACAUMsgHandler";
        private final ACAddUsr mActivity;

        public ACAUMsgHandler(ACAddUsr acstart) {
            super();
            mActivity = acstart;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMsgDef.MSG_REPLY: {
                    switch (msg.arg1)   {
                        case AppMsgDef.MSG_USR_ADDUSR :
                            afterAddUsr(msg);
                        break;

                        default:
                            Log.e(TAG, String.format("msg(%s) can not process", msg.toString()));
                            break;
                    }
                }
                break;

                default:
                    Log.e(TAG, String.format("msg(%s) can not process", msg.toString()));
                    break;
            }
        }

        /**
         * 如果添加用户成功就返回，否则显示错误信息
         * @param msg 返回的消息
         */
        private void afterAddUsr(Message msg) {
            Object[] arr = (Object[]) msg.obj;
            if(null != arr) {
                boolean ret = (boolean)arr[0];
                if(ret) {
                    Intent data = (Intent)arr[1];
                    assert null != data;

                    int ret_data = AppGobalDef.INTRET_USR_ADD;
                    mActivity.setResult(ret_data, data);
                    mActivity.finish();
                } else  {
                    String sstr = "添加用户失败!";
                    if(2 < arr.length)
                        sstr = UtilFun.cast(arr[2]);

                    Toast.makeText(ContextUtil.getInstance(),
                            sstr, Toast.LENGTH_LONG).show();

                    mActivity.repeatInput();
                }
            }
        }
    }
}
