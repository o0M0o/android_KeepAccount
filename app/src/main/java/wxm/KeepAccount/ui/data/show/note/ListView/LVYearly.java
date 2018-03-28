package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.androidutil.util.FastViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.ResourceHelper;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.extend.ValueShow.ValueShow;
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.uilib.IconButton.IconButton;

/**
 * 年数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class LVYearly extends LVBase {
    private final static String TAG = "LVYearly";
    private final LinkedList<String> mLLSubFilter = new LinkedList<>();
    private final LinkedList<View> mLLSubFilterVW = new LinkedList<>();

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;
    private boolean mBSelectSubFilter = false;

    class YearlyActionHelper extends ActionHelper    {
        @BindView(R.id.ib_sort)
        IconButton mIBSort;

        @BindView(R.id.ib_report)
        IconButton mIBReport;

        @BindView(R.id.ib_delete)
        IconButton mIBDelete;

        @BindView(R.id.ib_add)
        IconButton mIBAdd;

        YearlyActionHelper() {
            super();
        }

        @Override
        protected void initActs() {
            mIBReport.setVisibility(View.GONE);
            mIBAdd.setVisibility(View.GONE);
            mIBDelete.setVisibility(View.GONE);

            mIBSort.setActIcon(mBTimeDownOrder ?
                    R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
            mIBSort.setActName(mBTimeDownOrder ?
                    R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
        }

        @OnClick({R.id.ib_sort, R.id.ib_refresh})
        public void onActionClick(View v) {
            switch (v.getId()) {
                case R.id.ib_sort: {
                    mBTimeDownOrder = !mBTimeDownOrder;

                    mIBSort.setActIcon(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
                    mIBSort.setActName(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

                    reorderData();
                    loadUIUtility(true);
                }
                break;

                case R.id.ib_refresh: {
                    reloadView(v.getContext(), false);
                }
                break;
            }
        }
    }


    public LVYearly() {
        super();
        LOG_TAG = "LVYearly";
        mBActionExpand = false;

        mAHActs = new YearlyActionHelper();
    }

    /**
     * 过滤视图事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
    }



    /**
     * "接受"或者"取消"后动作
     *
     * @param v 动作view
     */
    @OnClick({R.id.bt_accpet, R.id.bt_giveup})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet:
                if (mBSelectSubFilter) {
                    if (!UtilFun.ListIsNullOrEmpty(mLLSubFilter)) {
                        ACNoteShow ac = getRootActivity();
                        ac.jumpByTabName(NoteDataHelper.TAB_TITLE_MONTHLY);

                        ArrayList<String> al_s = new ArrayList<>();
                        al_s.addAll(mLLSubFilter);
                        EventBus.getDefault().post(new FilterShowEvent(NoteDataHelper.TAB_TITLE_YEARLY, al_s));

                        mLLSubFilter.clear();
                    }

                    for (View i : mLLSubFilterVW) {
                        i.setSelected(false);
                        i.getBackground().setAlpha(0);
                    }
                    mLLSubFilterVW.clear();

                    mBSelectSubFilter = false;
                    refreshAttachLayout();
                }
                break;

            case R.id.bt_giveup:
                mBSelectSubFilter = false;
                mLLSubFilter.clear();

                for (View i : mLLSubFilterVW) {
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
    @Override
    protected void refreshData() {
        super.refreshData();

        mMainPara.clear();
        mHMSubPara.clear();

        ToolUtil.runInBackground(this.getActivity(),
                () -> {
                    ExecutorService fixedThreadPool = Executors.newCachedThreadPool();
                    Future<LinkedList<HashMap<String, String>>> ll_main_rets;
                    Future<LinkedList<Map.Entry<String, HashMap<String, String>>>> ll_sub_rets;

                    // for year
                    List<String> set_k = NoteDataHelper.getNotesYears();
                    Collections.sort(set_k, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                    ll_main_rets = fixedThreadPool
                            .submit(() -> {
                                LinkedList<HashMap<String, String>> ll_rets = new LinkedList<>();
                                for (String k : set_k) {
                                    NoteShowInfo ni = NoteDataHelper.getInfoByYear(k);

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
                                    map.put(K_SHOW, EShowFold.getByFold(!checkUnfoldItem(k)).getName());
                                    ll_rets.add(map);
                                }

                                return ll_rets;
                            });

                    // for month
                    List<String> set_k_m = NoteDataHelper.getNotesMonths();
                    Collections.sort(set_k_m, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                    ll_sub_rets = fixedThreadPool
                            .submit(() -> {
                                LinkedList<Map.Entry<String, HashMap<String, String>>>
                                        ll_rets = new LinkedList<>();
                                for (String k : set_k_m) {
                                    String ky = k.substring(0, 4);
                                    NoteShowInfo ni = NoteDataHelper.getInfoByMonth(k);
                                    HashMap<String, String> map = new HashMap<>();

                                    String km = k.substring(5, 7);
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

                                    ll_rets.add(new Map.Entry<String, HashMap<String, String>>() {
                                        @Override
                                        public String getKey() {
                                            return ky;
                                        }

                                        @Override
                                        public HashMap<String, String> getValue() {
                                            return map;
                                        }

                                        @Override
                                        public HashMap<String, String> setValue(HashMap<String, String> value) {
                                            return null;
                                        }
                                    });
                                }

                                return ll_rets;
                            });

                    try {
                        mMainPara.addAll(ll_main_rets.get());

                        for(Map.Entry<String, HashMap<String, String>> ret : ll_sub_rets.get()) {
                            LinkedList<HashMap<String, String>> lh = mHMSubPara.get(ret.getKey());
                            if(null == lh)  {
                                lh = new LinkedList<>();
                                mHMSubPara.put(ret.getKey(), lh);
                            }

                            lh.add(ret.getValue());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                },
                () -> loadUIUtility(true));
    }

    @Override
    protected void loadUI() {
        loadUIUtility(false);
    }

    /// BEGIN PRIVATE

    /**
     * 加载UI的工作
     *
     * @param b_fully 若为true则加载数据
     */
    private void loadUIUtility(boolean b_fully) {
        refreshAttachLayout();

        if (b_fully) {
            // update data
            LinkedList<HashMap<String, String>> n_mainpara;
            if (mBFilter) {
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
            } else {
                n_mainpara = mMainPara;
            }

            // 设置listview adapter
            SelfAdapter mSNAdapter = new SelfAdapter(ContextUtil.getInstance(), n_mainpara,
                    new String[]{}, new int[]{});
            mLVShow.setAdapter(mSNAdapter);
            mSNAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 调整数据排序
     */
    private void reorderData() {
        Collections.reverse(mMainPara);
    }

    /**
     * 更新附加layout
     */
    private void refreshAttachLayout() {
        setAttachLayoutVisible(mBFilter || mBSelectSubFilter ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(mBSelectSubFilter ? View.VISIBLE : View.GONE);
    }

    /**
     * 加载详细视图
     *
     * @param lv  视图
     * @param tag 数据tag
     */
    private void load_detail_view(ListView lv, String tag) {
        LinkedList<HashMap<String, String>> llhm = mHMSubPara.get(tag);
        if (!UtilFun.ListIsNullOrEmpty(llhm)) {
            SelfSubAdapter mAdapter = new SelfSubAdapter(getContext(), llhm,
                    new String[]{}, new int[]{});
            lv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ListViewHelper.setListViewHeightBasedOnChildren(lv);
        }
    }
    /// END PRIVATE


    /**
     * 首级adapter
     */
    private class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "MainAdapter";

        SelfAdapter(Context context,
                    List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_yearly_show, from, to);
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

            final HashMap<String, String> hm = UtilFun.cast(getItem(position));
            final ListView lv = viewHolder.getView(R.id.lv_show_detail);
            final String tag = hm.get(K_TAG);
            if (EShowFold.getByName(hm.get(K_SHOW)) == EShowFold.FOLD) {
                lv.setVisibility(View.GONE);
            } else {
                lv.setVisibility(View.VISIBLE);
                if (0 == lv.getCount())
                    load_detail_view(lv, tag);
            }

            View.OnClickListener local_cl = v -> {
                boolean bf = EShowFold.getByName(hm.get(K_SHOW)) == EShowFold.FOLD;
                hm.put(K_SHOW, EShowFold.getByFold(!bf).getName());

                if (bf) {
                    lv.setVisibility(View.VISIBLE);
                    if (0 == lv.getCount())
                        load_detail_view(lv, tag);

                    addUnfoldItem(tag);
                } else {
                    lv.setVisibility(View.GONE);

                    removeUnfoldItem(tag);
                }
            };

            // adjust row color
            ConstraintLayout rl = viewHolder.getView(R.id.cl_header);
            rl.setBackgroundColor(0 == position % 2 ?
                    ResourceHelper.mCRLVLineOne : ResourceHelper.mCRLVLineTwo);
            rl.setOnClickListener(local_cl);

            // for year
            viewHolder.setText(R.id.tv_year, hm.get(K_YEAR));

            // for graph value
            ValueShow vs = viewHolder.getView(R.id.vs_yearly_info);
            HashMap<String, Object> hm_attr = new HashMap<>();
            hm_attr.put(ValueShow.ATTR_PAY_COUNT, hm.get(K_YEAR_PAY_COUNT));
            hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, hm.get(K_YEAR_PAY_AMOUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_COUNT, hm.get(K_YEAR_INCOME_COUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, hm.get(K_YEAR_INCOME_AMOUNT));
            vs.adjustAttribute(hm_attr);
            return viewHolder.getConvertView();
        }
    }


    /**
     * 次级adapter
     */
    private class SelfSubAdapter extends SimpleAdapter {
        private final static String TAG = "SubAdapter";

        SelfSubAdapter(Context context,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_yearly_show_detail, from, to);
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
            ib.setBackgroundColor(mLLSubFilter.contains(sub_tag) ?
                    ResourceHelper.mCRLVItemSel : ResourceHelper.mCRLVItemTransFull);
            ib.setOnClickListener(v -> {
                String sub_tag1 = hm.get(K_SUB_TAG);

                if (!mLLSubFilter.contains(sub_tag1)) {
                    v.setBackgroundColor(ResourceHelper.mCRLVItemSel);

                    mLLSubFilter.add(sub_tag1);
                    mLLSubFilterVW.add(v);

                    if (!mBSelectSubFilter) {
                        mBSelectSubFilter = true;
                        refreshAttachLayout();
                    }
                } else {
                    v.setBackgroundColor(ResourceHelper.mCRLVItemTransFull);

                    mLLSubFilter.remove(sub_tag1);
                    mLLSubFilterVW.remove(v);

                    if (mLLSubFilter.isEmpty()) {
                        mLLSubFilterVW.clear();
                        mBSelectSubFilter = false;
                        refreshAttachLayout();
                    }
                }
            });

            // for show
            viewHolder.setText(R.id.tv_month, hm.get(K_MONTH));

            HelperDayNotesInfo.fillNoteInfo(viewHolder,
                    hm.get(K_MONTH_PAY_COUNT), hm.get(K_MONTH_PAY_AMOUNT),
                    hm.get(K_MONTH_INCOME_COUNT), hm.get(K_MONTH_INCOME_AMOUNT),
                    hm.get(K_AMOUNT));
            return root_view;
        }
    }
}
