package com.wxm.keepaccount;

import android.content.Intent;
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
        Intent data=new Intent();
        setResult(20, data);

        finish();
    }
}
