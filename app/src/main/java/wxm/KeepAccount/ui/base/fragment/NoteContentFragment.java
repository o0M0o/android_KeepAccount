package wxm.KeepAccount.ui.base.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
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
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.acutility.ACRecordType;

/**
 * 记录内容编辑块
 * Created by 123 on 2016/9/6.
 */
public class NoteContentFragment extends Fragment implements View.OnTouchListener  {
    private static final String TAG = "NoteContentFragment";

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";

    private String          mAction;
    private String          mNoteType;
    private PayNoteItem     mOldPayNote;
    private IncomeNoteItem  mOldIncomeNote;

    private EditText    mETInfo;
    private EditText    mETDate;
    private EditText    mETAmount;
    private EditText    mETNote;
    private Spinner     mSPBudget;

    private View    mCurView;

    public NoteContentFragment()    {
        super();
    }

    /**
     * @return a new instance of {@link LVContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static NoteContentFragment newInstance(CharSequence title, int indicatorColor,
                                                int dividerColor, Object[] para_arr) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

        NoteContentFragment fragment = new NoteContentFragment();
        fragment.setArguments(bundle);
        fragment.setIntentPara(para_arr);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        CharSequence cs = args.getCharSequence(KEY_TITLE);
        assert null != cs;

        View cur_view;
        String title = cs.toString();
        Log.i(TAG, "onCreateView, cur title = " + cs.toString());
        if(title.equals(AppGobalDef.CNSTR_RECORD_PAY))  {
            cur_view = inflater.inflate(R.layout.vw_edit_pay, container, false);
            init_view_pay(cur_view);
        }   else    {
            cur_view = inflater.inflate(R.layout.vw_edit_income, container, false);
            init_view_income(cur_view);
        }

        //if(null != mCurView)    {
        //}

        mNoteType = title;
        mCurView = cur_view;
        return cur_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(null != savedInstanceState) {
            CharSequence cs = savedInstanceState.getCharSequence(KEY_TITLE);
            if(null != cs)
                Log.i(TAG, "cur title = " + cs.toString());
        }

        /*Bundle args = getArguments();
        if (args != null) {
        }*/
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

    private void setIntentPara(Object[] para_arr)   {
        mAction         = UtilFun.cast(para_arr[0]);
        mOldPayNote     = UtilFun.cast(para_arr[1]);
        mOldIncomeNote  = UtilFun.cast(para_arr[2]);
    }

    private void init_view_pay(View vw) {
        // 填充预算数据
        mSPBudget = UtilFun.cast(vw.findViewById(R.id.ar_sp_budget));
        TextView mTVBudget = UtilFun.cast(vw.findViewById(R.id.ar_tv_budget));
        assert null != mSPBudget && null != mTVBudget;

        ArrayList<String> data_ls = new ArrayList<>();
        List<BudgetItem> bils = AppModel.getBudgetUtility().GetBudget();
        if(!ToolUtil.ListIsNullOrEmpty(bils)) {
            data_ls.add("无预算(不使用预算)");
            for (BudgetItem i : bils) {
                data_ls.add(i.getName());
            }
        }

        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_spinner_item, data_ls);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSPBudget.setAdapter(spAdapter);

        if(0 < spAdapter.getCount()) {
            mTVBudget.setVisibility(View.VISIBLE);
            mSPBudget.setVisibility(View.VISIBLE);

            mSPBudget.setSelection(0);
        } else  {
            mTVBudget.setVisibility(View.INVISIBLE);
            mSPBudget.setVisibility(View.INVISIBLE);
        }

        init_view(vw);
    }

    private void init_view_income(View vw) {
        init_view(vw);
    }

    private void init_view(View vw)     {
        mETInfo = UtilFun.cast(vw.findViewById(R.id.ar_et_info));
        mETDate = UtilFun.cast(vw.findViewById(R.id.ar_et_date));
        mETAmount = UtilFun.cast(vw.findViewById(R.id.ar_et_amount));
        mETNote = UtilFun.cast(vw.findViewById(R.id.ar_et_note));
        assert  null != mETInfo && null != mETDate && null != mETAmount && null != mETNote;

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
                if(pos >= 0) {
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
                if(s.length() > ACNoteEdit.DEF_NOTE_MAXLEN)    {
                    mETNote.setError(String.format(Locale.CHINA, "超过最大长度(%d)!", ACNoteEdit.DEF_NOTE_MAXLEN));
                    mETNote.setText(s.subSequence(0, ACNoteEdit.DEF_NOTE_MAXLEN));
                }
            }
        });

        if(mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY))   {
            String info;
            String note;
            String date;
            String amount;
            if(null != mOldPayNote)     {
                info     = mOldPayNote.getInfo();
                note     = mOldPayNote.getNote();
                date     = mOldPayNote.getTs().toString().substring(0, 10);
                amount   = mOldPayNote.getVal().toPlainString();

                BudgetItem bi = mOldPayNote.getBudget();
                if(null != bi)  {
                    String bn = bi.getName();
                    int cc = mSPBudget.getAdapter().getCount();
                    for(int i = 0; i < cc; ++i)  {
                        String bni = UtilFun.cast(mSPBudget.getAdapter().getItem(i));
                        if(bn.equals(bni))  {
                            mSPBudget.setSelection(i);
                            break;
                        }
                    }
                }
            } else  {
                info     = mOldIncomeNote.getInfo();
                note     = mOldIncomeNote.getNote();
                date     = mOldIncomeNote.getTs().toString().substring(0, 10);
                amount   = mOldIncomeNote.getVal().toPlainString();
            }

            if(!UtilFun.StringIsNullOrEmpty(date))
                mETDate.setText(date);

            if(!UtilFun.StringIsNullOrEmpty(info))
                mETInfo.setText(info);

            if(!UtilFun.StringIsNullOrEmpty(note))
                mETNote.setText(note);

            if(!UtilFun.StringIsNullOrEmpty(amount))
                mETAmount.setText(amount);
        }   else    {
            Intent it = getActivity().getIntent();
            if(null != it) {
                String ad_date = it.getStringExtra(AppGobalDef.STR_RECORD_DATE);
                if (!UtilFun.StringIsNullOrEmpty(ad_date)) {
                    mETDate.setText(ad_date);
                }
            }
        }
    }


    private void onTouchType(MotionEvent event) {
        Intent it = new Intent(getActivity(), ACRecordType.class);
        it.putExtra(AppGobalDef.STR_RECORD_TYPE,
                mNoteType.equals(AppGobalDef.CNSTR_RECORD_PAY) ?
                        AppGobalDef.STR_RECORD_PAY : AppGobalDef.STR_RECORD_INCOME);
        startActivityForResult(it, 1);
    }

    private void onTouchDate(MotionEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.date_dialog, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        builder.setView(view);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

        final EditText et_date = UtilFun.cast(mCurView.findViewById(R.id.ar_et_date));
        assert et_date != null;
        final int inType = et_date.getInputType();
        et_date.setInputType(InputType.TYPE_NULL);
        et_date.onTouchEvent(event);
        et_date.setInputType(inType);
        et_date.setSelection(et_date.getText().length());

        builder.setTitle(mNoteType.equals(AppGobalDef.STR_RECORD_PAY) ?
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);
        if(AppGobalDef.INTRET_SURE ==  resultCode)  {
            String ty = data.getStringExtra(AppGobalDef.STR_RECORD_TYPE);
            mETInfo.setText(ty);
            mETInfo.requestFocus();
        }
        else    {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }
    }
}



