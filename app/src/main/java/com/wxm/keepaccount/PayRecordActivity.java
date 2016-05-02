package com.wxm.keepaccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PayRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
    }


    /*
        确认输入数据
     */
    public void onClickOk(View view)    {
        Resources res = getResources();
        int ret_data = res.getInteger(R.integer.payrecord_return);

        Intent data=new Intent();
        setResult(ret_data, data);

        finish();
    }
}
