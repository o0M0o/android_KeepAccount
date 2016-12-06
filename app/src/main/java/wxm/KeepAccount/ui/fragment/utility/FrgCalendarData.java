package wxm.KeepAccount.ui.fragment.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.text.ParseException;
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
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.CalendarListView.CalendarShowItemAdapter;
import wxm.KeepAccount.ui.CalendarListView.CalendarShowItemModel;
import wxm.KeepAccount.ui.CalendarListView.NoteListAdapter;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.calendarlv_library.CalendarHelper;
import wxm.calendarlv_library.CalendarListView;

/**
 * for calendar show
 * Created by ookoo on 2016/12/4.
 */
public class FrgCalendarData extends FrgUtilityBase {
    public static final SimpleDateFormat DAY_FORMAT =
                new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

    public static final SimpleDateFormat YEAR_MONTH_FORMAT =
            new SimpleDateFormat("yyyy年MM月", Locale.CHINA);

    public static final SimpleDateFormat YEAR_MONTH_SIMPLE_FORMAT =
            new SimpleDateFormat("yyyy-MM", Locale.CHINA);

    // for ui
    @BindView(R.id.calendar_listview)
    CalendarListView mHGVDays;

    @BindView(R.id.load_progress)
    ProgressBar mPBLoad;

    // for data
    private NoteListAdapter             mNLAdapter;
    private CalendarShowItemAdapter     mCSIAdapter;
    //key:date "yyyy-mm-dd" format.
    private TreeMap<String, List<INote>> mTMList = new TreeMap<>();

    //private Handler handler = new Handler();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgCalendarData";
        View rootView = layoutInflater.inflate(R.layout.vw_calendar, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        mNLAdapter = new NoteListAdapter(getActivity());
        mCSIAdapter = new CalendarShowItemAdapter(getActivity());
        mHGVDays.setCalendarListViewAdapter(mCSIAdapter, mNLAdapter);

        // set start time,just for test.
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;

        final Calendar calendar = Calendar.getInstance();
        actionBar.setTitle(YEAR_MONTH_FORMAT.format(calendar.getTime()));

        // deal with refresh and load more event.
        mHGVDays.setOnListPullListener(new CalendarListView.onListPullListener() {
            @Override
            public void onRefresh() {
                Log.d(LOG_TAG, "onListPullListener:onRefresh");

                //String date = mTMList.firstKey();
                //Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
                //calendar.add(Calendar.MONTH, -1);
                //calendar.set(Calendar.DAY_OF_MONTH, 1);
            }

            @Override
            public void onLoadMore() {
                Log.d(LOG_TAG, "onListPullListener:onLoadMore");

                //String date = mTMList.lastKey();
                //Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
                //calendar.add(Calendar.MONTH, 1);
                //calendar.set(Calendar.DAY_OF_MONTH, 1);
            }
        });

        mHGVDays.setOnMonthChangedListener(yearMonth -> {
            Log.d(LOG_TAG, "OnMonthChangedListener, yearMonth = " + yearMonth);

            Calendar calendar1 = CalendarHelper.getCalendarByYearMonth(yearMonth);
            actionBar.setTitle(YEAR_MONTH_FORMAT.format(calendar1.getTime()));

            //loadNotes(DAY_FORMAT.format(calendar1.getTime()));
            loadCalendarData(yearMonth);
        });

        mHGVDays.setOnCalendarViewItemClickListener((View, selectedDate, listSection, selectedDateRegion) -> {
        });

        // load data
        showProgress(true);
        new AsyncTask<Void, Void, Void> () {
            @Override
            protected Void doInBackground(Void... params) {
                NoteShowDataHelper.getInstance().refreshData();
                loadNotes();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                showProgress(false);

                mNLAdapter.setDateDataMap(mTMList);
                mNLAdapter.notifyDataSetChanged();
                mCSIAdapter.notifyDataSetChanged();

                //loadCalendarData(YEAR_MONTH_SIMPLE_FORMAT.format(calendar.getTime()));
            }
        }.execute();
    }

    @Override
    protected void initUiInfo() {
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


    private void loadNotes()  {
        Log.d(LOG_TAG, "loadNotes");

        mTMList.clear();
        HashMap<String, ArrayList<INote>> hm_note = NoteShowDataHelper.getInstance().getNotesForDay();
        for(String day : hm_note.keySet())  {
            List<INote> ls_note = hm_note.get(day);
            if(!UtilFun.ListIsNullOrEmpty(ls_note)) {
                for (INote i : ls_note) {
                    if (mTMList.get(day) != null) {
                        List<INote> list = mTMList.get(day);
                        list.add(i);
                    } else {
                        List<INote> list = new ArrayList<>();
                        list.add(i);
                        mTMList.put(day, list);
                    }
                }
            }
        }
    }

    // date (yyyy-MM),load data for Calendar View by date,load one month data one times.
    // generate test data for CalendarView,imitate to be a Network Requests. update "mCSIAdapter.getDayModelList()"
    //and notifyDataSetChanged will update CalendarView.
    private void loadCalendarData(final String date) {
        Log.d(LOG_TAG, "loadCalendarData, date = " + date);

        for(int d = 1; d < 32; d++) {
            String day = date + (d > 9 ? "-" + d : "-0" + d);
            List<INote> ls_n = mTMList.get(day);
            if(!UtilFun.ListIsNullOrEmpty(ls_n))    {
                CalendarShowItemModel calendarItemModel = mCSIAdapter.getDayModelList().get(day);
                if (calendarItemModel != null) {
                    calendarItemModel.setRecordCount(ls_n.size());
                }
            }
        }

        mCSIAdapter.notifyDataSetChanged();
    }

    public static Calendar getCalendarByYearMonthDay(String yearMonthDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(DAY_FORMAT.parse(yearMonthDay).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
