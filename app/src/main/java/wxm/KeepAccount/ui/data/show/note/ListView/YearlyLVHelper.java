package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.ui.utility.FastViewHolder;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo;

/**
 * 年数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class YearlyLVHelper extends LVShowDataBase {
    private final static String TAG = "YearlyLVHelper";

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;

    private boolean mBSelectSubFilter = false;
    private final LinkedList<String> mLLSubFilter = new LinkedList<>();
    private final LinkedList<View>   mLLSubFilterVW = new LinkedList<>();

    @BindView(R.id.lv_show)
    ListView    mLVShow;

    public YearlyLVHelper()    {
        super();
        LOG_TAG = "YearlyLVHelper";
        mBActionExpand = false;
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

    @OnClick({R.id.bt_accpet, R.id.bt_giveup})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_accpet :
                if(mBSelectSubFilter) {
                    if(!UtilFun.ListIsNullOrEmpty(mLLSubFilter)) {
                        ACNoteShow ac = getRootActivity();
                        ac.jumpByTabName(NoteShowDataHelper.TAB_TITLE_MONTHLY);
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
     * 初始化可隐藏动作条
     */
    @Override
    protected void initActs() {
        mRLActReport.setVisibility(View.GONE);
        mRLActAdd.setVisibility(View.GONE);
        mRLActDelete.setVisibility(View.GONE);

        mRLActRefresh.setOnClickListener(v -> reloadView(v.getContext(), false));

        final ImageView iv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.iv_sort));
        final TextView tv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.tv_sort));
        iv_sort.setImageDrawable(getContext().getResources()
                .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
        tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
        mRLActSort.setOnClickListener(v -> {
            mBTimeDownOrder = !mBTimeDownOrder;

            iv_sort.setImageDrawable(getContext().getResources()
                    .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
            tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

            refreshData();
            refreshView();
        });
    }


    /**
     * 重新加载数据
     */
    @Override
    protected void refreshData() {
        super.refreshData();

        mTSLastLoadViewTime.setTime(Calendar.getInstance().getTimeInMillis());

        mMainPara.clear();
        mHMSubPara.clear();

        // for year
        HashMap<String, NoteShowInfo> hm_y = NoteShowDataHelper.getInstance().getYearInfo();
        ArrayList<String> set_k = new ArrayList<>(hm_y.keySet());
        Collections.sort(set_k, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
        for(String k : set_k)   {
            NoteShowInfo ni = hm_y.get(k);

            HashMap<String, String> map = new HashMap<>();
            map.put(K_YEAR, k);
            map.put(K_YEAR_PAY_COUNT, String.valueOf(ni.getPayCount()));
            map.put(K_YEAR_INCOME_COUNT, String.valueOf(ni.getIncomeCount()));
            map.put(K_YEAR_PAY_AMOUNT, String.format(Locale.CHINA,
                    "%.02f", ni.getPayAmount()));
            map.put(K_YEAR_INCOME_AMOUNT, String.format(Locale.CHINA,
                    "%.02f", ni.getIncomeAmount()));

            BigDecimal bd_l = ni.getBalance();
            String v_l = String.format(Locale.CHINA,
                    0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
            map.put(K_AMOUNT, v_l);

            map.put(K_TAG, k);
            map.put(K_SHOW, checkUnfoldItem(k) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
            mMainPara.add(map);
        }

        // for month
        HashMap<String, NoteShowInfo> hm_m = NoteShowDataHelper.getInstance().getMonthInfo();
        ArrayList<String> set_k_m = new ArrayList<>(hm_m.keySet());
        Collections.sort(set_k_m, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
        for(String k : set_k_m)   {
            String ky = k.substring(0, 4);
            NoteShowInfo ni = hm_m.get(k);
            HashMap<String, String> map = new HashMap<>();

            String km = k.substring(5,7);
            km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
            map.put(K_MONTH, km);
            map.put(K_MONTH_PAY_COUNT, String.valueOf(ni.getPayCount()));
            map.put(K_MONTH_INCOME_COUNT, String.valueOf(ni.getIncomeCount()));
            map.put(K_MONTH_PAY_AMOUNT, String.format(Locale.CHINA,
                    "%.02f", ni.getPayAmount()));
            map.put(K_MONTH_INCOME_AMOUNT, String.format(Locale.CHINA,
                    "%.02f", ni.getIncomeAmount()));

            BigDecimal bd_l = ni.getBalance();
            String v_l = String.format(Locale.CHINA,
                    0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
            map.put(K_AMOUNT, v_l);

            map.put(K_TAG, ky);
            map.put(K_SUB_TAG, k);

            LinkedList<HashMap<String, String>>  ls_hm = mHMSubPara.get(ky);
            if(UtilFun.ListIsNullOrEmpty(ls_hm))   {
                ls_hm = new LinkedList<>();
            }

            ls_hm.add(map);
            mHMSubPara.put(ky, ls_hm);
        }


    }

    @Override
    protected void initUiInfo() {
        refreshAttachLayout();

        // update data
        LinkedList<HashMap<String, String>> n_mainpara;
        if(mBFilter) {
            n_mainpara = new LinkedList<>();
            for (HashMap<String, String> i : mMainPara) {
                String cur_tag = i.get(K_TAG);
                for (String ii : mFilterPara) {
                    if (cur_tag.equals(ii)) {
                        n_mainpara.add(i);
                        break;
                    }
                }
            }
        } else  {
            n_mainpara = mMainPara;
        }

        // 设置listview adapter
        SelfAdapter mSNAdapter = new SelfAdapter(getContext(), n_mainpara,
                new String[]{}, new int[]{});
        mLVShow.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    private void refreshAttachLayout()    {
        setAttachLayoutVisible(mBFilter || mBSelectSubFilter ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(mBSelectSubFilter ? View.VISIBLE : View.GONE);
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

        RelativeLayout rl = UtilFun.cast_t(v.findViewById(R.id.rl_detail));
        if(llhm.isEmpty())  {
            rl.setVisibility(View.GONE);
        } else {
            rl.setVisibility(View.VISIBLE);

            // init sub adapter
            ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
            assert null != mLVShowDetail;
            SelfSubAdapter mAdapter = new SelfSubAdapter(getContext(), llhm,
                    new String[]{}, new int[]{});
            mLVShowDetail.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ListViewHelper.setListViewHeightBasedOnChildren(mLVShowDetail);
        }
    }


    /**
     * 首级adapter
     */
    private class SelfAdapter extends SimpleAdapter implements View.OnClickListener {
        private final static String TAG = "SelfAdapter";
        private int         mClOne;
        private int         mClTwo;

        SelfAdapter(Context context,
                    List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_yearly_show, from, to);

            Resources res   = context.getResources();
            mClOne = res.getColor(R.color.color_1);
            mClTwo = res.getColor(R.color.color_2);
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
            FastViewHolder viewHolder = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_yearly_show);

            View root_view = viewHolder.getConvertView();
            HashMap<String, String> hm = UtilFun.cast(getItem(position));
            init_detail_view(root_view, hm);

            root_view.setOnClickListener(this);

            // adjust row color
            RelativeLayout rl = viewHolder.getView(R.id.rl_header);
            rl.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

            // for show
            TextView tv = viewHolder.getView(R.id.tv_year);
            tv.setText(hm.get(K_YEAR));

            HelperDayNotesInfo.fillNoteInfo(viewHolder.getView(R.id.rl_info),
                    hm.get(K_YEAR_PAY_COUNT), hm.get(K_YEAR_PAY_AMOUNT),
                    hm.get(K_YEAR_INCOME_COUNT), hm.get(K_YEAR_INCOME_AMOUNT),
                    hm.get(K_AMOUNT));

            return root_view;
        }

        @Override
        public void onClick(View view) {
            int pos = mLVShow.getPositionForView(view);

            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
            hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);

            init_detail_view(view, hm);
            if (bf)
                addUnfoldItem(hm.get(K_TAG));
            else
                removeUnfoldItem(hm.get(K_TAG));
        }
    }


    /**
     * 次级adapter
     */
    private class SelfSubAdapter  extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";
        private int         mCINoSel;
        private int         mCISel;

        SelfSubAdapter(Context context,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_yearly_show_detail, from, to);
            Resources res = context.getResources();
            mCINoSel = res.getColor(R.color.trans_full);
            mCISel = res.getColor(R.color.trans_1);
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
            FastViewHolder viewHolder = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_yearly_show_detail);

            View root_view = viewHolder.getConvertView();
            final HashMap<String, String> hm = UtilFun.cast(getItem(position));
            final String sub_tag = hm.get(K_SUB_TAG);

            final ImageView ib = viewHolder.getView(R.id.iv_action);
            ib.setBackgroundColor(mLLSubFilter.contains(sub_tag) ? mCISel : mCINoSel);
            ib.setOnClickListener(v1 -> {
                if(!mLLSubFilter.contains(sub_tag)) {
                    Log.d(TAG, "add selected");
                    ib.setBackgroundColor(mCISel);

                    mLLSubFilter.add(sub_tag);
                    mLLSubFilterVW.add(v1);

                    if(!mBSelectSubFilter) {
                        mBSelectSubFilter = true;
                        refreshAttachLayout();
                    }
                }   else {
                    Log.d(TAG, "remove selected");
                    ib.setBackgroundColor(mCINoSel);

                    mLLSubFilter.remove(sub_tag);
                    mLLSubFilterVW.remove(v1);

                    if(mLLSubFilter.isEmpty()) {
                        mLLSubFilterVW.clear();
                        mBSelectSubFilter = false;
                        refreshAttachLayout();
                    }
                }
            });

            // for show
            TextView tv = viewHolder.getView(R.id.tv_month);
            tv.setText(hm.get(K_MONTH));

            HelperDayNotesInfo.fillNoteInfo(viewHolder.getView(R.id.rl_info),
                    hm.get(K_MONTH_PAY_COUNT), hm.get(K_MONTH_PAY_AMOUNT),
                    hm.get(K_MONTH_INCOME_COUNT), hm.get(K_MONTH_INCOME_AMOUNT),
                    hm.get(K_AMOUNT));
            return root_view;
        }
    }
}