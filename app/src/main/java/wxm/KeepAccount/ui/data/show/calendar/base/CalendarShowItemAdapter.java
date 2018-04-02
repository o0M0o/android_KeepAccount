package wxm.KeepAccount.ui.data.show.calendar.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.uilib.SimpleCalendar.BaseCalendarItemAdapter;
import wxm.uilib.SimpleCalendar.BaseCalendarItemModel;
import wxm.androidutil.util.UiUtil;

/**
 * adapter for calendar
 * Created by WangXM on 2017/07/03.
 */
public class CalendarShowItemAdapter extends BaseCalendarItemAdapter<CalendarShowItemModel> {
    private final static int mCLToday;
    private final static int mCLHoliday;
    private final static int mCLDisable;

    static {
        Context ct = ContextUtil.getInstance();
        mCLToday = UiUtil.getColor(ct, R.color.red_ff725f);
        mCLHoliday = UiUtil.getColor(ct, R.color.red_ff725f);
        mCLDisable = UiUtil.getColor(ct, android.R.color.darker_gray);
    }


    public CalendarShowItemAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(String date, CalendarShowItemModel model, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.gi_calendar_item, null);
        view.setBackgroundResource(model.getRecordCount() > 0 ?
                R.drawable.day_shape : R.drawable.day_empty_shape);

        TextView dayNum = view.findViewById(R.id.tv_day_num);
        dayNum.setText(model.getDayNumber());
        if (model.isToday()) {
            dayNum.setTextColor(mCLToday);
            dayNum.setText(mContext.getResources().getString(R.string.today));
        }

        if (model.isHoliday()) {
            dayNum.setTextColor(mCLHoliday);
        }

        if (model.getStatus() == BaseCalendarItemModel.Status.DISABLE) {
            dayNum.setTextColor(mCLDisable);
        }

        TextView dayNewsCount = view.findViewById(R.id.tv_day_new_count);
        if (!model.isCurrentMonth()) {
            dayNum.setVisibility(View.GONE);
            dayNewsCount.setVisibility(View.GONE);
        } else {
            if (model.getRecordCount() > 0) {
                dayNewsCount.setText(String.format(mContext.getResources().getString(R.string.calendar_item_new_count), model.getRecordCount()));
                dayNewsCount.setVisibility(View.VISIBLE);
            } else {
                dayNewsCount.setVisibility(View.GONE);
            }
        }

        return view;
    }
}
