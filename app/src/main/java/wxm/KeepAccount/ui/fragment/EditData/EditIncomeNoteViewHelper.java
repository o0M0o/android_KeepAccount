package wxm.KeepAccount.ui.fragment.EditData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.acutility.ACRecordType;

/**
 * 编辑收入数据视图辅助类
 * Created by 123 on 2016/9/12.
 */
public class EditIncomeNoteViewHelper implements IEditNoteViewHelper, View.OnTouchListener {
    private static final String TAG = "EditINViewHelper";

    private View            mSelfView;
    private String          mAction;
    private IncomeNoteItem  mOldIncomeNote;

    private EditText mETInfo;
    private EditText mETDate;
    private EditText mETAmount;
    private EditText mETNote;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        if (UtilFun.StringIsNullOrEmpty(mAction))
            return null;

        if (mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY) && null == mOldIncomeNote)
            return null;

        mSelfView = inflater.inflate(R.layout.vw_edit_income, container, false);
        return mSelfView;
    }

    @Override
    public View getView() {
        return mSelfView;
    }

    @Override
    public void loadView() {
        // 填充其他数据
        mETInfo = UtilFun.cast(mSelfView.findViewById(R.id.ar_et_info));
        mETDate = UtilFun.cast(mSelfView.findViewById(R.id.ar_et_date));
        mETAmount = UtilFun.cast(mSelfView.findViewById(R.id.ar_et_amount));
        mETNote = UtilFun.cast(mSelfView.findViewById(R.id.ar_et_note));
        assert null != mETInfo && null != mETDate && null != mETAmount && null != mETNote;

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

        if (mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY)) {
            String info = mOldIncomeNote.getInfo();
            String note = mOldIncomeNote.getNote();
            String date = mOldIncomeNote.getTs().toString().substring(0, 10);
            String amount = String.format(Locale.CHINA, "%.02f", mOldIncomeNote.getVal());

            if (!UtilFun.StringIsNullOrEmpty(date))
                mETDate.setText(date);

            if (!UtilFun.StringIsNullOrEmpty(info))
                mETInfo.setText(info);

            if (!UtilFun.StringIsNullOrEmpty(note))
                mETNote.setText(note);

            if (!UtilFun.StringIsNullOrEmpty(amount))
                mETAmount.setText(amount);
        } else {
            Context ct = mSelfView.getContext();
            if (ct instanceof Activity) {
                Activity ac = UtilFun.cast(ct);
                Intent it = ac.getIntent();
                if (null != it) {
                    String ad_date = it.getStringExtra(AppGobalDef.STR_RECORD_DATE);
                    if (!UtilFun.StringIsNullOrEmpty(ad_date)) {
                        mETDate.setText(ad_date);
                    }
                }
            }
        }
    }

    @Override
    public void setPara(String action, Object ii) {
        mAction         = action;
        mOldIncomeNote  = UtilFun.cast(ii);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(AppGobalDef.INTRET_SURE ==  resultCode)  {
            String ty = data.getStringExtra(AppGobalDef.STR_RECORD_TYPE);
            mETInfo.setText(ty);
            mETInfo.requestFocus();
        }
        else    {
            Log.d(TAG, String.format(Locale.CHINA, "不处理的resultCode(%d)!", resultCode));
        }
    }

    @Override
    public boolean onAccept() {
        return checkResult() && fillResult();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Activity ac = getActivity();
        if (null != ac && event.getAction() == MotionEvent.ACTION_DOWN) {
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

                    final EditText et_date = UtilFun.cast(mSelfView.findViewById(R.id.ar_et_date));
                    assert et_date != null;
                    final int inType = et_date.getInputType();
                    et_date.setInputType(InputType.TYPE_NULL);
                    et_date.onTouchEvent(event);
                    et_date.setInputType(inType);
                    et_date.setSelection(et_date.getText().length());

                    builder.setTitle("选取支出日期");
                    builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            et_date.setText(String.format(Locale.CHINA, "%d-%02d-%02d",
                                    datePicker.getYear(),
                                    datePicker.getMonth() + 1,
                                    datePicker.getDayOfMonth()));
                            et_date.requestFocus();

                            dialog.cancel();
                        }
                    });

                    builder.create().show();
                }
                break;

                case R.id.ar_et_info: {
                    Intent it = new Intent(ac, ACRecordType.class);
                    it.putExtra(AppGobalDef.STR_RECORD_TYPE, AppGobalDef.STR_RECORD_PAY);
                    ac.startActivityForResult(it, 1);
                }
                break;
            }

            return true;
        }

        return false;
    }

    private boolean checkResult()   {
        Activity ac = getActivity();
        if (null == ac)
            return false;

        if(UtilFun.StringIsNullOrEmpty(mETAmount.getText().toString()))  {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入收入数值!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(mETInfo.getText().toString()))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入收入信息!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(mETDate.getText().toString()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage("请输入收入日期!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }


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
        IncomeNoteItem pi = new IncomeNoteItem();
        if(null != mOldIncomeNote && mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY)) {
            pi.setId(mOldIncomeNote.getId());
            pi.setUsr(mOldIncomeNote.getUsr());
        }

        pi.setInfo(str_info);
        pi.setTs(tsDT);
        pi.setVal(new BigDecimal(str_val));
        pi.setNote(str_note);

        // add/modify data
        if(mAction.equals(ACNoteEdit.LOAD_NOTE_ADD))    {
            return 1 == AppModel.getPayIncomeUtility()
                    .AddIncomeNotes(Collections.singletonList(pi));
        } else  {
            return 1 == AppModel.getPayIncomeUtility()
                    .ModifyIncomeNotes(Collections.singletonList(pi));
        }
    }


    /**
     * 获得本视图所在的activity
     * @return  若存在activity则返回其值，否则返回null
     */
    private Activity getActivity()  {
        Context ct = mSelfView.getContext();
        Activity ac = null;
        if (ct instanceof Activity)
            ac = UtilFun.cast(ct);

        return ac;
    }
}
