package wxm.KeepAccount.ui.data.show.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.ui.data.show.calendar.base.CalendarShowItemAdapter
import wxm.KeepAccount.ui.data.show.calendar.base.SelectedDayEvent
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.uilib.FrgCalendar.Base.ICalendarListener
import wxm.uilib.FrgCalendar.FrgCalendar
import java.text.SimpleDateFormat
import java.util.*


/**
 * in upper part show calendar, if usr click day, in bottom part show day data
 * Created by WangXM on 2016/12/4.
 */
class FrgCalendarShow : FrgSupportBaseAdv() {
    // for ui
    private val mHGVDays: FrgCalendar by bindView(R.id.frg_calender_lv)

    // for data
    private lateinit var mCSIAdapter: CalendarShowItemAdapter
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
        ToolUtil.runInBackground(this.activity,
                {
                    NoteDataHelper.notesYears.let {
                        if (it.isNotEmpty()) {
                            param[0] = it[0]
                        }
                    }
                },
                {
                    if (UtilFun.StringIsNullOrEmpty(param[0])) {
                        val ac = activity
                        val builder = android.app.AlertDialog.Builder(ac)
                        builder.setMessage("当前用户没有数据，请先添加数据!").setTitle("警告")
                        builder.setNegativeButton("确认") { _, _ -> ac.finish() }

                        val dlg = builder.create()
                        dlg.show()
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

        mCSIAdapter = CalendarShowItemAdapter(context)
        mHGVDays.setCalendarItemAdapter(CalendarShowItemAdapter(context), CalendarShowItemAdapter(context))

        mHGVDays.setDateChangeListener(object : ICalendarListener {
            override fun onDayChanged(day: String) {
                if(day != mSZCurrentDay) {
                    mSZCurrentDay = day
                    EventBus.getDefault().post(SelectedDayEvent(day))

                    var noteCount = 0
                    NoteDataHelper.getInfoByDay(day)?.let {
                        noteCount = it.incomeCount + it.payCount
                    }

                    if (noteCount > 4) {
                        if (!mHGVDays.isShrinkMode)
                            mHGVDays.isShrinkMode = true
                    } else {
                        if (mHGVDays.isShrinkMode)
                            mHGVDays.isShrinkMode = false
                    }
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
