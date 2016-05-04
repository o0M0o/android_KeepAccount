package com.wxm.keepaccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DBManager dbm;

    private static final String ITEM_TITLE = "ItemTitle";
    private static final String ITEM_TEXT = "ItemText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showListView();
    }

    /**
     * 加载并显示数据
     */
    private void showListView() {
        ListView lv = (ListView) findViewById(R.id.lv_main);
        ArrayList<HashMap<String, String>> mylist = getListViewHMap();

        SimpleAdapter mSchedule = new SimpleAdapter(this,
                                                    mylist,
                                                    R.layout.main_listitem,
                                                    new String[] {ITEM_TITLE, ITEM_TEXT},
                                                    new int[] {R.id.ItemTitle, R.id.ItemText});

        lv.setAdapter(mSchedule);
    }

    /**
     * 生成供listview显示的数据
     * @return 在listview上显示的数据
     */
    @NonNull
    private ArrayList<HashMap<String, String>> getListViewHMap() {

        /* get data from sqlite */
        dbm = new DBManager(this);
        List<RecordItem> lr = dbm.query();

        HashMap<String, ArrayList<RecordItem>> hm_data =
                                            new HashMap<String, ArrayList<RecordItem>>();
        for (RecordItem record : lr) {
            String h_k = record.record_ts.toString().substring(0, 10);
            ArrayList<RecordItem> h_v = hm_data.get(h_k);
            if(null == h_v)
            {
                ArrayList<RecordItem> v = new ArrayList<RecordItem>();
                v.add(record);
                hm_data.put(h_k, v);
            }
            else
            {
                h_v.add(record);
            }
        }

        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        ArrayList<String> set_k = new ArrayList<String>(hm_data.keySet());
        Collections.sort(set_k);
        for(String k : set_k)
        {
            ArrayList<RecordItem> v = hm_data.get(k);

            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;

            for(RecordItem r : v)
            {
                if((r.record_type.equals("pay")) || (r.record_type.equals("支出")))
                {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(r.record_val);
                }
                else
                {
                    income_cout += 1;
                    income_amount = income_amount.add(r.record_val);
                }
            }

            String show_str =
                    String.format("支出笔数 ： %d, 支出金额 ：%.02f\n收入笔数 ： %d, 收入金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(ITEM_TITLE, k);
            map.put(ITEM_TEXT, show_str);
            mylist.add(map);
        }

//        for (RecordItem record : lr) {
//            //Log.d(TAG, String.format("old record [%s]", record.toString()));
//            String show_str =
//                    String.format("类型 : %s  金额 : %.02f\n时间 : %s",
//                            record.record_info, record.record_val,
//                            record.record_ts.toString().substring(0, 19));
//        }

        return mylist;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actbar_menu, menu);
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

        Boolean bAdd = false;
        ArrayList<RecordItem> items = new ArrayList<RecordItem>();
        if(resultCode == pay_ret)
        {
            Log.i(TAG, "从支出页面返回");
            Date de = new Date();
            RecordItem ri = new RecordItem();
            ri.record_type = "支出";
            ri.record_info = data.getStringExtra(res.getString(R.string.pay_type));
            ri.record_val = new BigDecimal(
                                    data.getStringExtra(
                                            res.getString(R.string.pay_val)));
            ri.record_ts.setTime(de.getTime());
            items.add(ri);
            bAdd = true;
        }
        else if(resultCode == income_ret)
        {
            Log.i(TAG, "从收入页面返回");
            Date de = new Date();
            RecordItem ri = new RecordItem();
            ri.record_type = "收入";
            ri.record_info = data.getStringExtra(res.getString(R.string.income_type));
            ri.record_val = new BigDecimal(
                                    data.getStringExtra(
                                            res.getString(R.string.income_val)));
            ri.record_ts.setTime(de.getTime());
            items.add(ri);
            bAdd = true;
        }
        else
        {
            Log.d(TAG, String.format("不处理的resultCode(%d)!",  resultCode));
        }

        if(bAdd) {
            dbm.add(items);
            showListView();
        }
    }
}
