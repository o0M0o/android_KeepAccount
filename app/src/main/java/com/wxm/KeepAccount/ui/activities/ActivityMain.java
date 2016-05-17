package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wxm.KeepAccount.BaseLib.AppGobalDef;
import com.wxm.KeepAccount.BaseLib.AppManager;
import com.wxm.KeepAccount.BaseLib.AppMsg;
import com.wxm.KeepAccount.BaseLib.AppMsgDef;
import com.wxm.KeepAccount.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "ActivityMain";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        showListView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * 初始化可视控件
     */
    private void initViews()
    {
        Button pay_bt = (Button)findViewById(R.id.bt_record_pay);
        pay_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivityAddRecord.class);
                intent.putExtra(AppGobalDef.TEXT_RECORD_TYPE, AppGobalDef.TEXT_RECORD_PAY);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(AppGobalDef.TEXT_RECORD_DATE,
                        String.format("%d-%02d-%02d",
                                cal.get(cal.YEAR),
                                cal.get(cal.MONTH) + 1,
                                cal.get(cal.DAY_OF_MONTH)));

                startActivityForResult(intent, 1);
            }
        });

        Button income_bt = (Button)findViewById(R.id.bt_record_income);
        income_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivityAddRecord.class);
                intent.putExtra(AppGobalDef.TEXT_RECORD_TYPE, AppGobalDef.TEXT_RECORD_INCOME);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(AppGobalDef.TEXT_RECORD_DATE,
                        String.format("%d-%02d-%02d",
                                cal.get(cal.YEAR),
                                cal.get(cal.MONTH) + 1,
                                cal.get(cal.DAY_OF_MONTH)));

                startActivityForResult(intent, 1);
            }
        });

        ListView lv = (ListView) findViewById(R.id.lv_main);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hmsel =
                        (HashMap<String, String>)parent.getItemAtPosition(position);

                //String str= parent.getItemAtPosition(position).toString();
                //String class_str= parent.getItemAtPosition(position).getClass().toString();
                //Log.d(TAG, String.format("long click(%s) : %s", class_str, str));

                Intent intent = new Intent(parent.getContext(), ActivityDailyDetail.class);
                intent.putExtra(AppGobalDef.TEXT_SELECT_ITEM,
                                    (String)hmsel.get(AppGobalDef.ITEM_TITLE));
                startActivityForResult(intent, 1);
                return true;
            }
        });
    }

    /**
     * 加载并显示数据
     */
    private void showListView() {
        ListView lv = (ListView) findViewById(R.id.lv_main);

        AppMsg am = new AppMsg();
        am.msg = AppMsgDef.MSG_ALL_RECORDS_TO_DAYREPORT;
        am.sender = this;
        ArrayList<HashMap<String, String>> mylist =
                (ArrayList<HashMap<String, String>>) AppManager.getInstance().ProcessAppMsg(am);

        SimpleAdapter mSchedule = new SimpleAdapter(this,
                mylist,
                R.layout.main_listitem,
                new String[]{AppGobalDef.ITEM_TITLE, AppGobalDef.ITEM_TEXT},
                new int[]{R.id.ItemTitle, R.id.ItemText});

        lv.setAdapter(mSchedule);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.am_bi_logout : {
                Resources res = getResources();
                int ret_data = res.getInteger(R.integer.usr_logout);

                Intent data=new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }


    /*
        其它activity返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);

        Resources res = getResources();
        final int dailydetail_ret = res.getInteger(R.integer.dailydetail_goback);
        final int add_ret = res.getInteger(R.integer.addrecord_return);

        Boolean bModify = false;
        if(resultCode == add_ret)    {
            Log.i(TAG, "从'添加记录'页面返回");

            AppMsg am = new AppMsg();
            am.msg = AppMsgDef.MSG_ADD_RECORD;
            am.sender = this;
            am.obj = data;
            AppManager.getInstance().ProcessAppMsg(am);

            bModify = true;
        } else if (resultCode == dailydetail_ret) {
            Log.i(TAG, "从详情页面返回");

            bModify = true;
        } else {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

        if (bModify) {
            showListView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.wxm.keepaccount/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.wxm.keepaccount/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}