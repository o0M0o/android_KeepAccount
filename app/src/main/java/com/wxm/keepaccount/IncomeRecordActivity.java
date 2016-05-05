package com.wxm.keepaccount;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.Spinner;

import java.util.Calendar;

import static android.app.PendingIntent.getActivity;

public class IncomeRecordActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "IncomeRecordActivity";

    private EditText et_date;
    private EditText et_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_record);

        et_date = (EditText) findViewById(R.id.ed_income_date);
        et_info = (EditText) findViewById(R.id.et_income_type);
        et_date.setOnTouchListener(this);
        et_info.setOnTouchListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.income_actbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.incomemenu_save:
            {

                String et_val = ((EditText)this.findViewById(R.id.et_income_amount))
                                                .getText().toString();
                String et_type = ((EditText)this.findViewById(R.id.et_income_type))
                                                .getText().toString();
                String str_date = et_date.getText().toString();
                if(et_val.isEmpty())
                {
                    Log.i(TAG, "收入数值为空");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("收入数值为空,请输入收入数值!")
                            .setTitle("警告");

                    AlertDialog dlg = builder.create();
                    dlg.show();
                    return true;
                }

                if(et_type.isEmpty())
                {
                    Log.i(TAG, "收入类型为空");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("收入类型为空,请输入收入类型!")
                            .setTitle("警告");

                    AlertDialog dlg = builder.create();
                    dlg.show();
                    return true;
                }

                if(str_date.isEmpty())
                {
                    Log.i(TAG, "收入日期为空");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("收入日期为空,请输入收入日期!")
                            .setTitle("警告");

                    AlertDialog dlg = builder.create();
                    dlg.show();
                    return true;
                }

                /* send intent */
                Resources res = getResources();
                int ret_data = res.getInteger(R.integer.incomerecord_return);

                Intent data=new Intent();
                data.putExtra(res.getString(R.string.income_type), et_type);
                data.putExtra(res.getString(R.string.income_val), et_val);
                data.putExtra(res.getString(R.string.income_date), str_date);

                setResult(ret_data, data);
                finish();
            }
            return true;

            case R.id.incomemenu_giveup:
            {
                Resources res = getResources();
                int ret_data = res.getInteger(R.integer.incomerecord_giveup);

                Intent data=new Intent();
                setResult(ret_data, data);
                finish();
            }
            return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.ed_income_date) {
                onTouchDate(event);
            }
            else if(v.getId() == R.id.et_income_type)
            {
                onTouchType(event);
            }

        }

        return true;
    }


    private void onTouchType(MotionEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.incomeinfo_dialog, null);
        final EditText et_self_info = (EditText)view.findViewById(R.id.et_input_incomeinfo);
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

        Spinner sp = (Spinner)view.findViewById(R.id.sp_incomdinfo);
        sp.setAdapter(info_ap);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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
}
