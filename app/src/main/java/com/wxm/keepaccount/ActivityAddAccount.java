package com.wxm.KeepAccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityAddAccount
        extends AppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        initView();
    }


    @Override
    public void onClick(View v)    {
        switch (v.getId())  {
            case R.id.ac_nabt_confirm : {
                Resources res = getResources();
                int ret_data = res.getInteger(R.integer.account_add_return);

                Intent data=new Intent();
                setResult(ret_data, data);
                finish();
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


    private void initView() {
        Button bt_confirm = (Button)findViewById(R.id.ac_nabt_confirm);
        Button bt_giveup = (Button)findViewById(R.id.ac_nabt_giveup);

        bt_confirm.setOnClickListener(this);
        bt_giveup.setOnClickListener(this);
    }
}
