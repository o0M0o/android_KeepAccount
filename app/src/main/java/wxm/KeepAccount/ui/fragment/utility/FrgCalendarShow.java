package wxm.KeepAccount.ui.fragment.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.DBDataChangeEvent;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.CalendarListView.CalendarShowItemAdapter;
import wxm.KeepAccount.ui.CalendarListView.CalendarShowItemModel;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.simplecalendarlvb.CalendarListView;


/**
 * for calendar show
 * Created by ookoo on 2016/12/4.
 */
public class FrgCalendarShow extends FrgUtilityBase {
    public static final SimpleDateFormat DAY_FORMAT =
                new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

    public static final SimpleDateFormat YEAR_MONTH_FORMAT =
            new SimpleDateFormat("yyyy年MM月", Locale.CHINA);

    public static final SimpleDateFormat YEAR_MONTH_SIMPLE_FORMAT =
            new SimpleDateFormat("yyyy-MM", Locale.CHINA);

    public static final SimpleDateFormat ALL_SIMPLE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    // for ui
    @BindView(R.id.calendar_listview)
    CalendarListView mHGVDays;

    @BindView(R.id.load_progress)
    ProgressBar mPBLoad;

    @BindView(R.id.fl_holder)
    FrameLayout mFLHolder;

    // for data
    private CalendarShowItemAdapter     mCSIAdapter;
    //key:date "yyyy-mm-dd" format.
    private TreeMap<String, List<INote>> mTMList = new TreeMap<>();

    private FrgCalendarContent     mFGContent = new FrgCalendarContent();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgCalendarShow";
        View rootView = layoutInflater.inflate(R.layout.vw_calendar, viewGroup, false);
        ButterKnife.bind(this, rootView);

        if(null == bundle) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.fl_holder, mFGContent);
            ft.commit();
        }

        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Log.d(LOG_TAG, "initUiComponent");

        mCSIAdapter = new CalendarShowItemAdapter(getActivity());
        mHGVDays.setCalendarListViewAdapter(mCSIAdapter);


        mHGVDays.setOnMonthChangedListener(yearMonth -> {
            Log.d(LOG_TAG, "OnMonthChangedListener, yearMonth = " + yearMonth);
            loadCalendarData(yearMonth);
        });

        mHGVDays.setOnCalendarViewItemClickListener((View, selectedDate) -> {
            Log.d(LOG_TAG, "OnCalendarViewItemClick, selectedDate = " + selectedDate);
            mFGContent.setDay(selectedDate, mTMList.get(selectedDate));
        });

        reLoadFrg();
    }

    @Override
    protected void initUiInfo() {
        Log.d(LOG_TAG, "initUiInfo");
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
        super.enterActivity();

        EventBus.getDefault().unregister(this);
    }

    /**
     * 数据库内数据变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBDataChangeEvent(DBDataChangeEvent event) {
        reLoadFrg();
    }

    /**
     * Shows the progress UI and hides the login form.
     * @param show   如果为true则显示加载数据图
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mHGVDays.setVisibility(show ? View.GONE : View.VISIBLE);
        mHGVDays.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHGVDays.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mPBLoad.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoad.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * 加载月度数据
     * @param month  加载的月份，比如"2016-07"
     */
    private void loadNotes(String month)  {
        Log.d(LOG_TAG, "loadNotes");

        mTMList.clear();
        HashMap<String, ArrayList<INote>> hm_note
                = NoteShowDataHelper.getInstance().getNotesForMonth();
        if(null != hm_note) {
            ArrayList<INote> al_notes = hm_note.get(month);
            for(INote ci : al_notes)    {
                Calendar cd = Calendar.getInstance();
                cd.setTimeInMillis(ci.getTs().getTime());
                String day = ALL_SIMPLE_FORMAT.format(cd.getTime());

                List<INote> ls_note = mTMList.get(day);
                if(null == ls_note) {
                    ls_note = new ArrayList<>();
                    mTMList.put(day, ls_note);
                }

                ls_note.add(ci);
            }
        }
    }

    /**
     * 重新加载数据
     */
    private void reLoadFrg() {
        Log.d(LOG_TAG, "reLoadFrg");

        new AsyncTask<Void, Void, Void> () {
            private String  mSZFristMonth;

            @Override
            protected void onPreExecute()   {
                super.onPreExecute();
                showProgress(true);
            }


            @Override
            protected Void doInBackground(Void... params) {
                HashMap<String, ArrayList<INote>> hm
                        = NoteShowDataHelper.getInstance().getNotesForMonth();
                if(null != hm)  {
                    mSZFristMonth = UtilFun.cast_t(hm.keySet().toArray()[0]);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                showProgress(false);

                if(UtilFun.StringIsNullOrEmpty(mSZFristMonth)) {
                    Activity ac = getActivity();
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
                    builder.setMessage("当前用户没有数据，请先添加数据!").setTitle("警告");
                    builder.setNegativeButton("确认", (d, w) -> ac.finish());

                    android.app.AlertDialog dlg = builder.create();
                    dlg.show();
                } else {
                    Calendar c_cur = Calendar.getInstance();
                    String cur_month = YEAR_MONTH_SIMPLE_FORMAT.format(c_cur.getTime());
                    String cur_sel_month = mHGVDays.getCurrentSelectedDate();
                    if(!UtilFun.StringIsNullOrEmpty(cur_sel_month))
                        cur_sel_month = cur_sel_month.substring(0, 7);


                    if(!cur_month.equals(mSZFristMonth) && !mSZFristMonth.equals(cur_sel_month))
                        mHGVDays.changeMonth(mSZFristMonth);
                    else
                        loadCalendarData(mSZFristMonth);
                }
            }
        }.execute();
    }


    /**
     *
     // date (yyyy-MM),load data for Calendar View by date,load one month data one times.
     // generate test data for CalendarView,imitate to be a Network Requests. update "mCSIAdapter.getDayModelList()"
     // and notifyDataSetChanged will update CalendarView.
     * @param date  加载的月份，比如"2016-07"
     */
    private void loadCalendarData(final String date) {
        Log.d(LOG_TAG, "loadCalendarData, date = " + date
                + ", select_date = " + mHGVDays.getCurrentSelectedDate());
        loadNotes(date);

        if(!mTMList.isEmpty()) {
            for(String ik : mTMList.keySet())   {
                List<INote> ls_n = mTMList.get(ik);
                CalendarShowItemModel calendarItemModel = mCSIAdapter.getDayModelList().get(ik);
                if (calendarItemModel != null) {
                    calendarItemModel.setRecordCount(ls_n.size());
                }
            }
        }
        mCSIAdapter.notifyDataSetChanged();

        String cur_day = mHGVDays.getCurrentSelectedDate();
        mFGContent.setDay(cur_day,
                !UtilFun.StringIsNullOrEmpty(cur_day) ? mTMList.get(cur_day) : null);
    }
}
