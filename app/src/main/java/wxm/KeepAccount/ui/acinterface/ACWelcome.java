package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import java.util.Calendar;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACRecord;

/**
 * 用户登陆后首页面
 */
public class ACWelcome extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ACWelcome";
    private static final int    BTDRAW_WIDTH    = 96;
    private static final int    BTDRAW_HEIGHT   = 96;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_welcome);

        init_component();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_welcome);
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


    private void init_component() {
        // set nav view
        Toolbar tb = UtilFun.cast(findViewById(R.id.ac_navw_toolbar));
        setSupportActionBar(tb);

        DrawerLayout drawer = UtilFun.cast(findViewById(R.id.ac_welcome));
        assert null != drawer;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nv = UtilFun.cast(findViewById(R.id.start_nav_view));
        assert null != nv;
        nv.setNavigationItemSelectedListener(this);


        // init datashow
        Button bt_datashow = UtilFun.cast(findViewById(R.id.bt_lookdata));
        assert null != bt_datashow;
        bt_datashow.setOnClickListener(this);

        Drawable dr = bt_datashow.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_datashow.setCompoundDrawables(null, dr, null, null);

        // init setting
        Button bt_setting = UtilFun.cast(findViewById(R.id.bt_setting));
        assert null != bt_setting;
        bt_setting.setOnClickListener(this);

        // init add income
        Button bt_addincome = UtilFun.cast(findViewById(R.id.bt_add_income));
        assert null != bt_addincome;
        bt_addincome.setOnClickListener(this);

        dr = bt_addincome.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_addincome.setCompoundDrawables(null, dr, null, null);

        // init add pay
        Button bt_addpay = UtilFun.cast(findViewById(R.id.bt_add_pay));
        assert null != bt_addpay;
        bt_addpay.setOnClickListener(this);

        dr = bt_addpay.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_addpay.setCompoundDrawables(null, dr, null, null);

        // init leave login
        Button bt_leave_login = UtilFun.cast(findViewById(R.id.bt_leave_login));
        assert null != bt_leave_login;
        bt_leave_login.setOnClickListener(this);

        dr = bt_leave_login.getCompoundDrawables()[1];
        dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
        bt_leave_login.setCompoundDrawables(null, dr, null, null);

        // init edit budget
        Button bt_edit_budget = UtilFun.cast(findViewById(R.id.bt_edit_budget));
        assert null != bt_edit_budget;
        bt_edit_budget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_lookdata : {
                Intent intent = new Intent(this, ACShowRecord.class);
                startActivityForResult(intent, 1);
            }
            break;

            case R.id.bt_edit_budget: {
                Intent intent = new Intent(this, ACShowBudget.class);
                startActivityForResult(intent, 1);
            }
            break;

            case R.id.bt_setting : {
                Toast.makeText(getApplicationContext(), "invoke setting!", Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.bt_add_income :
            case R.id.bt_add_pay : {
                Intent intent = new Intent(v.getContext(), ACRecord.class);
                intent.putExtra(ACRecord.PARA_ACTION, ACRecord.LOAD_NOTE_ADD);

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

            case R.id.bt_leave_login :  {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                Log.e(TAG, "view(id :" + id + ")的click未处理");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_welcome);
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
}
