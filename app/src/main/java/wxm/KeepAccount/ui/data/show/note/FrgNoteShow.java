package wxm.KeepAccount.ui.data.show.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.androidutil.FrgUtility.FrgUtilityBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowBase;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowBudget;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowMonthly;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowYearly;
import wxm.KeepAccount.ui.base.ResourceHelper;
import wxm.KeepAccount.ui.utility.NoteDataHelper;


/**
 * for note show
 * Created by ookoo on 2016/11/30.
 */
public class FrgNoteShow extends FrgUtilityBase {
    protected final static int POS_DAY_FLOW = 0;
    protected final static int POS_MONTH_FLOW = 1;
    protected final static int POS_YEAR_FLOW = 2;
    protected final static int POS_BUDGET = 3;

    // for ui
    @BindView(R.id.vp_pages)
    ViewPager mVPPages;

    // for selecter ui
    @BindView(R.id.rl_day_flow)
    RelativeLayout mRLDayFlow;

    @BindView(R.id.rl_month_flow)
    RelativeLayout mRLMonthFlow;

    @BindView(R.id.rl_year_flow)
    RelativeLayout mRLYearFlow;

    @BindView(R.id.rl_budget)
    RelativeLayout mRLBudget;



    // for helper data
    private class pageHelper    {
        boolean mBADataChange;
        RelativeLayout mRLSelector;
        String   mSZName;
        TFShowBase   mSBPage;
        int  mPageIdx;
    }
    private pageHelper[] mPHHelper;
    private pageHelper mPHHot;

    /**
     * 数据库内数据变化处理器
     * @param event 事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBDataChangeEvent(DBDataChangeEvent event) {
        getHotTabItem().loadView(true);

        int cur_pos = mVPPages.getCurrentItem();
        for (int i = 0; i < mPHHelper.length; i++) {
            if (cur_pos != i)
                mPHHelper[i].mBADataChange = true;
        }
    }

    @Override
    protected void enterActivity() {
        Log.d(LOG_TAG, "in enterActivity");
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity() {
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
        // init adapter
        AppCompatActivity a_ac = UtilFun.cast_t(getActivity());
        final PagerAdapter adapter = new PagerAdapter(a_ac.getSupportFragmentManager());
        mVPPages.setAdapter(adapter);
        mVPPages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                enableRLStatus(mPHHelper[position].mRLSelector);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mRLDayFlow.setOnClickListener(v -> {
            if(!isEnableRL(mRLDayFlow)) {
                enableRLStatus(mRLDayFlow);
            }
        });

        mRLMonthFlow.setOnClickListener(v -> {
            if(!isEnableRL(mRLMonthFlow)) {
                enableRLStatus(mRLMonthFlow);
            }
        });

        mRLYearFlow.setOnClickListener(v -> {
            if(!isEnableRL(mRLYearFlow)) {
                enableRLStatus(mRLYearFlow);
            }
        });

        mRLBudget.setOnClickListener(v -> {
            if(!isEnableRL(mRLBudget)) {
                enableRLStatus(mRLBudget);
            }
        });

        mPHHelper = new pageHelper[POS_BUDGET + 1];

        mPHHelper[POS_DAY_FLOW] = new pageHelper();
        mPHHelper[POS_DAY_FLOW].mPageIdx = POS_DAY_FLOW;
        mPHHelper[POS_DAY_FLOW].mBADataChange = false;
        mPHHelper[POS_DAY_FLOW].mRLSelector = mRLDayFlow;
        mPHHelper[POS_DAY_FLOW].mSBPage = UtilFun.cast(adapter.getItem(POS_DAY_FLOW));

        mPHHelper[POS_MONTH_FLOW] = new pageHelper();
        mPHHelper[POS_MONTH_FLOW].mPageIdx = POS_MONTH_FLOW;
        mPHHelper[POS_MONTH_FLOW].mBADataChange = false;
        mPHHelper[POS_MONTH_FLOW].mRLSelector = mRLMonthFlow;
        mPHHelper[POS_MONTH_FLOW].mSBPage = UtilFun.cast(adapter.getItem(POS_MONTH_FLOW));

        mPHHelper[POS_YEAR_FLOW] = new pageHelper();
        mPHHelper[POS_YEAR_FLOW].mPageIdx = POS_YEAR_FLOW;
        mPHHelper[POS_YEAR_FLOW].mBADataChange = false;
        mPHHelper[POS_YEAR_FLOW].mRLSelector = mRLYearFlow;
        mPHHelper[POS_YEAR_FLOW].mSBPage = UtilFun.cast(adapter.getItem(POS_YEAR_FLOW));

        mPHHelper[POS_BUDGET] = new pageHelper();
        mPHHelper[POS_BUDGET].mPageIdx = POS_BUDGET;
        mPHHelper[POS_BUDGET].mBADataChange = false;
        mPHHelper[POS_BUDGET].mRLSelector = mRLBudget;
        mPHHelper[POS_BUDGET].mSBPage = UtilFun.cast(adapter.getItem(POS_BUDGET));

        for(pageHelper ph : mPHHelper)  {
            ph.mSZName = getSelectorName(ph.mRLSelector);
        }

        // 默认选择第一页为首页
        // 根据调用参数跳转到指定首页
        Intent it = getActivity().getIntent();
        if (null != it) {
            boolean b_hot = false;
            String ft = it.getStringExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB);
            if (!UtilFun.StringIsNullOrEmpty(ft)) {
                for(pageHelper ph : mPHHelper)  {
                    if (ph.mSZName.equals(ft)) {
                        enableRLStatus(ph.mRLSelector);
                        b_hot = true;
                        break;
                    }
                }
            }

            if (!b_hot) {
                enableRLStatus(mRLDayFlow);
            }
        } else {
            enableRLStatus(mRLDayFlow);
        }
    }

    @Override
    protected void loadUI() {
    }


    public void disableViewPageTouch(boolean bflag) {
        mVPPages.requestDisallowInterceptTouchEvent(bflag);
    }

    public void jumpByTabName(String tabname) {
        for(pageHelper ph : mPHHelper)  {
            if(tabname.equals(ph.mSZName))  {
                if(!isEnableRL(ph.mRLSelector)) {
                    enableRLStatus(ph.mRLSelector);
                }

                break;
            }
        }
    }


    /// PRIVATE BEGIN

    /**
     * 得到selector name
     * @param rl    for selector
     * @return      name for selector
     */
    @NonNull
    private String getSelectorName(RelativeLayout rl)   {
        return ((TextView)rl.findViewById(R.id.tv_tag)).getText().toString();
    }

    /**
     * 修改rl状态
     * @param rl        待修改rl
     */
    private void enableRLStatus(RelativeLayout rl)  {
        for(pageHelper ph : mPHHelper)  {
            if(ph.mRLSelector != rl)    {
                setRLStatus(ph.mRLSelector, false);
            } else {
                mPHHot = ph;

                setRLStatus(ph.mRLSelector, true);
                mVPPages.setCurrentItem(ph.mPageIdx);

                getHotTabItem().loadView(ph.mBADataChange);
                ph.mBADataChange = false;
            }
        }
    }

    /**
     * 判断rl是否enable
     * @param rl    待检查rl
     * @return      是否enable
     */
    private Boolean isEnableRL(RelativeLayout rl)  {
        return rl == mPHHot.mRLSelector;
    }

    /**
     * 设置rl的状态
     * @param rl            待设置relativelayout
     * @param bIsSelected   新状态
     */
    private void setRLStatus(RelativeLayout rl, boolean bIsSelected)    {
        int res;
        if(rl == mRLDayFlow)    {
            res = bIsSelected ? R.drawable.rl_item_left : R.drawable.rl_item_left_nosel;
        }  else if(rl == mRLBudget)    {
            res = bIsSelected ? R.drawable.rl_item_right : R.drawable.rl_item_right_nosel;
        }  else {
            res = bIsSelected ? R.drawable.rl_item_middle : R.drawable.rl_item_middle_nosel;
        }

        rl.setBackgroundResource(res);
        ((TextView)rl.findViewById(R.id.tv_tag))
                .setTextColor(bIsSelected ? ResourceHelper.mCRTextWhite : ResourceHelper.mCRTextFit);
    }

    /**
     * 得到当前选中的tab item
     * @return 当前选中的tab item
     */
    public TFShowBase getHotTabItem() {
        return mPHHot.mSBPage;
    }
    ///PRIVATE END

    /**
     * fragment adapter
     */
    private class PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> mALFrg;

        PagerAdapter(FragmentManager fm) {
            super(fm);

            mALFrg = new ArrayList<>();
            mALFrg.add(new TFShowDaily());
            mALFrg.add(new TFShowMonthly());
            mALFrg.add(new TFShowYearly());
            mALFrg.add(new TFShowBudget());
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mALFrg.get(position);
        }

        @Override
        public int getCount() {
            return mALFrg.size();
        }
    }
}
