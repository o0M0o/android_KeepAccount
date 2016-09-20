package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.DragGrid.DragGridView;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.utility.DGVButtonAdapter;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACBudgetEdit;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;

/**
 * 用户登陆后首页面
 */
public class ACWelcome extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ACWelcome";
    //private static final int    BTDRAW_WIDTH    = 96;
    //private static final int    BTDRAW_HEIGHT   = 96;

    private static final String CN_SETTING = "设定";
    private static final String CN_CHANNEL = "关注";

    private List<HashMap<String, Object>> mLSData = new ArrayList<>();

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
        for(String i : DGVButtonAdapter.ACTION_NAMES)  {
            HashMap<String, Object> ihm = new HashMap<>();
            ihm.put(DGVButtonAdapter.HKEY_ACT_NAME, i);
            mLSData.add(ihm);
        }

        // then init adapter & view
        final DragGridView dgv = UtilFun.cast(findViewById(R.id.dgv_buttons));
        assert null != dgv;
        final DGVButtonAdapter apt = new DGVButtonAdapter(this, mLSData,
                                new String[] {}, new int[] { });

        dgv.setAdapter(apt);
        dgv.setOnChangeListener(new DragGridView.OnChanageListener() {
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
                apt.notifyDataSetChanged();
            }
        });

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(v instanceof Button)     {
            Button vb = UtilFun.cast(v);
            String hv = vb.getText().toString();
            Log.i(TAG, "onClick, view_id = " + id + ", button name = " + hv);
            switch (hv)     {
                case DGVButtonAdapter.ACT_LOOK_BUDGET :
                case DGVButtonAdapter.ACT_LOOK_DATA :   {
                    Intent intent = new Intent(this, ACNoteShow.class);
                    startActivityForResult(intent, 1);
                }
                break;

                case DGVButtonAdapter.ACT_ADD_BUDGET :  {
                    Intent intent = new Intent(this, ACBudgetEdit.class);
                    startActivityForResult(intent, 1);
                }
                break;

                case DGVButtonAdapter.ACT_ADD_DATA: {
                    Intent intent = new Intent(v.getContext(), ACNoteEdit.class);
                    intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_ADD);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    intent.putExtra(AppGobalDef.STR_RECORD_DATE,
                            String.format(Locale.CHINA ,"%d-%02d-%02d"
                                    ,cal.get(Calendar.YEAR)
                                    ,cal.get(Calendar.MONTH) + 1
                                    ,cal.get(Calendar.DAY_OF_MONTH)));

                    startActivityForResult(intent, 1);
                }
                break;

                case DGVButtonAdapter.ACT_LOGOUT :      {
                    int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
                break;

                case CN_SETTING :  {
                    Toast.makeText(this, CN_SETTING, Toast.LENGTH_SHORT).show();
                }
                break;

                case CN_CHANNEL :  {
                    Toast.makeText(this, CN_CHANNEL, Toast.LENGTH_SHORT).show();
                }
                break;

            }
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
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_START);

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
