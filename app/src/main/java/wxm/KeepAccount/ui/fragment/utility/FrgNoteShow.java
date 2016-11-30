package wxm.KeepAccount.ui.fragment.utility;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.IDataChangeNotice;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBase;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBudget;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowMonthly;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowYearly;

import static wxm.KeepAccount.Base.data.AppModel.getBudgetUtility;
import static wxm.KeepAccount.Base.data.AppModel.getPayIncomeUtility;

/**
 * for note show
 * Created by ookoo on 2016/11/30.
 */
public class FrgNoteShow extends FrgUtilityBase {
    // for ui
    @BindView(R.id.tab_pager)
    ViewPager mVPPages;

    @BindView(R.id.tl_tabs)
    TabLayout mTLTab;

    // for notice
    private boolean[]   mBADataChange;
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
            new ATDataChange().execute();
        }
    };


    private class ATDataChange extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            NoteShowDataHelper.getInstance().refreshData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // After completing execution of given task, control will return here.
            // Hence if you want to populate UI elements with fetched data, do it here.
            TFShowBase tb = getHotTabItem();
            tb.onDataChange();

            int cur_pos = mVPPages.getCurrentItem();
            for(int i = 0; i < mBADataChange.length; i++)   {
                if(cur_pos != i)
                    mBADataChange[i] = true;
            }
        }
    }


    private class ATLoadUI extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            NoteShowDataHelper.getInstance().refreshData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // After completing execution of given task, control will return here.
            // Hence if you want to populate UI elements with fetched data, do it here.
            AppCompatActivity a_ac = UtilFun.cast_t(getActivity());
            final PagerAdapter adapter = new PagerAdapter(a_ac.getSupportFragmentManager(),
                    mTLTab.getTabCount());
            mVPPages.setAdapter(adapter);
            mVPPages.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLTab));
            mTLTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int pos = tab.getPosition();
                    mVPPages.setCurrentItem(pos);
                    if(mBADataChange[pos])  {
                        getHotTabItem().onDataChange();
                        mBADataChange[pos] = false;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            mBADataChange = new boolean[mTLTab.getTabCount()];
            for(int i = 0; i < mBADataChange.length; ++i)   {
                mBADataChange[i] = false;
            }


            // 根据调用参数跳转到首页
            Intent it = getActivity().getIntent();
            if(null != it)  {
                String ft = it.getStringExtra(NoteShowDataHelper.INTENT_PARA_FIRST_TAB);
                if(!UtilFun.StringIsNullOrEmpty(ft))    {
                    int tc = mTLTab.getTabCount();
                    for(int i = 0; i < tc; i++) {
                        TabLayout.Tab t = mTLTab.getTabAt(i);
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
    }


    @Override
    protected void enterActivity()  {
        getPayIncomeUtility().addDataChangeNotice(mIDCNotice);
        getBudgetUtility().addDataChangeNotice(mIDCNotice);
    }

    @Override
    protected void leaveActivity()  {
        getPayIncomeUtility().removeDataChangeNotice(mIDCNotice);
        getBudgetUtility().removeDataChangeNotice(mIDCNotice);
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgLogin";
        View rootView = layoutInflater.inflate(R.layout.vw_note_show, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        // init view
        mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_DAILY));
        mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_MONTHLY));
        mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_YEARLY));
        mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_BUDGET));
        mTLTab.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    @Override
    protected void initUiInfo() {
        new ATLoadUI().execute();
    }


    public void disableViewPageTouch(boolean bflag) {
        mVPPages.requestDisallowInterceptTouchEvent(bflag);
    }

    public void jumpByTabName(String tabname)  {
        int pos = -1;
        int tc = mTLTab.getTabCount();
        for(int i = 0; i < tc; ++i)     {
            TabLayout.Tab t = mTLTab.getTabAt(i);
            assert  null != t;

            CharSequence t_cs = t.getText();
            assert null != t_cs;

            if(tabname.equals(t_cs.toString())) {
                pos = i;
                break;
            }
        }

        if((-1 != pos) && (pos != mTLTab.getSelectedTabPosition()))   {
            mTLTab.setScrollPosition(pos, 0, true);
            mVPPages.setCurrentItem(pos);
        }
    }

    public void filterView(List<String> ls_tag) {
        TFShowBase hot = getHotTabItem();
        hot.filterView(ls_tag);
    }


    /// PRIVATE BEGIN
    /**
     * 得到当前选中的tab item
     * @return  当前选中的tab item
     */
    public TFShowBase getHotTabItem() {
        int pos = mTLTab.getSelectedTabPosition();
        PagerAdapter pa = UtilFun.cast(mVPPages.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
    }


    /**
     * 初始化tab页控件组
     */
    private void init_tabs() {


    }
    ///PRIVATE END

    /**
     * fragment adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        HashMap<String, Fragment> mHMFra;

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
            TabLayout.Tab t = mTLTab.getTabAt(position);
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
