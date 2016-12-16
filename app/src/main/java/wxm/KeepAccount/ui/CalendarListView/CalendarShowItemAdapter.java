package wxm.KeepAccount.ui.CalendarListView;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wxm.KeepAccount.R;
import wxm.simplecalendarlvb.BaseCalendarItemAdapter;
import wxm.simplecalendarlvb.BaseCalendarItemModel;


/**
 * Created by kelin on 16-7-19.
 */
public class CalendarShowItemAdapter extends BaseCalendarItemAdapter<CalendarShowItemModel> {
    private int mCLToday;
    private int mCLHoliday;

    private int mCLDisable;
    private int mCLNotCurrentMonth;

    public CalendarShowItemAdapter(Context context) {
        super(context);

        Resources res = mContext.getResources();
        mCLToday = res.getColor(R.color.red_ff725f);
        mCLHoliday = res.getColor(R.color.red_ff725f);
        mCLDisable = res.getColor(android.R.color.darker_gray);
        mCLNotCurrentMonth = res.getColor(R.color.gray_bbbbbb);
    }

    @Override
    public View getView(String date, CalendarShowItemModel model, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.griditem_custom_calendar_item, null);

        TextView dayNum = (TextView) view.findViewById(R.id.tv_day_num);
        dayNum.setText(model.getDayNumber());

        view.setBackgroundResource(model.getRecordCount() > 0 ?
                R.drawable.day_shape : R.drawable.day_empty_shape);

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

        TextView dayNewsCount = (TextView) view.findViewById(R.id.tv_day_new_count);
        if (!model.isCurrentMonth()) {
            dayNum.setVisibility(View.GONE);
            dayNewsCount.setVisibility(View.GONE);
            //dayNum.setTextColor(mCLNotCurrentMonth);
            view.setClickable(true);
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
