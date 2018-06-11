package wxm.KeepAccount.ui.data.show.calendar

import android.os.Bundle
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.ui.data.show.calendar.base.CalendarMonthAdapter
import wxm.KeepAccount.ui.data.show.calendar.base.CalendarWeekAdapter
import wxm.KeepAccount.ui.data.show.calendar.base.SelectedDayEvent
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.uilib.FrgCalendar.Base.ICalendarListener
import wxm.uilib.FrgCalendar.FrgCalendar
import java.text.SimpleDateFormat
import java.util.*


/**
 * in upper part show calendar, if usr click day, in bottom part show day data
 * Created by WangXM on 2016/12/4.
 */
class FrgCalendarHolder : FrgSupportBaseAdv() {
    // for ui
    private val mHGVDays: FrgCalendar by bindView(R.id.frg_calender_lv)

    // for data
    private lateinit var mCSIAdapter: CalendarMonthAdapter
    private val mFGContent = FrgCalendarContent()

    private var mSZCurrentDay: String? = null

    override fun getLayoutID(): Int {
        return R.layout.vw_calendar
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun loadUI(bundle: Bundle?) {
        val param = arrayOfNulls<String>(1)
        ToolUtil.runInBackground(this.activity!!,
                {
                    NoteDataHelper.notesYears.let {
                        if (it.isNotEmpty()) {
                            param[0] = it[0]
                        }
                    }
                },
                {
                    if (UtilFun.StringIsNullOrEmpty(param[0])) {
                        activity.let1 { ac ->
                            DlgAlert.showAlert(ac!!, R.string.dlg_warn, R.string.dlg_usr_no_data,
                                    { b ->
                                        b.setNegativeButton(R.string.act_accept)
                                        { _, _ -> ac.finish() }
                                    })
                        }

                        return@runInBackground
                    }

                    mSZCurrentDay?.let {
                        mHGVDays.setCalendarSelectedDay(Integer.parseInt(it.substring(0, 4)),
                                Integer.parseInt(it.substring(5, 7)) - 1,
                                Integer.parseInt(it.substring(8, 10)))
                        return@runInBackground
                    }

                    Calendar.getInstance().let {
                        mHGVDays.setCalendarSelectedDay(it.get(Calendar.YEAR),
                                it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH))
                    }
                })
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            val ft = childFragmentManager.beginTransaction()
            ft.add(R.id.fl_holder, mFGContent)
            ft.commit()
        }

        mCSIAdapter = CalendarMonthAdapter(context!!)
        mHGVDays.setCalendarItemAdapter(CalendarMonthAdapter(context!!), CalendarWeekAdapter(context!!))

        mHGVDays.setDateChangeListener(object : ICalendarListener {
            override fun onDayChanged(day: String) {
                if(day != mSZCurrentDay) {
                    mSZCurrentDay = day
                    EventBus.getDefault().post(SelectedDayEvent(day))
                }
            }

            override fun onMonthChanged(s: String) {
            }
        })

        loadUI(bundle)
    }

    /**
     * handler for DB event
     * @param event     param
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBChangeEvent(event: DBDataChangeEvent) {
        loadUI(null)
    }

    companion object {
        private val YEAR_MONTH_DAY_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    }
}
