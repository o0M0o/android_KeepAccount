package wxm.KeepAccount.ui.fragment.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.DBHelper.IDataChangeNotice;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.data.IncomeNoteItem;
import wxm.KeepAccount.Base.data.PayNoteItem;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.DataBase.NoteShowInfo;
import wxm.KeepAccount.ui.acutility.ACDailyDetail;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;

import static wxm.KeepAccount.Base.utility.ContextUtil.getPayIncomeUtility;

/**
 * for daily detail info
 * Created by ookoo on 2017/01/20.
 */
public class FrgDailyDetail extends FrgUtilityBase {
    // 展示时间信息的UI
    @BindView(R.id.tv_day)
    TextView mTVMonthDay;

    @BindView(R.id.tv_year_month)
    TextView mTVYearMonth;

    @BindView(R.id.tv_day_in_week)
    TextView mTVDayInWeek;

    // 展示数据的UI
    @BindView(R.id.lv_note)
    ListView mLVBody;

    // 跳转日期的UI
    @BindView(R.id.bt_prv)
    Button  mBTPrv;

    @BindView(R.id.bt_next)
    Button  mBTNext;

    // 展示日统计数据的UI
    @BindView(R.id.rl_daily_info)
    RelativeLayout mRLDailyInfo;

    @BindView(R.id.login_progress)
    ProgressBar mPBLoginProgress;

    // for color
    @BindColor(R.color.darkred)
    int mCLPay;

    @BindColor(R.color.darkslategrey)
    int mCLIncome;

    // for data
    private String          mSZHotDay;
    private List<INote>     mLSDayContents;

    // when data changed
    private IDataChangeNotice mIDCPayNotice = new IDataChangeNotice<PayNoteItem>() {
        @Override
        public void DataModifyNotice(List<PayNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataCreateNotice(List<PayNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataDeleteNotice(List<PayNoteItem> list) {
            reLoadFrg();
        }
    };


    private IDataChangeNotice mIDCIncomeNotice = new IDataChangeNotice<IncomeNoteItem>() {
        @Override
        public void DataModifyNotice(List<IncomeNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataCreateNotice(List<IncomeNoteItem> list) {
            reLoadFrg();
        }

        @Override
        public void DataDeleteNotice(List<IncomeNoteItem> list) {
            reLoadFrg();
        }
    };

    private class ATDataChange extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            NoteShowDataHelper.getInstance().refreshData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // After completing execution of given task, control will return here.
            // Hence if you want to populate UI elements with fetched data, do it here.

            if(!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
                HashMap<String, ArrayList<INote>> hl = NoteShowDataHelper.getInstance().getNotesForDay();
                mLSDayContents = hl.get(mSZHotDay);

                loadContent();
            }

            showProgress(false);
        }
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgDailyDetail";
        View rootView = layoutInflater.inflate(R.layout.vw_daily_detail, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    protected void initUiComponent(View view) {
        mSZHotDay       = null;
        mLSDayContents  = null;
    }

    @Override
    protected void initUiInfo() {
        Bundle bd = getArguments();
        mSZHotDay = bd.getString(ACDailyDetail.K_HOTDAY);
        if(!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteShowDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);
        }

        loadContent();
    }


    /**
     * 处理日期前后向导工作
     * @param view  触发的按键
     */
    @OnClick({ R.id.bt_prv, R.id.bt_next })
    public void dayButtonClick(View view) {
        String org_day = mSZHotDay;

        int vid = view.getId();
        switch (vid)    {
            case R.id.bt_prv :  {
                String prv_day = NoteShowDataHelper.getInstance().getPrvDay(mSZHotDay);
                if(!UtilFun.StringIsNullOrEmpty(prv_day))   {
                    mSZHotDay = prv_day;

                    if(!mBTNext.isClickable())
                        mBTNext.setClickable(true);
                }  else {
                    mBTPrv.setClickable(false);
                }
            }
            break;

            case R.id.bt_next :  {
                String next_day = NoteShowDataHelper.getInstance().getNextDay(mSZHotDay);
                if(!UtilFun.StringIsNullOrEmpty(next_day))   {
                    mSZHotDay = next_day;

                    if(!mBTPrv.isClickable())
                        mBTPrv.setClickable(true);
                }  else {
                    mBTNext.setClickable(false);
                }
            }
            break;
        }

        if(!UtilFun.StringIsNullOrEmpty(mSZHotDay) && !org_day.equals(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteShowDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);

            loadContent();
        }
    }


    /**
     * 处理动作点击
     * @param view  触发的按键
     */
    @OnClick({ R.id.rl_act_add, R.id.rl_act_delete})
    public void dayActionClick(View view) {
        int vid = view.getId();
        switch (vid)    {
            case R.id.rl_act_add :  {
                Intent intent = new Intent(getActivity(), ACNoteEdit.class);
                intent.putExtra(ACNoteEdit.PARA_ACTION, GlobalDef.STR_CREATE);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(GlobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA ,"%d-%02d-%02d %02d:%02d"
                                ,cal.get(Calendar.YEAR)
                                ,cal.get(Calendar.MONTH) + 1
                                ,cal.get(Calendar.DAY_OF_MONTH)
                                ,cal.get(Calendar.HOUR_OF_DAY)
                                ,cal.get(Calendar.MINUTE)));

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.rl_act_delete :  {
            }
            break;
        }
    }

    @Override
    protected void enterActivity()  {
        getPayIncomeUtility().getPayDBUtility().addDataChangeNotice(mIDCPayNotice);
        getPayIncomeUtility().getIncomeDBUtility().addDataChangeNotice(mIDCIncomeNotice);
    }

    @Override
    protected void leaveActivity()  {
        getPayIncomeUtility().getPayDBUtility().removeDataChangeNotice(mIDCPayNotice);
        getPayIncomeUtility().getIncomeDBUtility().removeDataChangeNotice(mIDCIncomeNotice);
    }

    /// PRIVATE BEGIN
    /**
     * 数据变化后重新加载数据
     */
    private void reLoadFrg()    {
        showProgress(true);
        new ATDataChange().execute();
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mPBLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoginProgress.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * 加载内容
     */
    private void loadContent()  {
        if(isDetached())
            return;

        if(UtilFun.StringIsNullOrEmpty(mSZHotDay))  {
            setVisibility(View.INVISIBLE);
            return;
        }

        String[] arr = mSZHotDay.split("-");
        if(3 != arr.length) {
            setVisibility(View.INVISIBLE);
            return;
        }

        setVisibility(View.VISIBLE);
        loadDayHeader();
        loadDayInfo();
        loadDayNotes();
    }

    /**
     * 加载日期头
     */
    private void loadDayHeader()    {
        String[] arr = mSZHotDay.split("-");
        mTVMonthDay.setText(arr[2]);
        mTVYearMonth.setText(
                String.format(Locale.CHINA, "%s年%s月", arr[0], arr[1]));

        try {
            Timestamp ts = ToolUtil.StringToTimestamp(mSZHotDay);
            mTVDayInWeek.setText(ToolUtil.getDayInWeek(ts));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载日期信息
     */
    private void loadDayInfo()    {
        HashMap<String, NoteShowInfo> hm_d = NoteShowDataHelper.getInstance().getDayInfo();
        NoteShowInfo ni = hm_d.get(mSZHotDay);

        String p_count  = String.valueOf(ni.getPayCount());
        String i_count  = String.valueOf(ni.getIncomeCount());
        String p_amount = String.format(Locale.CHINA, "%.02f", ni.getPayAmount());
        String i_amount = String.format(Locale.CHINA, "%.02f", ni.getIncomeAmount());

        BigDecimal bd_l = ni.getBalance();
        String b_amount = String.format(Locale.CHINA,
                            0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
        HelperDayNotesInfo.fillNoteInfo(mRLDailyInfo, p_count,
                        p_amount, i_count, i_amount, b_amount);
    }

    /**
     * 加载日内数据
     */
    private void loadDayNotes() {
        LinkedList<HashMap<String, INote>> c_para = new LinkedList<>();
        if(!UtilFun.ListIsNullOrEmpty(mLSDayContents)) {
            Collections.sort(mLSDayContents, (t1, t2) -> t1.getTs().compareTo(t2.getTs()));
            for (INote ci : mLSDayContents) {
                HashMap<String, INote> hm = new HashMap<>();
                hm.put(AdapterNoteDetail.K_NODE, ci);

                c_para.add(hm);
            }
        }

        AdapterNoteDetail ap = new AdapterNoteDetail(getActivity(), c_para,
                new String[]{}, new int[]{});
        mLVBody.setAdapter(ap);
        ap.notifyDataSetChanged();
    }

    /**
     * 切换UI显示状态
     * @param vis  显示参数
     */
    private void setVisibility(int vis) {
        mTVMonthDay.setVisibility(vis);
        mTVYearMonth.setVisibility(vis);
        mLVBody.setVisibility(vis);
    }
    /// PRIVATE END
}
