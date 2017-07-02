package wxm.KeepAccount.ui.data.edit.Note.utility;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.ui.data.edit.base.TFPreviewBase;

/**
 * preview fragment for budget
 * Created by 123 on 2016/10/29.
 */
public class TFPreviewBudget extends TFPreviewBase {
    private TextView mTVAllAmount;
    private TextView mTVLeaveAmount;
    private TextView mTVName;
    private TextView mTVNote;

    private BudgetItem mBIData;
    private View mSelfView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vw_budget_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mSelfView = view;

            mTVName = UtilFun.cast_t(view.findViewById(R.id.tv_name));
            mTVNote = UtilFun.cast_t(view.findViewById(R.id.tv_note));
            mTVAllAmount = UtilFun.cast_t(view.findViewById(R.id.tv_all_amount));
            mTVLeaveAmount = UtilFun.cast_t(view.findViewById(R.id.tv_leave_amount));

            init_view();
        }
    }

    @Override
    public void setPreviewPara(Object obj) {
        mBIData = UtilFun.cast(obj);
    }

    @Override
    public Object getCurData() {
        return mBIData;
    }

    @Override
    public void reLoadView() {
        if (null != mSelfView)
            init_view();
    }

    /**
     * 初始化视图
     */
    private void init_view() {
        if (null != mBIData) {
            mTVName.setText(mBIData.getName());
            mTVNote.setText(mBIData.getNote());
            mTVAllAmount.setText(String.format(Locale.CHINA, "%.02f", mBIData.getAmount()));

            String ra = String.format(Locale.CHINA, "%.02f", mBIData.getRemainderAmount());
            mTVLeaveAmount.setText(ra);
            if (ra.startsWith("-")) {
                mTVLeaveAmount.setTextColor(getResources().getColor(R.color.darkred));
            }
        } else {
            mTVName.setText("");
            mTVNote.setText("");
            mTVAllAmount.setText("");
            mTVLeaveAmount.setText("");
        }
    }
}
