package wxm.KeepAccount.ui.welcome.page


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv

import com.flyco.tablayout.SegmentTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import wxm.KeepAccount.ui.base.noScrollViewPager.NoScrollViewPager
import wxm.KeepAccount.ui.welcome.page.stat.DayStat
import wxm.KeepAccount.ui.welcome.page.stat.MonthStat
import wxm.KeepAccount.ui.welcome.page.stat.YearStat
import wxm.androidutil.log.TagLog
import wxm.androidutil.ui.frg.FrgSupportSwitcher
import java.util.ArrayList

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageStat : FrgSupportSwitcher<FrgSupportBaseAdv>(),  PageBase {
    private val mTLTab:SegmentTabLayout  by bindView(R.id.tl_stat)

    private val mPGDay = DayStat()
    private val mPGMonth = MonthStat()
    private val mPGYear = YearStat()

    init {
        setupFrgID(R.layout.pg_frg_stat, R.id.fl_page_holder)
    }
    override fun leavePage(): Boolean = true

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
