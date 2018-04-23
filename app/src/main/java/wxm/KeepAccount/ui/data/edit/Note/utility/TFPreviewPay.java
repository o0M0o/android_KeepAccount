package wxm.KeepAccount.ui.data.edit.Note.utility;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.data.edit.base.TFPreviewBase;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * preview fragment for budget
 * Created by WangXM on 2016/10/29.
 */
public class TFPreviewPay extends TFPreviewBase {
    private TextView mTVAmount;
    private TextView mTVInfo;
    private TextView mTVNote;
    private TextView mTVBudget;
    private TextView mTVDate;
    private TextView mTVDayInWeek;
    private TextView mTVTime;

    private View mSelfView;
    private PayNoteItem mPIData;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_preview_pay, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mSelfView = view;

            mTVAmount = UtilFun.cast_t(view.findViewById(R.id.tv_amount));
            mTVInfo = UtilFun.cast_t(view.findViewById(R.id.tv_info));
            mTVNote = UtilFun.cast_t(view.findViewById(R.id.tv_note));
            mTVBudget = UtilFun.cast_t(view.findViewById(R.id.tv_budget_name));
            mTVDate = UtilFun.cast_t(view.findViewById(R.id.tv_date));
            mTVDayInWeek = UtilFun.cast_t(view.findViewById(R.id.tv_day_in_week));
            mTVTime = UtilFun.cast_t(view.findViewById(R.id.tv_time));

            init_view();
        }
    }

    @Override
    public void setPreviewPara(Object obj) {
        mPIData = UtilFun.cast(obj);
    }

    @Override
    public Object getCurData() {
        return mPIData;
    }

    @Override
    public void reLoadView() {
        if (null != mSelfView)
            init_view();
    }

    private void init_view() {
        if (null != mPIData) {
            mTVAmount.setText(mPIData.getValToStr());
            mTVInfo.setText(mPIData.getInfo());
            mTVNote.setText(mPIData.getNote());
            mTVBudget.setText(null == mPIData.getBudget() ? "" : mPIData.getBudget().getName());
            mTVDate.setText(ToolUtil.INSTANCE.formatDateString(mPIData.getTs().toString().substring(0, 10)));
            mTVTime.setText(mPIData.getTs().toString().substring(11, 16));
            mTVDayInWeek.setText(ToolUtil.INSTANCE.getDayInWeek(mPIData.getTs()));
        } else {
            mTVAmount.setText("");
            mTVInfo.setText("");
            mTVNote.setText("");
            mTVBudget.setText("");
            mTVDate.setText("");
            mTVDayInWeek.setText("");
            mTVTime.setText("");
        }
    }
}
