package wxm.KeepAccount.ui.data.show.note;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import butterknife.BindView;
import wxm.KeepAccount.ui.base.Switcher.PageSwitcher;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowBudget;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowMonthly;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowYearly;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.utility.NoteDataHelper;


/**
 * for note show
 * Created by WangXM on 2016/11/30.
 */
public class FrgNoteShow extends FrgSupportSwitcher<FrgSupportBaseAdv> {
    protected final static int POS_DAY_FLOW = 0;
    protected final static int POS_MONTH_FLOW = 1;
    protected final static int POS_YEAR_FLOW = 2;
    protected final static int POS_BUDGET = 3;

    // for selector ui
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
        FrgSupportSwitcher mSBPage;
        int  mPageIdx;
    }
    private pageHelper[]    mPHHelper;
    private PageSwitcher    mPSSwitcher;

    private TFShowDaily     mTFDaily    = new TFShowDaily();
    private TFShowMonthly   mTFMonthly  = new TFShowMonthly();
    private TFShowYearly    mTFYearly   = new TFShowYearly();
    private TFShowBudget    mTFBudget   = new TFShowBudget();

    public FrgNoteShow()   {
        super();
        setupFrgID(R.layout.vw_note_show, R.id.fl_page_holder);
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    /**
     * DB data change handler
     * @param event     event param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBChangeEvent(DBDataChangeEvent event) {
        FrgSupportSwitcher tb = getHotTabItem();
        for (pageHelper aMPHHelper : mPHHelper) {
            if(tb != aMPHHelper.mSBPage) {
                aMPHHelper.mBADataChange = true;
            }
        }

        if(null != tb) {
            ((FrgSupportBaseAdv)tb.getHotPage()).reInitUI();
        }
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addChildFrg(mTFDaily);
        addChildFrg(mTFMonthly);
        addChildFrg(mTFYearly);
        addChildFrg(mTFBudget);

        initUIComponent();
    }

    /*
    @Override
    protected void initUI(Bundle bundle) {
        super.initUI(bundle);
    }

    @Override
    protected void loadUI(Bundle bundle) {
        super.loadUI(bundle);
    }
    */

    private void initUIComponent() {
        // init page switch
        mPSSwitcher = new PageSwitcher();
        mPHHelper = new pageHelper[POS_BUDGET + 1];

        mPHHelper[POS_DAY_FLOW] = new pageHelper();
        mPHHelper[POS_DAY_FLOW].mPageIdx = POS_DAY_FLOW;
        mPHHelper[POS_DAY_FLOW].mBADataChange = false;
        mPHHelper[POS_DAY_FLOW].mRLSelector = mRLDayFlow;
        mPHHelper[POS_DAY_FLOW].mSBPage = mTFDaily;
        mPHHelper[POS_DAY_FLOW].mSZName = ((TextView)mRLDayFlow.findViewById(R.id.tv_tag))
                                                .getText().toString();
        mPSSwitcher.addSelector(mPHHelper[POS_DAY_FLOW],
                () -> setPage(mPHHelper[POS_DAY_FLOW], true),
                () -> setPage(mPHHelper[POS_DAY_FLOW], false));

        mPHHelper[POS_MONTH_FLOW] = new pageHelper();
        mPHHelper[POS_MONTH_FLOW].mPageIdx = POS_MONTH_FLOW;
        mPHHelper[POS_MONTH_FLOW].mBADataChange = false;
        mPHHelper[POS_MONTH_FLOW].mRLSelector = mRLMonthFlow;
        mPHHelper[POS_MONTH_FLOW].mSBPage = mTFMonthly;
        mPHHelper[POS_MONTH_FLOW].mSZName = ((TextView)mRLMonthFlow.findViewById(R.id.tv_tag))
                                                .getText().toString();
        mPSSwitcher.addSelector(mPHHelper[POS_MONTH_FLOW],
                () -> setPage(mPHHelper[POS_MONTH_FLOW], true),
                () -> setPage(mPHHelper[POS_MONTH_FLOW], false));

        mPHHelper[POS_YEAR_FLOW] = new pageHelper();
        mPHHelper[POS_YEAR_FLOW].mPageIdx = POS_YEAR_FLOW;
        mPHHelper[POS_YEAR_FLOW].mBADataChange = false;
        mPHHelper[POS_YEAR_FLOW].mRLSelector = mRLYearFlow;
        mPHHelper[POS_YEAR_FLOW].mSBPage = mTFYearly;
        mPHHelper[POS_YEAR_FLOW].mSZName = ((TextView)mRLYearFlow.findViewById(R.id.tv_tag))
                                                .getText().toString();
        mPSSwitcher.addSelector(mPHHelper[POS_YEAR_FLOW],
                () -> setPage(mPHHelper[POS_YEAR_FLOW], true),
                () -> setPage(mPHHelper[POS_YEAR_FLOW], false));

        mPHHelper[POS_BUDGET] = new pageHelper();
        mPHHelper[POS_BUDGET].mPageIdx = POS_BUDGET;
        mPHHelper[POS_BUDGET].mBADataChange = false;
        mPHHelper[POS_BUDGET].mRLSelector = mRLBudget;
        mPHHelper[POS_BUDGET].mSBPage = mTFBudget;
        mPHHelper[POS_BUDGET].mSZName = ((TextView)mRLBudget.findViewById(R.id.tv_tag))
                                                .getText().toString();
        mPSSwitcher.addSelector(mPHHelper[POS_BUDGET],
                () -> setPage(mPHHelper[POS_BUDGET], true),
                () -> setPage(mPHHelper[POS_BUDGET], false));

        mRLDayFlow.setOnClickListener(v -> mPSSwitcher.doSelect(mPHHelper[POS_DAY_FLOW]));
        mRLMonthFlow.setOnClickListener(v -> mPSSwitcher.doSelect(mPHHelper[POS_MONTH_FLOW]));
        mRLYearFlow.setOnClickListener(v -> mPSSwitcher.doSelect(mPHHelper[POS_YEAR_FLOW]));
        mRLBudget.setOnClickListener(v -> mPSSwitcher.doSelect(mPHHelper[POS_BUDGET]));

        // 默认选择第一页为首页
        // 根据调用参数跳转到指定首页
        boolean b_hot = false;
        Intent it = getActivity().getIntent();
        if (null != it) {
            String ft = it.getStringExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB);
            if (!UtilFun.StringIsNullOrEmpty(ft)) {
                for(pageHelper ph : mPHHelper)  {
                    if (ph.mSZName.equals(ft)) {
                        mPSSwitcher.doSelect(ph);
                        b_hot = true;
                        break;
                    }
                }
            }
        }

        if (!b_hot) {
            mPSSwitcher.doSelect(mPHHelper[POS_DAY_FLOW]);
        }
    }

    public void disableViewPageTouch(boolean bflag) {
        //getHotTabItem().requestDisallowInterceptTouchEvent(bflag);
    }

    /**
     * jump to tab page use name
     * @param tabname       name for target page
     */
    public void jumpByTabName(String tabname) {
        for(pageHelper ph : mPHHelper)  {
            if(tabname.equals(ph.mSZName))  {
                if(!isEnableRL(ph.mRLSelector)) {
                    mPSSwitcher.doSelect(ph);
                }

                break;
            }
        }
    }

    /// PRIVATE BEGIN
    /**
     * switch to other page
     * @param ph            page helper data
     * @param enable        if true, switch to this page
     */
    private void setPage(pageHelper ph, boolean enable) {
        setRLStatus(ph.mRLSelector, enable);
        if(enable)  {
            switchToPage(ph.mSBPage);

            if(ph.mBADataChange) {
                ((FrgSupportBaseAdv)ph.mSBPage.getHotPage()).reInitUI();
                ph.mBADataChange = false;
            }
        }
    }

    /**
     * check whether rl is enabled
     * @param rl    rl need check
     * @return      True if enabled
     */
    private Boolean isEnableRL(RelativeLayout rl) {
        pageHelper ph = (pageHelper) mPSSwitcher.getSelected();
        return null != ph && ph.mRLSelector == rl;
    }

    /**
     * set rl status
     * @param rl            rl need set
     * @param bIsSelected   true is doSelect
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
     * get hot tab item
     * @return      hot tab item
     */
    public FrgSupportSwitcher getHotTabItem() {
        pageHelper ph = (pageHelper) mPSSwitcher.getSelected();
        return null == ph ? null : ph.mSBPage;
    }
    ///PRIVATE END
}
