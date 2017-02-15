package wxm.KeepAccount.ui.dialog;

import android.app.DatePickerDialog;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import wxm.KeepAccount.R;

/**
 * 选择输出报告的日期范围
 * Created by User on 2017/2/15.
 */
public class DlgSelectReportDays extends DlgOKOrNOBase {
    private String mSZStartDay;
    private String mSZEndDay;

    @BindString(R.string.hint_input_day)
    String mSZInputDay;

    @BindView(R.id.tv_start_day)
    TextView    mTVStartDay;

    @BindView(R.id.tv_end_day)
    TextView    mTVEndDay;

    /**
     * 得到需报告的起始日期
     * @return  起始日期
     */
    public String getStartDay() {
        return mSZStartDay;
    }

    /**
     * 得到需报告的结束日期
     * @return  结束日期
     */
    public String getEndDay() {
        return mSZEndDay;
    }

    @Override
    protected View InitDlgView() {
        mSZEndDay   = null;
        mSZStartDay = null;

        InitDlgTitle("选择起始日期",  "接受", "放弃");
        View vw = View.inflate(getActivity(), R.layout.dlg_select_report_days, null);
        ButterKnife.bind(this, vw);

        mTVStartDay.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTVEndDay.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        return vw;
    }

    /**
     * 用户点击textview后处理器
     * @param v     被点击的textview
     */
    @OnClick({R.id.tv_start_day, R.id.tv_end_day})
    public void tvClicks(View v) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = null;
        switch (v.getId())  {
            case R.id.tv_start_day :    {
                dialog = new DatePickerDialog(
                        getContext(),
                        (dp, year, month, dayOfMonth) -> {
                            mSZStartDay = String.format(Locale.CHINA, "%d-%02d-%02d",
                                            year, month + 1, dayOfMonth);
                            mTVStartDay.setText(mSZStartDay);
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));

            }
            break;

            case R.id.tv_end_day :    {
                dialog = new DatePickerDialog(
                        getContext(),
                        (dp, year, month, dayOfMonth) ->    {
                            mSZEndDay = String.format(Locale.CHINA, "%d-%02d-%02d",
                                            year, month + 1, dayOfMonth);
                            mTVEndDay.setText(mSZEndDay);
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
            }
            break;
        }

        if(null != dialog)
            dialog.show();
    }

    /**
     * 必须设置起止日期
     * @return  检查结果
     */
    @Override
    protected boolean checkBeforeOK() {
        String sz_alert = "警告";
        String sz_sure = "确定";
        if(null == mSZStartDay || null == mSZEndDay)    {
            String msg = null == mSZStartDay ? "未选择开始日期!" : "未选择结束日期";

            new AlertDialog.Builder(getContext())
                    .setTitle(sz_alert)
                    .setMessage(msg)
                    .setPositiveButton(sz_sure, null)
                    .show();
            return false;
        }

        if(0 <= mSZStartDay.compareTo(mSZEndDay))   {
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
}
