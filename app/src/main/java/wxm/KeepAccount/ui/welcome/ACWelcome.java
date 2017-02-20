package wxm.KeepAccount.ui.welcome;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ActionHelper;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.calendar.ACCalendarShow;
import wxm.KeepAccount.ui.data.edit.ACNoteEdit;
import wxm.KeepAccount.ui.data.edit.ACPreveiwAndEdit;
import wxm.KeepAccount.ui.data.edit.ACRemindEdit;
import wxm.KeepAccount.ui.setting.ACSetting;
import wxm.KeepAccount.ui.dialog.DlgUsrMessage;
import wxm.KeepAccount.ui.help.ACHelp;

/**
 * 用户登陆后首页面
 */
public class ACWelcome extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {
    private static final String TAG = "ACWelcome";
    private FrgWelcome   mFGWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_welcome);

        init_component(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        mFGWelcome.refreshUI();
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
    public void onClick(View v) {
        if(v instanceof RelativeLayout) {
            TextView tv = UtilFun.cast(v.findViewById(R.id.tv_name));
            if(null != tv)  {
                do_click(tv.getText().toString());
            }
        } else if(v instanceof Button)  {
            Button bt = UtilFun.cast(v);
            do_click(bt.getText().toString());
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_help: {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_START);

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.nav_setting: {
                Intent intent = new Intent(this, ACSetting.class);
                startActivityForResult(intent, 1);
            }
            break;

            case R.id.nav_share_app: {
                Toast.makeText(getApplicationContext(),
                        "invoke share!",
                        Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.nav_contact_writer: {
                contactWriter();
            }
            break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_welcome);
        assert null != drawer;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 执行onclick
     * @param act  onclick的动作
     */
    private void do_click(String act)   {
        switch (act)     {
            case ActionHelper.ACT_LOOK_BUDGET :     {
                Intent intent = new Intent(this, ACNoteShow.class);
                intent.putExtra(NoteShowDataHelper.INTENT_PARA_FIRST_TAB,
                                NoteShowDataHelper.TAB_TITLE_BUDGET);
                startActivityForResult(intent, 1);
            }
            break;

            case ActionHelper.ACT_LOOK_DATA :   {
                Intent intent = new Intent(this, ACNoteShow.class);
                startActivityForResult(intent, 1);
            }
            break;

            case ActionHelper.ACT_CALENDAR_VIEW :   {
                Intent intent = new Intent(this, ACCalendarShow.class);
                startActivityForResult(intent, 1);
            }
            break;

            case ActionHelper.ACT_ADD_BUDGET :  {
                Intent intent = new Intent(this, ACPreveiwAndEdit.class);
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET);
                startActivityForResult(intent, 1);
            }
            break;

            case ActionHelper.ACT_ADD_DATA: {
                Intent intent = new Intent(this, ACNoteEdit.class);
                intent.putExtra(ACNoteEdit.PARA_ACTION, GlobalDef.STR_CREATE);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(GlobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA ,"%d-%02d-%02d %02d:%02d"
                                ,cal.get(Calendar.YEAR)
                                ,cal.get(Calendar.MONTH) + 1
                                ,cal.get(Calendar.DAY_OF_MONTH)
                                ,cal.get(Calendar.HOUR_OF_DAY)
                                ,cal.get(Calendar.MINUTE)));

                startActivityForResult(intent, 1);
            }
            break;

            case ActionHelper.ACT_LOGOUT :      {
                int ret_data = GlobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            case ActionHelper.ACT_ADD_REMIND :  {
                Intent intent = new Intent(this, ACRemindEdit.class);
                startActivityForResult(intent, 1);
            }
            break;
        }
    }

    /**
     * 初始化activity
     * @param savedInstanceState  onclick的动作
     */
    private void init_component(Bundle savedInstanceState) {
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

        // load fragment
        if(null == savedInstanceState)  {
            mFGWelcome = new FrgWelcome();
            FragmentTransaction ft =  getFragmentManager().beginTransaction();
            ft.add(R.id.fl_holder, mFGWelcome);
            ft.commit();
        }
    }

    /**
     * 激活手机邮件客户端，往设定的地址发送邮件
     */
    private void contactWriter() {
        /*
        Resources res = getResources();

        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(
                Uri.parse(
                        String.format("mailto:%s", res.getString(R.string.contact_email))));
        //data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
        //data.putExtra(Intent.EXTRA_TEXT, "这是内容");
        startActivity(data);
        */

        DlgUsrMessage dlg = new DlgUsrMessage();
        dlg.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
            @Override
            public void onDialogPositiveResult(DialogFragment dialogFragment) {
            }

            @Override
            public void onDialogNegativeResult(DialogFragment dialogFragment) {
            }
        });

        dlg.show(getSupportFragmentManager(), "send message");
    }
}
