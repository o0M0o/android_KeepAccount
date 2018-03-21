package wxm.KeepAccount.ui.data.edit.Note.utility;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.ui.data.edit.base.TFPreviewBase;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * preview fragment for income
 * Created by 123 on 2016/10/29.
 */
public class TFPreviewIncome extends TFPreviewBase {
    private TextView mTVAmount;
    private TextView mTVInfo;
    private TextView mTVNote;
    private TextView mTVDate;
    private TextView mTVDayInWeek;
    private TextView mTVTime;

    private View mSelfView;
    private IncomeNoteItem mINData;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_preview_income, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mSelfView = view;

            mTVAmount = UtilFun.cast_t(view.findViewById(R.id.tv_amount));
            mTVInfo = UtilFun.cast_t(view.findViewById(R.id.tv_info));
            mTVNote = UtilFun.cast_t(view.findViewById(R.id.tv_note));
            mTVDate = UtilFun.cast_t(view.findViewById(R.id.tv_date));
            mTVDayInWeek = UtilFun.cast_t(view.findViewById(R.id.tv_day_in_week));
            mTVTime = UtilFun.cast_t(view.findViewById(R.id.tv_time));

            init_view();
        }
    }

    @Override
    public void setPreviewPara(Object obj) {
        mINData = UtilFun.cast(obj);
    }

    @Override
    public Object getCurData() {
        return mINData;
    }

    @Override
    public void reLoadView() {
        if (null != mSelfView)
            init_view();
    }

    private void init_view() {
        if (null != mINData) {
            mTVAmount.setText(mINData.getValToStr());
            mTVInfo.setText(mINData.getInfo());
            mTVNote.setText(mINData.getNote());
            mTVDate.setText(ToolUtil.FormatDateString(mINData.getTs().toString().substring(0, 10)));
            mTVTime.setText(mINData.getTs().toString().substring(11, 16));
            mTVDayInWeek.setText(ToolUtil.getDayInWeek(mINData.getTs()));
        } else {
            mTVAmount.setText("");
            mTVInfo.setText("");
            mTVNote.setText("");
            mTVDate.setText("");
            mTVDayInWeek.setText("");
            mTVTime.setText("");
        }
    }
}
