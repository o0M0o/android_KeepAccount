package wxm.KeepAccount.ui.data.show.note


import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.flyco.tablayout.SegmentTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
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
import wxm.KeepAccount.ui.welcome.page.PageStat
import wxm.KeepAccount.utility.let1
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher


/**
 * for note show
 * Created by WangXM on 2016/11/30.
 */
class FrgNoteShow : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    private val mTLTab: SegmentTabLayout  by bindView(R.id.tl_stat)

    private val mTFDaily = TFShowDaily()
    private val mTFMonthly = TFShowMonthly()
    private val mTFYearly = TFShowYearly()
    private val mTFBudget = TFShowBudget()

    private val changeFlag = Array(POS_BUDGET + 1, {false})

    /**
     * get hot tab item
     * @return      hot tab item
     */
    val hotTabItem: FrgSupportSwitcher<*>?
        get() {
            return hotPage as FrgSupportSwitcher<*>
        }

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
        changeFlag.fill(true)
        hotTabItem!!.reInitUI()
    }

    override fun setupFragment(bundle: Bundle?) {
        addChildFrg(mTFDaily)
        addChildFrg(mTFMonthly)
        addChildFrg(mTFYearly)
        addChildFrg(mTFBudget)

        mTLTab.setTabData(resources.getStringArray(R.array.page_flow))
        mTLTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                when(position)    {
                    POS_DAY_FLOW -> showPage(mTFDaily, position)
                    POS_MONTH_FLOW -> showPage(mTFMonthly, position)
                    POS_YEAR_FLOW -> showPage(mTFYearly, position)
                    POS_BUDGET -> showPage(mTFBudget, position)
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

    @Suppress("UNUSED_PARAMETER")
    fun disableViewPageTouch(bFlag: Boolean) {
        //getHotTabItem().requestDisallowInterceptTouchEvent(bFlag);
    }

    /**
     * jump to tab page use name
     * @param tabName       name for target page
     */
    fun jumpByTabName(tabName: String): Boolean {
        return resources.getStringArray(R.array.page_flow).indexOf(tabName).let {
            if(-1 == it) {false }
            else    {
                mTLTab.currentTab = it
                true
            }
        }
    }

    private fun showPage(pg:FrgSupportSwitcher<*>, pos:Int)    {
        switchToPage(pg)
        if(changeFlag[pos]) {
            pg.reInitUI()
            changeFlag[pos] = false
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
