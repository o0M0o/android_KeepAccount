package wxm.KeepAccount.ui.fragment.ListView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.db.INote;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.fragment.base.DefForTabLayout;
import wxm.KeepAccount.ui.fragment.base.LVShowDataBase;
import wxm.KeepAccount.ui.fragment.base.ListViewBase;

/**
 * 月数据辅助类
 * Created by 123 on 2016/9/10.
 */
public class MonthlyLVHelper extends LVShowDataBase {
    private final static String TAG = "MonthlyLVHelper";

    private boolean mBSelectSubFilter = false;
    private final LinkedList<String> mLLSubFilter = new LinkedList<>();
    private final LinkedList<View>   mLLSubFilterVW = new LinkedList<>();

    public MonthlyLVHelper()    {
        super();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_newpager, container, false);

        // 无附加动作
        ImageView mIVActions = UtilFun.cast_t(mSelfView.findViewById(R.id.iv_expand));
        GridLayout mGLActions = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_action));
        setLayoutVisible(mGLActions, View.INVISIBLE);
        mIVActions.setVisibility(View.INVISIBLE);

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
        mHMSubPara.clear();

        // format output
        HashMap<String, ArrayList<INote>> hm_data = getNewRootActivity().getNotesByMonth();
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
        LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
        if(mBFilter) {
            for (HashMap<String, String> i : mMainPara) {
                String cur_tag = i.get(ListViewBase.MPARA_TAG);
                for (String ii : mFilterPara) {
                    if (cur_tag.equals(ii)) {
                        n_mainpara.add(i);
                        break;
                    }
                }
            }
        } else  {
            n_mainpara.addAll(mMainPara);
        }

        // 设置listview adapter
        ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.lv_show));
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), n_mainpara,
                                        new String[]{}, new int[]{});
        lv.setAdapter(mSNAdapter);
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
        mHMSubPara.clear();

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
        int pay_cout = 0;
        int income_cout = 0;
        BigDecimal pay_amount = BigDecimal.ZERO;
        BigDecimal income_amount = BigDecimal.ZERO;
        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote r : notes) {
            String h_k = r.getTs().toString().substring(0, 10);
            if (r.isPayNote()) {
                pay_cout += 1;
                pay_amount = pay_amount.add(r.getVal());
            } else {
                income_cout += 1;
                income_amount = income_amount.add(r.getVal());
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

        HashMap<String, String> map = new HashMap<>();
        map.put(K_MONTH, tag.substring(0, 8));
        map.put(K_MONTH_PAY_COUNT, String.valueOf(pay_cout));
        map.put(K_MONTH_INCOME_COUNT, String.valueOf(income_cout));
        map.put(K_MONTH_PAY_AMOUNT, String.format(Locale.CHINA, "%.02f", pay_amount));
        map.put(K_MONTH_INCOME_AMOUNT, String.format(Locale.CHINA, "%.02f", income_amount));

        BigDecimal bd_l = income_amount.subtract(pay_amount);
        String v_l = String.format(Locale.CHINA,
                0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
        map.put(K_AMOUNT, v_l);

        map.put(K_TAG, tag);
        map.put(K_SHOW, checkUnfoldItem(tag) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
        mMainPara.add(map);

        parseDays(tag, hm_data);
    }

    /**
     * 解析一天的数据
     * @param tag           此天数据的tag
     * @param hm_data       此天的数据
     */
    private void parseDays(String tag, HashMap<String, ArrayList<INote>> hm_data)    {
        ArrayList<String> set_k = new ArrayList<>(hm_data.keySet());
        Collections.sort(set_k);
        LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
        for(String k : set_k) {
            ArrayList<INote> notes = hm_data.get(k);

            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;
            for (INote r : notes) {
                if (r.isPayNote()) {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(r.getVal());
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(r.getVal());
                }

            }

            HashMap<String, String> map = new HashMap<>();
            String km = k.substring(8, 10);
            if(km.startsWith("0"))
                km = km.replaceFirst("0", " ");
            map.put(K_DAY_NUMEBER, km);
            try {
                Timestamp ts = ToolUtil.StringToTimestamp(k);
                Calendar day = Calendar.getInstance();
                day.setTimeInMillis(ts.getTime());
                map.put(K_DAY_IN_WEEK, getDayInWeek(day.get(Calendar.DAY_OF_WEEK)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            map.put(K_DAY_PAY_COUNT, String.valueOf(pay_cout));
            map.put(K_DAY_INCOME_COUNT, String.valueOf(income_cout));
            map.put(K_DAY_PAY_AMOUNT, String.format(Locale.CHINA, "%.02f", pay_amount));
            map.put(K_DAY_INCOME_AMOUNT, String.format(Locale.CHINA, "%.02f", income_amount));

            BigDecimal bd_l = income_amount.subtract(pay_amount);
            String v_l = String.format(Locale.CHINA,
                    0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
            map.put(K_AMOUNT, v_l);
            map.put(K_TAG, tag);
            cur_llhm.add(map);
        }

        mHMSubPara.put(tag, cur_llhm);
    }


    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if(V_SHOW_UNFOLD.equals(hm.get(K_SHOW))) {
            llhm = mHMSubPara.get(hm.get(K_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast_t(v.findViewById(R.id.lv_show_detail));
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), llhm,
                                    new String[]{}, new int[]{});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ToolUtil.setListViewHeightBasedOnChildren(mLVShowDetail);
    }


    /**
     * 首级adapter
     */
    private class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";
        private int         mClOne;
        private int         mClTwo;

        private Drawable    mDAFold;
        private Drawable    mDAUnFold;

        SelfAdapter(Context context,
                    List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_monthly_show, from, to);

            Resources res   = context.getResources();
            mClOne = res.getColor(R.color.lightsteelblue);
            mClTwo = res.getColor(R.color.paleturquoise);

            mDAFold = res.getDrawable(R.drawable.ic_hide);
            mDAUnFold = res.getDrawable(R.drawable.ic_show);
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
                final HashMap<String, String> hm = UtilFun.cast(getItem(position));

                final View fv = v;
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageButton ib = UtilFun.cast(v);
                        if(V_SHOW_FOLD.equals(hm.get(K_SHOW)))    {
                            hm.put(K_SHOW, V_SHOW_UNFOLD);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(mDAFold);
                            addUnfoldItem(hm.get(K_TAG));
                        }   else    {
                            hm.put(K_SHOW, V_SHOW_FOLD);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(mDAUnFold);
                            removeUnfoldItem(hm.get(K_TAG));
                        }
                    }
                });

                if(V_SHOW_UNFOLD.equals(hm.get(K_SHOW)))    {
                    //init_detail_view(fv, hm);
                    ib.setImageDrawable(mDAFold);
                }   else    {
                    init_detail_view(fv, hm);
                    ib.setImageDrawable(mDAUnFold);
                }

                RelativeLayout rl = UtilFun.cast_t(v.findViewById(R.id.rl_header));
                rl.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

                // for show
                TextView tv = UtilFun.cast_t(v.findViewById(R.id.tv_month));
                tv.setText(hm.get(K_MONTH));

                RelativeLayout rl_info = UtilFun.cast_t(v.findViewById(R.id.rl_info));
                fillNoteInfo(rl_info, hm.get(K_MONTH_PAY_COUNT), hm.get(K_MONTH_PAY_AMOUNT),
                        hm.get(K_MONTH_INCOME_COUNT), hm.get(K_MONTH_INCOME_AMOUNT),
                        hm.get(K_AMOUNT));
            }

            return v;
        }
    }


    /**
     * 次级adapter
     */
    private class SelfSubAdapter  extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";

        SelfSubAdapter(Context context,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_monthly_show_detail, from, to);
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
                final HashMap<String, String> hm = UtilFun.cast(getItem(position));
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_action));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mBSelectSubFilter) {
                            mLLSubFilter.clear();
                            mLLSubFilterVW.clear();
                        }

                        HashMap<String, String> hp = UtilFun.cast(getItem(position));
                        final String hp_tag = hp.get(K_TAG);
                        Resources res = v.getResources();
                        if(!v.isSelected()) {
                            mLLSubFilter.add(hp_tag);
                            mLLSubFilterVW.add(v);

                            if(!mBSelectSubFilter) {
                                mBSelectSubFilter = true;
                                refreshAttachLayout();
                            }

                            v.getBackground().setAlpha(255);
                            v.setBackgroundColor(res.getColor(R.color.red));
                        }   else    {
                            mLLSubFilter.removeFirstOccurrence(hp_tag);
                            mLLSubFilterVW.removeFirstOccurrence(v);
                            v.getBackground().setAlpha(0);

                            if(mLLSubFilter.isEmpty()) {
                                mBSelectSubFilter = false;
                                refreshAttachLayout();
                            }
                        }

                        v.setSelected(!v.isSelected());
                    }
                });

                // for show
                TextView tv = UtilFun.cast_t(v.findViewById(R.id.tv_day_number));
                tv.setText(hm.get(K_DAY_NUMEBER));

                tv = UtilFun.cast_t(v.findViewById(R.id.tv_day_in_week));
                tv.setText(hm.get(K_DAY_IN_WEEK));

                RelativeLayout rl_info = UtilFun.cast_t(v.findViewById(R.id.rl_info));
                fillNoteInfo(rl_info, hm.get(K_DAY_PAY_COUNT), hm.get(K_DAY_PAY_AMOUNT),
                        hm.get(K_DAY_INCOME_COUNT), hm.get(K_DAY_INCOME_AMOUNT),
                        hm.get(K_AMOUNT));
            }

            return v;
        }
    }
}
