package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
import wxm.KeepAccount.ui.base.Adapter.LVAdapter;
import wxm.androidutil.ViewHolder.ViewDataHolder;
import wxm.androidutil.ViewHolder.ViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.data.show.note.base.ValueShow;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.uilib.IconButton.IconButton;

/**
 * ListView for monthly data
 * Created by WangXM on 2016/9/10.
 */
public class LVMonthly
        extends LVBase {
    private static Calendar CL_DAY = Calendar.getInstance();

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;

    class MainAdapterItem   {
        public String  tag;
        public String  show;

        public String  month;
        public recordDetail monthDetail;
        public String  amount;

        MainAdapterItem()    {
            show = EShowFold.FOLD.getName();
            monthDetail = new recordDetail();
        }
    }

    class MainItemHolder extends ViewDataHolder<String, MainAdapterItem>    {
        public MainItemHolder(String tag)   {
            super(tag);
        }

        @Override
        protected MainAdapterItem getDataByTag(String tag) {
            NoteShowInfo ni = NoteDataHelper.getInfoByMonth(tag);
            MainAdapterItem map = new MainAdapterItem();
            map.month = tag;
            map.monthDetail.mPayCount = String.valueOf(ni.getPayCount());
            map.monthDetail.mIncomeCount = String.valueOf(ni.getIncomeCount());
            map.monthDetail.mPayAmount = String.format(Locale.CHINA,
                    "%.02f", ni.getPayAmount());
            map.monthDetail.mIncomeAmount = String.format(Locale.CHINA,
                    "%.02f", ni.getIncomeAmount());

            BigDecimal bd_l = ni.getBalance();
            map.amount = String.format(Locale.CHINA,
                    (0 < bd_l.floatValue()) ? "+ %.02f" : "%.02f", bd_l);

            map.tag = tag;
            map.show = EShowFold.getByFold(!checkUnfoldItem(tag)).getName();

            return map;
        }
    }
    protected final LinkedList<MainItemHolder> mMainPara;

    class SubAdapterItem   {
        public String  tag;
        public String  subTag;

        public String  dayNumber;
        public String  dayInWeek;
        public recordDetail dayDetail;
        public String  amount;

        SubAdapterItem()    {
            dayDetail = new recordDetail();
        }
    }

    class SubItemHolder extends ViewDataHolder<String, SubAdapterItem> {
        public SubItemHolder(String tag)   {
            super(tag);
        }

        @Override
        protected SubAdapterItem getDataByTag(String tag) {
            String mk = tag.substring(0, 7);
            NoteShowInfo ni = NoteDataHelper.getInfoByDay(tag);
            SubAdapterItem map = new SubAdapterItem();

            String km = tag.substring(8, 10);
            km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
            map.dayNumber = km;

            int year = Integer.valueOf(tag.substring(0, 4));
            int month = Integer.valueOf(tag.substring(5, 7));
            int day = Integer.valueOf(tag.substring(8, 10));
            CL_DAY.set(year, month, day);
            map.dayInWeek = ToolUtil.getDayInWeek(CL_DAY.get(Calendar.DAY_OF_WEEK));

            map.dayDetail.mPayCount = String.valueOf(ni.getPayCount());
            map.dayDetail.mIncomeCount = String.valueOf(ni.getIncomeCount());
            map.dayDetail.mPayAmount = String.format(Locale.CHINA,
                    "%.02f", ni.getPayAmount());
            map.dayDetail.mIncomeAmount = String.format(Locale.CHINA,
                    "%.02f", ni.getIncomeAmount());

            BigDecimal bd_l = ni.getBalance();
            map.amount = String.format(Locale.CHINA,
                    (0 < bd_l.floatValue()) ? "+ %.02f" : "%.02f", bd_l);

            map.tag = mk;
            map.subTag = tag;

            return map;
        }
    }
    protected final HashMap<String, LinkedList<SubItemHolder>> mHMSubPara;


    class MonthlyActionHelper extends ActionHelper    {
        @BindView(R.id.ib_sort)
        IconButton mIBSort;

        @BindView(R.id.ib_report)
        IconButton mIBReport;

        @BindView(R.id.ib_add)
        IconButton mIBAdd;

        @BindView(R.id.ib_delete)
        IconButton mIBDelete;

        MonthlyActionHelper() {
            super();
        }

        @Override
        protected void initActs() {
            mIBReport.setVisibility(View.GONE);
            mIBAdd.setVisibility(View.GONE);
            mIBDelete.setVisibility(View.GONE);

            mIBSort.setActIcon(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
            mIBSort.setActName(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
        }

        @OnClick({R.id.ib_sort, R.id.ib_refresh})
        public void onActionClick(View v) {
            switch (v.getId()) {
                case R.id.ib_sort: {
                    mBTimeDownOrder = !mBTimeDownOrder;

                    mIBSort.setActIcon(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
                    mIBSort.setActName(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

                    reorderData();
                    loadUI(null);
                }
                break;

                case R.id.ib_refresh: {
                    reloadView(v.getContext(), false);
                }
                break;
            }
        }
    }


    public LVMonthly() {
        super();

        mBActionExpand = false;

        mMainPara = new LinkedList<>();
        mHMSubPara = new HashMap<>();
        mAHActs = new MonthlyActionHelper();
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    /**
     * filter view
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
        List<String> e_p = event.getFilterTag();
        if ((NoteDataHelper.TAB_TITLE_YEARLY.equals(event.getSender()))
                && (null != e_p)) {
            mBFilter = true;
            mBSelectSubFilter = false;

            mFilterPara.clear();
            mFilterPara.addAll(e_p);
            loadUI(null);
        }
    }

    /**
     * 'accpet' or 'giveup' click
     * @param v         action view
     */
    @OnClick({R.id.bt_accpet, R.id.bt_giveup, R.id.bt_giveup_filter})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet: {
                if (mBSelectSubFilter) {
                    if (!UtilFun.ListIsNullOrEmpty(mLLSubFilter)) {
                        ACNoteShow ac = getRootActivity();
                        ac.jumpByTabName(NoteDataHelper.TAB_TITLE_DAILY);

                        ArrayList<String> al_s = new ArrayList<>(mLLSubFilter);
                        EventBus.getDefault().post(new FilterShowEvent(NoteDataHelper.TAB_TITLE_MONTHLY, al_s));

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
            }
            break;

            case R.id.bt_giveup: {
                mBSelectSubFilter = false;
                mLLSubFilter.clear();

                for (View i : mLLSubFilterVW) {
                    i.setSelected(false);
                    i.getBackground().setAlpha(0);
                }
                mLLSubFilterVW.clear();

                refreshAttachLayout();
            }
            break;

            case R.id.bt_giveup_filter: {
                mBFilter = false;
                loadUI(null);
            }
            break;
        }
    }

    @Override
    protected void initUI(Bundle bundle) {
        super.initUI(bundle);

        ToolUtil.runInBackground(getActivity(),
                () -> {
                    mMainPara.clear();
                    mHMSubPara.clear();

                    // for month
                    List<String> set_k_m = NoteDataHelper.getNotesMonths();
                    Collections.sort(set_k_m, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                    for(String k : set_k_m) {
                        mMainPara.add(new MainItemHolder(k));
                    }

                    // for day
                    List<String> set_k_d = NoteDataHelper.getNotesDays();
                    Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                    for(String k : set_k_d) {
                        String km = k.substring(0, 7);
                        LinkedList<SubItemHolder> lsDay = mHMSubPara.get(km);
                        if(null == lsDay)   {
                            lsDay = new LinkedList<>();
                            mHMSubPara.put(km, lsDay);
                        }
                        lsDay.add(new SubItemHolder(k));
                    }
                },
                () -> loadUI(bundle));
    }

    @Override
    protected void loadUI(Bundle bundle) {
        // adjust attach layout
        refreshAttachLayout();

        // load show data
        LinkedList<MainItemHolder> n_mainpara;
        if (mBFilter) {
            n_mainpara = new LinkedList<>();
            for (MainItemHolder i : mMainPara) {
                for (String ii : mFilterPara) {
                    if (i.getTag().equals(ii)) {
                        n_mainpara.add(i);
                        break;
                    }
                }
            }
        } else {
            n_mainpara = mMainPara;
        }

        // 设置listview adapter
        MonthAdapter mSNAdapter = new MonthAdapter(ContextUtil.getInstance(), n_mainpara);
        mLVShow.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    /// BEGIN PRIVATE

    /**
     * adjust data order
     */
    private void reorderData() {
        Collections.reverse(mMainPara);
    }

    /**
     * update attach layout
     */
    private void refreshAttachLayout() {
        setAttachLayoutVisible(mBFilter || mBSelectSubFilter ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(mBSelectSubFilter ? View.VISIBLE : View.GONE);
    }

    /**
     * load detail view
     * @param lv        view need show detail view
     * @param tag       tag for data
     */
    private void load_detail_view(ListView lv, String tag) {
        LinkedList<SubItemHolder> llhm = mHMSubPara.get(tag);
        if (!UtilFun.ListIsNullOrEmpty(llhm)) {
            DayAdapter mAdapter = new DayAdapter(getContext(), llhm);
            lv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ListViewHelper.setListViewHeightBasedOnChildren(lv);
        }
    }
    /// END PRIVATE

    /**
     * month data adapter
     */
    private class MonthAdapter extends LVAdapter {
        private static final int SELF_TAG_ID = 0;

        MonthAdapter(Context context, List<?> mdata)  {
            super(context, mdata, R.layout.li_monthly_show);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            ViewHolder viewHolder = ViewHolder.get(getRootActivity(),
                    view, R.layout.li_monthly_show);
            if(null == viewHolder.getSelfTag(SELF_TAG_ID))   {
                viewHolder.setSelfTag(SELF_TAG_ID, new Object());

                final MainItemHolder holder = UtilFun.cast(getItem(position));
                final MainAdapterItem item = holder.getData();
                final ListView lv = viewHolder.getView(R.id.lv_show_detail);
                final String tag = item.tag;
                if (EShowFold.getByName(item.show) == EShowFold.FOLD) {
                    lv.setVisibility(View.GONE);
                } else {
                    lv.setVisibility(View.VISIBLE);
                    if (0 == lv.getCount())
                        load_detail_view(lv, tag);
                }

                View.OnClickListener local_cl = v -> {
                    boolean bf = EShowFold.getByName(item.show) == EShowFold.FOLD;
                    item.show = EShowFold.getByFold(!bf).getName();

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

                ConstraintLayout rl = viewHolder.getView(R.id.cl_header);
                rl.setBackgroundColor(0 == position % 2 ?
                        ResourceHelper.mCRLVLineOne : ResourceHelper.mCRLVLineTwo);
                rl.setOnClickListener(local_cl);

                // for month
                viewHolder.setText(R.id.tv_month, item.month);

                // for graph value
                ValueShow vs = viewHolder.getView(R.id.vs_monthly_info);
                HashMap<String, Object> hm_attr = new HashMap<>();
                hm_attr.put(ValueShow.ATTR_PAY_COUNT, item.monthDetail.mPayCount);
                hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, item.monthDetail.mPayAmount);
                hm_attr.put(ValueShow.ATTR_INCOME_COUNT, item.monthDetail.mIncomeCount);
                hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, item.monthDetail.mIncomeAmount);
                vs.adjustAttribute(hm_attr);
            }
            return viewHolder.getConvertView();
        }
    }


    /**
     * day data adapter
     */
    private class DayAdapter extends LVAdapter {
        private static final int SELF_TAG_ID = 0;

        DayAdapter(Context context, List<?> sdata)  {
            super(context, sdata, R.layout.li_monthly_show_detail);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            ViewHolder viewHolder = ViewHolder.get(getRootActivity(),
                    view, R.layout.li_monthly_show_detail);
            if(null == viewHolder.getSelfTag(SELF_TAG_ID))   {
                viewHolder.setSelfTag(SELF_TAG_ID, new Object());

                SubItemHolder holder = UtilFun.cast(getItem(position));
                SubAdapterItem item = holder.getData();
                String sub_tag = item.subTag;

                ImageView ib = viewHolder.getView(R.id.iv_action);
                ib.setBackgroundColor(mLLSubFilter.contains(sub_tag) ?
                        ResourceHelper.mCRLVItemSel : ResourceHelper.mCRLVItemTransFull);
                ib.setOnClickListener(v -> {
                    String sub_tag1 = item.subTag;

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
                viewHolder.setText(R.id.tv_day_number, item.dayNumber);
                viewHolder.setText(R.id.tv_day_in_week, item.dayInWeek);

                // for graph value
                ValueShow vs = viewHolder.getView(R.id.vs_daily_info);
                HashMap<String, Object> hm_attr = new HashMap<>();
                hm_attr.put(ValueShow.ATTR_PAY_COUNT, item.dayDetail.mPayCount);
                hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, item.dayDetail.mPayAmount);
                hm_attr.put(ValueShow.ATTR_INCOME_COUNT, item.dayDetail.mIncomeCount);
                hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, item.dayDetail.mIncomeAmount);
                vs.adjustAttribute(hm_attr);
            }
            return viewHolder.getConvertView();
        }
    }
}
