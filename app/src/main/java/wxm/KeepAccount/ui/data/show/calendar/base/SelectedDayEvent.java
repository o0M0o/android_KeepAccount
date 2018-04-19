package wxm.KeepAccount.ui.data.show.calendar.base;

/**
 * event when selected day in calendar fragment
 * @author WangXM
 * @version create：2018/4/18
 */
public class SelectedDayEvent {
    private String mSZDay;

    public SelectedDayEvent(String day) {
        mSZDay = day;
    }

    public String getDay()  {
        return mSZDay;
    }
}
