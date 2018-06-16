package wxm.KeepAccount.ui.welcome.page


import android.os.Bundle
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv

import com.flyco.tablayout.SegmentTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.event.PreferenceChange
import wxm.KeepAccount.ui.welcome.page.pieChartStat.DayPieChart
import wxm.KeepAccount.ui.welcome.page.stat.DayStat
import wxm.KeepAccount.ui.welcome.page.stat.MonthStat
import wxm.KeepAccount.ui.welcome.page.stat.YearStat
import wxm.androidutil.ui.frg.FrgSupportSwitcher

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageStat : FrgSupportSwitcher<FrgSupportBaseAdv>(),  PageBase {
    private val mTLTab:SegmentTabLayout  by bindView(R.id.tl_stat)

    private val mPGDay = DayPieChart()
    private val mPGMonth = DayPieChart()
    private val mPGYear = DayPieChart()

    override fun isUseEventBus(): Boolean = true

    init {
        setupFrgID(R.layout.pg_frg_stat, R.id.fl_page_holder)
    }
    override fun leavePage(): Boolean = true

    /**
     * for DB data change
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBEvent(event: DBDataChangeEvent) {
        mPGDay.reInitUI()
        mPGMonth.reInitUI()
        mPGYear.reInitUI()
    }

    /**
     * for preference change
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPreferencesEvent(event: PreferenceChange) {
        mPGDay.reInitUI()
        mPGMonth.reInitUI()
        mPGYear.reInitUI()
    }

    override fun setupFragment(savedInstanceState: Bundle?) {
        addChildFrg(mPGDay)
        addChildFrg(mPGMonth)
        addChildFrg(mPGYear)

        mTLTab.setTabData(resources.getStringArray(R.array.page_stat))
        mTLTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                when(position)    {
                    POS_DAY -> switchToPage(mPGDay)
                    POS_MONTH -> switchToPage(mPGMonth)
                    POS_YEAR -> switchToPage(mPGYear)
                }
            }

            override fun onTabReselect(position: Int) {}
        })
    }

    companion object {
        private const val POS_DAY = 0
        private const val POS_MONTH = 1
        private const val POS_YEAR = 2
    }
}
