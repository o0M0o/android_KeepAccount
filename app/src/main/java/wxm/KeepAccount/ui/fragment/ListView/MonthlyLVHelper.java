package wxm.KeepAccount.ui.fragment.ListView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.db.INote;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.fragment.base.DefForTabLayout;
import wxm.KeepAccount.ui.fragment.base.ListViewBase;

/**
 * 月数据辅助类
 * Created by 123 on 2016/9/10.
 */
public class MonthlyLVHelper extends ListViewBase {
    private final static String TAG = "MonthlyLVHelper";

    private boolean mBSelectSubFilter = false;
    private final LinkedList<String> mLLSubFilter = new LinkedList<>();
    private final LinkedList<View>   mLLSubFilterVW = new LinkedList<>();

    public MonthlyLVHelper()    {
        super();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_pager, container, false);

        // init ray menu
        RayMenu rayMenu = UtilFun.cast(mSelfView.findViewById(R.id.rm_show_record));
        assert null != rayMenu;
        rayMenu.setVisibility(View.INVISIBLE);

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
                new String[]{ListViewBase.MPARA_TITLE, ListViewBase.MPARA_ABSTRACT},
                new int[]{R.id.tv_title, R.id.tv_abstract});
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

        String show_str =
                String.format(Locale.CHINA,
                        "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                        pay_cout, pay_amount, income_cout, income_amount);

        HashMap<String, String> map = new HashMap<>();
        map.put(ListViewBase.MPARA_TITLE, tag);
        map.put(ListViewBase.MPARA_ABSTRACT, show_str);
        map.put(ListViewBase.MPARA_SHOW, ListViewBase.MPARA_SHOW_FOLD);
        map.put(ListViewBase.MPARA_TAG, tag);
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

            String sub_tag = ToolUtil.FormatDateString(k);
            String show =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(ListViewBase.SPARA_TITLE, sub_tag);
            map.put(ListViewBase.SPARA_DETAIL, show);
            map.put(ListViewBase.MPARA_TAG, tag);
            map.put(ListViewBase.SPARA_TAG, sub_tag);
            cur_llhm.add(map);
        }

        mHMSubPara.put(tag, cur_llhm);
    }


    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if(ListViewBase.MPARA_SHOW_UNFOLD.equals(hm.get(ListViewBase.MPARA_SHOW))) {
            llhm = mHMSubPara.get(hm.get(ListViewBase.MPARA_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), llhm,
                new String[]{ListViewBase.SPARA_TITLE, ListViewBase.SPARA_DETAIL},
                new int[]{R.id.tv_title, R.id.tv_detail});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ToolUtil.setListViewHeightBasedOnChildren(mLVShowDetail);
    }


    /**
     * 首级adapter
     */
    public class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        public SelfAdapter(Context context,
                           List<? extends Map<String, ?>> mdata,
                           String[] from, int[] to) {
            super(context, mdata, R.layout.li_monthly_show, from, to);
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
                //Log.i(TAG, "create view at pos = " + position);

                final View fv = v;
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Resources res = v.getResources();
                        ImageButton ib = UtilFun.cast(v);
                        HashMap<String, String> hm = UtilFun.cast(getItem(position));
                        if(ListViewBase.MPARA_SHOW_FOLD.equals(hm.get(ListViewBase.MPARA_SHOW)))    {
                            hm.put(ListViewBase.MPARA_SHOW, ListViewBase.MPARA_SHOW_UNFOLD);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
                        }   else    {
                            hm.put(ListViewBase.MPARA_SHOW, ListViewBase.MPARA_SHOW_FOLD);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(res.getDrawable(R.drawable.ic_show));
                        }
                    }
                });

                Resources res = v.getResources();
                if(0 == position % 2)   {
                    v.setBackgroundColor(res.getColor(R.color.lightsteelblue));
                } else  {
                    v.setBackgroundColor(res.getColor(R.color.paleturquoise));
                }
            }

            return v;
        }
    }


    /**
     * 次级adapter
     */
    public class SelfSubAdapter  extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";

        public SelfSubAdapter(Context context,
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
                        final String hp_tag = hp.get(ListViewBase.SPARA_TAG);
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
                        /*
                        ACNoteShow as = getRootActivity();
                        as.jumpByTabName(DefForTabLayout.TAB_TITLE_DAILY);

                        HashMap<String, String> hp = UtilFun.cast(getItem(position));
                        final String hp_tag = hp.get(DefForTabLayout.SPARA_TAG);
                        as.filterView(Collections.singletonList(hp_tag));
                        */
                    }
                });

                Resources res = v.getResources();
                if(0 == position % 2)   {
                    v.setBackgroundColor(res.getColor(R.color.wheat));
                } else  {
                    v.setBackgroundColor(res.getColor(R.color.salmon));
                }
            }

            return v;
        }
    }
}
