package wxm.KeepAccount.ui.welcome

import android.os.Bundle
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.KeepAccount.ui.welcome.page.PageMain
import wxm.KeepAccount.ui.welcome.page.PageSetting
import wxm.KeepAccount.ui.welcome.page.PageStat
import wxm.KeepAccount.ui.welcome.page.PageUsr
import wxm.androidutil.improve.let1
import wxm.androidutil.log.TagLog
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher
import wxm.androidutil.ui.view.EventHelper
import wxm.uilib.IconButton.IconButton

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class FrgWelcome : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    // for page
    private val mPageMain = PageMain()
    private val mPageSetting = PageSetting()
    private val mPageStat = PageStat()
    private val mPageUsr = PageUsr()

    init {
        setupFrgID(R.layout.frg_welcome, R.id.fl_page)
    }

    override fun setupFragment(savedInstanceState: Bundle?) {
        addChildFrg(mPageMain)
        addChildFrg(mPageSetting)
        addChildFrg(mPageStat)
        addChildFrg(mPageUsr)
    }


    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)

        val op = { v: View ->
            (v as IconButton).let1 {
                if (!it.isHot) {
                    activity!!.title = it.actName
                    setHotPage(v.id)
                }
            }
        }

        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.ib_main_page, R.id.ib_stats, R.id.ib_usr, R.id.ib_setting),
                op)

        op(view!!.findViewById(R.id.ib_main_page))
    }

    fun leaveFrg(): Boolean {
        if ((hotPage as PageBase).leavePage()) {
            if (hotPage is PageMain) {
                return true
            } else {
                setHotPage(R.id.ib_main_page)
            }

        }

        return false
    }

    private fun setHotPage(vId: Int) {
        val setHot = { id: Int ->
            intArrayOf(R.id.ib_main_page, R.id.ib_stats, R.id.ib_usr, R.id.ib_setting).forEach {
                (view!!.findViewById<IconButton>(it)).setColdOrHot(it == id)
            }
        }

        when (vId) {
            R.id.ib_main_page -> {
                setHot(R.id.ib_main_page)
                switchToPage(mPageMain)
            }

            R.id.ib_setting -> {
                setHot(R.id.ib_setting)
                switchToPage(mPageSetting)
            }

            R.id.ib_stats -> {
                setHot(R.id.ib_stats)
                switchToPage(mPageStat)
            }

            R.id.ib_usr -> {
                setHot(R.id.ib_usr)
                switchToPage(mPageUsr)
            }

            else -> {
                TagLog.e("$vId is not support!!")
            }
        }

    }
}
