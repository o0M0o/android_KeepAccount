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
import wxm.KeepAccount.ui.welcome.page.stat.DayStat
import wxm.KeepAccount.ui.welcome.page.stat.MonthStat
import wxm.KeepAccount.ui.welcome.page.stat.YearStat
import java.util.ArrayList

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageStat : FrgSupportBaseAdv(), PageBase {
    private val mTLTab:SegmentTabLayout  by bindView(R.id.tl_stat)
    private val mVPPage:ViewPager  by bindView(R.id.vp_stat)

    private lateinit var mASTitle:Array<String>
    private val mFragments = ArrayList<Fragment>()

    override fun getLayoutID(): Int = R.layout.pg_stat
    override fun leavePage(): Boolean = true

    override fun initUI(savedInstanceState: Bundle?) {
        mASTitle = resources.getStringArray(R.array.page_stat)

        mFragments.apply {
            clear()
            add(DayStat())
            add(MonthStat())
            add(YearStat())
        }

        mTLTab.setTabData(mASTitle)
        mVPPage.adapter = PageAdapter(activity!!.supportFragmentManager, mASTitle, mFragments)
        mTLTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mVPPage.currentItem = position
            }

            override fun onTabReselect(position: Int) {}
        })

        mVPPage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mTLTab.currentTab = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }


    class PageAdapter(fm: FragmentManager,
                      private val mTitle:Array<String>,
                      private val mFragment:ArrayList<Fragment>): FragmentPagerAdapter(fm) {
        override fun getCount(): Int = mFragment.size
        override fun getPageTitle(position: Int): CharSequence = mTitle[position]
        override fun getItem(position: Int): Fragment = mFragment[position]
    }
}
