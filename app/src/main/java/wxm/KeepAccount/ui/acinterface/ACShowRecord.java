package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Locale;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import wxm.KeepAccount.ui.fragment.GraphViewSlidingTabsFragment;
import wxm.KeepAccount.ui.fragment.ListViewSlidingTabsFragment;

/**
 * tab版本的main activity
 * Created by 123 on 2016/5/16.
 */
public class ACShowRecord
        extends AppCompatActivity   {
    private static final int[] ITEM_DRAWABLES = {
            R.drawable.ic_leave
            ,R.drawable.ic_switch
            ,R.drawable.ic_add};

    private static final String TAG = "ACShowRecord";

    private SlidingTabsColorsFragment mTabFragment;
    private GraphViewSlidingTabsFragment gvTabFragment = new GraphViewSlidingTabsFragment();
    private ListViewSlidingTabsFragment lvTabFragment = new ListViewSlidingTabsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_showrecord);

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
            case R.id.am_bi_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Boolean bModify = false;
        if (AppGobalDef.INTRET_RECORD_ADD == resultCode) {
            Log.i(TAG, "从'添加记录'页面返回");
            bModify = true;
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

        // init ray menu
        RayMenu rayMenu = UtilFun.cast(findViewById(R.id.rm_show_record));
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
            });// Add a menu item
        }
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

            case R.drawable.ic_switch :     {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (mTabFragment instanceof ListViewSlidingTabsFragment) {
                    mTabFragment = gvTabFragment;
                } else {
                    mTabFragment = lvTabFragment;
                }

                transaction.replace(R.id.tabfl_content, mTabFragment);
                transaction.commit();
            }
            break;

            case R.drawable.ic_leave :  {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

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
}
