package wxm.KeepAccount.ui.fragment.EditData;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.data.PayIncomeDataUtility;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.acutility.ACRecordTypeEdit;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;

/**
 * 编辑支出
 * Created by wxm on 2016/9/28.
 */
public class TFEditPay extends TFEditBase implements View.OnTouchListener {
    private final static String TAG = "TFEditPay";

    private PayNoteItem mOldPayNote;
    private View        mSelfView;

    private EditText mETInfo;
    private EditText mETDate;
    private EditText mETAmount;
    private EditText mETNote;
    private Spinner mSPBudget;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vw_edit_pay, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mSelfView = view;
            init_view(view);
        }
    }

    private void init_view(View v) {
        if (UtilFun.StringIsNullOrEmpty(mAction)
                || (AppGobalDef.STR_MODIFY.equals(mAction) && null == mOldPayNote))
            return ;

        // 填充预算数据
        mSPBudget = UtilFun.cast(v.findViewById(R.id.ar_sp_budget));
        TextView mTVBudget = UtilFun.cast(v.findViewById(R.id.ar_tv_budget));
        assert null != mSPBudget && null != mTVBudget;

        ArrayList<String> data_ls = new ArrayList<>();
        List<BudgetItem> bils = AppModel.getBudgetUtility().GetBudget();
        if (!ToolUtil.ListIsNullOrEmpty(bils)) {
            data_ls.add("无预算(不使用预算)");
            for (BudgetItem i : bils) {
                data_ls.add(i.getName());
            }
        }

        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, data_ls);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSPBudget.setAdapter(spAdapter);

        RelativeLayout rl = UtilFun.cast(v.findViewById(R.id.rl_budget));
        if (0 < spAdapter.getCount()) {
            rl.setVisibility(View.VISIBLE);
            mSPBudget.setSelection(0);
        } else {
            rl.setVisibility(View.INVISIBLE);
        }

        // 填充其他数据
        mETInfo = UtilFun.cast(v.findViewById(R.id.ar_et_info));
        mETDate = UtilFun.cast(v.findViewById(R.id.ar_et_date));
        mETAmount = UtilFun.cast(v.findViewById(R.id.ar_et_amount));
        mETNote = UtilFun.cast(v.findViewById(R.id.ar_et_note));

        mETDate.setOnTouchListener(this);
        mETInfo.setOnTouchListener(this);
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

        mETNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > ACNoteEdit.DEF_NOTE_MAXLEN) {
                    mETNote.setError(String.format(Locale.CHINA, "超过最大长度(%d)!", ACNoteEdit.DEF_NOTE_MAXLEN));
                    mETNote.setText(s.subSequence(0, ACNoteEdit.DEF_NOTE_MAXLEN));
                }
            }
        });

        if (mAction.equals(AppGobalDef.STR_MODIFY)) {
            String info = mOldPayNote.getInfo();
            String note = mOldPayNote.getNote();
            String date = mOldPayNote.getTs().toString().substring(0, 10);
            String amount = String.format(Locale.CHINA, "%.02f", mOldPayNote.getVal());

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

            if (!UtilFun.StringIsNullOrEmpty(date))
                mETDate.setText(date);

            if (!UtilFun.StringIsNullOrEmpty(info))
                mETInfo.setText(info);

            if (!UtilFun.StringIsNullOrEmpty(note))
                mETNote.setText(note);

            if (!UtilFun.StringIsNullOrEmpty(amount))
                mETAmount.setText(amount);
        } else {
            Activity ac = getActivity();
            Intent it = ac.getIntent();
            if (null != it) {
                String ad_date = it.getStringExtra(AppGobalDef.STR_RECORD_DATE);
                if (!UtilFun.StringIsNullOrEmpty(ad_date)) {
                    mETDate.setText(ad_date);
                }
            }
        }
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
        if(null != mSelfView) {
            PayNoteItem pi = new PayNoteItem();
            if(null != mOldPayNote) {
                pi.setId(mOldPayNote.getId());
                pi.setUsr(mOldPayNote.getUsr());
            }

            pi.setInfo(mETInfo.getText().toString());

            String str_val = mETAmount.getText().toString();
            pi.setVal(UtilFun.StringIsNullOrEmpty(str_val) ? BigDecimal.ZERO : new BigDecimal(str_val));
            pi.setNote(mETNote.getText().toString());

            String str_date = mETDate.getText().toString();
            Timestamp tsDT;
            try {
                tsDT = ToolUtil.StringToTimestamp(str_date);
            }
            catch(Exception ex)
            {
                tsDT = new Timestamp(0);
            }
            pi.setTs(tsDT);

            pi.setBudget(null);
            int pos = mSPBudget.getSelectedItemPosition();
            if(AdapterView.INVALID_POSITION != pos && 0 != pos) {
                BudgetItem bi = AppModel.getBudgetUtility()
                        .GetBudgetByName((String)mSPBudget.getSelectedItem());
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
     * 检查数据合法性
     * @return 若数据合法返回true, 否则返回false
     */
    private boolean checkResult()   {
        Activity ac = getActivity();
        if (null == ac)
            return false;

        if(UtilFun.StringIsNullOrEmpty(mETAmount.getText().toString()))  {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入支出数值!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(mETInfo.getText().toString()))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入支出信息!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(mETDate.getText().toString()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入支出日期!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }

    /**
     * 写入数据
     * @return  操作成功返回true, 否则返回false
     */
    private boolean fillResult()       {
        String str_val  = mETAmount.getText().toString();
        String str_info = mETInfo.getText().toString();
        String str_date = mETDate.getText().toString();
        String str_note = mETNote.getText().toString();

        Timestamp tsDT;
        try {
            tsDT = ToolUtil.StringToTimestamp(str_date);
        }
        catch(Exception ex)
        {
            Log.e(TAG, String.format(Locale.CHINA, "解析'%s'到日期失败", str_date));
            return false;
        }

        // set data
        PayNoteItem pi = new PayNoteItem();
        if(null != mOldPayNote && mAction.equals(AppGobalDef.STR_MODIFY)) {
            pi.setId(mOldPayNote.getId());
            pi.setUsr(mOldPayNote.getUsr());
        }

        pi.setInfo(str_info);
        pi.setTs(tsDT);
        pi.setVal(new BigDecimal(str_val));
        pi.setNote(str_note);

        pi.setBudget(null);
        int pos = mSPBudget.getSelectedItemPosition();
        if(AdapterView.INVALID_POSITION != pos && 0 != pos) {
            BudgetItem bi = AppModel.getBudgetUtility()
                    .GetBudgetByName((String)mSPBudget.getSelectedItem());
            if (null != bi) {
                pi.setBudget(bi);
            }
        }

        // add/modify data
        boolean b_create = mAction.equals(AppGobalDef.STR_CREATE);
        PayIncomeDataUtility uti = AppModel.getPayIncomeUtility();
        boolean b_ret =  b_create ?
                            1 == uti.AddPayNotes(Collections.singletonList(pi))
                            : 1 == uti.ModifyPayNotes(Collections.singletonList(pi));
        if(!b_ret)  {
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
        Activity ac = getActivity();
        View v_self = getView();
        if (null != v_self && event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.ar_et_date: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ac);
                    View view = View.inflate(ac, R.layout.dlg_date, null);
                    final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                    builder.setView(view);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    datePicker.init(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

                    final int inType = mETDate.getInputType();
                    mETDate.setInputType(InputType.TYPE_NULL);
                    mETDate.onTouchEvent(event);
                    mETDate.setInputType(inType);
                    mETDate.setSelection(mETDate.getText().length());

                    builder.setTitle("选取支出日期");
                    builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mETDate.setText(String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d:%02d",
                                    datePicker.getYear(),
                                    datePicker.getMonth() + 1,
                                    datePicker.getDayOfMonth(),
                                    12, 5, 30));
                            mETDate.requestFocus();

                            dialog.cancel();
                        }
                    });

                    builder.create().show();
                }
                break;

                case R.id.ar_et_info: {
                    Intent it = new Intent(ac, ACRecordTypeEdit.class);
                    it.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.STR_RECORD_PAY);
                    startActivityForResult(it, 1);
                }
                break;
            }

            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(AppGobalDef.INTRET_SURE ==  resultCode)  {
            String ty = data.getStringExtra(AppGobalDef.STR_RECORD_TYPE);
            mETInfo.setText(ty);
            mETInfo.requestFocus();
        }
        else    {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }
    }
}
