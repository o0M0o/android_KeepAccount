package wxm.KeepAccount.ui.fragment.Budget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.R;

/**
 * preview fragment for budget
 * Created by 123 on 2016/10/29.
 */
public class BudgetPreviewFrg extends Fragment {
    private TextView  mTVAllAmount;
    private TextView  mTVLeaveAmount;
    private TextView  mTVName;
    private TextView  mTVNote;

    private BudgetItem mBIData;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_budget_preview, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mTVName = UtilFun.cast_t(view.findViewById(R.id.tv_name));
            mTVNote = UtilFun.cast_t(view.findViewById(R.id.tv_note));
            mTVAllAmount = UtilFun.cast_t(view.findViewById(R.id.tv_all_amount));
            mTVLeaveAmount = UtilFun.cast_t(view.findViewById(R.id.tv_leave_amount));

            init_view();
        }
    }

    /**
     * 设置Budget数据
     * @param bi  预览数据
     */
    public void setBudgetData(BudgetItem bi)    {
        mBIData = bi;
    }

    /**
     * 初始化视图
     */
    private void init_view()    {
        if(null != mBIData) {
            mTVName.setText(mBIData.getName());
            mTVNote.setText(mBIData.getNote());
            mTVAllAmount.setText(String.format(Locale.CHINA, "%.02f", mBIData.getAmount()));

            String ra = String.format(Locale.CHINA, "%.02f", mBIData.getRemainderAmount());
            mTVLeaveAmount.setText(ra);
            if(ra.startsWith("-"))  {
                mTVLeaveAmount.setTextColor(getResources().getColor(R.color.darkred));
            }
        }
    }
}
