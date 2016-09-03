package wxm.KeepAccount.ui.acutility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
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
import android.widget.DatePicker;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACHelp;
import wxm.KeepAccount.ui.acinterface.ACShowBudget;


/**
 * 编辑或者新建预算数据
 */
public class ACBudgetEdit extends AppCompatActivity implements View.OnTouchListener {
    private final static String TAG = "ACBudgetEdit";
    private final static int  MAX_NOTELEN = 200;

    private BudgetItem              mCurBudget;
    private TextInputEditText       mETName;
    private TextInputEditText       mETAmount;
    private TextInputEditText       mETStartDate;
    private TextInputEditText       mETEndDate;
    private TextInputEditText       mETNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_budget_edit);

        init_component();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_budget_actbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mu_budget_save: {
                if(setResult()) {
                    int ret_data = AppGobalDef.INTRET_SURE;

                    Intent data = new Intent();
                    setResult(ret_data, data);

                    finish();
                }
            }
            break;

            case R.id.mu_budget_giveup : {
                int ret_data = AppGobalDef.INTRET_GIVEUP;

                Intent data = new Intent();
                setResult(ret_data, data);

                finish();
            }
            break;

            case R.id.mu_budget_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(AppGobalDef.STR_HELP_TYPE, ACHelp.STR_HELP_BUDGET);

                startActivityForResult(intent, 1);
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private boolean setResult() {
        String name = mETName.getText().toString();
        String note = mETNote.getText().toString();
        String sdt = mETStartDate.getText().toString();
        String edt = mETEndDate.getText().toString();
        String amount = mETAmount.getText().toString();

        if(UtilFun.StringIsNullOrEmpty(name))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入预算名!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();

            mETName.requestFocus();
            return false;
        }

        List<BudgetItem> bils = AppModel.getBudgetUtility().GetBudget();
        if(null != bils)    {
            int matchid = -1;
            boolean bmatch = false;
            for(BudgetItem i : bils)    {
                if(name.equals(i.getName()))    {
                    bmatch = true;
                    matchid = i.get_id();
                    break;
                }
            }

            if(bmatch &&
                    ((null == mCurBudget)
                    || (mCurBudget.get_id() != matchid)))  {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("预算名已经存在!").setTitle("警告");

                AlertDialog dlg = builder.create();
                dlg.show();

                mETName.requestFocus();
                return false;
            }
        }


        if(UtilFun.StringIsNullOrEmpty(amount))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入预算金额!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();

            mETAmount.requestFocus();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(sdt))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入预算开始日期!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();

            mETStartDate.requestFocus();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(edt))   {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入预算结束日期!").setTitle("警告");

            AlertDialog dlg = builder.create();
            dlg.show();

            mETEndDate.requestFocus();
            return false;
        }

        BudgetItem bi = null == mCurBudget ? new BudgetItem() : mCurBudget;
        bi.setName(name);
        bi.setStartDate(new Date(ToolUtil.StringToTimestamp(sdt).getTime()));
        bi.setEndDate(new Date(ToolUtil.StringToTimestamp(edt).getTime()));
        bi.setAmount(new BigDecimal(amount));
        bi.setNote(note);

        boolean ret;
        if(null == mCurBudget)
            ret = AppModel.getBudgetUtility().AddBudget(bi);
        else
            ret = AppModel.getBudgetUtility().ModifyBudget(bi);

        return ret;
    }


    private void init_component()   {
        // for name
        mETName = UtilFun.cast(findViewById(R.id.et_budget_name));
        assert null != mETName;

        // for amount
        mETAmount = UtilFun.cast(findViewById(R.id.et_budget_amount));
        assert null != mETAmount;
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
                if(pos >= 0) {
                    int after_len = s.length() - (pos + 1);
                    if (after_len > 2) {
                        mETAmount.setError("小数点后超过两位数!");
                        mETAmount.setText(s.subSequence(0, pos + 3));
                    }
                }
            }
        });

        // for start date
        mETStartDate = UtilFun.cast(findViewById(R.id.et_budget_start_date));
        assert null != mETStartDate;
        mETStartDate.setOnTouchListener(this);

        // for end date
        mETEndDate = UtilFun.cast(findViewById(R.id.et_budget_end_date));
        assert null != mETEndDate;
        mETEndDate.setOnTouchListener(this);

        // for note
        mETNote = UtilFun.cast(findViewById(R.id.et_budget_note));
        assert null != mETNote;
        mETNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > MAX_NOTELEN)    {
                    mETNote.setError(String.format(Locale.CHINA, "超过最大长度(%d)!", MAX_NOTELEN));
                    mETNote.setText(s.subSequence(0, MAX_NOTELEN));
                }
            }
        });

        // init budget
        Intent it = getIntent();
        mCurBudget = it.getParcelableExtra(ACShowBudget.INTENT_LOAD_BUDGET);
        if(null != mCurBudget)  {
            mETName.setText(mCurBudget.getName());
            mETAmount.setText(mCurBudget.getAmount().toPlainString());

            Date sdt = mCurBudget.getStartDate();
            mETStartDate.setText(ToolUtil.DateToDateStr(sdt));

            Date edt = mCurBudget.getEndDate();
            mETEndDate.setText(ToolUtil.DateToDateStr(edt));

            mETNote.setText(mCurBudget.getNote());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int vid = v.getId();
            switch (vid)  {
                case R.id.et_budget_end_date :
                case R.id.et_budget_start_date:  {
                    onTouchDate(event, vid);
                }
                break;

                default:
                    Log.e(TAG, "不识别的view, id = " + vid);
                break;
            }
        }

        return false;
    }


    private void onTouchDate(MotionEvent event, int vid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.date_dialog, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        builder.setView(view);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

        final EditText et_date = UtilFun.cast(findViewById(vid));
        assert et_date != null;
        final int inType = et_date.getInputType();
        et_date.setInputType(InputType.TYPE_NULL);
        et_date.onTouchEvent(event);
        et_date.setInputType(inType);
        et_date.setSelection(et_date.getText().length());

        builder.setTitle("选择日期");
        builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_date.setText(String.format(Locale.CHINA, "%d年%02d月%02d日",
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
}
