package wxm.KeepAccount.ui.fragment.Budget;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.R;

/**
 * edit fragment for budget
 * Created by 123 on 2016/10/29.
 */
public class BudgetEditFrg extends Fragment {
    private final static int  MAX_NOTELEN = 200;

    private View                    mSelfView;

    private BudgetItem              mBIData;
    private TextInputEditText       mETName;
    private TextInputEditText       mETAmount;
    private TextInputEditText       mETNote;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_budget_edit, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mSelfView = view;
            init_component();
            init_view();
        }
    }

    /**
     * 设置Budget数据
     * 如果设置Budget数据，则修改Budget数据，否则新建Budget数据
     * @param bi  预览数据
     */
    public void setBudgetData(BudgetItem bi)    {
        mBIData = bi;
    }


    /**
     * 保存fragment上的当前数据
     * @return  成功返回true, 否则返回false
     */
    public boolean saveResult() {
        String name = mETName.getText().toString();
        String note = mETNote.getText().toString();
        String amount = mETAmount.getText().toString();

        if(UtilFun.StringIsNullOrEmpty(name))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("请输入预算名!").setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();

            mETName.requestFocus();
            return false;
        }

        BudgetItem cbi = AppModel.getBudgetUtility().GetBudgetByName(name);
        if((null != cbi) &&
                ((null == mBIData) || (mBIData.get_id() != cbi.get_id())))  {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("预算名已经存在!").setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();

            mETName.requestFocus();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(amount))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("请输入预算金额!").setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();

            mETAmount.requestFocus();
            return false;
        }

        BudgetItem bi = null == mBIData ? new BudgetItem() : mBIData;
        bi.setName(name);
        bi.setAmount(new BigDecimal(amount));
        bi.setNote(note);

        return null == mBIData ? AppModel.getBudgetUtility().AddBudget(bi)
                                        : AppModel.getBudgetUtility().ModifyBudget(bi);
    }


    /**
     * 初始化视图
     */
    private void init_view()    {
        if(null != mBIData) {
            mETName.setText(mBIData.getName());
            mETAmount.setText(mBIData.getAmount().toPlainString());
            mETNote.setText(mBIData.getNote());
        }
    }

    /**
     * 初始化组件
     */
    private void init_component()   {
        // for name
        mETName = UtilFun.cast_t(mSelfView.findViewById(R.id.et_budget_name));

        // for amount
        mETAmount = UtilFun.cast_t(mSelfView.findViewById(R.id.et_budget_amount));
        mETAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int pos = s.toString().indexOf(".");
                if(pos >= 0) {
                    int after_len = s.length() - (pos + 1);
                    if (after_len > 2) {
                        mETAmount.setError("小数点后超过两位数!");
                        mETAmount.setText(s.subSequence(0, pos + 3));
                    }
                }
            }
        });

        // for note
        mETNote = UtilFun.cast_t(mSelfView.findViewById(R.id.et_budget_note));
        mETNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > MAX_NOTELEN)    {
                    mETNote.setError(String.format(Locale.CHINA,
                                    "超过最大长度(%d)!", MAX_NOTELEN));
                    mETNote.setText(s.subSequence(0, MAX_NOTELEN));
                }
            }
        });
    }
}
