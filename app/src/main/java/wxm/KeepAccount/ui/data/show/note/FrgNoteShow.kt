package wxm.KeepAccount.ui.data.show.note


import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.base.Switcher.PageSwitcher
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowBudget
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowDaily
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowMonthly
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowYearly
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher
import wxm.androidutil.util.UtilFun


/**
 * for note show
 * Created by WangXM on 2016/11/30.
 */
class FrgNoteShow : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    // for selector ui
    private val mRLDayFlow: RelativeLayout by bindView(R.id.rl_day_flow)
    private val mRLMonthFlow: RelativeLayout by bindView(R.id.rl_month_flow)
    private val mRLYearFlow: RelativeLayout by bindView(R.id.rl_year_flow)
    private val mRLBudget: RelativeLayout by bindView(R.id.rl_budget)

    private var mPHHelper: Array<PageHelper>
            = arrayOf(PageHelper(), PageHelper(), PageHelper(), PageHelper())
    private var mPSSwitcher: PageSwitcher = PageSwitcher()

    private val mTFDaily = TFShowDaily()
    private val mTFMonthly = TFShowMonthly()
    private val mTFYearly = TFShowYearly()
    private val mTFBudget = TFShowBudget()

    /**
     * get hot tab item
     * @return      hot tab item
     */
    val hotTabItem: FrgSupportSwitcher<*>?
        get() {
            val ph = mPSSwitcher.selected as PageHelper?
            return ph?.mSBPage
        }

    // for helper data
    private inner class PageHelper {
        internal var mBADataChange: Boolean = false
        internal var mRLSelector: RelativeLayout? = null
        internal var mSZName: String? = null
        internal var mSBPage: FrgSupportSwitcher<*>? = null
        internal var mPageIdx: Int = 0
    }

    init {
        setupFrgID(R.layout.vw_note_show, R.id.fl_page_holder)
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * DB data change handler
     * @param event     event param
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBChangeEvent(event: DBDataChangeEvent) {
        val tb = hotTabItem
        mPHHelper.filter { tb !== it.mSBPage }.forEach { it.mBADataChange = true }
        if (null != tb) {
            (tb.hotPage as FrgSupportBaseAdv).reInitUI()
        }
    }

    override fun setupFragment(bundle: Bundle?) {
        addChildFrg(mTFDaily)
        addChildFrg(mTFMonthly)
        addChildFrg(mTFYearly)
        addChildFrg(mTFBudget)

        initUIComponent()
    }

    private fun initUIComponent() {
        // init page switch
        mPHHelper[POS_DAY_FLOW].mPageIdx = POS_DAY_FLOW
        mPHHelper[POS_DAY_FLOW].mBADataChange = false
        mPHHelper[POS_DAY_FLOW].mRLSelector = mRLDayFlow
        mPHHelper[POS_DAY_FLOW].mSBPage = mTFDaily
        mPHHelper[POS_DAY_FLOW].mSZName = (mRLDayFlow.findViewById<View>(R.id.tv_tag) as TextView)
                .text.toString()
        mPSSwitcher.addSelector(mPHHelper[POS_DAY_FLOW],
                { setPage(mPHHelper[POS_DAY_FLOW], true) },
                { setPage(mPHHelper[POS_DAY_FLOW], false) })

        mPHHelper[POS_MONTH_FLOW].mPageIdx = POS_MONTH_FLOW
        mPHHelper[POS_MONTH_FLOW].mBADataChange = false
        mPHHelper[POS_MONTH_FLOW].mRLSelector = mRLMonthFlow
        mPHHelper[POS_MONTH_FLOW].mSBPage = mTFMonthly
        mPHHelper[POS_MONTH_FLOW].mSZName = (mRLMonthFlow.findViewById<View>(R.id.tv_tag) as TextView)
                .text.toString()
        mPSSwitcher.addSelector(mPHHelper[POS_MONTH_FLOW],
                { setPage(mPHHelper[POS_MONTH_FLOW], true) }
        ) { setPage(mPHHelper[POS_MONTH_FLOW], false) }

        mPHHelper[POS_YEAR_FLOW].mPageIdx = POS_YEAR_FLOW
        mPHHelper[POS_YEAR_FLOW].mBADataChange = false
        mPHHelper[POS_YEAR_FLOW].mRLSelector = mRLYearFlow
        mPHHelper[POS_YEAR_FLOW].mSBPage = mTFYearly
        mPHHelper[POS_YEAR_FLOW].mSZName = (mRLYearFlow.findViewById<View>(R.id.tv_tag) as TextView)
                .text.toString()
        mPSSwitcher.addSelector(mPHHelper[POS_YEAR_FLOW],
                { setPage(mPHHelper[POS_YEAR_FLOW], true) }
        ) { setPage(mPHHelper[POS_YEAR_FLOW], false) }

        mPHHelper[POS_BUDGET].mPageIdx = POS_BUDGET
        mPHHelper[POS_BUDGET].mBADataChange = false
        mPHHelper[POS_BUDGET].mRLSelector = mRLBudget
        mPHHelper[POS_BUDGET].mSBPage = mTFBudget
        mPHHelper[POS_BUDGET].mSZName = (mRLBudget.findViewById<View>(R.id.tv_tag) as TextView)
                .text.toString()
        mPSSwitcher.addSelector(mPHHelper[POS_BUDGET],
                { setPage(mPHHelper[POS_BUDGET], true) }
        ) { setPage(mPHHelper[POS_BUDGET], false) }

        mRLDayFlow.setOnClickListener { _ -> mPSSwitcher.doSelect(mPHHelper[POS_DAY_FLOW]) }
        mRLMonthFlow.setOnClickListener { _ -> mPSSwitcher.doSelect(mPHHelper[POS_MONTH_FLOW]) }
        mRLYearFlow.setOnClickListener { _ -> mPSSwitcher.doSelect(mPHHelper[POS_YEAR_FLOW]) }
        mRLBudget.setOnClickListener { _ -> mPSSwitcher.doSelect(mPHHelper[POS_BUDGET]) }

        // 默认选择第一页为首页
        // 根据调用参数跳转到指定首页
        val itActivity = activity.intent
        if (null != itActivity) {
            val ft = itActivity.getStringExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB)
            if (!UtilFun.StringIsNullOrEmpty(ft)) {
                jumpByTabName(ft)
            }
        }

        if (null == mPSSwitcher.selected) {
            mPSSwitcher.doSelect(mPHHelper[POS_DAY_FLOW])
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun disableViewPageTouch(bFlag: Boolean) {
        //getHotTabItem().requestDisallowInterceptTouchEvent(bFlag);
    }

    /**
     * jump to tab page use name
     * @param tabName       name for target page
     */
    fun jumpByTabName(tabName: String): Boolean {
        mPHHelper.find { it.mSZName == tabName }?.let {
            if (!isEnableRL(it.mRLSelector)) {
                mPSSwitcher.doSelect(it)
                return true
            }
        }

        return false
    }

    /// PRIVATE BEGIN
    /**
     * switch to other page
     * @param ph            page helper data
     * @param enable        if true, switch to this page
     */
    private fun setPage(ph: PageHelper, enable: Boolean) {
        setRLStatus(ph.mRLSelector, enable)
        if (enable) {
            switchToPage(ph.mSBPage)

            if (ph.mBADataChange) {
                (ph.mSBPage!!.hotPage as FrgSupportBaseAdv).reInitUI()
                ph.mBADataChange = false
            }
        }
    }

    /**
     * check whether rl is enabled
     * @param rl    rl need check
     * @return      True if enabled
     */
    private fun isEnableRL(rl: RelativeLayout?): Boolean {
        val ph = mPSSwitcher.selected as PageHelper?
        return null != ph && ph.mRLSelector === rl
    }

    /**
     * set rl status
     * @param rl            rl need set
     * @param bIsSelected   true is doSelect
     */
    private fun setRLStatus(rl: RelativeLayout?, bIsSelected: Boolean) {
        rl?.let {
            val res: Int = when (it) {
                mRLDayFlow -> if (bIsSelected) R.drawable.rl_item_left else R.drawable.rl_item_left_nosel
                mRLBudget -> if (bIsSelected) R.drawable.rl_item_right else R.drawable.rl_item_right_nosel
                else -> if (bIsSelected) R.drawable.rl_item_middle else R.drawable.rl_item_middle_nosel
            }

            it.setBackgroundResource(res)
            (it.findViewById<View>(R.id.tv_tag) as TextView)
                    .setTextColor(if (bIsSelected) ResourceHelper.mCRTextWhite else ResourceHelper.mCRTextFit)
        }
    }

    companion object {
        private const val POS_DAY_FLOW = 0
        private const val POS_MONTH_FLOW = 1
        private const val POS_YEAR_FLOW = 2
        private const val POS_BUDGET = 3
    }
    ///PRIVATE END
}
