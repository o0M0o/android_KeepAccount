package wxm.KeepAccount.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View

import com.allure.lbanners.LMBanners
import com.allure.lbanners.transformer.TransitionEffect

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

import kotterknife.bindView
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.DragGrid.DragGridView
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.dialog.DlgSelectChannel
import wxm.KeepAccount.ui.setting.ACSetting
import wxm.KeepAccount.ui.welcome.banner.FrgAdapter
import wxm.KeepAccount.ui.welcome.banner.FrgPara
import wxm.KeepAccount.utility.DGVButtonAdapter
import wxm.KeepAccount.utility.EventHelper
import wxm.KeepAccount.utility.PreferencesUtil

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class FrgWelcome : FrgSupportBaseAdv() {
    // for ui
    private val mDGVActions: DragGridView by bindView(R.id.dgv_buttons)
    private val mLBanners: LMBanners<FrgPara> by bindView(R.id.banners)

    // for data
    private val mLSData = ArrayList<HashMap<String, Any>>()
    private val mALFrgPara = ArrayList<FrgPara>()

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBEvent(event: DBDataChangeEvent) {
        mLBanners.setAdapter(FrgAdapter(activity, null), mALFrgPara)
    }

    override fun getLayoutID(): Int {
        return R.layout.vw_welcome
    }

    override fun isUseEventBus(): Boolean {
        return true
    }


    override fun initUI(savedInstanceState: Bundle?) {
        initFrgPara()
        initBanner()

        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.ib_channel, R.id.ib_setting), this::onActClick)
        loadUI(savedInstanceState)
    }

    override fun loadUI(savedInstanceState: Bundle?) {
        mLSData.clear()
        for (i in PreferencesUtil.loadHotAction()) {
            val ihm = HashMap<String, Any>()
            ihm[DGVButtonAdapter.HKEY_ACT_NAME] = i
            mLSData.add(ihm)
        }

        val apt = DGVButtonAdapter(activity, mLSData)
        mDGVActions.adapter = apt
        mDGVActions.setOnChangeListener { from, to ->
            val temp = mLSData[from]
            if (from < to) {
                for (i in from until to) {
                    Collections.swap(mLSData, i, i + 1)
                }
            } else if (from > to) {
                for (i in from downTo to + 1) {
                    Collections.swap(mLSData, i, i - 1)
                }
            }

            mLSData[to] = temp

            val hotName = ArrayList<String>()
            for (hi in mLSData) {
                val an = UtilFun.cast_t<String>(hi[DGVButtonAdapter.HKEY_ACT_NAME])
                hotName.add(an)
            }
            PreferencesUtil.saveHotAction(hotName)

            apt.notifyDataSetChanged()
        }
        apt.notifyDataSetChanged()
    }

    private fun initFrgPara() {
        var fp = FrgPara()
        fp.mFPViewId = R.layout.banner_month
        mALFrgPara.add(fp)

        fp = FrgPara()
        fp.mFPViewId = R.layout.banner_year
        mALFrgPara.add(fp)
    }

    private fun onActClick(v: View) {
        when (v.id) {
            R.id.ib_channel -> {
                val dlg = DlgSelectChannel()
                dlg.hotChannel = (mDGVActions.adapter as DGVButtonAdapter).curAction
                dlg.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                    override fun onDialogPositiveResult(dialog: DialogFragment) {
                        val dsc = UtilFun.cast<DlgSelectChannel>(dialog)
                        PreferencesUtil.saveHotAction(dsc.hotChannel)

                        mLSData.clear()
                        for (i in PreferencesUtil.loadHotAction()) {
                            val ihm = HashMap<String, Any>()
                            ihm[DGVButtonAdapter.HKEY_ACT_NAME] = i
                            mLSData.add(ihm)
                        }

                        (mDGVActions.adapter as DGVButtonAdapter).notifyDataSetChanged()
                    }

                    override fun onDialogNegativeResult(dialog: DialogFragment) {}
                })

                dlg.show(activity.supportFragmentManager, "选择频道")
            }

            R.id.ib_setting -> {
                val intent = Intent(activity, ACSetting::class.java)
                startActivity(intent)
            }
        }
    }

    /**
     * banner is show in head of welcome page
     */
    private fun initBanner() {
        mLBanners.setAdapter(FrgAdapter(activity, null), mALFrgPara)

        //参数设置
        mLBanners.setAutoPlay(false)//自动播放
        mLBanners.setVertical(false)//是否可以垂直
        mLBanners.setScrollDurtion(222)//两页切换时间
        mLBanners.setCanLoop(true)//循环播放
        mLBanners.setSelectIndicatorRes(R.drawable.page_indicator_select)//选中的原点
        mLBanners.setUnSelectUnIndicatorRes(R.drawable.page_indicator_unselect)//未选中的原点
        mLBanners.setIndicatorWidth(5)//默认为5dp
        mLBanners.setHoriZontalTransitionEffect(TransitionEffect.Default)//选中喜欢的样式
        //mLBanners.setHoriZontalCustomTransformer(new ParallaxTransformer(R.id.id_image));//自定义样式
        mLBanners.setDurtion(5000)//切换时间
        mLBanners.hideIndicatorLayout()//隐藏原点
        mLBanners.showIndicatorLayout()//显示原点
        mLBanners.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID)//设置原点显示位置
    }
}
