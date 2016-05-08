package com.wxm.keepaccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wxm.keepaccout.base.AppGobalDef;
import com.wxm.keepaccout.base.AppManager;
import com.wxm.keepaccout.base.AppMsg;
import com.wxm.keepaccout.base.AppMsgDef;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityDailyDetail extends AppCompatActivity {
    private static final String TAG = "ActivityDailyDetail";
    private ListView lv_show;
    private String invoke_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);

        Intent i = getIntent();
        invoke_str = i.getStringExtra(AppGobalDef.TEXT_SELECT_ITEM);
        Log.i(TAG, String.format("invoke with '%s'", invoke_str));

        initViews();
        showListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dailydetail_actbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dailydetailmenu_goback: {
                Resources res = getResources();
                int ret_data = res.getInteger(R.integer.dailydetail_goback);

                Intent data=new Intent();
                setResult(ret_data, data);
                finish();
            }
            return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void initViews()    {
        lv_show = (ListView)findViewById(R.id.lv_daily_detail);
    }

    private void showListView() {
        AppMsg am = new AppMsg();
        am.msg = AppMsgDef.MSG_DAILY_RECORDS_TO_DETAILREPORT;
        am.sender = this;
        am.obj = invoke_str;
        ArrayList<HashMap<String, String>> mylist =
                (ArrayList<HashMap<String, String>>) AppManager.getInstance().ProcessAppMsg(am);

        SimpleAdapter mSchedule = new SimpleAdapter(this,
                mylist,
                R.layout.main_listitem,
                new String[]{AppGobalDef.ITEM_TITLE, AppGobalDef.ITEM_TEXT},
                new int[]{R.id.ItemTitle, R.id.ItemText});

        lv_show.setAdapter(mSchedule);
    }
}
