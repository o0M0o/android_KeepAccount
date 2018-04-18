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

import butterknife.BindView;
import wxm.KeepAccount.ui.data.show.calendar.base.CalendarShowItemAdapter;
import wxm.KeepAccount.ui.data.show.calendar.base.CalendarShowItemModel;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.utility.NoteDataHelper;

import wxm.uilib.FrgCalendar.FrgCalendar;
import wxm.uilib.SimpleCalendar.CalendarListView;


/**
 * for calendar show
 * Created by WangXM on 2016/12/4.
 */
public class FrgCalendarShow extends FrgSupportBaseAdv {
    public static final SimpleDateFormat YEAR_MONTH_SIMPLE_FORMAT =
            new SimpleDateFormat("yyyy-MM", Locale.CHINA);

    // for ui
    @BindView(R.id.frg_calender_lv)
    FrgCalendar mHGVDays;

    @BindView(R.id.fl_holder)
    FrameLayout mFLHolder;

    // for data
    private CalendarShowItemAdapter mCSIAdapter;
    private FrgCalendarContent mFGContent = new FrgCalendarContent();

    private String  mSZCurrentMonth;
    private String  mSZCurrentDay;

    @Override
    protected int getLayoutID() {
        return R.layout.vw_calendar;
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void loadUI(Bundle bundle) {
        final String[] param = new String[1];
        ToolUtil.runInBackground(this.getActivity(),
                () -> {
                    HashMap<String, ArrayList<INote>> hm
                            = NoteDataHelper.getInstance().getNotesForMonth();
                    if (null != hm) {
                        param[0] = UtilFun.cast_t(hm.keySet().toArray()[0]);
                    }
                },
                () -> {
                    String fist_month = param[0];
                    if (UtilFun.StringIsNullOrEmpty(fist_month)) {
                        Activity ac = getActivity();
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
                        builder.setMessage("当前用户没有数据，请先添加数据!").setTitle("警告");
                        builder.setNegativeButton("确认", (d, w) -> ac.finish());

                        android.app.AlertDialog dlg = builder.create();
                        dlg.show();
                        return;
                    }

                    if (!UtilFun.StringIsNullOrEmpty(mSZCurrentMonth)) {
                        updateCalendar(mSZCurrentMonth);
                        return;
                    }

                    String cur_month = YEAR_MONTH_SIMPLE_FORMAT.format(Calendar.getInstance().getTime());
                    updateCalendar(cur_month);
                });
    }

    @Override
    protected void initUI(Bundle bundle) {
        if (null == bundle) {
            android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.fl_holder, mFGContent);
            ft.commit();
        }

        mCSIAdapter = new CalendarShowItemAdapter(getActivity());
        mHGVDays.setCalendarItemAdapter(mCSIAdapter);

        mHGVDays.setOnMonthChangeListener(yearMonth -> {
            Log.d(LOG_TAG, "OnMonthChangedListener, yearMonth = " + yearMonth);
            updateCalendar(yearMonth);
        });

        mHGVDays.setOnSelectedListener((View, selectedDate, pos) -> {
            Log.d(LOG_TAG, "OnCalendarViewItemClick, selectedDate = " + selectedDate);
            mSZCurrentDay = selectedDate;
            mFGContent.updateContent(selectedDate);
        });

        loadUI(bundle);
    }

    /**
     * handler for DB event
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBChangeEvent(DBDataChangeEvent event) {
        loadUI(null);
    }

    /**
     *  update calendar
     *  @param newMonth     calendar month(example : "2016-07")
     */
    private void updateCalendar(final String newMonth) {
        mSZCurrentMonth = newMonth;

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

        if(!UtilFun.StringIsNullOrEmpty(mSZCurrentDay)) {
            mFGContent.updateContent(mSZCurrentDay);
        }
    }
}
