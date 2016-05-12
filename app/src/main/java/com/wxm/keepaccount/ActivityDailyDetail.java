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
import android.widget.AdapterView;
import android.widget.CheckBox;
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
    private boolean delete_visity = false;
    private MenuItem mi_delete = null;

    private HashMap<Integer, Boolean> cb_state = new HashMap<>();
    private HashMap<Integer, String> cb_sqltag = new HashMap<>();

    // for listview
    private ArrayList<HashMap<String, String>> lv_datalist =
            new ArrayList<>();
    private SimpleAdapter lv_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);

        Intent i = getIntent();
        invoke_str = i.getStringExtra(AppGobalDef.TEXT_SELECT_ITEM);
        Log.i(TAG, String.format("invoke with '%s'", invoke_str));

        initViews();
        updateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dailydetail_actbar_menu, menu);

        mi_delete = menu.findItem(R.id.dailydetailmenu_delete);
        mi_delete.setVisible(delete_visity);
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

            case R.id.dailydetailmenu_add : {
                Intent intent = new Intent(this, ActivityAddRecord.class);
                intent.putExtra(AppGobalDef.TEXT_RECORD_DATE, invoke_str);
                startActivityForResult(intent, 1);
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
                    updateListView();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    private void switchCheckbox()       {
        delete_visity = !delete_visity;
        int vv = delete_visity ? View.VISIBLE : View.INVISIBLE;

        int ct = lv_show.getChildCount();
        for(int i = 0; i < ct; ++i) {
            View v = lv_show.getChildAt(i);

            CheckBox cb = (CheckBox)v.findViewById(R.id.dailydetail_cb);
            cb.setVisibility(vv);
        }

        mi_delete.setVisible(delete_visity);
    }

    private void initViews()    {
        // set title
        //String tt = String.format("%s日详情", invoke_str);
        //getDelegate().setTitle(tt);

        // set listview
        lv_show = (ListView)findViewById(R.id.lv_daily_detail);
        lv_show.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switchCheckbox();
                return false;
            }
        });

        lv_datalist.clear();
        cb_state.clear();
        cb_sqltag.clear();

        lv_adapter= new SimpleAdapter(this,
                lv_datalist,
                R.layout.daily_detail_listitem,
                new String[]{AppGobalDef.ITEM_TITLE, AppGobalDef.ITEM_TEXT},
                new int[]{R.id.DailyDetailTitle, R.id.DailyDetailText}) {
            @Override
            public int getViewTypeCount() {
                int org_ct = getCount();
                return org_ct < 1 ? 1 : org_ct;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }
        };

        lv_show.setAdapter(lv_adapter);
    }

    private void updateListView()   {
        // clear delete flag
        int cct = lv_show.getChildCount();
        for(int i = 0; i < cct; ++i) {
            View v = lv_show.getChildAt(i);

            CheckBox cb = (CheckBox)v.findViewById(R.id.dailydetail_cb);
            cb.setVisibility(View.INVISIBLE);
        }

        if(null != mi_delete) {
            mi_delete.setVisible(false);
        }

        // update date
        AppMsg am = new AppMsg();
        am.msg = AppMsgDef.MSG_DAILY_RECORDS_TO_DETAILREPORT;
        am.sender = this;
        am.obj = invoke_str;
        ArrayList<HashMap<String, String>> up_ls =
                (ArrayList<HashMap<String, String>>) AppManager.getInstance().ProcessAppMsg(am);

        cb_state.clear();
        lv_datalist.clear();
        for(HashMap<String, String> r : up_ls)   {
            lv_datalist.add(r);
        }

        cb_sqltag.clear();
        int ct = lv_datalist.size();
        for (int i = 0; i < ct; ++i) {
            cb_sqltag.put(i, lv_datalist.get(i).get(AppGobalDef.TEXT_ITEMID));
        }

        lv_adapter.notifyDataSetChanged();
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


    /**
     * 其它activity返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);

        updateListView();
    }
}

