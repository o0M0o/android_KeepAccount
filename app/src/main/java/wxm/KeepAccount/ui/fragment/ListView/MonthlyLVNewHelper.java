package wxm.KeepAccount.ui.fragment.ListView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
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
        HashMap<String, ArrayList<Object>> hm_data = getRootActivity().getNotesByMonth();
        parseNotes(hm_data);
    }

    /**
     * 不重新加载数据，仅更新视图
     */
    @Override
    protected void refreshView()  {
        // set layout
        refreshAttachLayout();

        // update data

        // 设置listview adapter
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), mMainPara,
                new String[]{},  new int[]{});
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
    private void parseNotes(HashMap<String, ArrayList<Object>> notes)   {
        mMainPara.clear();

        ArrayList<String> set_k = new ArrayList<>(notes.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            String title = ToolUtil.FormatDateString(k);
            ArrayList<Object> v = notes.get(k);
            parseOneMonth(title, v);
        }
    }

    /**
     * 解析一个月的数据
     * @param tag       此月数据的tag
     * @param notes     此月数据
     */
    private void parseOneMonth(String tag, List<Object> notes)    {
        // for monthly tag
        HashMap<String, String> map = new HashMap<>();
        map.put(SelfAdapter.KEY_TAG, tag);
        map.put(SelfAdapter.KEY_TYPE, SelfAdapter.VAL_MONTH);
        map.put(SelfAdapter.KEY_SHOW_OR_HIDE, SelfAdapter.VAL_HIDE);
        map.put(SelfAdapter.KEY_SIMPLE_SHOW, tag);
        map.put(SelfAdapter.KEY_BACK_COLOR,
                String.valueOf(getRootActivity().getResources().getColor(R.color.azure)));
        mMainPara.add(map);

        // for days in month
        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for (Object r : notes) {
            String h_k;
            if (r instanceof PayNoteItem) {
                PayNoteItem pi = UtilFun.cast(r);
                h_k = pi.getTs().toString().substring(0, 10);
            } else {
                IncomeNoteItem ii = UtilFun.cast(r);
                h_k = ii.getTs().toString().substring(0, 10);
            }

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(r);
                hm_data.put(h_k, v);
            } else {
                h_v.add(r);
            }
        }

        ArrayList<String> set_k = new ArrayList<>(hm_data.keySet());
        Collections.sort(set_k);
        for(String k : set_k) {
            ArrayList<Object> ns = hm_data.get(k);
            parseDays(k, ns);
        }
    }

    /**
     * 解析一天的数据
     * @param tag        此天数据的tag
     * @param data       此天的数据
     */
    private void parseDays(String tag, ArrayList<Object> data)    {
        int pos = 0;
        int dlen = data.size();
        for(; pos < dlen; pos++)    {
            Object r = data.get(pos);

            Timestamp ts;
            HashMap<String, String> map = new HashMap<>();
            if (r instanceof PayNoteItem) {
                PayNoteItem pi = UtilFun.cast(r);
                ts = pi.getTs();

                map.put(SelfAdapter.KEY_TYPE, SelfAdapter.VAL_PAY);
                map.put(SelfAdapter.KEY_RECORD_INFO, pi.getInfo());
                map.put(SelfAdapter.KEY_ARISE_AMOUNT,
                        String.format(Locale.CHINA, "- %.02f", pi.getVal()));
            } else {
                IncomeNoteItem ii = UtilFun.cast(r);
                ts = ii.getTs();

                map.put(SelfAdapter.KEY_TYPE, SelfAdapter.VAL_INCOME);
                map.put(SelfAdapter.KEY_RECORD_INFO, ii.getInfo());
                map.put(SelfAdapter.KEY_ARISE_AMOUNT,
                        String.format(Locale.CHINA, "+ %.02f", ii.getVal()));
            }

            Calendar day = Calendar.getInstance();
            day.setTimeInMillis(ts.getTime());
            if(0 == pos)    {
                map.put(SelfAdapter.KEY_DAY_NUMBER, String.valueOf(day.get(Calendar.DAY_OF_MONTH) + 1));
                map.put(SelfAdapter.KEY_DAY_IN_WEEK, getDayInWeek(day.get(Calendar.DAY_OF_WEEK)));
            }

            String color = String.valueOf(getRootActivity().getResources().getColor(
                                            0 == pos ?
                                                R.color.grey_4
                                                : 0 == pos% 2 ? R.color.grey_2 : R.color.grey_3));

            map.put(SelfAdapter.KEY_BACK_COLOR, color);
            map.put(SelfAdapter.KEY_TAG, tag);
            map.put(SelfAdapter.KEY_ARISE_TIME,
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

    /**
     * 首级adapter
     *   -- 月度项
     *   -- 收入项
     *   -- 支出项
     */
    private class SelfAdapter extends SimpleAdapter
                    implements View.OnClickListener {
        private final static String TAG = "SelfAdapter";

        /// for item data begin
        final static String KEY_TAG         = "k_tag";

        // for color
        final static String KEY_BACK_COLOR  = "k_back_color";

        // for item type begin
        final static String KEY_TYPE        = "k_type";
        final static String VAL_MONTH       = "v_month";
        final static String VAL_PAY         = "v_pay";
        final static String VAL_INCOME      = "v_income";
        // for item type end

        // for item show_or_hide begin
        final static String KEY_SHOW_OR_HIDE    = "k_show_or_hide";
        final static String VAL_SHOW            = "v_show";
        final static String VAL_HIDE            = "v_hide";
        // for item show_or_hide end

        // for others
        final static String KEY_SIMPLE_SHOW     = "k_simple_show";

        final static String KEY_DAY_NUMBER      = "k_day_number";
        final static String KEY_DAY_IN_WEEK     = "k_day_in_week";
        final static String KEY_ARISE_TIME      = "k_arise_time";
        final static String KEY_ARISE_AMOUNT    = "k_arise_amount";
        final static String KEY_RECORD_INFO     = "k_record_info";
        /// for item data end

        SelfAdapter(Context context,
                    List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_test_detail, from, to);
        }

        @Override
        public int getViewTypeCount() {
            int org_ct = getCount();
            return org_ct < 1 ? 1 : org_ct;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                v.setBackgroundColor(Integer.parseInt(hm.get(KEY_BACK_COLOR)));
                v.setOnClickListener(this);

                RelativeLayout rl_month = UtilFun.cast(v.findViewById(R.id.lo_month));
                RelativeLayout rl_income = UtilFun.cast(v.findViewById(R.id.lo_income));
                RelativeLayout rl_pay = UtilFun.cast(v.findViewById(R.id.lo_pay));
                String it = hm.get(KEY_TYPE);
                switch (it)     {
                    case VAL_MONTH:   {
                        setLayoutVisible(rl_income, View.INVISIBLE);
                        setLayoutVisible(rl_pay, View.INVISIBLE);
                        init_month(rl_month, hm);
                    }
                    break;

                    case VAL_PAY:   {
                        setLayoutVisible(rl_month, View.INVISIBLE);
                        setLayoutVisible(rl_income, View.INVISIBLE);
                        init_pay(rl_pay, hm);
                    }
                    break;

                    case VAL_INCOME:   {
                        setLayoutVisible(rl_month, View.INVISIBLE);
                        setLayoutVisible(rl_pay, View.INVISIBLE);
                        init_income(rl_income, hm);
                    }
                    break;
                }
            }

            return v;
        }

        /**
         * 初始化“月信息”节点
         * @param rl    father view
         * @param hm    data for view
         */
        private void init_month(RelativeLayout rl, HashMap<String, String> hm)  {
            TextView tv = UtilFun.cast(rl.findViewById(R.id.tv_show));
            ToolUtil.throwExIf(null == tv);
            tv.setText(hm.get(SelfAdapter.KEY_SIMPLE_SHOW));

            ImageView iv = UtilFun.cast(rl.findViewById(R.id.iv_show_hide));
            ToolUtil.throwExIf(null == iv);
            iv.setOnClickListener(this);
        }

        /**
         * 初始化“收入信息”节点
         * @param rl            father view
         * @param mHMData       data for view
         */
        private void init_income(RelativeLayout rl, HashMap<String, String> mHMData)  {
            boolean b_show_day = !UtilFun.StringIsNullOrEmpty(mHMData.get(SelfAdapter.KEY_DAY_NUMBER));
            TextView tv;
            if(b_show_day) {
                tv = UtilFun.cast(rl.findViewById(R.id.tv_day_number));
                ToolUtil.throwExIf(null == tv);
                tv.setText(mHMData.get(SelfAdapter.KEY_DAY_NUMBER));

                tv = UtilFun.cast(rl.findViewById(R.id.tv_day_in_week));
                ToolUtil.throwExIf(null == tv);
                tv.setText(mHMData.get(SelfAdapter.KEY_DAY_IN_WEEK));

            } else  {
                RelativeLayout rll = UtilFun.cast(rl.findViewById(R.id.rl_left_show));
                ToolUtil.throwExIf(null == rll);
                rll.setVisibility(View.INVISIBLE);
            }

            tv = UtilFun.cast(rl.findViewById(R.id.tv_record_type));
            ToolUtil.throwExIf(null == tv);
            tv.setText(mHMData.get(SelfAdapter.KEY_RECORD_INFO));

            tv = UtilFun.cast(rl.findViewById(R.id.tv_time));
            ToolUtil.throwExIf(null == tv);
            tv.setText(mHMData.get(SelfAdapter.KEY_ARISE_TIME));

            tv = UtilFun.cast(rl.findViewById(R.id.tv_amount));
            ToolUtil.throwExIf(null == tv);
            tv.setText(mHMData.get(SelfAdapter.KEY_ARISE_AMOUNT));
        }

        /**
         * 初始化“支出信息”节点
         * @param rl       father view
         * @param hm       data for view
         */
        private void init_pay(RelativeLayout rl, HashMap<String, String> hm)  {
            init_income(rl, hm);
        }

        /**
         * 设置layout可见性
         * 仅调整可见性，其它设置保持不变
         * @param visible  若为 :
         *                  1. {@code View.INVISIBLE}, 不可见
         *                  2. {@code View.VISIBLE}, 可见
         */
        private void setLayoutVisible(RelativeLayout rl, int visible)    {
            int h = 0;
            if(View.INVISIBLE != visible)
                h = rl.getHeight();

            ViewGroup.LayoutParams param = rl.getLayoutParams();
            param.width = rl.getWidth();
            param.height = h;
            rl.setLayoutParams(param);
        }

        @Override
        public void onClick(View v) {
            if(v instanceof RelativeLayout || v instanceof LinearLayout) {
                click_listitem(v);
            } else if(v instanceof ImageView)   {
                if(R.id.iv_show_hide == v.getId()) {
                    click_show_hide(v);
                }
            } else  {
                Log.e(TAG, "can not process onClick at view : " + v.toString());
            }
        }

        /**
         * "展开"或者"隐藏"子节点
         * @param v  点击的view
         */
        private void click_show_hide(View v) {
            int pos = mLVHolder.getPositionForView(v);
            if(ListView.INVALID_POSITION != pos) {
                ImageView iv = UtilFun.cast(v);
                HashMap<String, String> hd = UtilFun.cast(mLVHolder.getItemAtPosition(pos));
                if(null != hd)  {
                    if(VAL_HIDE.equals(hd.get(KEY_SHOW_OR_HIDE)))   {
                        hd.put(KEY_SHOW_OR_HIDE, VAL_SHOW);
                        iv.setImageDrawable(getRootActivity()
                                .getResources().getDrawable(R.drawable.ic_hide));
                    } else  {
                        hd.put(KEY_SHOW_OR_HIDE, VAL_HIDE);
                        iv.setImageDrawable(getRootActivity()
                                .getResources().getDrawable(R.drawable.ic_show));
                    }
                }
            }
        }

        /**
         * 点击listview节点
         * @param v  点击的view
         */
        private void click_listitem(View v) {
            int pos = mLVHolder.getPositionForView(v);
            if (ListView.INVALID_POSITION != pos) {
                Toast.makeText(getRootActivity(), "invoke click at " + pos,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
