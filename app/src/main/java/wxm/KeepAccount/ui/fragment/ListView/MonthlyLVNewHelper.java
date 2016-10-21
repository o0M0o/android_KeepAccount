package wxm.KeepAccount.ui.fragment.ListView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.db.INote;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.adapter.LVShowNoteAdapter;
import wxm.KeepAccount.ui.fragment.base.DefForTabLayout;

/**
 * 月数据辅助类
 * Created by 123 on 2016/9/10.
 */
public class MonthlyLVNewHelper extends ListViewBase {
    private final static String TAG = "MonthlyLVHelper";
    private final static String[] DAY_IN_WEEK = {
            "星期日", "星期一", "星期二","星期三",
            "星期四","星期五","星期六"};

    private ListView    mLVHolder;

    private boolean mBSelectSubFilter = false;
    private final LinkedList<String> mLLSubFilter = new LinkedList<>();
    private final LinkedList<View>   mLLSubFilterVW = new LinkedList<>();

    public MonthlyLVNewHelper()    {
        super();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_pager, container, false);

        // init ray menu
        RayMenu rayMenu = UtilFun.cast(mSelfView.findViewById(R.id.rm_show_record));
        ToolUtil.throwExIf(null == rayMenu);
        rayMenu.setVisibility(View.INVISIBLE);

        mLVHolder = UtilFun.cast(mSelfView.findViewById(R.id.tabvp_lv_main));
        ToolUtil.throwExIf(null == mLVHolder);

        return mSelfView;
    }

    @Override
    public void loadView() {
        reloadData();
        refreshView();
    }

    @Override
    public void checkView() {
        if(getRootActivity().getMonthNotesDirty())
            loadView();
    }

    @Override
    public void filterView(List<String> ls_tag) {
        if(null != ls_tag) {
            mBFilter = true;
            mFilterPara.clear();
            mFilterPara.addAll(ls_tag);
            refreshView();
        } else  {
            mBFilter = false;
            refreshView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)  {
            case AppGobalDef.INTRET_RECORD_ADD    :
            case AppGobalDef.INTRET_RECORD_MODIFY :
            case AppGobalDef.INTRET_DAILY_DETAIL :
                reloadData();
                break;

            default:
                Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_accpet :
                if(mBSelectSubFilter) {
                    if(!ToolUtil.ListIsNullOrEmpty(mLLSubFilter)) {
                        ACNoteShow ac = getRootActivity();
                        ac.jumpByTabName(DefForTabLayout.TAB_TITLE_DAILY);
                        ac.filterView(mLLSubFilter);

                        mLLSubFilter.clear();
                    }

                    for(View i : mLLSubFilterVW)    {
                        i.setSelected(false);
                        i.getBackground().setAlpha(0);
                    }
                    mLLSubFilterVW.clear();

                    mBSelectSubFilter = false;
                    refreshAttachLayout();
                }
                break;

            case R.id.bt_giveup :
                mBSelectSubFilter = false;
                mLLSubFilter.clear();

                for(View i : mLLSubFilterVW)    {
                    i.setSelected(false);
                    i.getBackground().setAlpha(0);
                }
                mLLSubFilterVW.clear();

                refreshAttachLayout();
                break;
        }
    }


    /**
     * 重新加载数据
     */
    private void reloadData() {
        mMainPara.clear();

        // format output
        HashMap<String, ArrayList<INote>> hm_data = getRootActivity().getNotesByMonth();
        parseNotes(hm_data);
    }

    /**
     * 不重新加载数据，仅更新视图
     */
    @Override
    protected void refreshView()  {
        // set layout
        refreshAttachLayout();

        // 设置listview adapter
        LVShowNoteAdapter mSNAdapter = new LVShowNoteAdapter(getRootActivity(),
                                    mLVHolder, mMainPara, new String[]{},  new int[]{});
        mLVHolder.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }


    private void refreshAttachLayout()    {
        setAttachLayoutVisible(mBFilter || mBSelectSubFilter ? View.VISIBLE : View.INVISIBLE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setAccpetGiveupLayoutVisible(mBSelectSubFilter ? View.VISIBLE : View.INVISIBLE);
    }


    /**
     * 解析数据
     * @param notes  待解析数据
     */
    private void parseNotes(HashMap<String, ArrayList<INote>> notes)   {
        mMainPara.clear();

        ArrayList<String> set_k = new ArrayList<>(notes.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            String title = ToolUtil.FormatDateString(k);
            ArrayList<INote> v = notes.get(k);
            parseOneMonth(title, v);
        }
    }

    /**
     * 解析一个月的数据
     * @param tag       此月数据的tag
     * @param notes     此月数据
     */
    private void parseOneMonth(String tag, List<INote> notes)    {
        // for monthly tag
        HashMap<String, String> map = new HashMap<>();
        map.put(LVShowNoteAdapter.KEY_TAG, tag);
        map.put(LVShowNoteAdapter.KEY_TYPE, LVShowNoteAdapter.VAL_MONTH);
        map.put(LVShowNoteAdapter.KEY_ITEM_SHOW_OR_HIDE, LVShowNoteAdapter.VAL_SHOW);
        map.put(LVShowNoteAdapter.KEY_SHOW_OR_HIDE, LVShowNoteAdapter.VAL_HIDE);
        map.put(LVShowNoteAdapter.KEY_SIMPLE_SHOW, tag);
        map.put(LVShowNoteAdapter.KEY_BACK_COLOR,
                String.valueOf(getRootActivity().getResources().getColor(R.color.azure)));
        mMainPara.add(map);

        // for days in month
        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote r : notes) {
            String h_k;
            if (r instanceof PayNoteItem) {
                PayNoteItem pi = UtilFun.cast(r);
                h_k = pi.getTs().toString().substring(0, 10);
            } else {
                IncomeNoteItem ii = UtilFun.cast(r);
                h_k = ii.getTs().toString().substring(0, 10);
            }

            ArrayList<INote> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<INote> v = new ArrayList<>();
                v.add(r);
                hm_data.put(h_k, v);
            } else {
                h_v.add(r);
            }
        }

        ArrayList<String> set_k = new ArrayList<>(hm_data.keySet());
        Collections.sort(set_k);
        for(String k : set_k) {
            ArrayList<INote> ns = hm_data.get(k);
            parseDays(k, ns);
        }
    }

    /**
     * 解析一天的数据
     * @param tag        此天数据的tag
     * @param data       此天的数据
     */
    private void parseDays(String tag, ArrayList<INote> data)    {
        int pos = 0;
        int dlen = data.size();
        for(; pos < dlen; pos++)    {
            Object r = data.get(pos);

            Timestamp ts;
            HashMap<String, String> map = new HashMap<>();
            if (r instanceof PayNoteItem) {
                PayNoteItem pi = UtilFun.cast(r);
                ts = pi.getTs();

                map.put(LVShowNoteAdapter.KEY_TYPE, LVShowNoteAdapter.VAL_PAY);
                map.put(LVShowNoteAdapter.KEY_RECORD_INFO, pi.getInfo());
                map.put(LVShowNoteAdapter.KEY_ARISE_AMOUNT,
                        String.format(Locale.CHINA, "- %.02f", pi.getVal()));
            } else {
                IncomeNoteItem ii = UtilFun.cast(r);
                ts = ii.getTs();

                map.put(LVShowNoteAdapter.KEY_TYPE, LVShowNoteAdapter.VAL_INCOME);
                map.put(LVShowNoteAdapter.KEY_RECORD_INFO, ii.getInfo());
                map.put(LVShowNoteAdapter.KEY_ARISE_AMOUNT,
                        String.format(Locale.CHINA, "+ %.02f", ii.getVal()));
            }

            Calendar day = Calendar.getInstance();
            day.setTimeInMillis(ts.getTime());
            if(0 == pos)    {
                map.put(LVShowNoteAdapter.KEY_DAY_NUMBER, String.valueOf(day.get(Calendar.DAY_OF_MONTH) + 1));
                map.put(LVShowNoteAdapter.KEY_DAY_IN_WEEK, getDayInWeek(day.get(Calendar.DAY_OF_WEEK)));
            }

            String color = String.valueOf(getRootActivity().getResources().getColor(
                                            0 == pos ?
                                                R.color.grey_4
                                                : 0 == pos% 2 ? R.color.grey_2 : R.color.grey_3));

            map.put(LVShowNoteAdapter.KEY_ITEM_SHOW_OR_HIDE, LVShowNoteAdapter.VAL_SHOW);
            map.put(LVShowNoteAdapter.KEY_BACK_COLOR, color);
            map.put(LVShowNoteAdapter.KEY_TAG, tag);
            map.put(LVShowNoteAdapter.KEY_ARISE_TIME,
                        String.format(Locale.CHINA, "%02d:%02d",
                            day.get(Calendar.HOUR_OF_DAY), day.get(Calendar.MINUTE)));
            mMainPara.add(map);
        }
    }

    /**
     * 返回“星期*"
     * @param dw    0-6格式的星期数
     * @return  星期*
     */
    private String getDayInWeek(int dw) {
        dw--;
        return DAY_IN_WEEK[dw];
    }
}
