package wxm.KeepAccount.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Locale;

import cn.wxm.andriodutillib.SlidingTab.SlidingTabLayout;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.base.fragment.NoteContentFragment;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;

/**
 * 编辑收支数据的视图
 * Created by 123 on 2016/9/6.
 */
public class EditNoteSlidingTabsFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "EditNoteSTFragment ";

    private static String          mAction;
    private static PayNoteItem     mPayNote;
    private static IncomeNoteItem  mIncomeNote;

    private int     mCurTabPos = AppGobalDef.INVALID_ID;
    private View    mCurView;

    protected static class ListViewPagerItem extends SamplePagerItem {
        public ListViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            super(title, indicatorColor, dividerColor);
        }

        @Override
        protected Fragment createFragment() {
            return NoteContentFragment.newInstance(mTitle, mIndicatorColor,
                            mDividerColor, new Object[]{mAction, mPayNote, mIncomeNote});
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get intent para
        Intent it = getActivity().getIntent();
        assert null != it;

        mAction = it.getStringExtra(ACNoteEdit.PARA_ACTION);
        if(UtilFun.StringIsNullOrEmpty(mAction)) {
            Log.e(TAG, "调用intent缺少'PARA_ACTION'参数");
            return ;
        }

        if(!mAction.equals(ACNoteEdit.LOAD_NOTE_ADD)
                && !mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY))   {
            Log.e(TAG, "调用intent中'PARA_ACTION'参数不正确, cur_val = " + mAction);
            return ;
        }

        if(mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY)) {
            mPayNote = it.getParcelableExtra(ACNoteEdit.PARA_NOTE_PAY);
            if(null == mPayNote)
                mIncomeNote = it.getParcelableExtra(ACNoteEdit.PARA_NOTE_INCOME);

            if(null == mIncomeNote && null == mPayNote)   {
                Log.e(TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return ;
            }
        } else  {
            mIncomeNote = null;
            mPayNote = null;
        }

        // BEGIN_INCLUDE (populate_tabs)
        if(mTabs.isEmpty()) {
            if(mAction.equals(ACNoteEdit.LOAD_NOTE_ADD)) {
                mTabs.add(new ListViewPagerItem(
                        AppGobalDef.CNSTR_RECORD_PAY, // Title
                        Color.GREEN, // Indicator color
                        Color.GRAY// Divider color
                ));

                mTabs.add(new ListViewPagerItem(
                        AppGobalDef.CNSTR_RECORD_INCOME, // Title
                        Color.GREEN, // Indicator color
                        Color.GRAY// Divider color
                ));
            }   else    {
                if(null != mPayNote)    {
                    mTabs.add(new ListViewPagerItem(
                            AppGobalDef.CNSTR_RECORD_PAY, // Title
                            Color.GREEN, // Indicator color
                            Color.GRAY// Divider color
                    ));
                } else  {
                    mTabs.add(new ListViewPagerItem(
                            AppGobalDef.CNSTR_RECORD_INCOME, // Title
                            Color.GREEN, // Indicator color
                            Color.GRAY// Divider color
                    ));
                }
            }
        }
        // END_INCLUDE (populate_tabs)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vw = inflater.inflate(R.layout.fm_main, container, false);

        SlidingTabLayout stl = UtilFun.cast(vw.findViewById(R.id.sliding_tabs));
        stl.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurTabPos = position;
                mCurView = mViewPager.getChildAt(position);
            }

            @Override
            public void onPageSelected(int position) {
                mCurTabPos = position;
                mCurView = mViewPager.getChildAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return vw;
    }


    public boolean onAccpet()    {
        return checkResult() && fillResult();
    }

    private boolean checkResult()   {
        if(AppGobalDef.INVALID_ID == mCurTabPos || null == mCurView)
            return false;

        String record_type = mTabs.get(mCurTabPos).getTitle()
                                    .toString().equals(AppGobalDef.CNSTR_RECORD_PAY) ?
                                AppGobalDef.STR_RECORD_PAY : AppGobalDef.STR_RECORD_INCOME;

        String str_val = ((EditText)mCurView.findViewById(R.id.ar_et_amount)).getText().toString();
        String str_info = ((EditText)mCurView.findViewById(R.id.ar_et_info)).getText().toString();
        String str_date = ((EditText)mCurView.findViewById(R.id.ar_et_date)).getText().toString();

        if(UtilFun.StringIsNullOrEmpty(str_val))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        String record_type = mTabs.get(mCurTabPos).getTitle()
                .toString().equals(AppGobalDef.CNSTR_RECORD_PAY) ?
                AppGobalDef.STR_RECORD_PAY : AppGobalDef.STR_RECORD_INCOME;

        String str_val = ((EditText)mCurView.findViewById(R.id.ar_et_amount)).getText().toString();
        String str_info = ((EditText)mCurView.findViewById(R.id.ar_et_info)).getText().toString();
        String str_date = ((EditText)mCurView.findViewById(R.id.ar_et_date)).getText().toString();
        String str_note = ((EditText)mCurView.findViewById(R.id.ar_et_note)).getText().toString();

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
            if(null != mPayNote) {
                pi.setId(mPayNote.getId());
                pi.setUsr(mPayNote.getUsr());
            }

            pi.setInfo(str_info);
            pi.setTs(tsDT);
            pi.setVal(new BigDecimal(str_val));
            pi.setNote(str_note);

            // set budget
            Spinner spbi = UtilFun.cast(mCurView.findViewById(R.id.ar_sp_budget));
            assert null != spbi;
            pi.setBudget(null);
            int pos = spbi.getSelectedItemPosition();
            if(View.VISIBLE == spbi.getVisibility()
                    && AdapterView.INVALID_POSITION != pos && 0 != pos) {
                BudgetItem bi = AppModel.getBudgetUtility()
                                    .GetBudgetByName((String)spbi.getSelectedItem());
                if (null != bi) {
                    pi.setBudget(bi);
                }
            }

            if(mAction.equals(ACNoteEdit.LOAD_NOTE_ADD))    {
                return 1 == AppModel.getPayIncomeUtility()
                        .AddPayNotes(Collections.singletonList(pi));
            } else  {
                return 1 == AppModel.getPayIncomeUtility()
                        .ModifyPayNotes(Collections.singletonList(pi));
            }

        } else  {
            IncomeNoteItem ii = new IncomeNoteItem();
            if(null != mIncomeNote)  {
                ii.setId(mIncomeNote.getId());
                ii.setUsr(mIncomeNote.getUsr());
            }

            ii.setInfo(str_info);
            ii.setTs(tsDT);
            ii.setVal(new BigDecimal(str_val));
            ii.setNote(str_note);

            if(mAction.equals(ACNoteEdit.LOAD_NOTE_ADD))    {
                return 1 == AppModel.getPayIncomeUtility()
                        .AddIncomeNotes(Collections.singletonList(ii));
            } else  {
                return 1 == AppModel.getPayIncomeUtility()
                        .ModifyIncomeNotes(Collections.singletonList(ii));
            }
        }
    }
}
