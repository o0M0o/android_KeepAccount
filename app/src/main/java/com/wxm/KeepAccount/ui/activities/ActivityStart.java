package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wxm.KeepAccount.BaseLib.AppGobalDef;
import com.wxm.KeepAccount.BaseLib.AppManager;
import com.wxm.KeepAccount.BaseLib.AppMsg;
import com.wxm.KeepAccount.BaseLib.AppMsgDef;
import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import com.wxm.KeepAccount.ui.base.activities.TabActivityBase;

import java.util.Calendar;

/**
 * tab版本的main activity
 * Created by 123 on 2016/5/16.
 */
public class ActivityStart
        extends TabActivityBase
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ActivityStart";
    private Button bt_add_pay = null;
    private Button bt_add_income = null;

    private SlidingTabsColorsFragment mTabFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_start);

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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    /**
     * 处理按键
     * @param v 被点击
     */
    @Override
    public void onClick(View v) {
        switch(v.getId())    {
            case R.id.tabbt_record_pay :    {
                Intent intent = new Intent(v.getContext(), ActivityRecord.class);
                intent.putExtra(AppGobalDef.STR_RECORD_ACTION, AppGobalDef.STR_RECORD_ACTION_ADD);
                intent.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.CNSTR_RECORD_PAY);

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

            case R.id.tabbt_record_income :    {
                Intent intent = new Intent(v.getContext(), ActivityRecord.class);
                intent.putExtra(AppGobalDef.STR_RECORD_ACTION, AppGobalDef.STR_RECORD_ACTION_ADD);
                intent.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.CNSTR_RECORD_INCOME);

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
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);

        Resources res = getResources();
        final int dailydetail_ret = res.getInteger(R.integer.dailydetail_goback);

        Boolean bModify = false;
        if(AppGobalDef.INTRET_RECORD_ADD == resultCode)    {
            Log.i(TAG, "从'添加记录'页面返回");

            AppMsg am = new AppMsg();
            am.msg = AppMsgDef.MSG_RECORD_ADD;
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
            mTabFragment.notifyDataChange();
        }
    }


    /**
     * 初始化
     * @param savedInstanceState activity创建参数
     */
    private void initView(Bundle savedInstanceState) {
        // set nav view
        Toolbar tb = (Toolbar)findViewById(R.id.ac_navw_toolbar);
        setSupportActionBar(tb);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_start_outerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView nv = (NavigationView) findViewById(R.id.start_nav_view);
        nv.setNavigationItemSelectedListener(this);

        // set fragment for tab
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mTabFragment = new SlidingTabsColorsFragment();
            transaction.replace(R.id.tabfl_content, mTabFragment);
            transaction.commit();
        }

        // set button
        bt_add_pay = (Button)findViewById(R.id.tabbt_record_pay);
        bt_add_income = (Button)findViewById(R.id.tabbt_record_income);
        bt_add_pay.setOnClickListener(this);
        bt_add_income.setOnClickListener(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id)  {
            case R.id.nav_help :    {
                /*Toast.makeText(getApplicationContext(),
                        "invoke help!",
                        Toast.LENGTH_SHORT).show();*/

                Intent intent = new Intent(this, ActivityHelp.class);
                intent.putExtra(AppGobalDef.STR_HELP_TYPE, AppGobalDef.STR_HELP_START);

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.nav_setting :    {
                Toast.makeText(getApplicationContext(),
                        "invoke setting!",
                        Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.nav_share_app :    {
                Toast.makeText(getApplicationContext(),
                        "invoke share!",
                        Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.nav_contact_writer :    {
                /*Toast.makeText(getApplicationContext(),
                        "invoke contact!",
                        Toast.LENGTH_SHORT).show();*/
                contactWriter();
            }
            break;
        }

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            return false;
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_start_outerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void contactWriter()    {
        Resources res = getResources();

        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(
                Uri.parse(
                    String.format("mailto:%s", res.getString(R.string.contact_email))));
        //data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
        //data.putExtra(Intent.EXTRA_TEXT, "这是内容");
        startActivity(data);
    }
}
