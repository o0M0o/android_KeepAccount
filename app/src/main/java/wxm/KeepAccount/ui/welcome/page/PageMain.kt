package wxm.KeepAccount.ui.welcome.page

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.Button
import com.allure.lbanners.LMBanners
import com.allure.lbanners.transformer.TransitionEffect
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.define.EAction
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.ui.data.show.calendar.ACCalendarShow
import wxm.KeepAccount.ui.data.show.note.ACNoteShow
import wxm.KeepAccount.ui.dialog.DlgSelectChannel
import wxm.KeepAccount.ui.setting.ACSetting
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.welcome.banner.FrgAdapter
import wxm.KeepAccount.ui.welcome.banner.FrgPara
import wxm.KeepAccount.utility.DGVButtonAdapter
import wxm.KeepAccount.utility.PreferencesUtil
import wxm.KeepAccount.utility.let1
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.dragGrid.DragGridView
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.util.UtilFun
import wxm.uilib.IconButton.IconButton
import java.util.*

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageMain : FrgSupportBaseAdv() {
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
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBEvent(event: DBDataChangeEvent) {
        mLBanners.setAdapter(FrgAdapter(activity, null), mALFrgPara)
    }

    override fun getLayoutID(): Int = R.layout.vw_main
    override fun isUseEventBus(): Boolean = true

    override fun initUI(savedInstanceState: Bundle?) {
        initFrgPara()
        initBanner()

        loadUI(savedInstanceState)
    }

    override fun loadUI(savedInstanceState: Bundle?) {
        mLSData.clear()
        mLSData.addAll(PreferencesUtil.loadHotAction()
                .map { HashMap<String, Any>().apply { put(DGVButtonAdapter.KEY_ACT_NAME, it) } })

        mDGVActions.adapter = DGVButtonAdapter(activity, mLSData)
        mDGVActions.setOnChangeListener { from, to ->
            // adjust position
            if (from < to) {
                for (i in from until to) {
                    Collections.swap(mLSData, i, i + 1)
                }
            } else if (from > to) {
                for (i in from downTo to + 1) {
                    Collections.swap(mLSData, i, i - 1)
                }
            }
            mLSData[to] = mLSData[from]

            // save position to preferences
            PreferencesUtil.saveHotAction(ArrayList<String>().apply {
                addAll(mLSData.map { it[DGVButtonAdapter.KEY_ACT_NAME] as String })
            })
        }

        mDGVActions.setOnItemClickListener { _, view, _, _ ->
            doClick((view as Button).text.toString())
        }
    }

    private fun initFrgPara() {
        mALFrgPara.add(FrgPara().apply { mFPViewId = R.layout.banner_month })
        mALFrgPara.add(FrgPara().apply { mFPViewId = R.layout.banner_year })
    }

    /**
     * banner is show in head of welcome page
     */
    private fun initBanner() {
        mLBanners.let {
            it.setAdapter(FrgAdapter(activity, null), mALFrgPara)

            //参数设置
            it.setAutoPlay(false)//自动播放
            it.setVertical(false)//是否可以垂直
            it.setScrollDurtion(222)//两页切换时间
            it.setCanLoop(true)//循环播放
            it.setSelectIndicatorRes(R.drawable.page_indicator_select)//选中的原点
            it.setUnSelectUnIndicatorRes(R.drawable.page_indicator_unselect)//未选中的原点
            it.setIndicatorWidth(5)//默认为5dp
            it.setHoriZontalTransitionEffect(TransitionEffect.Default)//选中喜欢的样式
            //it.setHoriZontalCustomTransformer(new ParallaxTransformer(R.id.id_image));//自定义样式
            it.setDurtion(5000)//切换时间
            it.hideIndicatorLayout()//隐藏原点
            it.showIndicatorLayout()//显示原点
            it.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID)//设置原点显示位置
        }
    }


    /**
     * click handler
     * @param act   name for action
     */
    private fun doClick(act: String) {
        val ea = EAction.getEAction(act)!!
        when (ea) {
            EAction.LOOK_BUDGET -> {
                Intent(context, ACNoteShow::class.java).apply {
                    putExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB,
                            NoteDataHelper.TAB_TITLE_BUDGET)
                    startActivityForResult(this, 1)
                }
            }

            EAction.LOOK_DATA -> {
                startActivityForResult(Intent(context, ACNoteShow::class.java), 1)
            }

            EAction.CALENDAR_VIEW -> {
                startActivityForResult(Intent(context, ACCalendarShow::class.java), 1)
            }

            EAction.ADD_BUDGET -> {
                Intent(context, ACNoteEdit::class.java).apply {
                    putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET)
                    startActivityForResult(this, 1)
                }
            }

            EAction.ADD_DATA -> {
                Intent(context, ACNoteCreate::class.java).apply {
                    putExtra(GlobalDef.STR_RECORD_DATE,
                            CalendarUtility.SDF_YEAR_MONTH_DAY_HOUR_MINUTE.format(System.currentTimeMillis()))

                    startActivityForResult(this, 1)
                }
            }

            EAction.LOGOUT -> {
                activity.let1 {
                    it.setResult(GlobalDef.INTRET_USR_LOGOUT, Intent())
                    it.finish()
                }
            }
        }
    }
}
