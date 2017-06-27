package wxm.KeepAccount.ui.data.show.note;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
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
import java.util.Arrays;

import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowBase;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowBudget;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowMonthly;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowYearly;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.utility.ContextUtil;


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
    //@BindView(R.id.tab_pager)
    ViewPager mVPPages;

    // for selecter ui
    RelativeLayout mRLDayFlow;
    RelativeLayout mRLMonthFlow;
    RelativeLayout mRLYearFlow;
    RelativeLayout mRLBudget;

    private int mCRWhite;
    private int mCRTextFit;
    private RelativeLayout mRLHot;

    // for notice
    private boolean[] mBADataChange;
    private RelativeLayout[]  mRASelector;

    /**
     * 数据库内数据变化处理器
     *
     * @param event 事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBDataChangeEvent(DBDataChangeEvent event) {
        TFShowBase tb = getHotTabItem();
        tb.loadView(true);

        int cur_pos = mVPPages.getCurrentItem();
        for (int i = 0; i < mBADataChange.length; i++) {
            if (cur_pos != i)
                mBADataChange[i] = true;
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
        //ButterKnife.bind(this, rootView);

        mVPPages = UtilFun.cast_t(rootView.findViewById(R.id.tab_pager));
        mRLDayFlow = UtilFun.cast_t(rootView.findViewById(R.id.rl_day_flow));
        mRLMonthFlow = UtilFun.cast_t(rootView.findViewById(R.id.rl_month_flow));
        mRLYearFlow = UtilFun.cast_t(rootView.findViewById(R.id.rl_year_flow));
        mRLBudget = UtilFun.cast_t(rootView.findViewById(R.id.rl_budget));
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Context ct = ContextUtil.getInstance();
        Resources res = ct.getResources();
        Resources.Theme te = ct.getTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCRWhite = res.getColor(R.color.white, te);
            mCRTextFit= res.getColor(R.color.text_fit, te);
        } else {
            mCRWhite = res.getColor(R.color.white);
            mCRTextFit= res.getColor(R.color.text_fit);
        }

        // init view
        // init adapter
        AppCompatActivity a_ac = UtilFun.cast_t(getActivity());
        final PagerAdapter adapter = new PagerAdapter(a_ac.getSupportFragmentManager());
        mVPPages.setAdapter(adapter);

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

        mBADataChange = new boolean[POS_BUDGET + 1];
        Arrays.fill(mBADataChange, false);

        mRASelector = new RelativeLayout[POS_BUDGET + 1];
        mRASelector[POS_DAY_FLOW] = mRLDayFlow;
        mRASelector[POS_MONTH_FLOW] = mRLMonthFlow;
        mRASelector[POS_YEAR_FLOW] = mRLYearFlow;
        mRASelector[POS_BUDGET] = mRLBudget;

        // 默认选择第一页为首页
        // 根据调用参数跳转到指定首页
        Intent it = getActivity().getIntent();
        if (null != it) {
            boolean b_hot = false;
            String ft = it.getStringExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB);
            if (!UtilFun.StringIsNullOrEmpty(ft)) {
                for (RelativeLayout aMRASelector : mRASelector) {
                    String sn = getSelectorName(aMRASelector);
                    if (sn.equals(ft)) {
                        enableRLStatus(aMRASelector);
                        b_hot = true;
                        break;
                    }
                }
            }

            if (!b_hot) {
                enableRLStatus(mRLDayFlow);
            }
        }
    }

    @Override
    protected void loadUI() {
        //getHotTabItem().loadView(true);
    }


    public void disableViewPageTouch(boolean bflag) {
        mVPPages.requestDisallowInterceptTouchEvent(bflag);
    }

    public void jumpByTabName(String tabname) {
        int pos = -1;
        int tc = mRASelector.length;
        for (int i = 0; i < tc; ++i) {
            if (tabname.equals(getSelectorName(mRASelector[i]))) {
                pos = i;
                break;
            }
        }

        if ((-1 != pos) && isEnableRL(mRASelector[pos])) {
            enableRLStatus(mRASelector[pos]);
        }
    }


    /// PRIVATE BEGIN
    @NonNull
    private String getSelectorName(RelativeLayout rl)   {
        return ((TextView)rl.findViewById(R.id.tv_tag)).getText().toString();
    }

    /**
     * 修改rl状态
     * @param rl        待修改rl
     */
    private void enableRLStatus(RelativeLayout rl)  {
        setRLStatus(rl, true);

        int pos = -1;
        for(int i = 0; i < mRASelector.length; i++) {
            RelativeLayout it = mRASelector[i];
            if(it != rl)    {
                setRLStatus(it, false);
            } else {
                pos = i;
            }
        }

        mRLHot = rl;
        mVPPages.setCurrentItem(pos);
        getHotTabItem().loadView(mBADataChange[pos]);
        mBADataChange[pos] = false;
    }

    /**
     * 判断rl是否enable
     * @param rl    待检查rl
     * @return      是否enable
     */
    private Boolean isEnableRL(RelativeLayout rl)  {
        return rl == mRLHot;
    }

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
                .setTextColor(bIsSelected ? mCRWhite : mCRTextFit);
    }

    /**
     * 得到当前选中的tab item
     *
     * @return 当前选中的tab item
     */
    public TFShowBase getHotTabItem() {
        int pos = -1;
        for(int i = 0; i < mRASelector.length; ++i) {
            if(isEnableRL(mRASelector[i]))  {
                pos = i;
                break;
            }
        }

        PagerAdapter pa = UtilFun.cast(mVPPages.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
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
