package wxm.KeepAccount.ui.data.show.note


import android.os.Bundle
import com.flyco.tablayout.SegmentTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.ui.data.show.note.ListView.LVBudget
import wxm.KeepAccount.ui.data.show.note.ListView.LVDaily
import wxm.KeepAccount.ui.data.show.note.ListView.LVMonthly
import wxm.KeepAccount.ui.data.show.note.ListView.LVYearly
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.let1
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher


/**
 * for note show
 * Created by WangXM on 2016/11/30.
 */
class FrgNoteShow : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    private val mTLTab: SegmentTabLayout  by bindView(R.id.tl_stat)

    private val mTabTitles = arrayOf(
            NoteDataHelper.TAB_TITLE_DAILY, NoteDataHelper.TAB_TITLE_MONTHLY,
            NoteDataHelper.TAB_TITLE_YEARLY, NoteDataHelper.TAB_TITLE_BUDGET)

    private val mTFDaily = LVDaily()
    private val mTFMonthly = LVMonthly()
    private val mTFYearly = LVYearly()
    private val mTFBudget = LVBudget()

    private val changeFlag = Array(POS_BUDGET + 1, { false })

    init {
        setupFrgID(R.layout.frg_note_show, R.id.fl_page_holder)
    }

    override fun isUseEventBus(): Boolean = true

    /**
     * DB data change handler
     * @param event     event param
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBChangeEvent(event: DBDataChangeEvent) {
        hotPage.reInitUI()

        changeFlag.fill(true)
        changeFlag[getPagePos(hotPage)] = false
    }

    override fun setupFragment(bundle: Bundle?) {
        addChildFrg(mTFDaily)
        addChildFrg(mTFMonthly)
        addChildFrg(mTFYearly)
        addChildFrg(mTFBudget)

        mTLTab.setTabData(mTabTitles)
        mTLTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                when (position) {
                    POS_DAY_FLOW -> showPage(mTFDaily)
                    POS_MONTH_FLOW -> showPage(mTFMonthly)
                    POS_YEAR_FLOW -> showPage(mTFYearly)
                    POS_BUDGET -> showPage(mTFBudget)
                }
            }

            override fun onTabReselect(position: Int) {}
        })

        // 默认选择第一页为首页
        // 根据调用参数跳转到指定首页
        activity!!.intent?.let1 {
            it.getStringExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB)?.let1 {
                jumpByTabName(it)
            }
        }
    }

    /**
     * jump to tab page use name
     * @param tabName       name for target page
     */
    fun jumpByTabName(tabName: String): Boolean {
        return mTabTitles.indexOf(tabName).let {
            if (-1 == it) {
                false
            } else {
                mTLTab.currentTab = it
                true
            }
        }
    }

    private fun showPage(pg: FrgSupportBaseAdv) {
        switchToPage(pg)

        val pos = getPagePos(pg)
        if (-1 != pos && changeFlag[pos]) {
            pg.reInitUI()
            changeFlag[pos] = false
        }
    }

    private fun getPagePos(pg: FrgSupportBaseAdv): Int {
        return when (pg) {
            is LVDaily -> POS_DAY_FLOW
            is LVMonthly -> POS_MONTH_FLOW
            is LVYearly -> POS_YEAR_FLOW
            is LVBudget -> POS_BUDGET
            else -> -1
        }
    }

    /// PRIVATE BEGIN
    companion object {
        private const val POS_DAY_FLOW = 0
        private const val POS_MONTH_FLOW = 1
        private const val POS_YEAR_FLOW = 2
        private const val POS_BUDGET = 3
    }
    ///PRIVATE END
}
