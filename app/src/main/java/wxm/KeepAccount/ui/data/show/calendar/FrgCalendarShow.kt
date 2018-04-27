package wxm.KeepAccount.ui.data.show.calendar

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import kotterknife.bindView
import wxm.KeepAccount.ui.data.show.calendar.base.CalendarShowItemAdapter
import wxm.KeepAccount.ui.data.show.calendar.base.SelectedDayEvent
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.ui.utility.NoteDataHelper

import wxm.uilib.FrgCalendar.FrgCalendar


/**
 * in upper part show calendar, if usr click day, in bottom part show day data
 * Created by WangXM on 2016/12/4.
 */
class FrgCalendarShow : FrgSupportBaseAdv() {
    // for ui
    private val mHGVDays: FrgCalendar by bindView(R.id.frg_calender_lv)
    private val mFLHolder: FrameLayout by bindView(R.id.fl_holder)

    // for data
    private var mCSIAdapter: CalendarShowItemAdapter? = null
    private val mFGContent = FrgCalendarContent()

    private var mSZCurrentMonth: String? = null
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
                Runnable {
                    val hm = NoteDataHelper.instance.notesForMonth
                    if (null != hm) {
                        param[0] = UtilFun.cast_t<String>(hm.keys.toTypedArray()[0])
                    }
                },
                Runnable {
                    val fistMonth = param[0]
                    if (UtilFun.StringIsNullOrEmpty(fistMonth)) {
                        val ac = activity
                        val builder = android.app.AlertDialog.Builder(ac)
                        builder.setMessage("当前用户没有数据，请先添加数据!").setTitle("警告")
                        builder.setNegativeButton("确认") { _, _ -> ac.finish() }

                        val dlg = builder.create()
                        dlg.show()
                        return@Runnable
                    }

                    mSZCurrentMonth?.let {
                        updateCalendar(it)
                        return@Runnable
                    }

                    val curMonth = YEAR_MONTH.format(Calendar.getInstance().time)
                    updateCalendar(curMonth)
                })
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            val ft = childFragmentManager.beginTransaction()
            ft.add(R.id.fl_holder, mFGContent)
            ft.commit()
        }

        mCSIAdapter = CalendarShowItemAdapter(activity)
        mHGVDays.setCalendarItemAdapter(mCSIAdapter)

        mHGVDays.setDateChangeListener(object : FrgCalendar.DateChangeListener {
            override fun onDayChanged(view: View, s: String, i: Int) {
                mSZCurrentDay = s
                EventBus.getDefault().post(SelectedDayEvent(mSZCurrentDay!!))
            }

            override fun onMonthChanged(s: String) {
                updateCalendar(s)
            }
        })

        loadUI(bundle)
    }

    /**
     * handler for DB event
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBChangeEvent(event: DBDataChangeEvent) {
        loadUI(null)
    }

    /**
     * update calendar
     * @param newMonth     calendar month(example : "2016-07")
     */
    private fun updateCalendar(newMonth: String) {
        mSZCurrentMonth = newMonth

        val tmDays = mCSIAdapter!!.dayModelList
        for (day in tmDays.keys) {
            val itModel = tmDays[day]
            if (itModel != null) {
                var ni = NoteDataHelper.getInfoByDay(day)
                if (ni != null && !day.startsWith(newMonth)) {
                    ni = null
                }

                itModel.recordCount = if (ni != null) ni.incomeCount + ni.payCount else 0
            }
        }
        mCSIAdapter!!.notifyDataSetChanged()
    }

    companion object {
        val YEAR_MONTH = SimpleDateFormat("yyyy-MM", Locale.CHINA)
    }
}
