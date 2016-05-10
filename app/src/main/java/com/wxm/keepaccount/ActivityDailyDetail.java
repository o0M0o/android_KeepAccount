package com.wxm.keepaccount;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wxm.keepaccout.base.AppGobalDef;
import com.wxm.keepaccout.base.AppManager;
import com.wxm.keepaccout.base.AppMsg;
import com.wxm.keepaccout.base.AppMsgDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class ActivityDailyDetail extends AppCompatActivity {
    private static final String TAG = "ActivityDailyDetail";
    private ListView lv_show;
    private String invoke_str;

    private HashMap<Integer, Boolean> cb_state = new HashMap<>();
    private HashMap<Integer, String> cb_sqltag = new HashMap<>();

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
            break;

            case R.id.dailydetailmenu_delete :  {
                ArrayList<String> str_ls = new ArrayList<>();
                for(int i : cb_state.keySet()) {
                    if(cb_state.get(i))     {
                        str_ls.add(cb_sqltag.get(i));
                    }
                }

                if(0 < str_ls.size()) {
                    AppMsg am = new AppMsg();
                    am.msg = AppMsgDef.MSG_DELETE_RECORDS;
                    am.sender = this;
                    am.obj = str_ls;

                    AppManager.getInstance().ProcessAppMsg(am);
                    showListView();
                }

            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
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

        int ct  = mylist.size();
        for(int i = 0; i < ct; ++i) {
            cb_sqltag.put(i, mylist.get(i).get(AppGobalDef.TEXT_ITEMID));
        }

        SimpleAdapter mSchedule = new SimpleAdapter(this,
                mylist,
                R.layout.daily_detail_listitem,
                new String[]{AppGobalDef.ITEM_TITLE, AppGobalDef.ITEM_TEXT},
                new int[]{R.id.DailyDetailTitle, R.id.DailyDetailText}) {
            @Override
            public int getViewTypeCount() {
                return getCount();
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }
        };

        lv_show.setAdapter(mSchedule);
    }


    /**
     * checkbox被点击后激活
     * @param v  被点击的view
     */
    public void onCBClick(View v)
    {
        CheckBox sv = (CheckBox)v;
        int pos = lv_show.getPositionForView(sv);
        Log.d(TAG, "onCBClick at " + pos);

        if(!sv.isSelected())     {
            cb_state.put(pos, true);
        }
        else       {
            cb_state.put(pos, false);
        }
    }
}

