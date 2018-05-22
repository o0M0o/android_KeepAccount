package wxm.KeepAccount.ui.data.show.calendar.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.viewUtil.ViewHolder
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter

/**
 * for calendar-month-mode
 * Created by WangXM on 2017/07/03.
 */
open class CalendarMonthAdapter(context: Context)
    : BaseItemAdapter<CalendarDayModel>(context) {
    override fun getView(date: String, model: CalendarDayModel,
                         convertView: View?, parent: ViewGroup?): View {
        val vhParent = ViewHolder.get(mContext, convertView, R.layout.gi_calendar_item)
        val vwParent = vhParent.convertView
        vwParent.setBackgroundResource(
                when {
                    model.isCurrentMonth -> {
                        if (model.recordCount > 0) R.drawable.day_shape
                        else R.drawable.day_empty_shape
                    }

                    else -> R.drawable.day_other_month_shape
                }
        )

        vhParent.getView<TextView>(R.id.tv_day_num).apply {
            text = if (model.isToday) mSZToday else model.dayNumber
            setTextColor(
                    when {
                        model.isCurrentMonth -> {
                            when {
                                model.isToday -> mCLToday
                                model.isHoliday -> mCLHoliday
                                else -> mCLCurrentMonth
                            }
                        }

                        else -> mCLOtherMonth
                    }
            )
        }

        vhParent.getView<TextView>(R.id.tv_day_new_count).apply {
            visibility = if (model.recordCount > 0) {
                text = String.format(ContextUtil.getString(R.string.calendar_item_new_count),
                        model.recordCount)
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        return vwParent
    }

    companion object {
        private val mCLToday = ContextUtil.getColor(R.color.red_ff725f)
        private val mCLHoliday = ContextUtil.getColor(R.color.red_ff725f)
        private val mCLDisable = ContextUtil.getColor(R.color.grey)

        private val mCLOtherMonth = ContextUtil.getColor(R.color.text_half_fit)
        private val mCLCurrentMonth = ContextUtil.getColor(R.color.text_fit)

        private val mSZToday = ContextUtil.getString(R.string.today)
    }
}
