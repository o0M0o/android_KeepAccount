package wxm.KeepAccount.ui.welcome

import android.os.Bundle
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.welcome.page.PageMain
import wxm.KeepAccount.ui.welcome.page.PageSetting
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

    init {
        setupFrgID(R.layout.vw_welcome, R.id.fl_page)
    }

    override fun setupFragment(savedInstanceState: Bundle?) {
        addChildFrg(mPageMain)
        addChildFrg(mPageSetting)
    }


    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)

        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.ib_channel, R.id.ib_stats, R.id.ib_usr, R.id.ib_setting),
                this::onActClick)

        onActClick(view!!.findViewById(R.id.ib_channel))
    }


    private fun onActClick(v: View) {
        val setHot = { vId: Int ->
            intArrayOf(R.id.ib_channel, R.id.ib_stats, R.id.ib_usr, R.id.ib_setting).forEach {
                (view!!.findViewById<IconButton>(it)).setColdOrHot(it == vId)
            }
        }

        val ibVW = (v as IconButton)
        if (!ibVW.isHot) {
            when (v.id) {
                R.id.ib_channel -> {
                    setHot(R.id.ib_channel)
                    /*
            val dlg = DlgSelectChannel()
            dlg.hotChannel = (mDGVActions.adapter as DGVButtonAdapter).curAction
            dlg.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                override fun onDialogPositiveResult(dialog: DialogFragment) {
                    val dsc = UtilFun.cast<DlgSelectChannel>(dialog)
                    PreferencesUtil.saveHotAction(dsc.hotChannel)

                    mLSData.clear()
                    for (i in PreferencesUtil.loadHotAction()) {
                        val ihm = HashMap<String, Any>()
                        ihm[DGVButtonAdapter.KEY_ACT_NAME] = i
                        mLSData.add(ihm)
                    }

                    (mDGVActions.adapter as DGVButtonAdapter).notifyDataSetChanged()
                }

                override fun onDialogNegativeResult(dialog: DialogFragment) {}
            })

            dlg.show(activity.supportFragmentManager, "选择频道")
            */
                    switchToPage(mPageMain)
                }

                R.id.ib_setting -> {
                    setHot(R.id.ib_setting)
                    switchToPage(mPageSetting)
                }

                R.id.ib_stats -> {
                    setHot(R.id.ib_stats)
                }

                R.id.ib_usr -> {
                    setHot(R.id.ib_usr)
                }
            }
        }
    }
}
