package wxm.KeepAccount.ui.data.edit.Remind;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.RemindItem;

/**
 * 收入提醒
 * Created by 123 on 2016/10/8.
 */
public class TFEditRemindIncome extends TFEditRemindBase {
    private final static String[] PERIOD_TYPE = {
            RemindItem.PERIOD_WEEKLY,
            RemindItem.PERIOD_MONTHLY,
            RemindItem.PERIOD_YEARLY
    };
    private final static String[] RAT_TYPE = {
            RemindItem.RAT_AMOUNT_BELOW,
            RemindItem.RAT_AMOUNT_EXCEED
    };
    private Spinner mSPPeriod;
    private Spinner mSPRemindActiveType;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vw_edit_remind_income, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mSPPeriod = UtilFun.cast(view.findViewById(R.id.sp_period));
            mSPRemindActiveType = UtilFun.cast(view.findViewById(R.id.sp_remind_active));
            assert null != mSPPeriod && null != mSPRemindActiveType;

            // init period
            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, PERIOD_TYPE);
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSPPeriod.setAdapter(spAdapter);

            // init remind active type
            ArrayAdapter<String> spAdapter1 = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, RAT_TYPE);
            spAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSPRemindActiveType.setAdapter(spAdapter1);
        }
    }

    @Override
    public boolean onAccept() {
        return false;
    }
}
