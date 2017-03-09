package wxm.KeepAccount.ui.data.show.note;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.AdapterNoteDetail;
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.ui.data.edit.Note.ACNoteEdit;


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
    @BindView(R.id.rl_prv)
    RelativeLayout  mRLPrv;

    @BindView(R.id.rl_next)
    RelativeLayout mRLNext;

    // 展示日统计数据的UI
    @BindView(R.id.rl_daily_info)
    RelativeLayout mRLDailyInfo;

    @BindView(R.id.login_progress)
    ProgressBar mPBLoginProgress;

    // 确认或者放弃选择
    @BindView(R.id.rl_accpet_giveup)
    RelativeLayout mRLAcceptGiveup;

    // 确认或者放弃选择
    @BindView(R.id.rl_act_add)
    RelativeLayout mRLActAdd;

    // 确认或者放弃选择
    @BindView(R.id.rl_act_delete)
    RelativeLayout mRLActDelete;

    // for color
    @BindColor(R.color.darkred)
    int mCLPay;

    @BindColor(R.color.darkslategrey)
    int mCLIncome;

    // for data
    private String          mSZHotDay;
    private List<INote>     mLSDayContents;

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
    protected void loadUI() {
        Bundle bd = getArguments();
        mSZHotDay = bd.getString(ACDailyDetail.K_HOTDAY);
        if(!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);
        }

        loadContent();
    }

    @Override
    protected void enterActivity()  {
        Log.d(LOG_TAG, "in enterActivity");
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity()  {
        Log.d(LOG_TAG, "in leaveActivity");
        EventBus.getDefault().unregister(this);

        super.leaveActivity();
    }

    /**
     * 数据库内数据变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBDataChangeEvent(DBDataChangeEvent event) {
        if(!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);

            loadContent();
        }
    }


    /**
     * 处理日期前后向导工作
     * @param view  触发的按键
     */
    @OnClick({ R.id.rl_prv, R.id.rl_next })
    public void dayButtonClick(View view) {
        String org_day = mSZHotDay;

        int vid = view.getId();
        switch (vid)    {
            case R.id.rl_prv :  {
                String prv_day = NoteDataHelper.getInstance().getPrvDay(mSZHotDay);
                if(!UtilFun.StringIsNullOrEmpty(prv_day))   {
                    mSZHotDay = prv_day;

                    if(View.VISIBLE != mRLNext.getVisibility())
                        mRLNext.setVisibility(View.VISIBLE);
                }  else {
                    mRLPrv.setVisibility(View.GONE);
                }
            }
            break;

            case R.id.rl_next :  {
                String next_day = NoteDataHelper.getInstance().getNextDay(mSZHotDay);
                if(!UtilFun.StringIsNullOrEmpty(next_day))   {
                    mSZHotDay = next_day;

                    if(View.VISIBLE != mRLPrv.getVisibility())
                        mRLPrv.setVisibility(View.VISIBLE);
                }  else {
                    mRLNext.setVisibility(View.GONE);
                }
            }
            break;
        }

        if(!UtilFun.StringIsNullOrEmpty(mSZHotDay) && !org_day.equals(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);

            loadContent();
        }
    }


    /**
     * 处理动作点击
     * @param view  触发的按键
     */
    @OnClick({ R.id.rl_act_add, R.id.rl_act_delete, R.id.bt_giveup})
    public void dayActionClick(View view) {
        int vid = view.getId();
        switch (vid)    {
            // 添加数据
            case R.id.rl_act_add :  {
                Intent intent = new Intent(getActivity(), ACNoteEdit.class);
                intent.putExtra(ACNoteEdit.PARA_ACTION, GlobalDef.STR_CREATE);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(GlobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA ,"%s %02d:%02d"
                                ,mSZHotDay
                                ,cal.get(Calendar.HOUR_OF_DAY)
                                ,cal.get(Calendar.MINUTE)));

                startActivityForResult(intent, 1);
            }
            break;

            // 删除数据
            case R.id.rl_act_delete :  {
                boolean bdel = !(mRLActAdd.getVisibility() == View.VISIBLE);
                if(bdel)    {
                    AdapterNoteDetail ap = (AdapterNoteDetail)mLVBody.getAdapter();
                    List<INote> w_d = ap.getWantDeleteNotes();
                    if(!w_d.isEmpty()) {
                        ArrayList<Integer> al_i = new ArrayList<>();
                        ArrayList<Integer> al_p = new ArrayList<>();
                        for (INote it : w_d) {
                            if (it.isPayNote())
                                al_p.add(it.getId());
                            else
                                al_i.add(it.getId());
                        }

                        if (!al_i.isEmpty())
                            ContextUtil.getPayIncomeUtility().deleteIncomeNotes(al_i);

                        if (!al_p.isEmpty())
                            ContextUtil.getPayIncomeUtility().deletePayNotes(al_p);
                    }   else    {
                        loadActBars(false);
                        loadDayNotes(false);
                    }
                } else {
                    loadActBars(true);
                    loadDayNotes(true);
                }
            }
            break;

            // 取消删除数据
            case R.id.bt_giveup :  {
                loadActBars(false);
                loadDayNotes(false);
            }
            break;
        }
    }

    /// PRIVATE BEGIN

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
        loadDayNotes(false);
        loadActBars(false);
    }

    /**
     * 加载动作条
     * @param bDelStatus  若为true则处于删除数据状态
     */
    private void loadActBars(boolean bDelStatus) {
        mRLAcceptGiveup.setVisibility(bDelStatus ? View.VISIBLE : View.GONE);
        mRLActAdd.setVisibility(bDelStatus ? View.GONE : View.VISIBLE);
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
        NoteShowInfo ni = NoteDataHelper.getInfoByDay(mSZHotDay);

        String p_count;
        String i_count;
        String p_amount;
        String i_amount;
        BigDecimal bd_l;
        if(null != ni) {
            p_count = String.valueOf(ni.getPayCount());
            i_count = String.valueOf(ni.getIncomeCount());
            p_amount = ni.getSZPayAmount();
            i_amount = ni.getSZIncomeAmount();

            bd_l = ni.getBalance();
        }   else    {
            p_count = "0";
            i_count = "0";
            p_amount = "0.00";
            i_amount = "0.00";

            bd_l = BigDecimal.ZERO;
        }

        String b_amount = String.format(Locale.CHINA,
                            0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
        HelperDayNotesInfo.fillNoteInfo(mRLDailyInfo, p_count,
                        p_amount, i_count, i_amount, b_amount);
    }

    /**
     * 加载日内数据
     * @param bDelStatus  若为true则处于删除数据状态
     */
    private void loadDayNotes(boolean bDelStatus) {
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
        ap.setCanDelete(bDelStatus);
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
