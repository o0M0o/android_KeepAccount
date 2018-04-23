package wxm.KeepAccount.ui.data.edit.Remind;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.RemindItem;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * 预算提醒
 * Created by WangXM on 2016/10/8.
 */
public class TFEditRemindBudget extends TFEditRemindBase {
    private Spinner mSPBudget;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vw_edit_remind_budget, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mSPBudget = UtilFun.cast(view.findViewById(R.id.sp_budget));
            assert null != mSPBudget;

            // init budget
            ArrayList<String> data_ls = new ArrayList<>();
            List<BudgetItem> bils = ContextUtil.Companion.getBudgetUtility().getBudgetForCurUsr();
            if (!UtilFun.ListIsNullOrEmpty(bils)) {
                for (BudgetItem i : bils) {
                    data_ls.add(i.getName());
                }
            }

            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, data_ls);
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSPBudget.setAdapter(spAdapter);
        }
    }

    @Override
    public boolean onAccept() {
        if (!checkName())
            return false;

        if (!checkDate())
            return false;

        if (!checkBudget())
            return false;

        if (!checkAmount())
            return false;

        if (!checkType())
            return false;

        RemindItem ri = new RemindItem();
        ri.setName(mETName.getText().toString().trim());
        ri.setType(RemindItem.REMIND_BUDGET);
        ri.setStartDate(mTSStartDate);
        ri.setEndDate(mTSEndDate);
        ri.setAmount(mBDAmount);

        String r = UtilFun.cast(mSPRemindActiveType.getSelectedItem());
        ri.setReason(r);

        return ContextUtil.Companion.getRemindUtility().AddOrUpdateRemind(ri);
    }


    /**
     * 检查预算数据合法性
     *
     * @return 合法返回true, 否则返回false
     */
    private boolean checkBudget() {
        String sel_budget = UtilFun.cast(mSPBudget.getSelectedItem());
        BudgetItem bi = ContextUtil.Companion.getBudgetUtility().getBudgetByName(sel_budget);
        if (null == bi) {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("缺少预算项").
                    setMessage("需要选择预算项!").
                    create();
            alertDialog.show();

            mSPBudget.requestFocus();
            return false;
        }

        return true;
    }
}
