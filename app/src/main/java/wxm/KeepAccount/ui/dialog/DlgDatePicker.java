package wxm.KeepAccount.ui.dialog;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * 日期选择对话框
 * Created by 123 on 2016/11/1.
 */
public class DlgDatePicker extends DlgOKAndNOBase {
    private String      mInitDate;
    private DatePicker  mDatePicker;
    private TimePicker  mTimePicker;

    public void setInitDate(String initDate)    {
        mInitDate = initDate;
    }

    public String getCurDate()  {
        if(null == mDatePicker || null == mTimePicker)
            return "";

        return String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d:00",
                mDatePicker.getYear(),
                mDatePicker.getMonth() + 1,
                mDatePicker.getDayOfMonth(),
                mTimePicker.getCurrentHour(),
                mTimePicker.getCurrentMinute());
    }


    @Override
    protected View InitDlgView() {
        InitDlgTitle("选择日期与时间", "接受", "放弃");

        if(UtilFun.StringIsNullOrEmpty(mInitDate))
            return null;

        View vw = View.inflate(getActivity(), R.layout.dlg_date, null);
        mDatePicker = UtilFun.cast_t(vw.findViewById(R.id.date_picker));
        mTimePicker = UtilFun.cast_t(vw.findViewById(R.id.time_picker));
        mTimePicker.setIs24HourView(true);

        mDatePicker.init(Integer.valueOf(mInitDate.substring(0, 4)),
                Integer.valueOf(mInitDate.substring(5, 7)) - 1,
                Integer.valueOf(mInitDate.substring(8, 10)),
                null);
        mTimePicker.setCurrentHour(Integer.valueOf(mInitDate.substring(11, 13)));
        mTimePicker.setCurrentMinute(Integer.valueOf(mInitDate.substring(14, 16)));
        return vw;
    }
}
