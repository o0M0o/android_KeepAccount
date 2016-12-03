package wxm.KeepAccount.ui.fragment.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.DBHelper.IDataChangeNotice;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.BudgetItem;
import wxm.KeepAccount.Base.data.IncomeNoteItem;
import wxm.KeepAccount.Base.data.PayNoteItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBase;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBudget;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowMonthly;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowYearly;

import static wxm.KeepAccount.Base.utility.ContextUtil.getBudgetUtility;
import static wxm.KeepAccount.Base.utility.ContextUtil.getPayIncomeUtility;

/**
 * for note show
 * Created by ookoo on 2016/11/30.
 */
public class FrgNoteShow extends FrgUtilityBase {
    // for ui
    @BindView(R.id.login_progress)
    ProgressBar mPBLoginProgress;

    @BindView(R.id.tab_pager)
    ViewPager mVPPages;

    @BindView(R.id.tl_tabs)
    TabLayout mTLTab;

    // for notice
    private boolean[]   mBADataChange;
    private IDataChangeNotice mIDCBudgetNotice = new IDataChangeNotice<BudgetItem>() {
        @Override
        public void DataModifyNotice(List<BudgetItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataCreateNotice(List<BudgetItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataDeleteNotice(List<BudgetItem> list) {
            reLoadFrg();
        }
    };

    private IDataChangeNotice mIDCPayNotice = new IDataChangeNotice<PayNoteItem>() {
        @Override
        public void DataModifyNotice(List<PayNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataCreateNotice(List<PayNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataDeleteNotice(List<PayNoteItem> list) {
            reLoadFrg();
        }
    };


    private IDataChangeNotice mIDCIncomeNotice = new IDataChangeNotice<IncomeNoteItem>() {
        @Override
        public void DataModifyNotice(List<IncomeNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataCreateNotice(List<IncomeNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataDeleteNotice(List<IncomeNoteItem> list) {
            reLoadFrg();
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

            showProgress(false);
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
                    getHotTabItem().loadView();

                    /*
                    if(mBADataChange[pos])  {
                        getHotTabItem().onDataChange();
                        mBADataChange[pos] = false;
                    }
                    */
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


            // 默认选择第一页为首页
            // 根据调用参数跳转到指定首页
            Intent it = getActivity().getIntent();
            if(null != it)  {
                boolean b_hot = false;
                String ft = it.getStringExtra(NoteShowDataHelper.INTENT_PARA_FIRST_TAB);
                if(!UtilFun.StringIsNullOrEmpty(ft))    {
                    int tc = mTLTab.getTabCount();
                    for(int i = 0; i < tc; i++) {
                        TabLayout.Tab t = mTLTab.getTabAt(i);
                        if(null != t) {
                            CharSequence cs = t.getText();
                            if (null != cs && cs.toString().equals(ft)){
                                t.select();
                                b_hot = true;
                                break;
                            }
                        }
                    }
                }

                if(!b_hot) {
                    TabLayout.Tab t = mTLTab.getTabAt(0);
                    if (null != t)
                        t.select();
                }

                getHotTabItem().loadView();
            }

            showProgress(false);
        }
    }


    /**
     * 数据变化后重新加载数据
     */
    private void reLoadFrg()    {
        showProgress(true);
        new ATDataChange().execute();
    }


    @Override
    protected void enterActivity()  {
        getPayIncomeUtility().getPayDBUtility().addDataChangeNotice(mIDCPayNotice);
        getPayIncomeUtility().getIncomeDBUtility().addDataChangeNotice(mIDCIncomeNotice);
        getBudgetUtility().addDataChangeNotice(mIDCBudgetNotice);
    }

    @Override
    protected void leaveActivity()  {
        getPayIncomeUtility().getPayDBUtility().removeDataChangeNotice(mIDCPayNotice);
        getPayIncomeUtility().getIncomeDBUtility().removeDataChangeNotice(mIDCIncomeNotice);
        getBudgetUtility().removeDataChangeNotice(mIDCBudgetNotice);
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
        showProgress(true);
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
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mPBLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoginProgress.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * 得到当前选中的tab item
     * @return  当前选中的tab item
     */
    public TFShowBase getHotTabItem() {
        int pos = mTLTab.getSelectedTabPosition();
        PagerAdapter pa = UtilFun.cast(mVPPages.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
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
