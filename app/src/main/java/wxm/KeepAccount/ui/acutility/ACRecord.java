package wxm.KeepAccount.ui.acutility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACHelp;

public class ACRecord
        extends AppCompatActivity
        implements View.OnTouchListener, View.OnClickListener     {
    public static final String  PARA_ACTION          = "para_action";
    public static final String  PARA_NOTE_PAY        = "note_pay";
    public static final String  PARA_NOTE_INCOME     = "note_income";

    public static final String  LOAD_NOTE_ADD        = "note_add";
    public static final String  LOAD_NOTE_MODIFY     = "note_modify";

    private static final String TAG = "ACRecord";
    private static final int MAX_NOTELEN = 200;

    private String action;
    private String record_type;
    private PayNoteItem     mOLDPayNote;
    private IncomeNoteItem  mOLDIncomeNote;


    private EditText et_info;
    private EditText et_date;
    private EditText et_amount;
    private EditText et_note;

    private RadioButton rb_income;
    private RadioButton rb_pay;
    private Spinner     mSPBudget;
    private TextView    mTVBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_record_edit);

        Intent it = getIntent();
        action = it.getStringExtra(PARA_ACTION);
        if(UtilFun.StringIsNullOrEmpty(action)) {
            Log.e(TAG, "调用intent缺少'PARA_ACTION'参数");
            assert false;
            finish();
        }

        if(!action.equals(LOAD_NOTE_ADD) && !action.equals(LOAD_NOTE_MODIFY))   {
            Log.e(TAG, "调用intent中'PARA_ACTION'参数不正确, cur_val = " + action);
            assert false;
            finish();
        }

        if(action.equals(LOAD_NOTE_MODIFY)) {
            mOLDPayNote = it.getParcelableExtra(PARA_NOTE_PAY);

            if(null == mOLDPayNote)
                mOLDIncomeNote = it.getParcelableExtra(PARA_NOTE_INCOME);

            assert null != mOLDIncomeNote || null != mOLDPayNote;
            if(null == mOLDIncomeNote && null == mOLDPayNote)   {
                Log.e(TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                finish();
            }
        }

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_record_actbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.record_memenu_save: {
                if(checkResult() && fillResult()) {
                    Intent data=new Intent();
                    setResult(action.equals(ACRecord.LOAD_NOTE_ADD) ?
                                    AppGobalDef.INTRET_RECORD_ADD
                                    : AppGobalDef.INTRET_RECORD_MODIFY, data);
                    finish();
                }
            }
            break;

            case R.id.record_menu_giveup: {
                int ret_data = AppGobalDef.INTRET_GIVEUP;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            case R.id.recordmenu_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId())  {
                case R.id.ar_et_date :  {
                    onTouchDate(event);
                }
                break;

                case R.id.ar_et_info :  {
                    onTouchType(event);
                }
                break;
            }
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if(action.equals(LOAD_NOTE_MODIFY)
                && (R.id.ar_rb_income == vid || R.id.ar_rb_pay == vid)) {
            if(record_type.equals(AppGobalDef.STR_RECORD_INCOME)) {
                rb_income.setChecked(true);
                rb_pay.setChecked(false);
            }
            else    {
                rb_income.setChecked(false);
                rb_pay.setChecked(true);
            }
        } else  {
            switch(vid)    {
                case R.id.ar_rb_income :    {
                    rb_pay.setChecked(false);
                    if(!record_type.equals(AppGobalDef.STR_RECORD_INCOME))  {
                        et_info.clearComposingText();
                        et_info.setText("");
                    }

                    mTVBudget.setVisibility(View.INVISIBLE);
                    mSPBudget.setVisibility(View.INVISIBLE);

                    record_type = AppGobalDef.STR_RECORD_INCOME;
                }
                break;

                case R.id.ar_rb_pay:    {
                    rb_income.setChecked(false);
                    if(!record_type.equals(AppGobalDef.STR_RECORD_PAY))  {
                        et_info.clearComposingText();
                        et_info.setText("");
                    }

                    if(0 < mSPBudget.getChildCount()) {
                        mTVBudget.setVisibility(View.VISIBLE);
                        mSPBudget.setVisibility(View.VISIBLE);
                    }

                    record_type = AppGobalDef.STR_RECORD_PAY;
                }
                break;

                default:
                    Log.e(TAG, "not process onclick on view(view id = " + vid + ")");
                    break;
            }
        }
    }


    private void initView() {
        rb_income = (RadioButton)findViewById(R.id.ar_rb_income);
        rb_pay = (RadioButton)findViewById(R.id.ar_rb_pay);
        et_info = (EditText)findViewById(R.id.ar_et_info);
        et_date = (EditText)findViewById(R.id.ar_et_date);
        et_amount = (EditText)findViewById(R.id.ar_et_amount);
        et_note = (EditText)findViewById(R.id.ar_et_note);
        mSPBudget = UtilFun.cast(findViewById(R.id.ar_sp_budget));
        mTVBudget = UtilFun.cast(findViewById(R.id.ar_tv_budget));
        assert null != mSPBudget && null != rb_income && null != rb_pay && null != et_info
                && null != et_date && null != et_amount && null != et_note && null != mTVBudget;

        rb_income.setSelected(true);
        rb_pay.setSelected(false);

        if(action.equals(LOAD_NOTE_ADD))    {
            record_type = AppGobalDef.STR_RECORD_PAY;
        }   else    {
            record_type = (null != mOLDPayNote) ? AppGobalDef.STR_RECORD_PAY
                                : AppGobalDef.STR_RECORD_INCOME;
        }

        if(record_type.equals(AppGobalDef.STR_RECORD_INCOME)) {
            rb_income.setChecked(true);
            rb_pay.setChecked(false);
        }
        else    {
            rb_income.setChecked(false);
            rb_pay.setChecked(true);
        }

        et_date.setOnTouchListener(this);
        et_info.setOnTouchListener(this);
        rb_income.setOnClickListener(this);
        rb_pay.setOnClickListener(this);

        et_amount.addTextChangedListener(new TextWatcher() {
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
                        et_amount.setError("小数点后超过两位数!");
                        et_amount.setText(s.subSequence(0, pos + 3));
                    }
                }
            }
        });

        et_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > MAX_NOTELEN)    {
                    et_note.setError(String.format(Locale.CHINA, "超过最大长度(%d)!", MAX_NOTELEN));
                    et_note.setText(s.subSequence(0, MAX_NOTELEN));
                }
            }
        });


        if(action.equals(LOAD_NOTE_MODIFY))    {
            String info;
            String note;
            String date;
            String amount;
            if(null != mOLDPayNote) {
                info = mOLDPayNote.getInfo();
                note = mOLDPayNote.getNote();
                date = mOLDPayNote.getTs().toString().substring(0, 10);
                amount = mOLDPayNote.getVal().toPlainString();
            } else  {
                info = mOLDIncomeNote.getInfo();
                note = mOLDIncomeNote.getNote();
                date = mOLDIncomeNote.getTs().toString().substring(0, 10);
                amount = mOLDIncomeNote.getVal().toPlainString();
            }

            if(!UtilFun.StringIsNullOrEmpty(date))
                et_date.setText(date);

            if(!UtilFun.StringIsNullOrEmpty(info))
                et_info.setText(info);

            if(!UtilFun.StringIsNullOrEmpty(note))
                et_note.setText(note);

            if(!UtilFun.StringIsNullOrEmpty(amount))
                et_amount.setText(amount);

            /*
            String oldval = old_item.getVal().toString();
            int pos = oldval.indexOf(".");
            if (pos >= 0) {
                et_amount.setText(oldval.substring(0, pos + 3));
            } else {
                et_amount.setText(oldval);
            }
            */
        }   else    {
            Intent it = getIntent();
            String ad_date = it.getStringExtra(AppGobalDef.STR_RECORD_DATE);
            if(!UtilFun.StringIsNullOrEmpty(ad_date)) {
                et_date.setText(ad_date);
            }
        }

        // 填充预算数据
        ArrayList<String> data_ls = new ArrayList<>();
        List<BudgetItem> bils = AppModel.getBudgetUtility().GetBudget();
        if(!ToolUtil.ListIsNullOrEmpty(bils)) {
            for (BudgetItem i : bils) {
                data_ls.add(i.getName());
            }
        }

        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, data_ls);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSPBudget.setAdapter(spAdapter);

        if(record_type.equals(AppGobalDef.STR_RECORD_PAY)
                && (0 < mSPBudget.getChildCount())) {
            mTVBudget.setVisibility(View.VISIBLE);
            mSPBudget.setVisibility(View.VISIBLE);
        } else  {
            mTVBudget.setVisibility(View.INVISIBLE);
            mSPBudget.setVisibility(View.INVISIBLE);
        }
    }


    private void onTouchType(MotionEvent event) {
        Intent it = new Intent(this, ACRecordTypeEdit.class);
        it.putExtra(AppGobalDef.STR_RECORD_TYPE, record_type);
        startActivityForResult(it, 1);
    }

    private void onTouchDate(MotionEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.date_dialog, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        builder.setView(view);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

        final EditText et_date = (EditText)findViewById(R.id.ar_et_date);
        assert et_date != null;
        final int inType = et_date.getInputType();
        et_date.setInputType(InputType.TYPE_NULL);
        et_date.onTouchEvent(event);
        et_date.setInputType(inType);
        et_date.setSelection(et_date.getText().length());

        builder.setTitle(record_type.equals(AppGobalDef.STR_RECORD_PAY) ?
                            "选取支出日期" : "选取收入日期");
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

        Dialog dialog = builder.create();
        dialog.show();
    }


    private boolean checkResult()   {
        String str_val = et_amount.getText().toString();
        String str_info = et_info.getText().toString();
        String str_date = et_date.getText().toString();

        if(UtilFun.StringIsNullOrEmpty(str_val))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(record_type.equals(AppGobalDef.STR_RECORD_PAY)) {
                Log.i(TAG, "支出数值为空");
                builder.setMessage("请输入支出数值!").setTitle("警告");
            }
            else    {
                Log.i(TAG, "收入数值为空");
                builder.setMessage("请输入收入数值!").setTitle("警告");
            }

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(str_info))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(record_type.equals(AppGobalDef.STR_RECORD_PAY)) {
                Log.i(TAG, "支出信息为空");
                builder.setMessage("请输入支出信息!").setTitle("警告");
            }
            else    {
                Log.i(TAG, "收入信息为空");
                builder.setMessage("请输入收入信息!").setTitle("警告");
            }

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(str_date))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(record_type.equals(AppGobalDef.STR_RECORD_PAY)) {
                Log.i(TAG, "支出日期为空");
                builder.setMessage("请输入支出日期!").setTitle("警告");
            }
            else    {
                Log.i(TAG, "收入日期为空");
                builder.setMessage("请输入收入日期!").setTitle("警告");
            }

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }


    private boolean fillResult()       {
        String str_val = et_amount.getText().toString();
        String str_info = et_info.getText().toString();
        String str_date = et_date.getText().toString();
        String str_note = et_note.getText().toString();

        Timestamp tsDT;
        try {
            tsDT = ToolUtil.StringToTimestamp(str_date);
        }
        catch(Exception ex)
        {
            Log.e(TAG, String.format(Locale.CHINA
                            ,"解析'%s'到日期失败" ,str_date));
            return false;
        }

        if(record_type.equals(AppGobalDef.STR_RECORD_PAY))  {
            PayNoteItem pi = new PayNoteItem();
            if(null != mOLDPayNote) {
                pi.setId(mOLDPayNote.getId());
                pi.setUsr(mOLDPayNote.getUsr());
            }

            pi.setInfo(str_info);
            pi.setTs(tsDT);
            pi.setVal(new BigDecimal(str_val));
            pi.setNote(str_note);

            if(action.equals(LOAD_NOTE_ADD))    {
                return 1 == AppModel.getPayIncomeUtility()
                                .AddPayNotes(Collections.singletonList(pi));
            } else  {
                return 1 == AppModel.getPayIncomeUtility()
                                .ModifyPayNotes(Collections.singletonList(pi));
            }

        } else  {
            IncomeNoteItem ii = new IncomeNoteItem();
            if(null != mOLDIncomeNote)  {
                ii.setId(mOLDIncomeNote.getId());
                ii.setUsr(mOLDIncomeNote.getUsr());
            }

            ii.setInfo(str_info);
            ii.setTs(tsDT);
            ii.setVal(new BigDecimal(str_val));
            ii.setNote(str_note);

            if(action.equals(LOAD_NOTE_ADD))    {
                return 1 == AppModel.getPayIncomeUtility()
                        .AddIncomeNotes(Collections.singletonList(ii));
            } else  {
                return 1 == AppModel.getPayIncomeUtility()
                        .ModifyIncomeNotes(Collections.singletonList(ii));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);

        if(AppGobalDef.INTRET_SURE ==  resultCode)  {
            String ty = data.getStringExtra(AppGobalDef.STR_RECORD_TYPE);
            et_info.setText(ty);
            et_info.requestFocus();
        }
        else    {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

    }
}


