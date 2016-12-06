package wxm.KeepAccount.ui.CalendarListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wxm.KeepAccount.R;
import wxm.calendarlv_library.BaseCalendarItemAdapter;
import wxm.calendarlv_library.BaseCalendarItemModel;


/**
 * Created by kelin on 16-7-19.
 */
public class CalendarShowItemAdapter extends BaseCalendarItemAdapter<CalendarShowItemModel> {

    public CalendarShowItemAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(String date, CalendarShowItemModel model, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.griditem_custom_calendar_item, null);

        TextView dayNum = (TextView) view.findViewById(R.id.tv_day_num);
        dayNum.setText(model.getDayNumber());

        view.setBackgroundResource(R.drawable.bg_shape_calendar_item_normal);

        if (model.isToday()) {
            dayNum.setTextColor(mContext.getResources().getColor(R.color.red_ff725f));
            dayNum.setText(mContext.getResources().getString(R.string.today));
        }

        if (model.isHoliday()) {
            dayNum.setTextColor(mContext.getResources().getColor(R.color.red_ff725f));
        }


        if (model.getStatus() == BaseCalendarItemModel.Status.DISABLE) {
            dayNum.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        }

        if (!model.isCurrentMonth()) {
            dayNum.setTextColor(mContext.getResources().getColor(R.color.gray_bbbbbb));
            view.setClickable(true);
        }

        TextView dayNewsCount = (TextView) view.findViewById(R.id.tv_day_new_count);
        if (model.getRecordCount() > 0) {
            dayNewsCount.setText(String.format(mContext.getResources().getString(R.string.calendar_item_new_count), model.getRecordCount()));
            dayNewsCount.setVisibility(View.VISIBLE);
        } else {
            dayNewsCount.setVisibility(View.GONE);
        }

        return view;
    }
}
