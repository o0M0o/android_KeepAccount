package com.wxm.keepaccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
        记录收入
     */
    public void onClickIncomeRecord(View view) {
        // Do something in response to button click
        Intent intent=new Intent(this, IncomeRecordActivity.class);
        startActivityForResult(intent, 1);
    }

    /*
        记录支出
     */
    public void onClickPayRecord(View view) {
        // Do something in response to button click
        Intent intent=new Intent(this, PayRecordActivity.class);
        startActivityForResult(intent, 1);
    }


    /*
        其它activity返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Resources res = getResources();
        final int pay_ret = res.getInteger(R.integer.payrecord_return);
        final int income_ret = res.getInteger(R.integer.incomerecord_return);

        if(resultCode == pay_ret)
        {
            Log.i(TAG, "从支出页面返回");
        }
        else if(resultCode == income_ret)
        {
            Log.i(TAG, "从收入页面返回");
        }
        else
        {
            Log.e(TAG, String.format("非法的resultCode(%d)!",  resultCode));
        }
    }
}
