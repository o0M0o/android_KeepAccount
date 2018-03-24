package wxm.KeepAccount.ui.data.show.calendar;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.androidutil.FrgUtility.FrgUtilityBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.utility.NoteDataHelper;

import wxm.uilib.SimpleCalendar.CalendarListView;


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

    @BindView(R.id.fl_holder)
    FrameLayout mFLHolder;

    // for data
    private CalendarShowItemAdapter mCSIAdapter;
    private FrgCalendarContent mFGContent = new FrgCalendarContent();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgCalendarShow";
        View rootView = layoutInflater.inflate(R.layout.vw_calendar, viewGroup, false);
        ButterKnife.bind(this, rootView);

        if (null == bundle) {
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
            updateCalendar(yearMonth);
        });

        mHGVDays.setOnCalendarViewItemClickListener((View, selectedDate) -> {
            Log.d(LOG_TAG, "OnCalendarViewItemClick, selectedDate = " + selectedDate);
            mFGContent.updateContent(selectedDate);
        });

        reLoadFrg();
    }

    @Override
    protected void loadUI() {
    }

    @Override
    protected void enterActivity() {
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity() {
        super.enterActivity();

        EventBus.getDefault().unregister(this);
    }

    /**
     * handler for DB event
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBChangeEvent(DBDataChangeEvent event) {
        reLoadFrg();
    }

    private void reLoadFrg() {
        Log.d(LOG_TAG, "reLoadFrg");

        Activity h = this.getActivity();
        ExecutorService tp = Executors.newCachedThreadPool();
        tp.submit(() -> {
            String mSZFristMonth = null;
            HashMap<String, ArrayList<INote>> hm
                        = NoteDataHelper.getInstance().getNotesForMonth();
            if (null != hm) {
                mSZFristMonth = UtilFun.cast_t(hm.keySet().toArray()[0]);
            }

            final String fist_month = mSZFristMonth;
            h.runOnUiThread(() -> {
                if (UtilFun.StringIsNullOrEmpty(fist_month)) {
                    Activity ac = getActivity();
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
                    builder.setMessage("当前用户没有数据，请先添加数据!").setTitle("警告");
                    builder.setNegativeButton("确认", (d, w) -> ac.finish());

                    android.app.AlertDialog dlg = builder.create();
                    dlg.show();
                } else {
                    Calendar c_cur = Calendar.getInstance();
                    String cur_month = YEAR_MONTH_SIMPLE_FORMAT.format(c_cur.getTime());
                    String cur_sel_month = mHGVDays.getSelectedDate();
                    if (!UtilFun.StringIsNullOrEmpty(cur_sel_month))
                        cur_sel_month = cur_sel_month.substring(0, 7);


                    if (!cur_month.equals(fist_month) && !fist_month.equals(cur_sel_month))
                        mHGVDays.changeMonth(fist_month);
                    else
                        updateCalendar(fist_month);
                }
            });
        });
    }


    /**
     *  update calendar
     *  @param newMonth     calendar month(example : "2016-07")
     */
    private void updateCalendar(final String newMonth) {
        Log.d(LOG_TAG, "updateCalendar, date = " + newMonth
                + ", select_date = " + mHGVDays.getSelectedDate());

        TreeMap<String, CalendarShowItemModel> tmDays = mCSIAdapter.getDayModelList();
        for(String day : tmDays.keySet()) {
            CalendarShowItemModel itModel = tmDays.get(day);
            if (itModel != null) {
                NoteShowInfo ni = NoteDataHelper.getInfoByDay(day);
                if(ni != null && !day.startsWith(newMonth))    {
                    ni = null;
                }

                itModel.setRecordCount(ni != null ? ni.getIncomeCount() + ni.getPayCount() : 0);
            }
        }
        mCSIAdapter.notifyDataSetChanged();

        String cur_day = mHGVDays.getSelectedDate();
        if(!UtilFun.StringIsNullOrEmpty(cur_day)) {
            mFGContent.updateContent(cur_day);
        }
    }
}
