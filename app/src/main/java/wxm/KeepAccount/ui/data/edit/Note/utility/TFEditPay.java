package wxm.KeepAccount.ui.data.edit.Note.utility;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.PayIncomeDBUtility;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;
import wxm.KeepAccount.ui.dialog.DlgLongTxt;
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;

import static java.lang.String.format;

/**
 * edit pay
 * Created by wxm on 2016/9/28.
 */
public class TFEditPay extends TFEditBase implements View.OnTouchListener {
    private final static String TAG = "TFEditPay";
    @BindView(R.id.ar_et_info)
    EditText mETInfo;
    @BindView(R.id.ar_et_date)
    EditText mETDate;
    @BindView(R.id.ar_et_amount)
    EditText mETAmount;
    @BindView(R.id.ar_sp_budget)
    Spinner mSPBudget;
    @BindView(R.id.ar_tv_budget)
    TextView mTVBudget;
    @BindView(R.id.tv_note)
    TextView mTVNote;
    @BindString(R.string.notice_input_note)
    String mSZDefNote;
    private PayNoteItem mOldPayNote;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_edit_pay, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            if (UtilFun.StringIsNullOrEmpty(mAction)
                    || (GlobalDef.STR_MODIFY.equals(mAction) && null == mOldPayNote))
                return;

            init_component();
            fill_data();
        }
    }

    /**
     * fill data
     */
    private void fill_data() {
        if (mAction.equals(GlobalDef.STR_MODIFY)) {
            BudgetItem bi = mOldPayNote.getBudget();
            if (null != bi) {
                String bn = bi.getName();
                int cc = mSPBudget.getAdapter().getCount();
                for (int i = 0; i < cc; ++i) {
                    String bni = UtilFun.cast(mSPBudget.getAdapter().getItem(i));
                    if (bn.equals(bni)) {
                        mSPBudget.setSelection(i);
                        break;
                    }
                }
            }

            mETDate.setText(mOldPayNote.getTs().toString().substring(0, 16));
            mETInfo.setText(mOldPayNote.getInfo());

            String o_n = mOldPayNote.getNote();
            mTVNote.setText(UtilFun.StringIsNullOrEmpty(o_n) ? mSZDefNote : o_n);
            mTVNote.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

            mETAmount.setText(mOldPayNote.getValToStr());
        } else {
            mTVNote.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

            Bundle bd = getArguments();
            if (null != bd) {
                String ad_date = bd.getString(GlobalDef.STR_RECORD_DATE);
                if (!UtilFun.StringIsNullOrEmpty(ad_date)) {
                    mETDate.setText(ad_date);
                }
            }
        }
    }

    private void init_component() {
        // 填充预算数据
        ArrayList<String> data_ls = new ArrayList<>();
        data_ls.add("无预算(不使用预算)");
        List<BudgetItem> bils = ContextUtil.getBudgetUtility().getBudgetForCurUsr();
        if (!UtilFun.ListIsNullOrEmpty(bils)) {
            for (BudgetItem i : bils) {
                data_ls.add(i.getName());
            }
        }

        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, data_ls);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSPBudget.setAdapter(spAdapter);

        /*
        不显示,UI会比较难看
        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_budget));
        if (0 < spAdapter.getCount()) {
            rl.setVisibility(View.VISIBLE);
            mSPBudget.setSelection(0);
        } else {
            rl.setVisibility(View.INVISIBLE);
        }
        */

        // 填充其他数据
        mETDate.setOnTouchListener(this);
        mETInfo.setOnTouchListener(this);
        mTVNote.setOnTouchListener(this);

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
    }


    @Override
    public void setCurData(String action, Object obj) {
        mAction = action;
        mOldPayNote = UtilFun.cast(obj);
    }

    @Override
    public boolean onAccept() {
        return checkResult() && fillResult();
    }

    @Override
    public Object getCurData() {
        if (null != getView()) {
            PayNoteItem pi = new PayNoteItem();
            if (null != mOldPayNote) {
                pi.setId(mOldPayNote.getId());
                pi.setUsr(mOldPayNote.getUsr());
            }

            pi.setInfo(mETInfo.getText().toString());

            String str_val = mETAmount.getText().toString();
            pi.setVal(UtilFun.StringIsNullOrEmpty(str_val) ? BigDecimal.ZERO : new BigDecimal(str_val));

            String tv_sz = mTVNote.getText().toString();
            pi.setNote(mSZDefNote.equals(tv_sz) ? null : tv_sz);

            String str_date = mETDate.getText().toString() + ":00";
            Timestamp tsDT;
            try {
                tsDT = ToolUtil.StringToTimestamp(str_date);
            } catch (Exception ex) {
                tsDT = new Timestamp(0);
            }
            pi.setTs(tsDT);

            pi.setBudget(null);
            int pos = mSPBudget.getSelectedItemPosition();
            if (AdapterView.INVALID_POSITION != pos && 0 != pos) {
                BudgetItem bi = ContextUtil.getBudgetUtility()
                        .getBudgetByName((String) mSPBudget.getSelectedItem());
                if (null != bi) {
                    pi.setBudget(bi);
                }
            }

            return pi;
        }

        return null;
    }

    @Override
    public void reLoadView() {
    }


    /**
     * check data validity
     * @return      true if data validity
     */
    private boolean checkResult() {
        Activity ac = getActivity();
        if (null == ac)
            return false;

        if (UtilFun.StringIsNullOrEmpty(mETAmount.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入支出数值!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if (UtilFun.StringIsNullOrEmpty(mETInfo.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入支出信息!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if (UtilFun.StringIsNullOrEmpty(mETDate.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入支出日期!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }

    /**
     * write data
     * @return      true if success
     */
    private boolean fillResult() {
        String str_val = mETAmount.getText().toString();
        String str_info = mETInfo.getText().toString();
        String str_date = mETDate.getText().toString();

        String tv_sz = mTVNote.getText().toString();
        String str_note = mSZDefNote.equals(tv_sz) ? null : tv_sz;

        Timestamp tsDT;
        try {
            tsDT = ToolUtil.StringToTimestamp(str_date + ":00");
        } catch (Exception ex) {
            Log.e(TAG, format(Locale.CHINA, "解析'%s'到日期失败", str_date));
            return false;
        }

        // set data
        PayNoteItem pi = new PayNoteItem();
        if (null != mOldPayNote && mAction.equals(GlobalDef.STR_MODIFY)) {
            pi.setId(mOldPayNote.getId());
            pi.setUsr(mOldPayNote.getUsr());
        }

        pi.setInfo(str_info);
        pi.setTs(tsDT);
        pi.setVal(new BigDecimal(str_val));
        pi.setNote(str_note);

        pi.setBudget(null);
        int pos = mSPBudget.getSelectedItemPosition();
        if (AdapterView.INVALID_POSITION != pos && 0 != pos) {
            BudgetItem bi = ContextUtil.getBudgetUtility()
                    .getBudgetByName((String) mSPBudget.getSelectedItem());
            if (null != bi) {
                pi.setBudget(bi);
            }
        }

        // add/modify data
        boolean b_create = mAction.equals(GlobalDef.STR_CREATE);
        PayIncomeDBUtility uti = ContextUtil.getPayIncomeUtility();
        boolean b_ret = b_create ?
                1 == uti.addPayNotes(Collections.singletonList(pi))
                : uti.getPayDBUtility().modifyData(pi);
        if (!b_ret) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(b_create ? "创建支出数据失败!" : "更新支出数据失败")
                    .setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();
        }
        return b_ret;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        View v_self = getView();
        if (null != v_self && event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.tv_note: {
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
                }
                break;

                case R.id.ar_et_date: {
                    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                    Date j_dt = new Date();
                    try {
                        j_dt = sd.parse(mETDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar cd = Calendar.getInstance();
                    cd.setTime(j_dt);

                    DatePickerDialog.OnDateSetListener dt = (view, year, month, dayOfMonth) -> {
                        String str_date = format(Locale.CHINA, "%04d-%02d-%02d",
                                year, month + 1, dayOfMonth);

                        TimePickerDialog.OnTimeSetListener ot = (view1, hourOfDay, minute) -> {
                            String tm = format(Locale.CHINA, "%s %02d:%02d",
                                    str_date, hourOfDay, minute);

                            mETDate.setText(tm);
                            mETDate.requestFocus();
                        };

                        TimePickerDialog td = new TimePickerDialog(getContext(), ot,
                                cd.get(Calendar.HOUR_OF_DAY),
                                cd.get(Calendar.MINUTE), true);
                        td.show();
                    };

                    DatePickerDialog dd = new DatePickerDialog(getContext(), dt
                            , cd.get(Calendar.YEAR)
                            , cd.get(Calendar.MONTH)
                            , cd.get(Calendar.DAY_OF_MONTH));
                    dd.show();
                }
                break;

                case R.id.ar_et_info: {
                    DlgSelectRecordType dp = new DlgSelectRecordType();
                    dp.setOldType(GlobalDef.STR_RECORD_PAY, mETInfo.getText().toString());
                    dp.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                        @Override
                        public void onDialogPositiveResult(DialogFragment dialog) {
                            DlgSelectRecordType dp_cur = UtilFun.cast_t(dialog);
                            String cur_info = dp_cur.getCurType();
                            mETInfo.setText(cur_info);
                            mETInfo.requestFocus();
                        }

                        @Override
                        public void onDialogNegativeResult(DialogFragment dialog) {
                            mETInfo.requestFocus();
                        }
                    });

                    dp.show(getFragmentManager(), "选择类型");
                }
                break;
            }

            return true;
        }

        return false;
    }
}
