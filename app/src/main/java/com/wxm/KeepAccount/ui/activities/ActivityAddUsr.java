package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wxm.KeepAccount.BaseLib.AppManager;
import com.wxm.KeepAccount.BaseLib.AppMsg;
import com.wxm.KeepAccount.BaseLib.AppMsgDef;
import com.wxm.KeepAccount.R;

public class ActivityAddUsr
        extends AppCompatActivity
        implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = "ActivityAddUsr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_usr_add);

        initView();
    }


    @Override
    public void onClick(View v)    {
        switch (v.getId())  {
            case R.id.ac_nabt_confirm : {
                if(checkInput()) {
                    EditText et_usrname = (EditText)findViewById(R.id.ac_naet_accountname);
                    EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);

                    Resources res = getResources();
                    int ret_data = res.getInteger(R.integer.account_add_return);

                    Intent data = new Intent();
                    data.putExtra(res.getString(R.string.usr_name),
                                    et_usrname.getText().toString());
                    data.putExtra(res.getString(R.string.usr_pwd),
                                    et_pwd.getText().toString());

                    AppMsg am = new AppMsg();
                    am.msg = AppMsgDef.MSG_USR_ADDUSR;
                    am.sender = this;
                    am.obj = data;
                    AppManager.getInstance().ProcessAppMsg(am);

                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.ac_nabt_giveup : {
                Resources res = getResources();
                int ret_data = res.getInteger(R.integer.account_add_giveup);

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
                Log.d(TAG, "now usr name : " + et_usrname.getText().toString());
            }
            break;

            case R.id.ac_naet_accountpwd : {
                EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);
                Log.d(TAG, "now pwd : " + et_pwd.getText().toString());
            }
            break;

            case R.id.ac_naet_repeatpwd : {
                EditText et_repeatpwd = (EditText)findViewById(R.id.ac_naet_repeatpwd);
                Log.d(TAG, "now repeatpwd : " + et_repeatpwd.getText().toString());
            }
            break;
        }

        return false;
    }


    private void initView() {
        Button bt_confirm = (Button)findViewById(R.id.ac_nabt_confirm);
        Button bt_giveup = (Button)findViewById(R.id.ac_nabt_giveup);

        bt_confirm.setOnClickListener(this);
        bt_giveup.setOnClickListener(this);

        EditText et_usrname = (EditText)findViewById(R.id.ac_naet_accountname);
        EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);
        EditText et_repeatpwd = (EditText)findViewById(R.id.ac_naet_repeatpwd);

        et_usrname.setOnEditorActionListener(this);
        et_pwd.setOnEditorActionListener(this);
        et_repeatpwd.setOnEditorActionListener(this);
    }

    /**
     * 检查输入数据合法性，并设置提示信息
     * @return  如果数据合法返回true, 否则返回false
     */
    private boolean checkInput()    {
        EditText et_usrname = (EditText)findViewById(R.id.ac_naet_accountname);
        EditText et_pwd = (EditText)findViewById(R.id.ac_naet_accountpwd);
        EditText et_repeatpwd = (EditText)findViewById(R.id.ac_naet_repeatpwd);

        String usr_name = et_usrname.getText().toString();
        String usr_pwd = et_pwd.getText().toString();
        String usr_rpwd = et_repeatpwd.getText().toString();

        Resources res = getResources();
        if(!usr_name.contains("@"))     {
            et_usrname.setError(res.getString(R.string.error_invalid_email));
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
}
