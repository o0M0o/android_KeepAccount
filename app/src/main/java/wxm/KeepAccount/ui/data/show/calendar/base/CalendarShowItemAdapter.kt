package wxm.KeepAccount.ui.data.show.calendar.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.ViewHolder.ViewHolder
import wxm.androidutil.util.UiUtil
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter
import wxm.uilib.FrgCalendar.CalendarItem.EItemStatus
import java.util.*

/**
 * adapter for calendar
 * Created by WangXM on 2017/07/03.
 */
class CalendarShowItemAdapter(context: Context) : BaseItemAdapter<CalendarShowItemModel>(context) {

    override fun getView(date: String?, model: CalendarShowItemModel, convertView: View?, parent: ViewGroup?): View {
        val vhParent = ViewHolder.get(mContext, convertView, R.layout.gi_calendar_item)
        val vwParent = vhParent.convertView
        vwParent.setBackgroundResource(if (model.recordCount > 0)
            R.drawable.day_shape
        else
            R.drawable.day_empty_shape)

        val dayNum = vhParent.getView<TextView>(R.id.tv_day_num)
        dayNum.text = model.dayNumber
        if (model.isToday) {
            dayNum.setTextColor(mCLToday)
            dayNum.text = mContext.resources.getString(R.string.today)
        }

        if (model.isHoliday) {
            dayNum.setTextColor(mCLHoliday)
        }

        if (model.status == EItemStatus.DISABLE) {
            dayNum.setTextColor(mCLDisable)
        }

        val dayNewsCount = vhParent.getView<TextView>(R.id.tv_day_new_count)
        if (model.recordCount > 0) {
            dayNewsCount.text = String.format(mContext.resources.getString(R.string.calendar_item_new_count), model.recordCount)
            dayNewsCount.visibility = View.VISIBLE
        } else {
            dayNewsCount.visibility = View.GONE
        }

        return vwParent
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
