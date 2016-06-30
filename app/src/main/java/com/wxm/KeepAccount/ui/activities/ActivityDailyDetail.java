package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.base.data.AppGobalDef;
import com.wxm.KeepAccount.base.data.AppMsgDef;
import com.wxm.KeepAccount.base.data.RecordItem;
import com.wxm.KeepAccount.base.utility.ContextUtil;
import com.wxm.KeepAccount.base.utility.ToolUtil;

import java.util.ArrayList;
import java.util.HashMap;


public class ActivityDailyDetail extends AppCompatActivity {
    private static final String TAG = "ActivityDailyDetail";
    private ListView        lv_show;
    private String          invoke_str;
    private boolean         delete_visity = false;
    private MenuItem        mi_delete;
    private ACDDMsgHandler  mMHHandler;

    private HashMap<Integer, Boolean> cb_state = new HashMap<>();
    private HashMap<Integer, String> cb_sqltag = new HashMap<>();

    // for listview
    private ArrayList<HashMap<String, String>> lv_datalist =
            new ArrayList<>();
    private SimpleAdapter lv_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_daily_detail);
        mMHHandler = new ACDDMsgHandler(this);

        Intent i = getIntent();
        invoke_str = ToolUtil.ReFormatDateString(i.getStringExtra(AppGobalDef.STR_SELECT_ITEM));
        Log.i(TAG, String.format("invoke with '%s'", invoke_str));

        initViews();
        updateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_dailydetail_actbar, menu);

        mi_delete = menu.findItem(R.id.dailydetailmenu_delete);
        mi_delete.setVisible(delete_visity);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home :
            case R.id.dailydetailmenu_goback: {
                int ret_data = AppGobalDef.INTRET_DAILY_DETAIL;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            case R.id.dailydetailmenu_add : {
                Intent intent = new Intent(this, ActivityRecord.class);
                intent.putExtra(AppGobalDef.STR_RECORD_ACTION, AppGobalDef.STR_RECORD_ACTION_ADD);
                intent.putExtra(AppGobalDef.STR_RECORD_DATE, invoke_str);
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
                    Message m = Message.obtain(ContextUtil.getMsgHandler(),
                                       AppMsgDef.MSG_DELETE_RECORDS);
                    m.obj = new Object[] {str_ls, mMHHandler};
                    m.sendToTarget();
                    updateListView();
                }
            }
            break;

            case R.id.dailydetailmenu_help : {
                Intent intent = new Intent(this, ActivityHelp.class);
                intent.putExtra(AppGobalDef.STR_HELP_TYPE, AppGobalDef.STR_HELP_DAILYDETAIL);

                startActivityForResult(intent, 1);
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    private void switchCheckbox()       {
        delete_visity = !delete_visity;
        updateCheckBox(delete_visity ? View.VISIBLE : View.INVISIBLE);
    }

    private void initViews()    {
        // set title
        String tt = String.format("%s详情", ToolUtil.FormatDateString(invoke_str));
        setTitle(tt);

        // set listview
        lv_show = (ListView)findViewById(R.id.lv_daily_detail);
        lv_show.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switchCheckbox();
                return false;
            }
        });

        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("get click : %s", view.toString()));
                //view.setBackgroundResource(R.color.wheat);
                switchCheckbox();
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
        // update date
        Message m = Message.obtain(ContextUtil.getMsgHandler(),
                            AppMsgDef.MSG_TO_DAILY_DETAILREPORT);
        m.obj = new Object[] {invoke_str, mMHHandler};
        m.sendToTarget();
    }

    /**
     * 设置listview的附加选择控件是否显示
     * @param show_stat  如果是View.VISIBLE则显示，否则不显示
     */
    private void updateCheckBox(int show_stat)   {
        int ct = lv_show.getChildCount();
        for(int i = 0; i < ct; ++i) {
            View v = lv_show.getChildAt(i);

            CheckBox cb = (CheckBox)v.findViewById(R.id.dailydetail_cb);
            cb.setVisibility(show_stat);
            if(View.VISIBLE == show_stat)  {
                cb.setChecked(false);
            }

            Button mod = (Button)v.findViewById(R.id.dailydetail_bt);
            mod.setVisibility(show_stat);
        }

        if(null != mi_delete)
            mi_delete.setVisible(show_stat == View.VISIBLE);
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

    public void onBTClick(View v)   {
        Button mod_bt = (Button)v;
        int pos = lv_show.getPositionForView(mod_bt);
        //Log.d(TAG, "onBTClick at " + pos);

        String str_id = cb_sqltag.get(pos);
        Message m = Message.obtain(ContextUtil.getMsgHandler(), AppMsgDef.MSG_RECORD_GET);
        m.obj = new Object[] {str_id, mMHHandler};
        m.sendToTarget();
    }

    private void loadView(ArrayList<HashMap<String, String>> nal)   {
        // clear old view
        if(!lv_datalist.isEmpty()) {
            cb_sqltag.clear();
            cb_state.clear();
            lv_datalist.clear();
        }

        lv_datalist.addAll(nal);
        int ct = lv_datalist.size();
        for (int i = 0; i < ct; ++i) {
            cb_sqltag.put(i, lv_datalist.get(i).get(AppGobalDef.ITEM_ID));
        }

        lv_adapter.notifyDataSetChanged();
        updateCheckBox(View.INVISIBLE);
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

        Message m = Message.obtain(ContextUtil.getMsgHandler());
        m.obj = new Object[] {data, mMHHandler};
        if(AppGobalDef.INTRET_RECORD_ADD ==  resultCode)  {
            //Log.i(TAG, "从'添加记录'页面返回");
            m.what = AppMsgDef.MSG_RECORD_ADD;
            m.sendToTarget();
        }
        else if(AppGobalDef.INTRET_RECORD_MODIFY == resultCode)   {
            //Log.i(TAG, "从'修改记录'页面返回");
            m.what = AppMsgDef.MSG_RECORD_MODIFY;
            m.sendToTarget();
        }
        else    {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

    }


    public class ACDDMsgHandler extends Handler  {
        private static final String TAG = "ACDDMsgHandler";
        private ActivityDailyDetail     mACCur;

        public ACDDMsgHandler(ActivityDailyDetail cur)  {
            super();
            mACCur = cur;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMsgDef.MSG_REPLY: {
                    switch (msg.arg1)   {
                        case AppMsgDef.MSG_RECORD_ADD :
                        case AppMsgDef.MSG_RECORD_MODIFY :
                            updateActivity(msg);
                            break;

                        case AppMsgDef.MSG_RECORD_GET :
                            switchActivity(msg);
                            break;

                        case AppMsgDef.MSG_TO_DAILY_DETAILREPORT :
                            reloadView(msg);
                            break;

                        default:
                            Log.e(TAG, String.format("msg(%s) can not process", msg.toString()));
                            break;
                    }
                }
                break;

                default:
                    Log.e(TAG, String.format("msg(%s) can not process", msg.toString()));
                    break;
            }
        }

        private void reloadView(Message msg) {
            ArrayList<HashMap<String, String>> up_ls = ToolUtil.cast(msg.obj);
            assert null != up_ls;

            mACCur.loadView(up_ls);
        }

        private void switchActivity(Message msg) {
            RecordItem ri = ToolUtil.cast(msg.obj);
            if(null != ri)  {
                Intent intent = new Intent(mACCur, ActivityRecord.class);
                intent.putExtra(AppGobalDef.STR_RECORD_ACTION,
                        AppGobalDef.STR_RECORD_ACTION_MODIFY);

                intent.putExtra(AppGobalDef.STR_RECORD_DATE, invoke_str);
                intent.putExtra(AppGobalDef.STR_RECORD, ri);
                startActivityForResult(intent, 1);
            }
            else   {
                Log.e(TAG, "get reply message but with no result");
            }
        }

        private void updateActivity(Message msg) {
            boolean ret = ToolUtil.cast(msg.obj);
            if(ret) {
                mACCur.updateListView();
            }
        }
    }
}

