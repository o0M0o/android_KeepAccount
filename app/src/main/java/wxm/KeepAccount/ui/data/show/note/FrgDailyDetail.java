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
import wxm.KeepAccount.ui.base.FrgUitlity.FrgAdvBase;
import wxm.KeepAccount.ui.base.FrgUitlity.FrgWithEventBus;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.data.edit.Note.ACNoteAdd;
import wxm.KeepAccount.ui.data.show.note.base.ValueShow;
import wxm.KeepAccount.ui.utility.AdapterNoteDetail;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.uilib.IconButton.IconButton;


/**
 * for daily detail info
 * Created by WangXM on 2017/01/20.
 */
public class FrgDailyDetail extends FrgAdvBase {
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
    RelativeLayout mRLPrv;

    @BindView(R.id.rl_next)
    RelativeLayout mRLNext;

    // 展示日统计数据的UI
    @BindView(R.id.vs_daily_info)
    ValueShow mVSDataUI;

    @BindView(R.id.login_progress)
    ProgressBar mPBLoginProgress;

    // 确认或者放弃选择
    @BindView(R.id.rl_accpet_giveup)
    RelativeLayout mRLAcceptGiveup;

    // create new data
    @BindView(R.id.ib_add)
    IconButton mIBAdd;

    // delete data
    @BindView(R.id.ib_delete)
    IconButton mIBDelete;

    // for color
    @BindColor(R.color.darkred)
    int mCLPay;

    @BindColor(R.color.darkslategrey)
    int mCLIncome;

    // for data
    private String mSZHotDay;
    private List<INote> mLSDayContents;

    @Override
    protected void initUI(Bundle bundle) {
        Bundle bd = getArguments();
        mSZHotDay = bd.getString(ACDailyDetail.K_HOTDAY);
        if (!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);
        }

        loadUI(bundle);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.vw_daily_detail;
    }

    @Override
    protected void loadUI(Bundle bundle) {
        if (UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            setVisibility(View.INVISIBLE);
            return;
        }

        String[] arr = mSZHotDay.split("-");
        if (3 != arr.length) {
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
     * handler for DB data change
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBDataChangeEvent(DBDataChangeEvent event) {
        if (!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);

            initUI(null);
        }
    }


    /**
     * process day prior/next browse
     * @param view      for button
     */
    @OnClick({R.id.rl_prv, R.id.rl_next})
    public void dayButtonClick(View view) {
        String org_day = mSZHotDay;

        int vid = view.getId();
        switch (vid) {
            case R.id.rl_prv: {
                String prv_day = NoteDataHelper.getInstance().getPrvDay(mSZHotDay);
                if (!UtilFun.StringIsNullOrEmpty(prv_day)) {
                    mSZHotDay = prv_day;

                    if (View.VISIBLE != mRLNext.getVisibility())
                        mRLNext.setVisibility(View.VISIBLE);
                } else {
                    mRLPrv.setVisibility(View.GONE);
                }
            }
            break;

            case R.id.rl_next: {
                String next_day = NoteDataHelper.getInstance().getNextDay(mSZHotDay);
                if (!UtilFun.StringIsNullOrEmpty(next_day)) {
                    mSZHotDay = next_day;

                    if (View.VISIBLE != mRLPrv.getVisibility())
                        mRLPrv.setVisibility(View.VISIBLE);
                } else {
                    mRLNext.setVisibility(View.GONE);
                }
            }
            break;
        }

        if (!UtilFun.StringIsNullOrEmpty(mSZHotDay) && !org_day.equals(mSZHotDay)) {
            HashMap<String, ArrayList<INote>> hl = NoteDataHelper.getInstance().getNotesForDay();
            mLSDayContents = hl.get(mSZHotDay);

            loadUI(null);
        }
    }


    /**
     * click on action
     * @param view      for action
     */
    @OnClick({R.id.ib_delete, R.id.ib_add, R.id.bt_giveup})
    public void dayActionClick(View view) {
        int vid = view.getId();
        switch (vid) {
            // 添加数据
            case R.id.ib_add: {
                Intent intent = new Intent(getActivity(), ACNoteAdd.class);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(GlobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA, "%s %02d:%02d"
                                , mSZHotDay
                                , cal.get(Calendar.HOUR_OF_DAY)
                                , cal.get(Calendar.MINUTE)));

                startActivityForResult(intent, 1);
            }
            break;

            // 删除数据
            case R.id.ib_delete: {
                boolean bdel = !(mIBAdd.getVisibility() == View.VISIBLE);
                if (bdel) {
                    AdapterNoteDetail ap = (AdapterNoteDetail) mLVBody.getAdapter();
                    List<INote> w_d = ap.getWantDeleteNotes();
                    if (!w_d.isEmpty()) {
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
                    } else {
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
            case R.id.bt_giveup: {
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
     * load act bars
     * @param bDelStatus    when true, in 'delete data' mode
     */
    private void loadActBars(boolean bDelStatus) {
        mRLAcceptGiveup.setVisibility(bDelStatus ? View.VISIBLE : View.GONE);
        mIBAdd.setVisibility(bDelStatus ? View.GONE : View.VISIBLE);
    }

    /**
     * load day info header
     */
    private void loadDayHeader() {
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
     * load day info
     */
    private void loadDayInfo() {
        NoteShowInfo ni = NoteDataHelper.getInfoByDay(mSZHotDay);

        String p_count;
        String i_count;
        String p_amount;
        String i_amount;
        if (null != ni) {
            p_count = String.valueOf(ni.getPayCount());
            i_count = String.valueOf(ni.getIncomeCount());
            p_amount = ni.getSZPayAmount();
            i_amount = ni.getSZIncomeAmount();
        } else {
            p_count = "0";
            i_count = "0";
            p_amount = "0.00";
            i_amount = "0.00";
        }

        HashMap<String, Object> hm = new HashMap<>();
        hm.put(ValueShow.ATTR_PAY_COUNT, p_count);
        hm.put(ValueShow.ATTR_PAY_AMOUNT, p_amount);
        hm.put(ValueShow.ATTR_INCOME_COUNT, i_count);
        hm.put(ValueShow.ATTR_INCOME_AMOUNT, i_amount);
        mVSDataUI.adjustAttribute(hm);
    }

    /**
     * load day data
     * @param bDelStatus    when true, in 'delete data' mode
     */
    private void loadDayNotes(boolean bDelStatus) {
        LinkedList<HashMap<String, INote>> c_para = new LinkedList<>();
        if (!UtilFun.ListIsNullOrEmpty(mLSDayContents)) {
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
     * set UI visibility
     * @param vis       visibility param
     */
    private void setVisibility(int vis) {
        mTVMonthDay.setVisibility(vis);
        mTVYearMonth.setVisibility(vis);
        mLVBody.setVisibility(vis);
    }
    /// PRIVATE END
}
