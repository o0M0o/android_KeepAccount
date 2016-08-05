package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wxm.KeepAccount.Base.data.AppGobalDef;
import com.wxm.KeepAccount.Base.data.AppMsgDef;
import com.wxm.KeepAccount.Base.utility.ContextUtil;
import com.wxm.KeepAccount.Base.utility.ToolUtil;
import com.wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import com.wxm.KeepAccount.ui.fragment.GraphViewSlidingTabsFragment;
import com.wxm.KeepAccount.ui.fragment.ListViewSlidingTabsFragment;
import com.wxm.KeepAccount.R;

import java.util.Calendar;

/**
 * tab版本的main activity
 * Created by 123 on 2016/5/16.
 */
public class ACStart
        extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ACStart";
    private static final String GV_VIEW_TXT = "切换列表";
    private static final String LV_VIEW_TXT = "切换图表";

    private ACSMsgHandler mMHHandler;

    private Button bt_add_pay = null;
    private Button bt_add_income = null;
    private Button bt_view_switch = null;

    private SlidingTabsColorsFragment mTabFragment;
    private GraphViewSlidingTabsFragment gvTabFragment = new GraphViewSlidingTabsFragment();
    private ListViewSlidingTabsFragment lvTabFragment = new ListViewSlidingTabsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_start);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_start_outerlayout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                        String.format("%d-%02d-%02d",
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.DAY_OF_MONTH)));

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
                } else {
                    mTabFragment = lvTabFragment;
                    //mTabFragment = new ListViewSlidingTabsFragment();

                    bt_view_switch.setText(LV_VIEW_TXT);
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
        // set nav view
        Toolbar tb = (Toolbar) findViewById(R.id.ac_navw_toolbar);
        setSupportActionBar(tb);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_start_outerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nv = (NavigationView) findViewById(R.id.start_nav_view);
        nv.setNavigationItemSelectedListener(this);
        //nv.findViewById(R.id.nav_setting).setVisibility(View.INVISIBLE);
        //nv.findViewById(R.id.nav_share_app).setVisibility(View.INVISIBLE);

        // set fragment for tab
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //mTabFragment = new ListViewSlidingTabsFragment();
            mTabFragment = lvTabFragment;
            transaction.replace(R.id.tabfl_content, mTabFragment);
            transaction.commit();
        }

        // set button
        bt_add_pay = (Button) findViewById(R.id.tabbt_record_pay);
        bt_add_income = (Button) findViewById(R.id.tabbt_record_income);
        bt_view_switch = (Button) findViewById(R.id.tabbt_view_switch);
        bt_add_pay.setOnClickListener(this);
        bt_add_income.setOnClickListener(this);
        bt_view_switch.setOnClickListener(this);

        bt_view_switch.setText(LV_VIEW_TXT);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_help: {
                /*Toast.makeText(getApplicationContext(),
                        "invoke help!",
                        Toast.LENGTH_SHORT).show();*/

                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(AppGobalDef.STR_HELP_TYPE, AppGobalDef.STR_HELP_START);

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.nav_setting: {
                Toast.makeText(getApplicationContext(),
                        "invoke setting!",
                        Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.nav_share_app: {
                Toast.makeText(getApplicationContext(),
                        "invoke share!",
                        Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.nav_contact_writer: {
                /*Toast.makeText(getApplicationContext(),
                        "invoke contact!",
                        Toast.LENGTH_SHORT).show();*/
                contactWriter();
            }
            break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_start_outerlayout);
        assert null != drawer;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void contactWriter() {
        Resources res = getResources();

        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(
                Uri.parse(
                        String.format("mailto:%s", res.getString(R.string.contact_email))));
        //data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
        //data.putExtra(Intent.EXTRA_TEXT, "这是内容");
        startActivity(data);
    }


    public class ACSMsgHandler extends Handler {
        private static final String TAG = "ACSMsgHandler";
        private ACStart mACCur;

        public ACSMsgHandler(ACStart cur) {
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
            boolean ret = ToolUtil.cast(msg.obj);
            if(ret) {
                mACCur.updateView();
            }
        }
    }
}
