package wxm.KeepAccount.ui.fragment.EditData;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.EditText;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Locale;

import cn.wxm.andriodutillib.Dialog.DlgDatePicker;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayIncomeDBUtility;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;

/**
 * 编辑收入
 * Created by wxm on 2016/9/28.
 */
public class TFEditIncome extends TFEditBase implements View.OnTouchListener {
    private final static String TAG = "TFEditIncome";

    private IncomeNoteItem mOldIncomeNote;
    private View        mSelfView;

    private EditText mETInfo;
    private EditText mETDate;
    private EditText mETAmount;
    private EditText mETNote;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vw_edit_income, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            if (UtilFun.StringIsNullOrEmpty(mAction)
                    || (mAction.equals(GlobalDef.STR_MODIFY) && null == mOldIncomeNote))
                return ;

            mSelfView = view;
            init_compent(view);
            init_view(view);
        }
    }

    private void init_compent(View v) {
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
                    mETNote.setError(String.format(Locale.CHINA,
                            "超过最大长度(%d)!", ACNoteEdit.DEF_NOTE_MAXLEN));
                    mETNote.setText(s.subSequence(0, ACNoteEdit.DEF_NOTE_MAXLEN));
                }
            }
        });
    }

    private void init_view(View v) {
        if (mAction.equals(GlobalDef.STR_MODIFY)) {
            mETDate.setText(mOldIncomeNote.getTs().toString().substring(0, 16));
            mETInfo.setText(mOldIncomeNote.getInfo());
            mETNote.setText(mOldIncomeNote.getNote());
            mETAmount.setText(String.format(Locale.CHINA, "%.02f", mOldIncomeNote.getVal()));
        } else {
            Activity ac = getActivity();
            Intent it = ac.getIntent();
            if (null != it) {
                String ad_date = it.getStringExtra(GlobalDef.STR_RECORD_DATE);
                if (!UtilFun.StringIsNullOrEmpty(ad_date)) {
                    mETDate.setText(ad_date);
                }
            }
        }
    }


    @Override
    public void setCurData(String action, Object obj) {
        mAction = action;
        mOldIncomeNote = UtilFun.cast(obj);
    }

    @Override
    public boolean onAccept() {
        return checkResult() && fillResult();
    }

    @Override
    public Object getCurData() {
        if(null != mSelfView) {
            IncomeNoteItem ii = new IncomeNoteItem();
            if(null != mOldIncomeNote) {
                ii.setId(mOldIncomeNote.getId());
                ii.setUsr(mOldIncomeNote.getUsr());
            }

            ii.setInfo(mETInfo.getText().toString());

            String str_val = mETAmount.getText().toString();
            ii.setVal(UtilFun.StringIsNullOrEmpty(str_val) ? BigDecimal.ZERO : new BigDecimal(str_val));
            ii.setNote(mETNote.getText().toString());

            String str_date = mETDate.getText().toString();
            Timestamp tsDT;
            try {
                tsDT = ToolUtil.StringToTimestamp(str_date);
            } catch(Exception ex) {
                tsDT = new Timestamp(0);
            }
            ii.setTs(tsDT);

            return ii;
        }

        return null;
    }

    @Override
    public void reLoadView() {
        if(null != mSelfView)
            init_view(mSelfView);
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
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
            builder.setMessage("请输入收入数值!").setTitle("警告");

            android.app.AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(mETInfo.getText().toString()))   {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
            builder.setMessage("请输入收入信息!").setTitle("警告");

            android.app.AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(mETDate.getText().toString()))
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
            builder.setMessage("请输入收入日期!").setTitle("警告");

            android.app.AlertDialog dlg = builder.create();
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
            tsDT = ToolUtil.StringToTimestamp(str_date + ":00");
        }
        catch(Exception ex)
        {
            Log.e(TAG, String.format(Locale.CHINA, "解析'%s'到日期失败", str_date));
            return false;
        }

        // set data
        IncomeNoteItem pi = new IncomeNoteItem();
        if(null != mOldIncomeNote && mAction.equals(GlobalDef.STR_MODIFY)) {
            pi.setId(mOldIncomeNote.getId());
            pi.setUsr(mOldIncomeNote.getUsr());
        }

        pi.setInfo(str_info);
        pi.setTs(tsDT);
        pi.setVal(new BigDecimal(str_val));
        pi.setNote(str_note);

        // add/modify data
        boolean b_create = mAction.equals(GlobalDef.STR_CREATE);
        PayIncomeDBUtility uti = ContextUtil.getPayIncomeUtility();
        boolean b_ret =  b_create ?
                            1 == uti.addIncomeNotes(Collections.singletonList(pi))
                            : uti.getIncomeDBUtility().modifyData(pi);
        if(!b_ret)  {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(b_create ? "创建收入数据失败!" : "更新收入数据失败")
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
                case R.id.ar_et_date: {
                    DlgDatePicker dp = new DlgDatePicker();
                    dp.setInitDate(mETDate.getText().toString());
                    dp.setDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                        @Override
                        public void onDialogPositiveResult(DialogFragment dialog) {
                            DlgDatePicker cur_dp = UtilFun.cast_t(dialog);
                            String cur_date = cur_dp.getCurDate();

                            if(!UtilFun.StringIsNullOrEmpty(cur_date))
                                mETDate.setText(cur_date);

                            mETDate.requestFocus();
                        }

                        @Override
                        public void onDialogNegativeResult(DialogFragment dialog) {
                            mETDate.requestFocus();
                        }
                    });

                    dp.show(getFragmentManager(), "选择日期");
                }
                break;

                case R.id.ar_et_info: {
                    DlgSelectRecordType dp = new DlgSelectRecordType();
                    dp.setOldType(GlobalDef.STR_RECORD_INCOME, mETInfo.getText().toString());
                    dp.setDialogListener(new DlgOKOrNOBase.DialogResultListener() {
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
