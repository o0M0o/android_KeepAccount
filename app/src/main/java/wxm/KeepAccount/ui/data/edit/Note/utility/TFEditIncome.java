package wxm.KeepAccount.ui.data.edit.Note.utility;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnTouch;
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText;
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.PayIncomeDBUtility;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;
import wxm.KeepAccount.ui.dialog.DlgLongTxt;
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;

import static java.lang.String.format;

/**
 * edit income
 * Created by WangXM on2016/9/28.
 */
public class TFEditIncome extends TFEditBase {
    @BindView(R.id.ar_et_info)
    TouchEditText mETInfo;

    @BindView(R.id.ar_et_date)
    TouchEditText mETDate;

    @BindView(R.id.ar_et_amount)
    TouchEditText mETAmount;

    @BindView(R.id.tv_note)
    TouchTextView mTVNote;

    @BindString(R.string.notice_input_note)
    String mSZDefNote;

    private IncomeNoteItem mOldIncomeNote;

    @Override
    protected void initUI(Bundle bundle) {
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
    protected int getLayoutID() {
        return R.layout.page_edit_income;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void loadUI(Bundle bundle) {
        if (UtilFun.StringIsNullOrEmpty(mAction)
                || (mAction.equals(GlobalDef.STR_MODIFY) && null == mOldIncomeNote))
            return;

        if (mAction.equals(GlobalDef.STR_MODIFY)) {
            mETDate.setText(mOldIncomeNote.getTs().toString().substring(0, 16));
            mETInfo.setText(mOldIncomeNote.getInfo());

            String o_n = mOldIncomeNote.getNote();
            mTVNote.setText(UtilFun.StringIsNullOrEmpty(o_n) ? mSZDefNote : o_n);
            mTVNote.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

            mETAmount.setText(mOldIncomeNote.getValToStr());
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
        if (null != getView()) {
            IncomeNoteItem ii = new IncomeNoteItem();
            if (null != mOldIncomeNote) {
                ii.setId(mOldIncomeNote.getId());
                ii.setUsr(mOldIncomeNote.getUsr());
            }

            ii.setInfo(mETInfo.getText().toString());

            String str_val = mETAmount.getText().toString();
            ii.setAmount(UtilFun.StringIsNullOrEmpty(str_val) ? BigDecimal.ZERO : new BigDecimal(str_val));

            String tv_sz = mTVNote.getText().toString();
            ii.setNote(mSZDefNote.equals(tv_sz) ? null : tv_sz);

            String str_date = mETDate.getText().toString() + ":00";
            Timestamp tsDT;
            try {
                tsDT = ToolUtil.INSTANCE.stringToTimestamp(str_date);
            } catch (Exception ex) {
                tsDT = new Timestamp(0);
            }
            ii.setTs(tsDT);

            return ii;
        }

        return null;
    }


    /**
     * check record validity
     * @return      true if record validity
     */
    private boolean checkResult() {
        Activity ac = getActivity();
        if (null == ac)
            return false;

        if (UtilFun.StringIsNullOrEmpty(mETAmount.getText().toString())) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
            builder.setMessage("请输入收入数值!").setTitle("警告");

            android.app.AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if (UtilFun.StringIsNullOrEmpty(mETInfo.getText().toString())) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
            builder.setMessage("请输入收入信息!").setTitle("警告");

            android.app.AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if (UtilFun.StringIsNullOrEmpty(mETDate.getText().toString())) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
            builder.setMessage("请输入收入日期!").setTitle("警告");

            android.app.AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }

    /**
     * write record
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
            tsDT = ToolUtil.INSTANCE.stringToTimestamp(str_date + ":00");
        } catch (Exception ex) {
            Log.e(LOG_TAG, String.format(Locale.CHINA, "解析'%s'到日期失败", str_date));
            return false;
        }

        // set data
        IncomeNoteItem pi = new IncomeNoteItem();
        if (null != mOldIncomeNote && mAction.equals(GlobalDef.STR_MODIFY)) {
            pi.setId(mOldIncomeNote.getId());
            pi.setUsr(mOldIncomeNote.getUsr());
        }

        pi.setInfo(str_info);
        pi.setTs(tsDT);
        pi.setAmount(new BigDecimal(str_val));
        pi.setNote(str_note);

        // add/modify data
        boolean b_create = mAction.equals(GlobalDef.STR_CREATE);
        PayIncomeDBUtility uti = ContextUtil.Companion.getPayIncomeUtility();
        boolean b_ret = b_create ?
                1 == uti.addIncomeNotes(Collections.singletonList(pi))
                : uti.getIncomeDBUtility().modifyData(pi);
        if (!b_ret) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(b_create ? "创建收入数据失败!" : "更新收入数据失败")
                    .setTitle("警告");
            AlertDialog dlg = builder.create();
            dlg.show();
        }
        return b_ret;
    }

    @OnTouch({R.id.ar_et_info, R.id.ar_et_date, R.id.tv_note})
    boolean OnTouchChildView(View v, MotionEvent event) {
        switch (event.getAction())  {
            case MotionEvent.ACTION_DOWN :  {
                switch (v.getId())  {
                    case R.id.ar_et_info :  {
                        DlgSelectRecordType dp = new DlgSelectRecordType();
                        dp.setOldType(GlobalDef.STR_RECORD_INCOME, mETInfo.getText().toString());
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

                    case R.id.ar_et_date :  {
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

                    case R.id.tv_note :  {
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
                }
            }
            break;

            case MotionEvent.ACTION_UP :    {
                v.performClick();
            }
            break;
        }

        return true;
    }
}
