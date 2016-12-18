package wxm.KeepAccount.ui.fragment.RemindEdit;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.RemindItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;

/**
 * 编辑提醒基类
 * Created by 123 on 2016/10/9.
 */
public abstract class TFEditRemindBase extends Fragment implements View.OnTouchListener  {
    private final static String TAG = "TFEditRemindBase";

    protected Spinner         mSPRemindActiveType;

    protected EditText      mETStartDate;
    protected EditText      mETEndDate;
    protected EditText      mETName;

    protected EditText      mETAmount;
    protected BigDecimal    mBDAmount;

    protected Timestamp       mTSStartDate;
    protected Timestamp       mTSEndDate;


    private final static String[] RAT_TYPE = {
            RemindItem.RAT_AMOUNT_BELOW,
            RemindItem.RAT_AMOUNT_EXCEED
    };


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != view) {
            // init active type
            mSPRemindActiveType = UtilFun.cast(view.findViewById(R.id.sp_remind_active));
            assert null != mSPRemindActiveType;

            ArrayAdapter<String> spAdapter1 = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, RAT_TYPE);
            spAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSPRemindActiveType.setAdapter(spAdapter1);

            // init name
            mETName = UtilFun.cast(view.findViewById(R.id.et_name));
            assert null != mETName;

            // init start & end date
            mETStartDate = UtilFun.cast(view.findViewById(R.id.et_start_date));
            mETEndDate = UtilFun.cast(view.findViewById(R.id.et_end_date));
            assert null != mETStartDate && null != mETEndDate;

            mETStartDate.setOnTouchListener(this);
            mETEndDate.setOnTouchListener(this);

            // init amount
            mETAmount = UtilFun.cast(view.findViewById(R.id.et_amount));
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
        }
    }


    /**
     * 确认输入数据接口API
     * 在此API中检查输入数据合法性/完整性，并完成后续工作
     * @return  若一切正常返回true, 否则返回false
     */
    public abstract boolean onAccept();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            Activity ac = getActivity();
            int vid = v.getId();
            switch (vid) {
                case R.id.et_end_date:
                case R.id.et_start_date: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ac);
                    View view = View.inflate(ac, R.layout.dlg_date, null);
                    final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                    builder.setView(view);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    datePicker.init(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

                    if (R.id.et_start_date == vid) {
                        final int inType = mETStartDate.getInputType();
                        mETStartDate.setInputType(InputType.TYPE_NULL);
                        mETStartDate.setInputType(inType);
                        mETStartDate.setSelection(mETStartDate.getText().length());

                        builder.setTitle("选取预警开始日期");
                        builder.setPositiveButton("确  定", (dialog, which) -> {
                            mETStartDate.setText(String.format(Locale.CHINA, "%d-%02d-%02d",
                                    datePicker.getYear(),
                                    datePicker.getMonth() + 1,
                                    datePicker.getDayOfMonth()));
                            mETStartDate.requestFocus();

                            dialog.cancel();
                        });
                    } else {
                        final int inType = mETEndDate.getInputType();
                        mETEndDate.setInputType(InputType.TYPE_NULL);
                        mETEndDate.setInputType(inType);
                        mETEndDate.setSelection(mETEndDate.getText().length());

                        builder.setTitle("选取预警结束日期");
                        builder.setPositiveButton("确  定", (dialog, which) -> {
                            mETEndDate.setText(String.format(Locale.CHINA, "%d-%02d-%02d",
                                    datePicker.getYear(),
                                    datePicker.getMonth() + 1,
                                    datePicker.getDayOfMonth()));
                            mETEndDate.requestFocus();

                            dialog.cancel();
                        });
                    }

                    builder.create().show();
                }
                break;
            }
        }

        return false;
    }

    /**
     * 检查名字有效性
     * @return  若有效返回true, 否则返回false
     */
    protected boolean checkName()   {
        String name = mETName.getText().toString().trim();
        if(0 == name.length())  {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("提醒名不正确").
                    setMessage("提醒名不能为空!").
                    create();
            alertDialog.show();

            mETName.requestFocus();
            return false;
        }

        if(ContextUtil.getRemindUtility().CheckRemindName(name))   {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("提醒名不正确").
                    setMessage("提醒名已经存在!").
                    create();
            alertDialog.show();

            mETName.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * 检查起止日期合法性
     * 并保存结果
     * @return  若有效返回true, 否则返回false
     */
    protected boolean checkDate()   {
        String s_de = mETStartDate.getText().toString().trim();
        String e_de = mETEndDate.getText().toString().trim();
        if(0 == s_de.length())  {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("缺少起始日期").
                    setMessage("起始日期不能为空!").
                    create();
            alertDialog.show();

            mETStartDate.requestFocus();
            return false;
        }

        if(0 == e_de.length())  {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("缺少结束日期").
                    setMessage("结束日期不能为空!").
                    create();
            alertDialog.show();

            mETEndDate.requestFocus();
            return false;
        }


        try {
            mTSStartDate = ToolUtil.StringToTimestamp(s_de);
            mTSEndDate = ToolUtil.StringToTimestamp(e_de);
        }
        catch(Exception ex)
        {
            Log.e(TAG, String.format(Locale.CHINA, "解析'%s'或'%s'到日期失败", s_de, e_de));
            return false;
        }


        if(mTSEndDate.before(mTSStartDate))     {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("结束日期不正确").
                    setMessage("结束日期不能早于起始日期!").
                    create();
            alertDialog.show();

            mETEndDate.requestFocus();
            return false;
        }

        return true;
    }


    /**
     * 检查金额数据合法性
     * @return  合法返回true,否则返回false
     */
    protected boolean checkAmount()   {
        String val = mETAmount.getText().toString().trim();
        if(UtilFun.StringIsNullOrEmpty(val))    {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("缺少预警金额").
                    setMessage("需要输入预警金额!").
                    create();
            alertDialog.show();

            mETAmount.requestFocus();
            return false;
        }

        mBDAmount = new BigDecimal(val);
        return true;
    }

    /**
     * 检查预警条件合法性
     * @return  合法返回true,否则返回false
     */
    protected boolean checkType()   {
        String r = UtilFun.cast(mSPRemindActiveType.getSelectedItem());
        if(UtilFun.StringIsNullOrEmpty(r))  {
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setTitle("缺少预警条件").
                    setMessage("需要选择预警条件!").
                    create();
            alertDialog.show();

            mETAmount.requestFocus();
            return false;
        }

        return true;
    }
}
