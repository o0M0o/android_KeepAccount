package wxm.KeepAccount.ui.fragment.RemindEdit;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.RemindItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;

/**
 * 预算提醒
 * Created by 123 on 2016/10/8.
 */
public class TFEditRemindBudget extends TFEditRemindBase  {
    private Spinner     mSPBudget;
    private Spinner     mSPRemindActiveType;


    private final static String[] RAT_TYPE = {
            RemindItem.RAT_AMOUNT_BELOW
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_edit_remind_budget, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != view) {
            mSPBudget = UtilFun.cast(view.findViewById(R.id.sp_budget));
            mSPRemindActiveType = UtilFun.cast(view.findViewById(R.id.sp_remind_active));
            assert null != mSPBudget && null != mSPRemindActiveType;

            // init budget
            ArrayList<String> data_ls = new ArrayList<>();
            List<BudgetItem> bils = AppModel.getBudgetUtility().GetBudget();
            if (!ToolUtil.ListIsNullOrEmpty(bils)) {
                for (BudgetItem i : bils) {
                    data_ls.add(i.getName());
                }
            }

            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, data_ls);
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSPBudget.setAdapter(spAdapter);

            // init remind active type
            ArrayAdapter<String> spAdapter1 = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, RAT_TYPE);
            spAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSPRemindActiveType.setAdapter(spAdapter1);
        }
    }

    @Override
    public boolean onAccept() {
        if(!checkName())
            return false;

        if(!checkDate())
            return false;

        return false;
    }


}
