package wxm.KeepAccount.ui.utility;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * Data helper for NoteShow
 * Created by wxm on 2016/5/30.
 */
public class NoteDataHelper {
    // 定义调用参数
    public static final String INTENT_PARA_FIRST_TAB = "first_tab";
    // 定义tab页标签内容
    public static final String TAB_TITLE_DAILY = "日流水";
    public static final String TAB_TITLE_MONTHLY = "月流水";
    public static final String TAB_TITLE_YEARLY = "年流水";
    public static final String TAB_TITLE_BUDGET = "预算";

    // use singleton
    private static NoteDataHelper instance = new NoteDataHelper();
    /**
     *  example :
     * '2016-10-24' ---- data   (日数据）
     * '2016-10'    ---- data   (月数据)
     * '2016'       ---- data   (年数据)
     */
    private HashMap<String, NoteShowInfo> mHMDayInfo;
    private HashMap<String, NoteShowInfo> mHMMonthInfo;
    private HashMap<String, NoteShowInfo> mHMYearInfo;
    private HashMap<String, ArrayList<INote>> mHMDayNotes;
    private HashMap<String, ArrayList<INote>> mHMMonthNotes;
    private HashMap<String, ArrayList<INote>> mHMYearNotes;
    private ArrayList<String> mALOrderedDays;

    private NoteDataHelper() {
        mHMDayInfo = new HashMap<>();
        mHMMonthInfo = new HashMap<>();
        mHMYearInfo = new HashMap<>();
    }

    public static NoteDataHelper getInstance() {
        return instance;
    }

    /**
     * get data for month
     * @param mt    month（example : '2017-01')
     * @return      data
     */
    public static NoteShowInfo getInfoByMonth(String mt) {
        return getInstance().mHMMonthInfo.get(mt);
    }

    /**
     * get month that have records
     * @return  month
     */
    public static List<String> getNotesMonths() {
        LinkedList<String> ls_sz = new LinkedList<>();
        ls_sz.addAll(getInstance().mHMMonthInfo.keySet());
        return ls_sz;
    }

    /**
     * get data for year
     * @param yr    year(example : '2017')
     * @return      data
     */
    public static NoteShowInfo getInfoByYear(String yr) {
        return getInstance().mHMYearInfo.get(yr);
    }

    /**
     * get year that have records
     * @return      year
     */
    public static List<String> getNotesYears() {
        LinkedList<String> ls_sz = new LinkedList<>();
        ls_sz.addAll(getInstance().mHMYearInfo.keySet());
        return ls_sz;
    }

    /**
     * get data for year
     * @param day   day（example : '2017-01-12')
     * @return      data
     */
    public static NoteShowInfo getInfoByDay(String day) {
        return getInstance().mHMDayInfo.get(day);
    }

    /**
     * get day that have records
     * @return      days
     */
    public static List<String> getNotesDays() {
        LinkedList<String> ls_sz = new LinkedList<>();
        ls_sz.addAll(getInstance().mHMDayInfo.keySet());
        return ls_sz;
    }

    /**
     * update data for day/month/year
     */
    public void refreshData() {
        mHMDayInfo.clear();
        mHMMonthInfo.clear();
        mHMYearInfo.clear();

        mHMYearNotes = ContextUtil.getPayIncomeUtility().getAllNotesToYear();
        mHMMonthNotes = ContextUtil.getPayIncomeUtility().getAllNotesToMonth();
        mHMDayNotes = ContextUtil.getPayIncomeUtility().getAllNotesToDay();

        mALOrderedDays = new ArrayList<>(mHMDayNotes.keySet());
        Collections.sort(mALOrderedDays);

        refresh_day();
        refresh_month();
        refresh_year();
    }

    /**
     * get data group by day
     * @return  data
     */
    public HashMap<String, ArrayList<INote>> getNotesForDay() {
        return mHMDayNotes;
    }

    /**
     * get data group by month
     * @return  data
     */
    public HashMap<String, ArrayList<INote>> getNotesForMonth() {
        return mHMMonthNotes;
    }

    /**
     * get data group by year
     * @return  data
     */
    public HashMap<String, ArrayList<INote>> getNotesForYear() {
        return mHMYearNotes;
    }

    /**
     * get data between start to end
     * @param start     start day
     * @param end       end day
     * @return          data
     */
    public HashMap<String, ArrayList<INote>> getNotesBetweenDays(String start, String end) {
        HashMap<String, ArrayList<INote>> ls_note = new HashMap<>();
        for (String day : mALOrderedDays) {
            if (day.compareTo(start) >= 0) {
                if (day.compareTo(end) > 0)
                    break;

                ls_note.put(day, mHMDayNotes.get(day));
            }
        }

        return ls_note;
    }

    /**
     * get records for day
     * @param day   day(example : '2017-07-06')
     * @return      records
     */
    public List<INote> getNotesByDay(String day)    {
        return mHMDayNotes.get(day);
    }


    /**
     * get next day have record
     * @param org_day   origin day(example : "2017-02-24")
     * @return          next day or ""
     */
    public String getNextDay(String org_day) {
        if (mALOrderedDays.isEmpty())
            return "";

        int max_id = mALOrderedDays.size() - 1;
        int id = mALOrderedDays.indexOf(org_day);
        if (-1 == id) {
            for (int idx = max_id; idx >= 0; --idx) {
                String day = mALOrderedDays.get(idx);
                if (0 > day.compareTo(org_day)) {
                    id = idx < max_id ? idx + 1 : idx;
                    break;
                }
            }
        } else {
            id = id + 1;
        }

        return id <= max_id && id != -1 ?
                mALOrderedDays.get(id) : "";
    }


    /**
     * get prior day have record
     * @param org_day   origin day(example : "2017-02-24")
     * @return          prior day or ""
     */
    public String getPrvDay(String org_day) {
        if (mALOrderedDays.isEmpty())
            return "";

        int id = mALOrderedDays.indexOf(org_day);
        if (-1 == id) {
            int len = mALOrderedDays.size();
            for (int idx = 0; idx < len; ++idx) {
                String day = mALOrderedDays.get(idx);
                if (0 < day.compareTo(org_day)) {
                    id = idx > 0 ? idx - 1 : 0;
                    break;
                }
            }
        } else {
            id = id - 1;
        }

        return id >= 0 ? mALOrderedDays.get(id) : "";
    }

    /// PRIVATE BEGIN
    /**
     * update day stats
     * data from sqlite
     */
    private void refresh_day() {
        for (String k : mALOrderedDays) {
            ArrayList<INote> v = mHMDayNotes.get(k);
            NoteShowInfo ni = new NoteShowInfo();
            for (INote r : v) {
                if (r.isPayNote()) {
                    ni.setPayCount(ni.getPayCount() + 1);
                    ni.setPayAmount(ni.getPayAmount().add(r.getVal()));
                } else {
                    ni.setIncomeCount(ni.getIncomeCount() + 1);
                    ni.setIncomeAmount(ni.getIncomeAmount().add(r.getVal()));
                }
            }

            mHMDayInfo.put(k, ni);
        }
    }

    /**
     * update month stats
     * data from day stats
     */
    private void refresh_month() {
        ArrayList<String> set_k = new ArrayList<>(mHMDayInfo.keySet());
        for (String k : set_k) {
            boolean bn = false;
            String cur_m = k.substring(0, 7);
            NoteShowInfo v_d = mHMMonthInfo.get(cur_m);
            if (null == v_d) {
                bn = true;
                v_d = new NoteShowInfo();
            }

            NoteShowInfo cur_d = mHMDayInfo.get(k);
            v_d.setPayCount(cur_d.getPayCount() + v_d.getPayCount());
            v_d.setIncomeCount(cur_d.getIncomeCount() + v_d.getIncomeCount());
            v_d.setPayAmount(cur_d.getPayAmount().add(v_d.getPayAmount()));
            v_d.setIncomeAmount(cur_d.getIncomeAmount().add(v_d.getIncomeAmount()));

            if (bn)
                mHMMonthInfo.put(cur_m, v_d);
        }
    }

    /**
     * update year stats
     * data from month stats
     */
    private void refresh_year() {
        ArrayList<String> set_k = new ArrayList<>(mHMMonthInfo.keySet());
        for (String k : set_k) {
            boolean bn = false;
            String cur_m = k.substring(0, 4);
            NoteShowInfo v_d = mHMYearInfo.get(cur_m);
            if (null == v_d) {
                bn = true;
                v_d = new NoteShowInfo();
            }

            NoteShowInfo cur_d = mHMMonthInfo.get(k);
            v_d.setPayCount(cur_d.getPayCount() + v_d.getPayCount());
            v_d.setIncomeCount(cur_d.getIncomeCount() + v_d.getIncomeCount());
            v_d.setPayAmount(cur_d.getPayAmount().add(v_d.getPayAmount()));
            v_d.setIncomeAmount(cur_d.getIncomeAmount().add(v_d.getIncomeAmount()));

            if (bn)
                mHMYearInfo.put(cur_m, v_d);
        }
    }
    /// PRIVATE END
}
