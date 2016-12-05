package wxm.KeepAccount.ui.fragment.utility;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import rx.Notification;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import wxm.KeepAccount.Base.retrofit.RetrofitProvider;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.CalendarListView.CalendarItemAdapter;
import wxm.KeepAccount.ui.CalendarListView.CustomCalendarItemModel;
import wxm.KeepAccount.ui.CalendarListView.DayNewsListAdapter;
import wxm.KeepAccount.ui.CalendarListView.NewsService;
import wxm.calendarlv_library.CalendarHelper;
import wxm.calendarlv_library.CalendarListView;

/**
 * for test calendar
 * Created by ookoo on 2016/12/4.
 */
public class FrgTestCalendar extends FrgUtilityBase {
    public static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy年MM月");

    // for ui
    @BindView(R.id.calendar_listview)
    CalendarListView mHGVDays;

    // for data
    private DayNewsListAdapter dayNewsListAdapter;
    private CalendarItemAdapter calendarItemAdapter;
    //key:date "yyyy-mm-dd" format.
    private TreeMap<String, List<NewsService.News.StoriesBean>> listTreeMap = new TreeMap<>();

    private Handler handler = new Handler();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgTestCalendar";
        View rootView = layoutInflater.inflate(R.layout.vw_calendar, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        dayNewsListAdapter = new DayNewsListAdapter(getActivity());
        calendarItemAdapter = new CalendarItemAdapter(getActivity());
        mHGVDays.setCalendarListViewAdapter(calendarItemAdapter, dayNewsListAdapter);

        // set start time,just for test.
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -7);
        loadNewsList(DAY_FORMAT.format(calendar.getTime()));
        actionBar.setTitle(YEAR_MONTH_FORMAT.format(calendar.getTime()));

        // deal with refresh and load more event.
        mHGVDays.setOnListPullListener(new CalendarListView.onListPullListener() {
            @Override
            public void onRefresh() {
                String date = listTreeMap.firstKey();
                Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                loadNewsList(DAY_FORMAT.format(calendar.getTime()));
            }

            @Override
            public void onLoadMore() {
                String date = listTreeMap.lastKey();
                Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                loadNewsList(DAY_FORMAT.format(calendar.getTime()));
            }
        });

        //
        mHGVDays.setOnMonthChangedListener(yearMonth -> {
            Calendar calendar1 = CalendarHelper.getCalendarByYearMonth(yearMonth);
            actionBar.setTitle(YEAR_MONTH_FORMAT.format(calendar1.getTime()));
            loadCalendarData(yearMonth);
            Toast.makeText(getActivity(), YEAR_MONTH_FORMAT.format(calendar1.getTime()), Toast.LENGTH_SHORT).show();
        });

        mHGVDays.setOnCalendarViewItemClickListener((View, selectedDate, listSection, selectedDateRegion) -> {
        });
    }

    @Override
    protected void initUiInfo() {
    }


    /**
     * this code is just for generate test date for ListView!
     * @param date  for date
     */
    private void loadNewsList(String date) {
        Calendar calendar = getCalendarByYearMonthDay(date);
        String key = CalendarHelper.YEAR_MONTH_FORMAT.format(calendar.getTime());

        // just not care about how data to create.
        Random random = new Random();
        final List<String> set = new ArrayList<>();
        while (set.size() < 5) {
            int i = random.nextInt(29);
            if (i > 0) {
                if (!set.contains(key + "-" + i)) {
                    if (i < 10) {
                        set.add(key + "-0" + i);
                    } else {
                        set.add(key + "-" + i);
                    }
                }
            }
        }

        RxAppCompatActivity r_ac = (RxAppCompatActivity)getActivity();
        Observable<Notification<NewsService.News>> newsListOb =
                RetrofitProvider.getInstance().create(NewsService.class)
                        .getNewsList(date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(r_ac.bindToLifecycle())
                        .materialize().share();

        newsListOb.filter(Notification::isOnNext)
                .map(n -> n.getValue())
                .filter(m -> !m.getStories().isEmpty())
                .flatMap(m -> Observable.from(m.getStories()))
                .doOnNext(i -> {
                    int index = random.nextInt(5);
                    String day = set.get(index);
                    if (listTreeMap.get(day) != null) {
                        List<NewsService.News.StoriesBean> list = listTreeMap.get(day);
                        list.add(i);
                    } else {
                        List<NewsService.News.StoriesBean> list = new ArrayList<NewsService.News.StoriesBean>();
                        list.add(i);
                        listTreeMap.put(day, list);
                    }

                })
                .toList()
                .subscribe((l) -> {
                    dayNewsListAdapter.setDateDataMap(listTreeMap);
                    dayNewsListAdapter.notifyDataSetChanged();
                    calendarItemAdapter.notifyDataSetChanged();
                })
        ;
    }


    // date (yyyy-MM),load data for Calendar View by date,load one month data one times.
    // generate test data for CalendarView,imitate to be a Network Requests. update "calendarItemAdapter.getDayModelList()"
    //and notifyDataSetChanged will update CalendarView.
    private void loadCalendarData(final String date) {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    handler.post(() -> {
                        Random random = new Random();
                        String cur_sel = mHGVDays.getCurrentSelectedDate();
                        if (!UtilFun.StringIsNullOrEmpty(cur_sel) && date.equals(cur_sel.substring(0, 7))) {
                            for (String d : listTreeMap.keySet()) {
                                if (date.equals(d.substring(0, 7))) {
                                    CustomCalendarItemModel customCalendarItemModel = calendarItemAdapter.getDayModelList().get(d);
                                    if (customCalendarItemModel != null) {
                                        customCalendarItemModel.setNewsCount(listTreeMap.get(d).size());
                                        customCalendarItemModel.setFav(random.nextBoolean());
                                    }

                                }
                            }
                            calendarItemAdapter.notifyDataSetChanged();

                            /*
                            listTreeMap.keySet().stream().filter(d -> date.equals(d.substring(0, 7))).forEach(d -> {
                                CustomCalendarItemModel customCalendarItemModel = calendarItemAdapter.getDayModelList().get(d);
                                if (customCalendarItemModel != null) {
                                    customCalendarItemModel.setNewsCount(listTreeMap.get(d).size());
                                    customCalendarItemModel.setFav(random.nextBoolean());
                                }

                            });
                            calendarItemAdapter.notifyDataSetChanged();
                            */
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }.start();
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
