package com.wxm.keepaccount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

                data.putExtra(res.getString(R.string.income_type), et_type);
                data.putExtra(res.getString(R.string.income_val), et_val);

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
}
