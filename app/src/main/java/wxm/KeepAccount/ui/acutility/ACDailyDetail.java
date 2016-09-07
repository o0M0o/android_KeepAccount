package wxm.KeepAccount.ui.acutility;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppMsgDef;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACHelp;


public class ACDailyDetail extends AppCompatActivity {
    private static final String TAG = "ACDailyDetail";
    private static final String CHECKBOX_STATUS = "checkbox_status";
    private static final String CHECKBOX_VISIBLE = "checkbox_visible";
    private static final String CHECKBOX_STATUS_SELECTED = "selected";
    private static final String CHECKBOX_STATUS_NOSELECTED = "no_selected";
    private static final String CHECKBOX_VISIBLE_SHOW = "show";
    private static final String CHECKBOX_VISIBLE_NOSHOW = "no_show";

    private static final int[] ITEM_DRAWABLES = {
            R.drawable.ic_leave
            ,R.drawable.ic_edit
            ,R.drawable.ic_add};

    private ListView        lv_show;
    private String          invoke_str;
    private boolean         delete_visity = false;
    private MenuItem        mi_delete;
    private ACDDMsgHandler  mMHHandler;

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
            case R.id.dailydetailmenu_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_DAILYDETAIL);

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
        if(null != mi_delete)
            mi_delete.setVisible(delete_visity);

        for (HashMap<String, String> i : lv_datalist) {
            i.put(CHECKBOX_STATUS, CHECKBOX_STATUS_NOSELECTED);
            i.put(CHECKBOX_VISIBLE,
                    delete_visity ? CHECKBOX_VISIBLE_SHOW : CHECKBOX_VISIBLE_NOSHOW);
        }

        lv_adapter.notifyDataSetChanged();
    }

    private void initViews()    {
        // init ray menu
        RayMenu rayMenu = UtilFun.cast(findViewById(R.id.rm_record_dailydetail));
        assert null != rayMenu;
        final int itemCount = ITEM_DRAWABLES.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(ITEM_DRAWABLES[i]);

            final int position = ITEM_DRAWABLES[i];
            rayMenu.addItem(item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnRayMenuClick(position);
                    }
                });
        }

        // set title
        String tt = String.format("%s详情", ToolUtil.FormatDateString(invoke_str));
        setTitle(tt);

        // set listview
        lv_show = (ListView)findViewById(R.id.lv_daily_detail);

        lv_datalist.clear();
        lv_adapter= new SimpleAdapter(this,
                lv_datalist,
                R.layout.li_daily_detail,
                new String[]{AppGobalDef.ITEM_TITLE, AppGobalDef.ITEM_TEXT},
                new int[]{R.id.DailyDetailTitle, R.id.DailyDetailText}) {
            @Override
            public int getViewTypeCount() {
                int org_ct = getCount();
                return org_ct < 1 ? 1 : org_ct;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View ret = super.getView(position, convertView, parent);
                if(null != ret) {
                    CheckBox cb = (CheckBox)ret.findViewById(R.id.dailydetail_cb);
                    Button mod = (Button)ret.findViewById(R.id.dailydetail_bt);
                    assert null != cb && null != mod;

                    HashMap<String, String> m = lv_datalist.get(position);
                    if(m.get(CHECKBOX_VISIBLE).equals(CHECKBOX_VISIBLE_SHOW))   {
                        cb.setVisibility(View.VISIBLE);
                        cb.setChecked(false);

                        mod.setVisibility(View.VISIBLE);
                    }
                    else    {
                        cb.setVisibility(View.INVISIBLE);

                        mod.setVisibility(View.INVISIBLE);
                    }
                }
                return ret;
            }


            @Override
            public int getItemViewType(int position) {
                return position;
            }
        };

        lv_show.setAdapter(lv_adapter);
    }

    /**
     * 发出消息请求新数据
     */
    private void updateListView()   {
        // update date
        Message m = Message.obtain(ContextUtil.getMsgHandler(),
                            AppMsgDef.MSG_TO_DAILY_DETAILREPORT);
        m.obj = new Object[] {invoke_str, mMHHandler};
        m.sendToTarget();
    }


    /**
     * checkbox被点击后激活
     * @param v  被点击的view
     */
    public void onCBClick(View v)       {
        CheckBox sv = (CheckBox)v;
        int pos = lv_show.getPositionForView(sv);
        //Log.d(TAG, "onCBClick at " + pos);

        HashMap<String, String> m = lv_datalist.get(pos);
        m.put(CHECKBOX_STATUS,
                sv.isSelected() ? CHECKBOX_STATUS_NOSELECTED: CHECKBOX_STATUS_SELECTED);
    }

    public void onBTClick(View v)   {
        int pos = lv_show.getPositionForView(v);
        //Log.d(TAG, "onBTClick at " + pos);

        HashMap<String, String> hm = lv_datalist.get(pos);
        String str_id = hm.get(AppGobalDef.ITEM_ID);
        String str_type = hm.get(AppGobalDef.ITEM_TYPE);

        Message m = Message.obtain(ContextUtil.getMsgHandler(), AppMsgDef.MSG_RECORD_GET);
        m.obj = new Object[] { str_type, Integer.parseInt(str_id), mMHHandler};
        m.sendToTarget();
    }

    /**
     * 用新数据更新视图
     * @param nal  新数据
     */
    private void loadView(ArrayList<HashMap<String, String>> nal)   {
        // clear old view
        lv_datalist.clear();

        lv_datalist.addAll(nal);
        for(HashMap<String, String> i : lv_datalist)    {
            i.put(CHECKBOX_STATUS, CHECKBOX_STATUS_NOSELECTED);
            i.put(CHECKBOX_VISIBLE, CHECKBOX_VISIBLE_NOSHOW);
        }

        lv_adapter.notifyDataSetChanged();
        delete_visity = false;
        if(null != mi_delete)
            mi_delete.setVisible(false);
    }


    /**
     * raymenu点击事件
     * @param resid 点击发生的资源ID
     */
    private void OnRayMenuClick(int resid)  {
        switch (resid)  {
            case R.drawable.ic_add :    {
                Intent intent = new Intent(this, ACNoteEdit.class);
                intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_ADD);
                intent.putExtra(AppGobalDef.STR_RECORD_DATE, invoke_str);
                startActivityForResult(intent, 1);
            }
            break;

            case R.drawable.ic_edit :     {
                switchCheckbox();
            }
            break;

            case R.drawable.ic_leave :  {
                int ret_data = AppGobalDef.INTRET_DAILY_DETAIL;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                Log.e(TAG, "未处理的resid : " + resid);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);

        Message m = Message.obtain(ContextUtil.getMsgHandler());
        m.obj = new Object[] {data, mMHHandler};
        if(AppGobalDef.INTRET_RECORD_ADD ==  resultCode)  {
            //Log.i(TAG, "从'添加记录'页面返回");
            updateListView();
        }
        else if(AppGobalDef.INTRET_RECORD_MODIFY == resultCode)   {
            //Log.i(TAG, "从'修改记录'页面返回");
            updateListView();
        }
        else    {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

    }


    public class ACDDMsgHandler extends Handler  {
        private static final String TAG = "ACDDMsgHandler";
        private ACDailyDetail mACCur;

        public ACDDMsgHandler(ACDailyDetail cur)  {
            super();
            mACCur = cur;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMsgDef.MSG_REPLY: {
                    switch (msg.arg1)   {
                        case AppMsgDef.MSG_DELETE_RECORDS :
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
            ArrayList<HashMap<String, String>> up_ls = UtilFun.cast(msg.obj);
            assert null != up_ls;

            mACCur.loadView(up_ls);
        }

        /**
         * 切换到记录修改UI
         * @param msg  消息
         */
        private void switchActivity(Message msg) {
            Object[] objarr = UtilFun.cast(msg.obj);
            String ty = UtilFun.cast(objarr[0]);
            Object ret = objarr[1];
            if(null != ret) {
                Intent intent = new Intent(mACCur, ACNoteEdit.class);
                intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_MODIFY);
                if (ty.equals(AppGobalDef.CNSTR_RECORD_PAY)) {
                    intent.putExtra(ACNoteEdit.PARA_NOTE_PAY, (PayNoteItem)ret);
                } else  {
                    intent.putExtra(ACNoteEdit.PARA_NOTE_INCOME, (IncomeNoteItem)ret);
                }

                startActivityForResult(intent, 1);
            }
            else   {
                Log.e(TAG, "get reply message but with no result");
            }
        }

        private void updateActivity(Message msg) {
            boolean ret = UtilFun.cast(msg.obj);
            if(ret) {
                mACCur.updateListView();
            }
        }
    }
}

