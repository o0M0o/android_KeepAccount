package wxm.KeepAccount.ui.dialog;

import android.app.DatePickerDialog;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.KeepAccount.R;

/**
 * select day range for report
 * Created by WangXM on 2017/2/15.
 */
public class DlgSelectReportDays extends DlgOKOrNOBase {
    @BindString(R.string.hint_input_day)
    String mSZInputDay;
    @BindView(R.id.tv_start_day)
    TextView mTVStartDay;
    @BindView(R.id.tv_end_day)
    TextView mTVEndDay;
    private String mSZStartDay;
    private String mSZEndDay;

    /**
     * get start day
     * @return      start day
     */
    public String getStartDay() {
        return mSZStartDay;
    }

    /**
     * get end day
     * @return      end day
     */
    public String getEndDay() {
        return mSZEndDay;
    }

    @Override
    protected View InitDlgView() {
        mSZEndDay = null;
        mSZStartDay = null;

        InitDlgTitle("选择起始日期", "接受", "放弃");
        View vw = View.inflate(getActivity(), R.layout.dlg_select_report_days, null);
        ButterKnife.bind(this, vw);

        //mTVStartDay.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        //mTVEndDay.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        // 默认是过去3个月的数据
        Calendar cd = Calendar.getInstance();
        mSZEndDay = dayToSZ(cd.get(Calendar.YEAR),
                cd.get(Calendar.MONTH) + 1, cd.get(Calendar.DAY_OF_MONTH));
        mTVEndDay.setText(mSZEndDay);

        cd.add(Calendar.MONTH, -3);
        mSZStartDay = dayToSZ(cd.get(Calendar.YEAR),
                cd.get(Calendar.MONTH) + 1, cd.get(Calendar.DAY_OF_MONTH));
        mTVStartDay.setText(mSZStartDay);

        return vw;
    }

    /**
     * handler for click event
     * @param v     clicked view
     */
    @OnClick({R.id.tv_start_day, R.id.tv_end_day})
    public void tvClicks(View v) {
        DatePickerDialog dialog = null;
        switch (v.getId()) {
            case R.id.tv_start_day: {
                int y = Integer.valueOf(mSZStartDay.substring(0, 4));
                int m = Integer.valueOf(mSZStartDay.substring(5, 7)) - 1;
                int d = Integer.valueOf(mSZStartDay.substring(8, 10));

                dialog = new DatePickerDialog(
                        getContext(),
                        (dp, year, month, dayOfMonth) -> {
                            mSZStartDay = dayToSZ(year, month + 1, dayOfMonth);
                            mTVStartDay.setText(mSZStartDay);
                        },
                        y, m, d);

            }
            break;

            case R.id.tv_end_day: {
                int y = Integer.valueOf(mSZEndDay.substring(0, 4));
                int m = Integer.valueOf(mSZEndDay.substring(5, 7)) - 1;
                int d = Integer.valueOf(mSZEndDay.substring(8, 10));

                dialog = new DatePickerDialog(
                        getContext(),
                        (dp, year, month, dayOfMonth) -> {
                            mSZEndDay = dayToSZ(year, month + 1, dayOfMonth);
                            mTVEndDay.setText(mSZEndDay);
                        },
                        y, m, d);
            }
            break;
        }

        if (null != dialog)
            dialog.show();
    }

    /**
     * check whether data is ok
     * @return      true if ok
     */
    @Override
    protected boolean checkBeforeOK() {
        String sz_alert = "警告";
        String sz_sure = "确定";
        if (null == mSZStartDay || null == mSZEndDay) {
            String msg = null == mSZStartDay ? "未选择开始日期!" : "未选择结束日期";

            new AlertDialog.Builder(getContext())
                    .setTitle(sz_alert)
                    .setMessage(msg)
                    .setPositiveButton(sz_sure, null)
                    .show();
            return false;
        }

        if (0 <= mSZStartDay.compareTo(mSZEndDay)) {
            String msg = String.format(Locale.CHINA,
                    "开始日期(%s)比结束日期(%s)晚!", mSZStartDay, mSZEndDay);

            new AlertDialog.Builder(getContext())
                    .setTitle(sz_alert)
                    .setMessage(msg)
                    .setPositiveButton(sz_sure, null)
                    .show();
            return false;
        }

        return true;
    }

    /**
     * parse day to string
     * @param year  年
     * @param month 月(1-12)
     * @param day   日(1-31)
     * @return 字符串
     */
    private String dayToSZ(int year, int month, int day) {
        return String.format(Locale.CHINA, "%d-%02d-%02d",
                year, month, day);
    }
}
