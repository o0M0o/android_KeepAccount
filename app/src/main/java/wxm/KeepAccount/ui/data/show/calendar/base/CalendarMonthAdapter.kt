package wxm.KeepAccount.ui.data.show.calendar.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import wxm.KeepAccount.R
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.view.ViewHolder
import wxm.androidutil.improve.doJudge
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
        vhParent.getView<TextView>(R.id.tv_day_num).apply {
            text = (model.isToday).doJudge(context.getString(R.string.today), model.dayNumber)
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
            visibility = (model.recordCount > 0)
                    .doJudge({
                        text = context.getString(R.string.calendar_item_new_count, model.recordCount)
                        View.VISIBLE
                    }, { View.GONE })
        }

        return vhParent.convertView.apply {
            setBackgroundResource(
                    when {
                        model.isCurrentMonth -> {
                            (model.recordCount > 0)
                                    .doJudge(R.drawable.day_shape, R.drawable.day_empty_shape)
                        }

                        else -> R.drawable.day_other_month_shape
                    }
            )
        }
    }

    companion object {
        private val mCLToday = AppBase.getColor(R.color.red_ff725f)
        private val mCLHoliday = AppBase.getColor(R.color.red_ff725f)
        private val mCLDisable = AppBase.getColor(R.color.grey)

        private val mCLOtherMonth = AppBase.getColor(R.color.text_half_fit)
        private val mCLCurrentMonth = AppBase.getColor(R.color.text_fit)
    }
}
