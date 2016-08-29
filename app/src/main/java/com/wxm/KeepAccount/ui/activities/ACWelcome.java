package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wxm.KeepAccount.Base.data.AppGobalDef;
import com.wxm.KeepAccount.R;

import java.util.Calendar;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * 用户登陆后首页面
 */
public class ACWelcome extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ACWelcome";
    private static final int    BTDRAW_WIDTH    = 96;
    private static final int    BTDRAW_HEIGHT   = 96;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_welcome);

        init_component();
    }


    private void init_component() {
        // init datashow
        Button bt_datashow = UtilFun.cast(findViewById(R.id.bt_lookdata));
        assert null != bt_datashow;
        bt_datashow.setOnClickListener(this);

        Drawable dr = bt_datashow.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_datashow.setCompoundDrawables(null, dr, null, null);

        // init setting
        Button bt_setting = UtilFun.cast(findViewById(R.id.bt_setting));
        assert null != bt_setting;
        bt_setting.setOnClickListener(this);

        // init add income
        Button bt_addincome = UtilFun.cast(findViewById(R.id.bt_add_income));
        assert null != bt_addincome;
        bt_addincome.setOnClickListener(this);

        dr = bt_addincome.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_addincome.setCompoundDrawables(null, dr, null, null);

        // init add pay
        Button bt_addpay = UtilFun.cast(findViewById(R.id.bt_add_pay));
        assert null != bt_addpay;
        bt_addpay.setOnClickListener(this);

        dr = bt_addpay.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_addpay.setCompoundDrawables(null, dr, null, null);

        // init leave login
        Button bt_leave_login = UtilFun.cast(findViewById(R.id.bt_leave_login));
        assert null != bt_leave_login;
        bt_leave_login.setOnClickListener(this);

        dr = bt_leave_login.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_leave_login.setCompoundDrawables(null, dr, null, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_lookdata : {
                Intent intent = new Intent(this, ACShowRecord.class);
                startActivityForResult(intent, 1);
            }
            break;

            case R.id.bt_setting : {
                Toast.makeText(getApplicationContext(),
                        "invoke setting!",
                        Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.bt_add_income :
            case R.id.bt_add_pay : {
                Intent intent = new Intent(v.getContext(), ACRecord.class);
                intent.putExtra(AppGobalDef.STR_RECORD_ACTION, AppGobalDef.STR_RECORD_ACTION_ADD);

                if (R.id.bt_add_income == id) {
                    intent.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.CNSTR_RECORD_INCOME);
                } else {
                    intent.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.CNSTR_RECORD_PAY);
                }

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(AppGobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA
                                ,"%d-%02d-%02d"
                                ,cal.get(Calendar.YEAR)
                                ,cal.get(Calendar.MONTH) + 1
                                ,cal.get(Calendar.DAY_OF_MONTH)));

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.bt_leave_login :  {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                Log.e(TAG, "view(id :" + id + ")的click未处理");
        }
    }
}
