package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.data.IDataChangeNotice;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBase;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBudget;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowMonthly;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowYearly;

public class ACNoteShow extends AppCompatActivity {
    private final static String TAG = "ACNoteShow";

    private TabLayout   mTLTabs;
    private ViewPager   mVPTabs;

    private IDataChangeNotice mIDCNotice = new IDataChangeNotice() {
        @Override
        public void DataModifyNotice() {
            reLoadFrg();
        }

        @Override
        public void DataCreateNotice() {
            reLoadFrg();
        }

        @Override
        public void DataDeleteNotice() {
            reLoadFrg();
        }

        private void reLoadFrg()    {
            NoteShowDataHelper.getInstance().refreshData();
            TFShowBase tb = getHotTabItem();
            tb.onDataChange();
            /*
            PagerAdapter pa = UtilFun.cast(mVPTabs.getAdapter());
            for(int pos = 0; pos < pa.getCount(); pos++)    {
                TFShowBase tb =  UtilFun.cast_t(pa.getItem(pos));
                tb.onDataChange();
            }
            */
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_show);

        init_tabs();
        AppModel.getPayIncomeUtility().addDataChangeNotice(mIDCNotice);

        Intent it = getIntent();
        if(null != it)  {
            String ft = it.getStringExtra(NoteShowDataHelper.INTENT_PARA_FIRST_TAB);
            if(!UtilFun.StringIsNullOrEmpty(ft))    {
                int tc = mTLTabs.getTabCount();
                for(int i = 0; i < tc; i++) {
                    TabLayout.Tab t = mTLTabs.getTabAt(i);
                    if(null != t) {
                        CharSequence cs = t.getText();
                        if (null != cs && cs.toString().equals(ft)){
                            t.select();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy()  {
        AppModel.getPayIncomeUtility().removeDataChangeNotice(mIDCNotice);
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
                TFShowBase hot = getHotTabItem();
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
     * 得到当前选中的tab item
     * @return  当前选中的tab item
     */
    private TFShowBase getHotTabItem() {
        int pos = mTLTabs.getSelectedTabPosition();
        PagerAdapter pa = UtilFun.cast(mVPTabs.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
    }

    /**
     * 关闭/打开触摸功能
     * @param bflag  若为true则打开触摸功能，否则关闭触摸功能
     */
    public void disableViewPageTouch(boolean bflag) {
        mVPTabs.requestDisallowInterceptTouchEvent(bflag);
    }


    /**
     * 初始化tab页控件组
     */
    private void init_tabs() {
        // init data
        NoteShowDataHelper.getInstance().refreshData();

        // init view
        mTLTabs = (TabLayout) findViewById(R.id.tl_tabs);
        assert null != mTLTabs;
        mTLTabs.addTab(mTLTabs.newTab().setText(NoteShowDataHelper.TAB_TITLE_DAILY));
        mTLTabs.addTab(mTLTabs.newTab().setText(NoteShowDataHelper.TAB_TITLE_MONTHLY));
        mTLTabs.addTab(mTLTabs.newTab().setText(NoteShowDataHelper.TAB_TITLE_YEARLY));
        mTLTabs.addTab(mTLTabs.newTab().setText(NoteShowDataHelper.TAB_TITLE_BUDGET));
        mTLTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        mVPTabs = (ViewPager) findViewById(R.id.tab_pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mTLTabs.getTabCount());
        mVPTabs.setAdapter(adapter);
        mVPTabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLTabs));
        mTLTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    /**
     * 切换视图类型（列表视图/图表)
     */
    public void switchShow()    {
        TFShowBase hot = getHotTabItem();
        if(null != hot)
            hot.switchPage();
    }

    /**
     * 跳至对应名称的标签页
     * @param tabname 需跳转标签页的名字
     */
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
            mVPTabs.setCurrentItem(pos);
        }
    }

    /**
     * 过滤视图数据
     * @param ls_tag 过滤数据项
     */
    public void filterView(List<String> ls_tag) {
        TFShowBase hot = getHotTabItem();
        hot.filterView(ls_tag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        if(AppGobalDef.INTRET_RECORD_ADD == resultCode
                || AppGobalDef.INTRET_RECORD_MODIFY == resultCode
                || AppGobalDef.INTRET_SURE == resultCode)  {
            NoteShowDataHelper.getInstance().refreshData();
        }

        getHotTabItem().onDataChange(requestCode, resultCode, data);
        */
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
            mHMFra.put(NoteShowDataHelper.TAB_TITLE_DAILY, new TFShowDaily());
            mHMFra.put(NoteShowDataHelper.TAB_TITLE_MONTHLY, new TFShowMonthly());
            mHMFra.put(NoteShowDataHelper.TAB_TITLE_YEARLY, new TFShowYearly());
            mHMFra.put(NoteShowDataHelper.TAB_TITLE_BUDGET, new TFShowBudget());
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
