package wxm.KeepAccount.ui.welcome.page

import android.Manifest.permission.READ_SMS
import android.Manifest.permission.RECEIVE_SMS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.define.EAction
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.event.PreferenceChange
import wxm.KeepAccount.preference.PreferencesUtil
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.ui.data.edit.debt.ACDebtCreate
import wxm.KeepAccount.ui.data.show.calendar.ACCalendarShow
import wxm.KeepAccount.ui.data.show.note.ACNoteShow
import wxm.KeepAccount.ui.sync.ACSync
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.welcome.banner.BannerAp
import wxm.KeepAccount.ui.welcome.banner.BannerPara
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.KeepAccount.utility.DGVButtonAdapter
import wxm.androidutil.app.AppBase
import wxm.androidutil.improve.let1
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dragGrid.DragGridView
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.uilib.lbanners.LMBanners
import java.util.*
import kotlin.collections.ArrayList

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageMain : FrgSupportBaseAdv(), PageBase {
    // for ui
    private val mDGVActions: DragGridView by bindView(R.id.dgv_buttons)
    private val mLBanners: LMBanners<BannerPara> by bindView(R.id.banners)

    // for data
    private val mLSData = ArrayList<HashMap<String, Any>>()
    private val mALFrgPara = ArrayList<BannerPara>().apply {
        add(BannerPara(R.layout.banner_month))
        add(BannerPara(R.layout.banner_year))
    }

    override fun getLayoutID(): Int = R.layout.pg_main_page
    override fun isUseEventBus(): Boolean = true
    override fun leavePage(): Boolean = true

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBEvent(event: DBDataChangeEvent) {
        mLBanners.setAdapter(BannerAp(activity!!, null), mALFrgPara)
    }

    /**
     * for preference change
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPreferencesEvent(event: PreferenceChange) {
        reInitUI()
    }

    override fun initUI(savedInstanceState: Bundle?) {
        initBanner()
        loadUI(savedInstanceState)
    }

    override fun loadUI(savedInstanceState: Bundle?) {
        mLSData.clear()
        mLSData.addAll(PreferencesUtil.loadHotAction()
                .map { HashMap<String, Any>().apply { put(DGVButtonAdapter.KEY_ACT_NAME, it) } })

        mDGVActions.adapter = DGVButtonAdapter(activity!!, mLSData)
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
            view.findViewById<TextView>(R.id.tv_name)?.let {
                doClick(it.text.toString())
            }
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_ALL) {
            grantResults.indices.forEach {
                if (grantResults[it] != PackageManager.PERMISSION_GRANTED) {
                    String.format(Locale.CHINA, "由于缺少必须的权限(%s)，无法解析短信!",
                            permissions[it]).let {
                        DlgAlert.showAlert(context!!, R.string.dlg_warn, it) { b ->
                            b.setPositiveButton(R.string.cn_sure) { _, _ ->
                            }
                        }
                    }
                }
            }

            toSyncSms()
        }
    }


    /**
     * banner is show in head of welcome page
     */
    private fun initBanner() {
        mLBanners.setAdapter(BannerAp(activity!!, null), mALFrgPara)
    }

    private fun toSyncSms() {
        startActivityForResult(Intent(context, ACSync::class.java), 1)
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
                doLogout(activity!!)
            }

            EAction.ADD_DEBT -> {
                Intent(context, ACDebtCreate::class.java).apply {
                    startActivityForResult(this, 1)
                }
            }

            EAction.SYNC_SMS -> {
                ArrayList<String>().let {
                    if (!AppBase.checkPermission(READ_SMS)) {
                        it.add(READ_SMS)
                    }

                    if (!AppBase.checkPermission(RECEIVE_SMS)) {
                        it.add(RECEIVE_SMS)
                    }

                    it
                }.toTypedArray().let1 {
                    if (it.isEmpty()) {
                        toSyncSms()
                    } else {
                        ActivityCompat.requestPermissions(activity!!, it, REQUEST_ALL)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_ALL = 99
    }
}
