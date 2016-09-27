package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.test.AndroidTestRunner;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.ShowData.STListViewFragment;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBase;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowYearly;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowMonthly;

public class ACNoteShowNew extends AppCompatActivity {
    private final static String TAG = "ACNoteShowNew";

    protected final static String TAB_DAILY     = "日统计";
    protected final static String TAB_MONTHLY   = "月统计";
    protected final static String TAB_YEARLY    = "年统计";

    private TabLayout   mTLTabs;
    private ViewPager   mVPTabs;

    // for notes data
    private boolean     mBDayNoteModify = true;
    private boolean     mBMonthNoteModify = true;
    private boolean     mBYearNoteModify = true;
    private HashMap<String, ArrayList<Object>> mHMNoteDataByDay;
    private HashMap<String, ArrayList<Object>> mHMNoteDataByMonth;
    private HashMap<String, ArrayList<Object>> mHMNoteDataByYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_show_new);

        init_tabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acbar_back_help, menu);

        // 开启"switch view"选项
        menu.getItem(0).setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acb_mi_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.acb_mi_leave :    {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            case R.id.acb_mi_switch :   {
                int pos = mTLTabs.getSelectedTabPosition();
                PagerAdapter pa = UtilFun.cast(mVPTabs.getAdapter());
                TFShowBase hot = UtilFun.cast(pa.getItem(pos));
                if(null != hot)  {
                    hot.switchPage();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }


    /**
     * 初始化tab页控件组
     */
    private void init_tabs() {
        mTLTabs = (TabLayout) findViewById(R.id.tl_tabs);
        assert null != mTLTabs;
        mTLTabs.addTab(mTLTabs.newTab().setText(TAB_DAILY));
        mTLTabs.addTab(mTLTabs.newTab().setText(TAB_MONTHLY));
        mTLTabs.addTab(mTLTabs.newTab().setText(TAB_YEARLY));
        mTLTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        mVPTabs = (ViewPager) findViewById(R.id.tab_pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), mTLTabs.getTabCount());
        mVPTabs.setAdapter(adapter);
        mVPTabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLTabs));
        mTLTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVPTabs.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public HashMap<String, ArrayList<Object>> getNotesByDay()   {
        if(mBDayNoteModify) {
            mHMNoteDataByDay = AppModel.getPayIncomeUtility().GetAllNotesToDay();
            mBDayNoteModify = false;
        }

        return mHMNoteDataByDay;
    }

    public HashMap<String, ArrayList<Object>> getNotesByMonth()   {
        if(mBMonthNoteModify) {
            mHMNoteDataByMonth = AppModel.getPayIncomeUtility().GetAllNotesToMonth();
            mBMonthNoteModify = false;
        }

        return mHMNoteDataByMonth;
    }

    public HashMap<String, ArrayList<Object>> getNotesByYear()   {
        if(mBYearNoteModify) {
            mHMNoteDataByYear = AppModel.getPayIncomeUtility().GetAllNotesToYear();
            mBYearNoteModify = false;
        }

        return mHMNoteDataByYear;
    }

    public void setNotesDirty() {
        mBDayNoteModify = true;
        mBMonthNoteModify = true;
        mBYearNoteModify = true;
    }

    public boolean getDayNotesDirty()   {
        return  mBDayNoteModify;
    }

    public boolean getMonthNotesDirty()   {
        return  mBMonthNoteModify;
    }

    public boolean getYearNotesDirty()   {
        return  mBYearNoteModify;
    }


    public void switchShow()    {
        int pos = mTLTabs.getSelectedTabPosition();
        PagerAdapter pa = UtilFun.cast(mVPTabs.getAdapter());
        TFShowBase hot = UtilFun.cast(pa.getItem(pos));
        if(null != hot)  {
            hot.switchPage();
        }
    }

    public void jumpByTabName(String tabname)  {
        int pos = -1;
        int tc = mTLTabs.getTabCount();
        for(int i = 0; i < tc; ++i)     {
            TabLayout.Tab t = mTLTabs.getTabAt(i);
            assert  null != t;

            CharSequence t_cs = t.getText();
            assert null != t_cs;

            if(tabname.equals(t_cs.toString())) {
                pos = i;
                break;
            }
        }

        if((-1 != pos) && (pos != mTLTabs.getSelectedTabPosition()))   {
            mTLTabs.setScrollPosition(pos, 0, true);
        }
    }

    public void filterView(List<String> ls_tag) {
        int pos = mTLTabs.getSelectedTabPosition();
        PagerAdapter pa = UtilFun.cast(mVPTabs.getAdapter());
        TFShowBase hot = UtilFun.cast(pa.getItem(pos));
        hot.filterView(ls_tag);
    }


    /**
     * fragment adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        HashMap<String, android.support.v4.app.Fragment>   mHMFra;

        PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;

            mHMFra = new HashMap<>();
            mHMFra.put(TAB_DAILY, new TFShowDaily());
            mHMFra.put(TAB_MONTHLY, new TFShowMonthly());
            mHMFra.put(TAB_YEARLY, new TFShowYearly());
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            TabLayout.Tab t = mTLTabs.getTabAt(position);
            assert null != t;
            CharSequence t_cs = t.getText();
            assert null != t_cs;
            return mHMFra.get(t_cs.toString());
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
