package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.DragGrid.DragGridView;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.utility.ActionHelper;
import wxm.KeepAccount.Base.utility.DGVButtonAdapter;
import wxm.KeepAccount.Base.utility.PreferencesUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.acutility.ACPreveiwAndEdit;
import wxm.KeepAccount.ui.acutility.ACRemindEdit;
import wxm.KeepAccount.ui.acutility.ACSetting;
import wxm.KeepAccount.ui.dialog.DlgSelectChannel;

/**
 * 用户登陆后首页面
 */
public class ACWelcome extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {
    private static final String TAG = "ACWelcome";
    //private static final int    BTDRAW_WIDTH    = 96;
    //private static final int    BTDRAW_HEIGHT   = 96;

    private static final String CN_SETTING = "设定";
    private static final String CN_CHANNEL = "关注";

    // for DragGridView
    private List<HashMap<String, Object>> mLSData = new ArrayList<>();
    private DragGridView        mDGVActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_welcome);

        init_component();
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

            case ActionHelper.ACT_ADD_BUDGET :  {
                Intent intent = new Intent(this, ACPreveiwAndEdit.class);
                intent.putExtra(AppGobalDef.INTENT_LOAD_RECORD_TYPE, AppGobalDef.STR_RECORD_BUDGET);
                startActivityForResult(intent, 1);
            }
            break;

            case ActionHelper.ACT_ADD_DATA: {
                Intent intent = new Intent(this, ACNoteEdit.class);
                intent.putExtra(ACNoteEdit.PARA_ACTION, AppGobalDef.STR_CREATE);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(AppGobalDef.STR_RECORD_DATE,
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
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

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

            case CN_SETTING :  {
                //Toast.makeText(this, CN_SETTING, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ACSetting.class);
                startActivityForResult(intent, 1);
            }
            break;

            case CN_CHANNEL :  {
                //Toast.makeText(this, CN_CHANNEL, Toast.LENGTH_SHORT).show();
                DGVButtonAdapter dapt = UtilFun.cast(mDGVActions.getAdapter());
                DlgSelectChannel dlg = new DlgSelectChannel();
                dlg.setHotChannel(dapt.getCurAction());
                dlg.setDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                    @Override
                    public void onDialogPositiveResult(DialogFragment dialog) {
                        DlgSelectChannel dsc = UtilFun.cast(dialog);
                        PreferencesUtil.saveHotAction(dsc.getHotChannel());

                        mLSData.clear();
                        for(String i : PreferencesUtil.loadHotAction())     {
                            HashMap<String, Object> ihm = new HashMap<>();
                            ihm.put(DGVButtonAdapter.HKEY_ACT_NAME, i);
                            mLSData.add(ihm);
                        }

                        DGVButtonAdapter dapt = UtilFun.cast(mDGVActions.getAdapter());
                        dapt.notifyDataSetChanged();
                    }

                    @Override
                    public void onDialogNegativeResult(DialogFragment dialog) {
                    }
                });

                dlg.show(getSupportFragmentManager(), "选择频道");
            }
            break;
        }
    }


    /**
     * 初始化activity
     */
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

        // init drag grid view
        // frist init data
        for(String i : PreferencesUtil.loadHotAction())  {
            HashMap<String, Object> ihm = new HashMap<>();
            ihm.put(DGVButtonAdapter.HKEY_ACT_NAME, i);
            mLSData.add(ihm);
        }

        // then init adapter & view
        mDGVActions = UtilFun.cast(findViewById(R.id.dgv_buttons));
        assert null != mDGVActions;
        final DGVButtonAdapter apt = new DGVButtonAdapter(this, mLSData,
                new String[] {}, new int[] { });

        mDGVActions.setAdapter(apt);
        mDGVActions.setOnChangeListener(new DragGridView.OnChanageListener() {
            @Override
            public void onChange(int from, int to) {
                HashMap<String, Object> temp = mLSData.get(from);
                if(from < to){
                    for(int i=from; i<to; i++){
                        Collections.swap(mLSData, i, i+1);
                    }
                }else if(from > to){
                    for(int i=from; i>to; i--){
                        Collections.swap(mLSData, i, i-1);
                    }
                }

                mLSData.set(to, temp);

                ArrayList<String> hot_name = new ArrayList<>();
                for(HashMap<String, Object> hi : mLSData)   {
                    String an = UtilFun.cast(hi.get(DGVButtonAdapter.HKEY_ACT_NAME));
                    hot_name.add(an);
                }
                PreferencesUtil.saveHotAction(hot_name);

                apt.notifyDataSetChanged();
            }
        });
        apt.notifyDataSetChanged();

        // init other button
        Button bt = UtilFun.cast(findViewById(R.id.bt_setting));
        assert null != bt;
        bt.setText(CN_SETTING);
        bt.setOnClickListener(this);

        bt = UtilFun.cast(findViewById(R.id.bt_channel));
        assert null != bt;
        bt.setText(CN_CHANNEL);
        bt.setOnClickListener(this);
    }

    /**
     * 激活手机邮件客户端，往设定的地址发送邮件
     */
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
