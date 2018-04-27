package wxm.KeepAccount.ui.data.show.calendar.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.Objects

import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil
import wxm.uilib.FrgCalendar.FrgCalendarItemAdapter
import wxm.uilib.FrgCalendar.FrgCalendarItemModel
import wxm.androidutil.util.UiUtil

/**
 * adapter for calendar
 * Created by WangXM on 2017/07/03.
 */
class CalendarShowItemAdapter(context: Context) : FrgCalendarItemAdapter<CalendarShowItemModel>(context) {

    override fun getView(date: String?, model: CalendarShowItemModel, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.gi_calendar_item, null) as ViewGroup
        view.setBackgroundResource(if (model.recordCount > 0)
            R.drawable.day_shape
        else
            R.drawable.day_empty_shape)

        val dayNum = view.findViewById<TextView>(R.id.tv_day_num)
        dayNum.text = model.dayNumber
        if (model.isToday) {
            dayNum.setTextColor(mCLToday)
            dayNum.text = mContext.resources.getString(R.string.today)
        }

        if (model.isHoliday) {
            dayNum.setTextColor(mCLHoliday)
        }

        if (model.status == FrgCalendarItemModel.Status.DISABLE) {
            dayNum.setTextColor(mCLDisable)
        }

        val dayNewsCount = view.findViewById<TextView>(R.id.tv_day_new_count)
        if (model.isNotCurrentMonth) {
            dayNum.visibility = View.GONE
            dayNewsCount.visibility = View.GONE
        } else {
            if (model.recordCount > 0) {
                dayNewsCount.text = String.format(mContext.resources.getString(R.string.calendar_item_new_count), model.recordCount)
                dayNewsCount.visibility = View.VISIBLE
            } else {
                dayNewsCount.visibility = View.GONE
            }
        }

        return view
    }

    companion object {
        private val mCLToday: Int
        private val mCLHoliday: Int
        private val mCLDisable: Int

        init {
            val ct = Objects.requireNonNull<ContextUtil>(ContextUtil.instance)
            mCLToday = UiUtil.getColor(ct, R.color.red_ff725f)
            mCLHoliday = UiUtil.getColor(ct, R.color.red_ff725f)
            mCLDisable = UiUtil.getColor(ct, android.R.color.darker_gray)
        }
    }
}
