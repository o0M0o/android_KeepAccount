package wxm.KeepAccount.ui.data.edit.Note.utility;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;

import butterknife.BindString;
import butterknife.BindView;
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;
import wxm.KeepAccount.ui.dialog.DlgLongTxt;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * edit budget
 * Created by WangXM on2016/9/28.
 */
public class TFEditBudget extends TFEditBase {
    private final static String TAG = "TFEditBudget";
    private final static int MAX_NOTELEN = 200;

    @BindView(R.id.et_budget_name)
    TextInputEditText mETName;

    @BindView(R.id.et_budget_amount)
    TextInputEditText mETAmount;

    @BindView(R.id.tv_note)
    TouchTextView mTVNote;

    @BindString(R.string.notice_input_note)
    String mSZDefNote;

    private BudgetItem mBIData;

    @SuppressLint("ClickableViewAccessibility")
    protected void initUI(Bundle bundle)    {
        mTVNote.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mTVNote.setOnTouchListener((view, motionEvent) -> {
            View v_self = getView();
            if (null != v_self && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                String tv_sz = mTVNote.getText().toString();
                String lt = mSZDefNote.equals(tv_sz) ? "" : tv_sz;

                DlgLongTxt dlg = new DlgLongTxt();
                dlg.setLongTxt(lt);
                dlg.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                    @Override
                    public void onDialogPositiveResult(DialogFragment dialogFragment) {
                        String lt = ((DlgLongTxt) dialogFragment).getLongTxt();
                        if (UtilFun.StringIsNullOrEmpty(lt))
                            lt = mSZDefNote;

                        mTVNote.setText(lt);
                        mTVNote.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }

                    @Override
                    public void onDialogNegativeResult(DialogFragment dialogFragment) {
                    }
                });

                dlg.show(getActivity().getSupportFragmentManager(), "edit note");
                return true;
            }

            return false;
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
                if (pos >= 0) {
                    int after_len = s.length() - (pos + 1);
                    if (after_len > 2) {
                        mETAmount.setError("小数点后超过两位数!");
                        mETAmount.setText(s.subSequence(0, pos + 3));
                    }
                }
            }
        });

        loadUI(bundle);
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

        if (UtilFun.StringIsNullOrEmpty(name)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("请输入预算名!").setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();

            mETName.requestFocus();
            return false;
        }

        BudgetItem cbi = ContextUtil.Companion.getBudgetUtility().getBudgetByName(name);
        if ((null != cbi)
                && ((null == mBIData) || (mBIData.get_id() != cbi.get_id()))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("预算名已经存在!").setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();

            mETName.requestFocus();
            return false;
        }

        if (UtilFun.StringIsNullOrEmpty(amount)) {
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

        boolean s_ret = b_create ? ContextUtil.Companion.getBudgetUtility().createData(bi)
                : ContextUtil.Companion.getBudgetUtility().modifyData(bi);
        if (!s_ret) {
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
        if (null != getView()) {
            String name = mETName.getText().toString();
            String amount = mETAmount.getText().toString();

            BudgetItem bi = new BudgetItem();
            if (null != mBIData) {
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
    protected int getLayoutID() {
        return R.layout.vw_budget_edit;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    /**
     * init UI
     */
    @Override
    protected void loadUI(Bundle bundle) {
        if (UtilFun.StringIsNullOrEmpty(mAction)
                || (GlobalDef.STR_MODIFY.equals(mAction) && null == mBIData))
            return;

        if (null != mBIData) {
            mETName.setText(mBIData.getName());
            mETAmount.setText(mBIData.getAmount().toPlainString());

            String o_n = mBIData.getNote();
            mTVNote.setText(UtilFun.StringIsNullOrEmpty(o_n) ? mSZDefNote : o_n);
            mTVNote.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
