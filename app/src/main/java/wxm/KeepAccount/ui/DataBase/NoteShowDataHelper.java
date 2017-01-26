package wxm.KeepAccount.ui.DataBase;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.utility.ContextUtil;

/**
 * Data helper for NoteShow
 * Created by wxm on 2016/5/30.
 */
public class NoteShowDataHelper {
    private static final String TAG = "NoteShowDataHelper ";

    // 定义调用参数
    public static final String INTENT_PARA_FIRST_TAB   = "first_tab";

    // 定义tab页标签内容
    public static final String TAB_TITLE_DAILY      = "日流水";
    public static final String TAB_TITLE_MONTHLY    = "月流水";
    public static final String TAB_TITLE_YEARLY     = "年流水";
    public static final String TAB_TITLE_BUDGET     = "预算";

    /**
     * 分别对应 :
     * '2016-10-24' ---- data   (日数据）
     * '2016-10'    ---- data   (月数据)
     * '2016'       ---- data   (年数据)
     */
    HashMap<String, NoteShowInfo>   mHMDayInfo;
    HashMap<String, NoteShowInfo>   mHMMonthInfo;
    HashMap<String, NoteShowInfo>   mHMYearInfo;

    HashMap<String, ArrayList<INote>>   mHMDayNotes;
    HashMap<String, ArrayList<INote>>   mHMMonthNotes;
    HashMap<String, ArrayList<INote>>   mHMYearNotes;

    ArrayList<String>   mALOrderedDays;

    // use singleton
    private static NoteShowDataHelper instance = new NoteShowDataHelper();
    private NoteShowDataHelper() {
        mHMDayInfo = new HashMap<>();
        mHMMonthInfo = new HashMap<>();
        mHMYearInfo = new HashMap<>();
    }

    public static NoteShowDataHelper getInstance() {
        return instance;
    }

    /**
     * 更新统计数据
     */
    public void refreshData()    {
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
     * 获得日统计数据
     * @return  日统计数据
     */
    public HashMap<String, NoteShowInfo> getDayInfo()   {
        return mHMDayInfo;
    }

    /**
     * 获得月统计数据
     * @return  月统计数据
     */
    public HashMap<String, NoteShowInfo> getMonthInfo()   {
        return mHMMonthInfo;
    }

    /**
     * 获得年统计数据
     * @return  年统计数据
     */
    public HashMap<String, NoteShowInfo> getYearInfo()   {
        return mHMYearInfo;
    }

    /**
     * 获得按日归类的数据
     * @return    按日归类的数据
     */
    public HashMap<String, ArrayList<INote>> getNotesForDay()   {
        return mHMDayNotes;
    }

    /**
     * 获得按日归类的数据
     * @return    按日归类的数据
     */
    public HashMap<String, ArrayList<INote>> getNotesForMonth()   {
        return mHMMonthNotes;
    }

    /**
     * 获得按日归类的数据
     * @return    按日归类的数据
     */
    public HashMap<String, ArrayList<INote>> getNotesForYear()   {
        return mHMYearNotes;
    }


    /**
     * 根据当前日期, 获得下一天的日期
     * @param org_day   当前日期
     * @return  下一天的日期，或者""
     */
    public String getNextDay(String org_day)    {
        int id = mALOrderedDays.indexOf(org_day);
        if(-1 == id)
            return "";

        return id < (mALOrderedDays.size() - 1) ? mALOrderedDays.get(id + 1) : "";
    }


    /**
     * 根据当前日期, 获得上一天的日期
     * @param org_day   当前日期
     * @return  上一天的日期，或者""
     */
    public String getPrvDay(String org_day)    {
        int id = mALOrderedDays.indexOf(org_day);
        if(-1 == id)
            return "";

        return id > 0 ? mALOrderedDays.get(id - 1) : "";
    }

    /// PRIVATE BEGIN

    /**
     * 更新日统计数据
     * 数据取自sqlite原始数据
     */
    private void refresh_day()  {
        //ArrayList<String> set_k = new ArrayList<>(mHMDayNotes.keySet());
        //Collections.sort(set_k);
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
     * 更新月统计数据
     * !!数据取自日统计数据!!
     */
    private void refresh_month()  {
        ArrayList<String> set_k = new ArrayList<>(mHMDayInfo.keySet());
        for (String k : set_k) {
            boolean bn = false;
            String cur_m = k.substring(0, 7);
            NoteShowInfo v_d = mHMMonthInfo.get(cur_m);
            if(null == v_d) {
                bn = true;
                v_d = new NoteShowInfo();
            }

            NoteShowInfo cur_d = mHMDayInfo.get(k);
            v_d.setPayCount(cur_d.getPayCount() + v_d.getPayCount());
            v_d.setIncomeCount(cur_d.getIncomeCount() + v_d.getIncomeCount());
            v_d.setPayAmount(cur_d.getPayAmount().add(v_d.getPayAmount()));
            v_d.setIncomeAmount(cur_d.getIncomeAmount().add(v_d.getIncomeAmount()));

            if(bn)
                mHMMonthInfo.put(cur_m, v_d);
        }
    }

    /**
     * 更新年统计数据
     * !!数据取自月统计数据!!
     */
    private void refresh_year()  {
        ArrayList<String> set_k = new ArrayList<>(mHMMonthInfo.keySet());
        for (String k : set_k) {
            boolean bn = false;
            String cur_m = k.substring(0, 4);
            NoteShowInfo v_d = mHMYearInfo.get(cur_m);
            if(null == v_d) {
                bn = true;
                v_d = new NoteShowInfo();
            }

            NoteShowInfo cur_d = mHMMonthInfo.get(k);
            v_d.setPayCount(cur_d.getPayCount() + v_d.getPayCount());
            v_d.setIncomeCount(cur_d.getIncomeCount() + v_d.getIncomeCount());
            v_d.setPayAmount(cur_d.getPayAmount().add(v_d.getPayAmount()));
            v_d.setIncomeAmount(cur_d.getIncomeAmount().add(v_d.getIncomeAmount()));

            if(bn)
                mHMYearInfo.put(cur_m, v_d);
        }
    }
    /// PRIVATE END
}
