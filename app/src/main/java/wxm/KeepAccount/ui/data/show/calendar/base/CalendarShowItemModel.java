package wxm.KeepAccount.ui.data.show.calendar.base;


import wxm.uilib.FrgCalendar.FrgCalendarItemModel;

/**
 * for extended
 * Created by WangXM on 2017/07/06
 */
public class CalendarShowItemModel extends FrgCalendarItemModel {
    private int mRecordCount;

    public int getRecordCount() {
        return mRecordCount;
    }

    public void setRecordCount(int mRecordCount) {
        this.mRecordCount = mRecordCount;
    }
}
