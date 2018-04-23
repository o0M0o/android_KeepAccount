package wxm.KeepAccount.ui.data.show.calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindView;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.ui.data.show.calendar.base.SelectedDayEvent;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.utility.AdapterNoteDetail;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;

/**
 * for note pad content detail
 * Created by WangXM on 2016/12/12.
 */
public class FrgCalendarContent extends FrgSupportBaseAdv {
    // for ui
    @BindView(R.id.tv_month_day)
    TextView mTVMonthDay;

    @BindView(R.id.tv_year_month)
    TextView mTVYearMonth;

    @BindView(R.id.header_day_balance)
    TextView mTVBalance;

    @BindView(R.id.lv_body)
    ListView mLVBody;

    @BindColor(R.color.darkred)
    int mCLPay;

    @BindColor(R.color.darkslategrey)
    int mCLIncome;

    // for data
    private String mSZHotDay;
    private List<INote> mLSDayContents;

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBDataChange(DBDataChangeEvent event) {
    }

    /**
     * handler for selected day change
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectedDayChange(SelectedDayEvent event) {
        updateContent(event.getDay());
    }

    @Override
    protected int getLayoutID() {
        return R.layout.frg_calendar_content;
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void initUI(Bundle bundle)    {
        mSZHotDay = null;
        mLSDayContents = null;

        loadUI(bundle);
    }

    @Override
    protected void loadUI(Bundle bundle) {
        if (UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            setVisibility(View.INVISIBLE);
            return;
        }

        String[] arr = mSZHotDay.split("-");
        if (3 != arr.length) {
            setVisibility(View.INVISIBLE);
            return;
        }

        // for header;
        setVisibility(View.VISIBLE);
        mTVMonthDay.setText(arr[2]);
        mTVYearMonth.setText(String.format(Locale.CHINA, "%s年%s月", arr[0], arr[1]));

        NoteShowInfo ni = NoteDataHelper.Companion.getInfoByDay(mSZHotDay);
        BigDecimal bb = null != ni ? ni.getBalance() : BigDecimal.ZERO;
        mTVBalance.setText(String.format(Locale.CHINA, "%s %.02f",
                0 > bb.compareTo(BigDecimal.ZERO) ? "-" : "+",
                Math.abs(bb.floatValue())));
        mTVBalance.setTextColor(0 > bb.compareTo(BigDecimal.ZERO) ? mCLPay : mCLIncome);

        // for list body
        LinkedList<HashMap<String, INote>> c_para = new LinkedList<>();
        if (!UtilFun.ListIsNullOrEmpty(mLSDayContents)) {
            for (INote ci : mLSDayContents) {
                HashMap<String, INote> hm = new HashMap<>();
                hm.put(AdapterNoteDetail.K_NODE, ci);

                c_para.add(hm);
            }
        }

        AdapterNoteDetail ap = new AdapterNoteDetail(getActivity(), c_para);
        mLVBody.setAdapter(ap);
        ap.notifyDataSetChanged();
    }

    /// PRIVATE BEGIN
    /**
     * update hot day
     * @param day   show day(example : 2017-07-06)
     */
    private void updateContent(String day) {
        mSZHotDay = day;
        mLSDayContents = NoteDataHelper.Companion.getInstance().getNotesByDay(day);

        loadUI(null);
    }

    private void setVisibility(int vis) {
        mTVMonthDay.setVisibility(vis);
        mTVYearMonth.setVisibility(vis);
        mLVBody.setVisibility(vis);
    }
    /// PRIVATE END
}
