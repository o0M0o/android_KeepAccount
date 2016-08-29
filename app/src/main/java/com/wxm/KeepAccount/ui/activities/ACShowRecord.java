package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wxm.KeepAccount.Base.data.AppGobalDef;
import com.wxm.KeepAccount.Base.data.AppMsgDef;
import com.wxm.KeepAccount.Base.utility.ContextUtil;
import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import com.wxm.KeepAccount.ui.fragment.GraphViewSlidingTabsFragment;
import com.wxm.KeepAccount.ui.fragment.ListViewSlidingTabsFragment;

import java.util.Calendar;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * tab版本的main activity
 * Created by 123 on 2016/5/16.
 */
public class ACShowRecord
        extends AppCompatActivity
        implements View.OnClickListener  {

    private static final String TAG = "ACShowRecord";
    private static final String GV_VIEW_TXT = "切换列表";
    private static final String LV_VIEW_TXT = "切换图表";

    private ACSMsgHandler mMHHandler;

    private Button bt_view_switch = null;

    private SlidingTabsColorsFragment mTabFragment;
    private GraphViewSlidingTabsFragment gvTabFragment = new GraphViewSlidingTabsFragment();
    private ListViewSlidingTabsFragment lvTabFragment = new ListViewSlidingTabsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_showrecord);

        mMHHandler = new ACSMsgHandler(this);
        initView(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_start_actbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.am_bi_logout: {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    /**
     * 处理按键
     *
     * @param v 被点击
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabbt_record_pay:
            case R.id.tabbt_record_income: {
                Intent intent = new Intent(v.getContext(), ACRecord.class);
                intent.putExtra(AppGobalDef.STR_RECORD_ACTION, AppGobalDef.STR_RECORD_ACTION_ADD);

                if (v.getId() == R.id.tabbt_record_income) {
                    intent.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.CNSTR_RECORD_INCOME);
                } else {
                    intent.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.CNSTR_RECORD_PAY);
                }

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(AppGobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA
                                ,"%d-%02d-%02d"
                                ,cal.get(Calendar.YEAR)
                                ,cal.get(Calendar.MONTH) + 1
                                ,cal.get(Calendar.DAY_OF_MONTH)));

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.tabbt_view_switch: {
                Log.i(TAG, "切换视图");

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (mTabFragment instanceof ListViewSlidingTabsFragment) {
                    mTabFragment = gvTabFragment;
                    //mTabFragment = new GraphViewSlidingTabsFragment();

                    bt_view_switch.setText(GV_VIEW_TXT);
                    bt_view_switch.setCompoundDrawablesWithIntrinsicBounds(null
                                ,getResources().getDrawable(R.drawable.ic_listview)
                                ,null ,null);
                } else {
                    mTabFragment = lvTabFragment;
                    //mTabFragment = new ListViewSlidingTabsFragment();

                    bt_view_switch.setText(LV_VIEW_TXT);
                    bt_view_switch.setCompoundDrawablesWithIntrinsicBounds(null
                            ,getResources().getDrawable(R.drawable.ic_graphview)
                            ,null ,null);
                }

                transaction.replace(R.id.tabfl_content, mTabFragment);
                transaction.commit();
            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Boolean bModify = false;
        if (AppGobalDef.INTRET_RECORD_ADD == resultCode) {
            Log.i(TAG, "从'添加记录'页面返回");

            Message m = Message.obtain(ContextUtil.getMsgHandler(),
                    AppMsgDef.MSG_RECORD_ADD);
            m.obj = new Object[] {data, mMHHandler};
            m.sendToTarget();
        } else if (AppGobalDef.INTRET_DAILY_DETAIL == resultCode) {
            Log.i(TAG, "从详情页面返回");

            bModify = true;
        } else {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

        if (bModify) {
            updateView();
        }
    }

    private void updateView() {
        mTabFragment.notifyDataChange();
    }


    /**
     * 初始化
     *
     * @param savedInstanceState activity创建参数
     */
    private void initView(Bundle savedInstanceState) {
        // set fragment for tab
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //mTabFragment = new ListViewSlidingTabsFragment();
            mTabFragment = lvTabFragment;
            transaction.replace(R.id.tabfl_content, mTabFragment);
            transaction.commit();
        }

        // set button
        Button bt_add_pay = (Button) findViewById(R.id.tabbt_record_pay);
        Button bt_add_income = (Button) findViewById(R.id.tabbt_record_income);
        bt_view_switch = (Button) findViewById(R.id.tabbt_view_switch);
        assert null != bt_add_income && null != bt_add_pay && null != bt_view_switch;

        bt_add_pay.setOnClickListener(this);
        bt_add_income.setOnClickListener(this);
        bt_view_switch.setOnClickListener(this);

        bt_view_switch.setText(LV_VIEW_TXT);
    }



    public class ACSMsgHandler extends Handler {
        private static final String TAG = "ACSMsgHandler";
        private ACShowRecord mACCur;

        public ACSMsgHandler(ACShowRecord cur) {
            super();
            mACCur = cur;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMsgDef.MSG_REPLY: {
                    switch (msg.arg1) {
                        case AppMsgDef.MSG_RECORD_ADD:
                            updateActivity(msg);
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

        private void updateActivity(Message msg) {
            boolean ret = UtilFun.cast(msg.obj);
            if(ret) {
                mACCur.updateView();
            }
        }
    }
}
