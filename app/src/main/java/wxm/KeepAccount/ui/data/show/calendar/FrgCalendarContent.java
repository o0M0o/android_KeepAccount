package wxm.KeepAccount.ui.data.show.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.AdapterNoteDetail;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;

/**
 * for note pad content detail
 * Created by ookoo on 2016/12/12.
 */
public class FrgCalendarContent extends FrgUtilityBase {
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
    private String          mSZHotDay;
    private List<INote>     mLSDayContents;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgFLNotePadContent";
        View rootView = layoutInflater.inflate(R.layout.frg_calendar_content, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    /**
     * 设置展示类容
     * @param day           for day
     * @param content       for content
     */
    public void setDay(String day, List<INote> content)  {
        mSZHotDay = day;
        mLSDayContents = content;

        loadContent();
    }

    @Override
    protected void initUiComponent(View view) {
        mSZHotDay       = null;
        mLSDayContents  = null;
    }

    @Override
    protected void loadUI() {
        loadContent();
    }

    /// PRIVATE BEGIN
    private void loadContent()  {
        if(isDetached())
            return;

        if(UtilFun.StringIsNullOrEmpty(mSZHotDay))  {
            setVisibility(View.INVISIBLE);
            return;
        }

        String[] arr = mSZHotDay.split("-");
        if(3 != arr.length) {
            setVisibility(View.INVISIBLE);
            return;
        }

        // for header;
        setVisibility(View.VISIBLE);
        mTVMonthDay.setText(arr[2]);
        mTVYearMonth.setText(String.format(Locale.CHINA, "%s年%s月", arr[0], arr[1]));

        NoteShowInfo ni = NoteShowDataHelper.getInstance().getDayInfo().get(mSZHotDay);
        BigDecimal bb = null != ni ? ni.getBalance() : BigDecimal.ZERO;
        mTVBalance.setText(String.format(Locale.CHINA, "%s %.02f",
                0 > bb.compareTo(BigDecimal.ZERO) ? "-" : "+",
                Math.abs(bb.floatValue())));
        mTVBalance.setTextColor(0 > bb.compareTo(BigDecimal.ZERO) ?  mCLPay : mCLIncome);

        // for list body
        LinkedList<HashMap<String, INote>> c_para = new LinkedList<>();
        if(!UtilFun.ListIsNullOrEmpty(mLSDayContents)) {
            for (INote ci : mLSDayContents) {
                HashMap<String, INote> hm = new HashMap<>();
                hm.put(AdapterNoteDetail.K_NODE, ci);

                c_para.add(hm);
            }
        }

        AdapterNoteDetail ap = new AdapterNoteDetail(getActivity(), c_para,
                            new String[]{}, new int[]{});
        mLVBody.setAdapter(ap);
        ap.notifyDataSetChanged();
    }

    private void setVisibility(int vis) {
        mTVMonthDay.setVisibility(vis);
        mTVYearMonth.setVisibility(vis);
        mLVBody.setVisibility(vis);
    }
    /// PRIVATE END
}
