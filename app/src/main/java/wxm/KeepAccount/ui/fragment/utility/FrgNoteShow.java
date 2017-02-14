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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.DBDataChangeEvent;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBase;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBudget;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowMonthly;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowYearly;


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

    /**
     * 数据库内数据变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBDataChangeEvent(DBDataChangeEvent event) {
        TFShowBase tb = getHotTabItem();
        tb.loadView(true);

        int cur_pos = mVPPages.getCurrentItem();
        for(int i = 0; i < mBADataChange.length; i++)   {
            if(cur_pos != i)
                mBADataChange[i] = true;
        }
    }


    @Override
    protected void enterActivity()  {
        Log.d(LOG_TAG, "in enterActivity");
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity()  {
        Log.d(LOG_TAG, "in leaveActivity");
        EventBus.getDefault().unregister(this);

        super.leaveActivity();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgNoteShow";
        View rootView = layoutInflater.inflate(R.layout.vw_note_show, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        // init view
        if(0 == mTLTab.getTabCount()) {
            mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_DAILY));
            mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_MONTHLY));
            mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_YEARLY));
            mTLTab.addTab(mTLTab.newTab().setText(NoteShowDataHelper.TAB_TITLE_BUDGET));
            mTLTab.setTabGravity(TabLayout.GRAVITY_FILL);
        }
    }

    @Override
    protected void initUiInfo() {
        Log.d(LOG_TAG, "initUiInfo");
        new AsyncTask<Void, Void, Void>()   {
            @Override
            protected void onPreExecute() {
                showProgress(true);
            }

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
                        getHotTabItem().loadView(mBADataChange[pos]);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                mBADataChange = new boolean[mTLTab.getTabCount()];
                Arrays.fill(mBADataChange, false);

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

                    getHotTabItem().loadView(true);
                }

                showProgress(false);
            }
        }.execute();
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
