package com.wxm.keepaccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DBManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* for sqlite */
        dbm = new DBManager(this);
        // only for test
        List<RecordItem> lr = dbm.query();
        for (RecordItem record : lr) {
            Log.d(TAG, String.format("old record [%s]", record.toString()));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actbar_menu, menu);
        return true;
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

        ArrayList<RecordItem> items = new ArrayList<RecordItem>();
        if(resultCode == pay_ret)
        {
            Log.i(TAG, "从支出页面返回");
            Date de = new Date();
            RecordItem ri = new RecordItem();
            ri.record_type = "pay";
            ri.record_info = data.getStringExtra(res.getString(R.string.pay_type));
            ri.record_val = new BigDecimal(
                                    data.getStringExtra(
                                            res.getString(R.string.pay_val)));
            ri.record_ts.setTime(de.getTime());
            items.add(ri);
        }
        else if(resultCode == income_ret)
        {
            Log.i(TAG, "从收入页面返回");
            Date de = new Date();
            RecordItem ri = new RecordItem();
            ri.record_type = "income";
            ri.record_info = data.getStringExtra(res.getString(R.string.income_type));
            ri.record_val = new BigDecimal(
                                    data.getStringExtra(
                                            res.getString(R.string.income_val)));
            ri.record_ts.setTime(de.getTime());
            items.add(ri);
        }
        else
        {
            Log.e(TAG, String.format("非法的resultCode(%d)!",  resultCode));
        }

        dbm.add(items);
    }
}
