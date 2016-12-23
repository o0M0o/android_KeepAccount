package wxm.KeepAccount.ui.fragment.EditData;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.BudgetItem;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.dialog.DlgLongTxt;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;

/**
 * 编辑支出
 * Created by wxm on 2016/9/28.
 */
public class TFEditBudget extends TFEditBase {
    private final static String TAG = "TFEditBudget";
    private final static int  MAX_NOTELEN = 200;

    private BudgetItem              mBIData;

    @BindView(R.id.et_budget_name)
    TextInputEditText       mETName;

    @BindView(R.id.et_budget_amount)
    TextInputEditText       mETAmount;

    @BindView(R.id.tv_note)
    TextView    mTVNote;

    @BindString(R.string.notice_input_note)
    String      mSZDefNote;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_budget_edit, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            init_component();
            init_view();
        }
    }

    @Override
    public void setCurData(String action, Object obj) {
        mAction = action;
        mBIData = UtilFun.cast(obj);
    }

    @Override
    public boolean onAccept() {
        boolean b_create = GlobalDef.STR_CREATE.equals(mAction);
        String name = mETName.getText().toString();

        String tv_sz = mTVNote.getText().toString();
        String note = mSZDefNote.equals(tv_sz) ? null : tv_sz;

        String amount = mETAmount.getText().toString();

        if(UtilFun.StringIsNullOrEmpty(name))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("请输入预算名!").setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();

            mETName.requestFocus();
            return false;
        }

        BudgetItem cbi = ContextUtil.getBudgetUtility().getBudgetByName(name);
        if((null != cbi)
                && ((null == mBIData) || (mBIData.get_id() != cbi.get_id())))  {
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

        boolean s_ret = b_create ? ContextUtil.getBudgetUtility().createData(bi)
                                    : ContextUtil.getBudgetUtility().modifyData(bi);
        if(!s_ret)  {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(b_create ? "创建预算数据失败!" : "更新预算数据失败")
                    .setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();
        }

        return s_ret;
    }

    @Override
    public Object getCurData() {
        if(null != getView()) {
            String name = mETName.getText().toString();
            String amount = mETAmount.getText().toString();

            BudgetItem bi = new BudgetItem();
            if(null != mBIData) {
                bi.set_id(mBIData.get_id());
                bi.setUsr(mBIData.getUsr());
            }
            bi.setName(name);
            bi.setAmount(UtilFun.StringIsNullOrEmpty(amount) ? BigDecimal.ZERO : new BigDecimal(amount));

            String tv_sz = mTVNote.getText().toString();
            bi.setNote(mSZDefNote.equals(tv_sz) ? null : tv_sz);
            return bi;
        }

        return null;
    }

    @Override
    public void reLoadView() {
        if(null != getView())
            init_view();
    }


    /**
     * 初始化视图
     */
    private void init_view()    {
        if (UtilFun.StringIsNullOrEmpty(mAction)
                || (GlobalDef.STR_MODIFY.equals(mAction) && null == mBIData))
            return ;

        if(null != mBIData) {
            mETName.setText(mBIData.getName());
            mETAmount.setText(mBIData.getAmount().toPlainString());

            String o_n = mBIData.getNote();
            mTVNote.setText(UtilFun.StringIsNullOrEmpty(o_n) ? mSZDefNote : o_n);
        }
    }

    /**
     * 初始化组件
     */
    private void init_component()   {
        mTVNote.setOnTouchListener((view, motionEvent) -> {
            String tv_sz = mTVNote.getText().toString();
            String lt = mSZDefNote.equals(tv_sz) ? "" : tv_sz;

            DlgLongTxt dlg = new DlgLongTxt();
            dlg.setLongTxt(lt);
            dlg.setDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                @Override
                public void onDialogPositiveResult(DialogFragment dialogFragment) {
                    String lt = ((DlgLongTxt)dialogFragment).getLongTxt();
                    if(UtilFun.StringIsNullOrEmpty(lt))
                        lt = mSZDefNote;

                    mTVNote.setText(lt);
                }

                @Override
                public void onDialogNegativeResult(DialogFragment dialogFragment) {
                }
            });

            dlg.show(getActivity().getSupportFragmentManager(), "edit note");

            return true;
        });

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
    }
}
