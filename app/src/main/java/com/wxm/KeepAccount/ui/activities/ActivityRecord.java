package com.wxm.KeepAccount.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.wxm.KeepAccount.BaseLib.AppGobalDef;
import com.wxm.KeepAccount.BaseLib.RecordItem;
import com.wxm.KeepAccount.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityRecord
        extends AppCompatActivity
        implements View.OnTouchListener, View.OnClickListener     {
    private static final String TAG = "ActivityRecord";
    private static final int MAX_NOTELEN = 200;

    private String action;
    private String record_type;
    private RecordItem old_item = null;

    private EditText et_info;
    private EditText et_date;
    private EditText et_amount;
    private EditText et_note;

    private RadioButton rb_income;
    private RadioButton rb_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_record_add);

        Intent it = getIntent();
        action = it.getStringExtra(AppGobalDef.STR_RECORD_ACTION);
        if((null == action) || (action.isEmpty()))  {
            action = AppGobalDef.STR_RECORD_ACTION_ADD;
        }

        if(action.equals(AppGobalDef.STR_RECORD_ACTION_MODIFY)) {
            old_item = it.getParcelableExtra(AppGobalDef.STR_RECORD);
        }

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_addrecord_actbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addrecord_memenu_save: {
                if(setResult(record_type)) {
                    finish();
                }
            }
            break;

            case R.id.addrecord_menu_giveup: {
                int ret_data = AppGobalDef.INTRET_GIVEUP;

                Intent data = new Intent();
                setResult(ret_data, data);

                finish();
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
        switch(v.getId())    {
            case R.id.ar_rb_income :    {
                if(!record_type.equals(AppGobalDef.CNSTR_RECORD_INCOME))  {
                    et_info.clearComposingText();
                    et_info.setText("");
                }

                record_type = AppGobalDef.CNSTR_RECORD_INCOME;

                et_info.setHint(R.string.cn_hint_income_info);
                et_date.setHint(R.string.cn_hint_income_date);
                et_amount.setHint(R.string.cn_hint_income_amount);

                rb_pay.setChecked(false);
            }
            break;

            case R.id.ar_rb_pay:    {
                if(!record_type.equals(AppGobalDef.CNSTR_RECORD_PAY))  {
                    et_info.clearComposingText();
                    et_info.setText("");
                }

                record_type = AppGobalDef.CNSTR_RECORD_PAY;

                et_info.setHint(R.string.cn_hint_pay_info);
                et_date.setHint(R.string.cn_hint_pay_date);
                et_amount.setHint(R.string.cn_hint_pay_amount);

                rb_income.setChecked(false);
            }
            break;
        }
    }


    private void initView() {
        rb_income = (RadioButton)findViewById(R.id.ar_rb_income);
        rb_pay = (RadioButton)findViewById(R.id.ar_rb_pay);
        et_info = (EditText)findViewById(R.id.ar_et_info);
        et_date = (EditText)findViewById(R.id.ar_et_date);
        et_amount = (EditText)findViewById(R.id.ar_et_amount);
        et_note = (EditText)findViewById(R.id.ar_et_note);

        rb_income.setSelected(true);
        rb_pay.setSelected(false);

        Intent it = getIntent();
        String ad_type = it.getStringExtra(AppGobalDef.STR_RECORD_TYPE);
        if(null != ad_type)     {
            record_type = ad_type;
        }
        else    {
            if(null == old_item) {
                record_type = AppGobalDef.CNSTR_RECORD_INCOME;
            }
            else    {
                record_type = old_item.record_type;
            }
        }

        if(record_type.equals(AppGobalDef.CNSTR_RECORD_INCOME)) {
            rb_income.setChecked(true);
            rb_pay.setChecked(false);

            et_info.setHint(R.string.cn_hint_income_info);
            //et_date.setHint(R.string.cn_hint_income_date);
            et_amount.setHint(R.string.cn_hint_income_amount);
        }
        else    {
            rb_income.setChecked(false);
            rb_pay.setChecked(true);

            et_info.setHint(R.string.cn_hint_pay_info);
            //et_date.setHint(R.string.cn_hint_pay_date);
            et_amount.setHint(R.string.cn_hint_pay_amount);
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
                    et_note.setError(String.format("超过最大长度(%d)!", MAX_NOTELEN));
                    et_note.setText(s.subSequence(0, MAX_NOTELEN));
                }
            }
        });

        // 填充其它预定值
        String ad_date = it.getStringExtra(AppGobalDef.STR_RECORD_DATE);
        if((null != ad_date) && (!ad_date.isEmpty())) {
            et_date.setText(ad_date);
        }

        if(null != old_item)    {
            if(!old_item.record_info.isEmpty())
                et_info.setText(old_item.record_info);

            if(!old_item.record_note.isEmpty())
                et_note.setText(old_item.record_note);

            et_date.setText(old_item.record_ts.toString().substring(0, 10));

            String oldval = old_item.record_val.toString();
            int pos = oldval.indexOf(".");
            if(pos >= 0)    {
                et_amount.setText(oldval.substring(0, pos + 3));
            }
            else    {
                et_amount.setText(oldval);
            }
        }
    }


    private void onTouchType(MotionEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(record_type.equals(AppGobalDef.CNSTR_RECORD_INCOME)) {
            View view = View.inflate(this, R.layout.incomeinfo_dialog, null);
            final EditText et_self_info = (EditText) view.findViewById(R.id.et_input_incomeinfo);
            builder.setView(view);
            builder.setTitle("输入或选择收入信息");
            builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    et_info.setText(et_self_info.getText().toString());
                    et_info.requestFocus();

                    dialog.cancel();
                }
            });

            final ArrayAdapter info_ap = ArrayAdapter.createFromResource(this,
                    R.array.incomeinfo, R.layout.incomeinfo_spinner);
            info_ap.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            Spinner sp = (Spinner) view.findViewById(R.id.sp_incomdinfo);
            sp.setAdapter(info_ap);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                           long arg3) {
                    et_self_info.setText(info_ap.getItem(arg2).toString());
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // do nothing
                    //et_self_info.setText("");
                }
            });
            sp.setVisibility(View.VISIBLE);
        }
        else    {
            View view = View.inflate(this, R.layout.payinfo_dialog, null);
            final EditText et_self_info = (EditText) view.findViewById(R.id.et_input_payinfo);
            builder.setView(view);
            builder.setTitle("输入或选择支出信息");
            builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    et_info.setText(et_self_info.getText().toString());
                    et_info.requestFocus();

                    dialog.cancel();
                }
            });

            final ArrayAdapter info_ap = ArrayAdapter.createFromResource(this,
                    R.array.payinfo, R.layout.payinfo_spinner);
            info_ap.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            Spinner sp = (Spinner) view.findViewById(R.id.sp_payinfo);
            sp.setAdapter(info_ap);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                           long arg3) {
                    et_self_info.setText(info_ap.getItem(arg2).toString());
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // do nothing
                    //et_self_info.setText("");
                }
            });
            sp.setVisibility(View.VISIBLE);
        }

        Dialog dialog = builder.create();
        dialog.show();
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
        final int inType = et_date.getInputType();
        et_date.setInputType(InputType.TYPE_NULL);
        et_date.onTouchEvent(event);
        et_date.setInputType(inType);
        et_date.setSelection(et_date.getText().length());

        builder.setTitle("选取收入日期");
        builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%d-%02d-%02d",
                        datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth()));

                et_date.setText(sb);
                et_date.requestFocus();

                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private boolean setResult(String retype)    {
        String str_val = et_amount.getText().toString();
        String str_info = et_info.getText().toString();
        String str_date = et_date.getText().toString();
        String str_note = et_note.getText().toString();

        if(str_val.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(retype.equals(AppGobalDef.CNSTR_RECORD_PAY)) {
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

        if(str_info.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(retype.equals(AppGobalDef.CNSTR_RECORD_PAY)) {
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

        if(str_date.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(retype.equals(AppGobalDef.CNSTR_RECORD_PAY)) {
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

        /* send intent */
        int ret_data = action.equals(AppGobalDef.STR_RECORD_ACTION_ADD) ?
                            AppGobalDef.INTRET_RECORD_ADD
                            : AppGobalDef.INTRET_RECORD_MODIFY;

        Intent data=new Intent();
        RecordItem ri = new RecordItem();
        ri.record_type = retype;
        ri.record_note = str_note;
        ri.record_info = str_info;
        ri.record_val = new BigDecimal(str_val);

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            ri.record_ts.setTime(df.parse(str_date).getTime());
        }
        catch(Exception ex)
        {
            Log.e(TAG, String.format("解析'%s'到日期失败", str_date));

            Date dt = new Date();
            ri.record_ts.setTime(dt.getTime());
        }

        if(null != old_item)    {
            ri._id = old_item._id;
        }

        data.putExtra(AppGobalDef.STR_RECORD, ri);
        setResult(ret_data, data);
        return true;
    }
}

