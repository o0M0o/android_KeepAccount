package com.wxm.keepaccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.app.PendingIntent.getActivity;

public class IncomeRecordActivity extends AppCompatActivity {
    private static final String TAG = "IncomeRecordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_record);
    }


    /*
       确认输入数据
    */
    public void onClickOk(View view)    {
        Resources res = getResources();
        int ret_data = res.getInteger(R.integer.incomerecord_return);

        Intent data=new Intent();
        String et_val = ((EditText)this.findViewById(R.id.et_income_amount)).getText().toString();
        String et_type = ((EditText)this.findViewById(R.id.et_income_type)).getText().toString();
        if(et_val.isEmpty())
        {
            Log.i(TAG, "收入数值为空");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("收入数值为空,请输入收入数值!")
                    .setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return;
        }

        if(et_type.isEmpty())
        {
            Log.i(TAG, "收入类型为空");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("收入类型为空,请输入收入类型!")
                    .setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();
            return;
        }

        data.putExtra(res.getString(R.string.income_type), et_type);
        data.putExtra(res.getString(R.string.income_val), et_val);

        setResult(ret_data, data);
        finish();
    }
}
