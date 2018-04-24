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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.ui.base.Adapter.LVAdapter;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.androidutil.ViewHolder.ViewDataHolder;
import wxm.androidutil.ViewHolder.ViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.data.show.note.base.ValueShow;
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.uilib.IconButton.IconButton;

/**
 * 年数据视图辅助类
 * Created by WangXM on 2016/9/10.
 */
public class LVYearly extends LVBase {
    private final LinkedList<String> mLLSubFilter = new LinkedList<>();
    private final LinkedList<View> mLLSubFilterVW = new LinkedList<>();

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;
    private boolean mBSelectSubFilter = false;

    // for main listview
    class MainAdapterItem   {
        public String  tag;
        public String  show;

        public String  year;
        public recordDetail yearDetail;
        public String  amount;

        MainAdapterItem()    {
            show = EShowFold.FOLD.getName();
            yearDetail = new recordDetail();
        }
    }

    class MainItemHolder extends ViewDataHolder<String, MainAdapterItem> {
        public MainItemHolder(String tag)   {
            super(tag);
        }

        @Override
        protected MainAdapterItem getDataByTag(String tag) {
            NoteShowInfo ni = NoteDataHelper.Companion.getInfoByYear(tag);

            MainAdapterItem map = new MainAdapterItem();
            map.year = tag;
            map.yearDetail.mPayCount = String.valueOf(ni.getPayCount());
            map.yearDetail.mIncomeCount = String.valueOf(ni.getIncomeCount());
            map.yearDetail.mPayAmount = String.format(Locale.CHINA,
                    "%.02f", ni.getPayAmount());
            map.yearDetail.mIncomeAmount = String.format(Locale.CHINA,
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

    // for sub listview
    class SubAdapterItem   {
        public String  tag;
        public String  subTag;

        public String  month;
        public recordDetail monthDetail;
        public String  amount;

        SubAdapterItem()    {
            monthDetail = new recordDetail();
        }
    }

    class SubItemHolder extends ViewDataHolder<String, SubAdapterItem>    {
        public SubItemHolder(String tag)   {
            super(tag);
        }

        @Override
        protected SubAdapterItem getDataByTag(String tag) {
            String ky = tag.substring(0, 4);
            NoteShowInfo ni = NoteDataHelper.Companion.getInfoByMonth(tag);
            SubAdapterItem map = new SubAdapterItem();

            String km = tag.substring(5, 7);
            km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
            map.month = km;
            map.monthDetail.mPayCount = String.valueOf(ni.getPayCount());
            map.monthDetail.mIncomeCount = String.valueOf(ni.getIncomeCount());
            map.monthDetail.mPayAmount = String.format(Locale.CHINA,
                    "%.02f", ni.getPayAmount());
            map.monthDetail.mIncomeAmount = String.format(Locale.CHINA,
                    "%.02f", ni.getIncomeAmount());

            BigDecimal bd_l = ni.getBalance();
            map.amount = String.format(Locale.CHINA,
                    (0 < bd_l.floatValue()) ? "+ %.02f" : "%.02f", bd_l);

            map.tag = ky;
            map.subTag = tag;
            return map;
        }
    }
    protected final HashMap<String, LinkedList<SubItemHolder>> mHMSubPara;


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


    public LVYearly() {
        super();
        mBActionExpand = false;

        mMainPara = new LinkedList<>();
        mHMSubPara = new HashMap<>();
        mAHActs = new YearlyActionHelper();
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
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

                        ArrayList<String> al_s = new ArrayList<>(mLLSubFilter);
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
    protected void initUI(Bundle bundle) {
        super.initUI(bundle);

        ToolUtil.INSTANCE.runInBackground(getActivity(),
                () -> {
                    mMainPara.clear();
                    mHMSubPara.clear();

                    // for year
                    List<String> set_k = NoteDataHelper.Companion.getNotesYears();
                    Collections.sort(set_k, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                    for(String k : set_k) {
                        mMainPara.add(new MainItemHolder(k));
                    }

                    // for month
                    List<String> set_k_m = NoteDataHelper.Companion.getNotesMonths();
                    Collections.sort(set_k_m, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                    for(String k : set_k_m) {
                        String ky = k.substring(0, 4);
                        LinkedList<SubItemHolder> lsDay = mHMSubPara.get(ky);
                        if(null == lsDay)   {
                            lsDay = new LinkedList<>();
                            mHMSubPara.put(ky, lsDay);
                        }
                        lsDay.add(new SubItemHolder(k));
                    }
                },
                () -> loadUI(bundle));
    }

    @Override
    protected void loadUI(Bundle bundle) {
        refreshAttachLayout();

        // update data
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
        YearAdapter mSNAdapter = new YearAdapter(ContextUtil.Companion.getInstance(), n_mainpara);
        mLVShow.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    /// BEGIN PRIVATE

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
     * load detail view(for month)
     * @param lv    for view
     * @param tag   tag for data
     */
    private void load_detail_view(ListView lv, String tag) {
        LinkedList<SubItemHolder> llhm = mHMSubPara.get(tag);
        if (!UtilFun.ListIsNullOrEmpty(llhm)) {
            MonthAdapter mAdapter = new MonthAdapter(getContext(), llhm);
            lv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ListViewHelper.INSTANCE.setListViewHeightBasedOnChildren(lv);
        }
    }
    /// END PRIVATE


    /**
     * year item view adapter
     */
    private class YearAdapter extends LVAdapter {
        private static final int SELF_TAG_ID = 0;

        YearAdapter(Context context, List<?> mdata)    {
            super(context, mdata, R.layout.li_yearly_show);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            ViewHolder viewHolder = ViewHolder.get(getRootActivity(), view,
                                        R.layout.li_yearly_show);
            if(null == viewHolder.getSelfTag(SELF_TAG_ID))   {
                viewHolder.setSelfTag(SELF_TAG_ID, new Object());

                final MainItemHolder holder = UtilFun.cast(getItem(position));
                final MainAdapterItem hm = holder.getData();
                final ListView lv = viewHolder.getView(R.id.lv_show_detail);
                final String tag = hm.tag;
                if (EShowFold.getByName(hm.show) == EShowFold.FOLD) {
                    lv.setVisibility(View.GONE);
                } else {
                    lv.setVisibility(View.VISIBLE);
                    if (0 == lv.getCount())
                        load_detail_view(lv, tag);
                }

                View.OnClickListener local_cl = v -> {
                    boolean bf = EShowFold.getByName(hm.show) == EShowFold.FOLD;
                    hm.show = EShowFold.getByFold(!bf).getName();

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
                        ResourceHelper.INSTANCE.getMCRLVLineOne() : ResourceHelper.INSTANCE.getMCRLVLineTwo());
                rl.setOnClickListener(local_cl);

                // for year
                viewHolder.setText(R.id.tv_year, hm.year);

                // for graph value
                ValueShow vs = viewHolder.getView(R.id.vs_yearly_info);
                HashMap<String, Object> hm_attr = new HashMap<>();
                hm_attr.put(ValueShow.ATTR_PAY_COUNT, hm.yearDetail.mPayCount);
                hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, hm.yearDetail.mPayAmount);
                hm_attr.put(ValueShow.ATTR_INCOME_COUNT, hm.yearDetail.mIncomeCount);
                hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, hm.yearDetail.mIncomeAmount);
                vs.adjustAttribute(hm_attr);
            }
            return viewHolder.getConvertView();
        }
    }


    /**
     * month item view adapter
     */
    private class MonthAdapter extends LVAdapter {
        private static final int SELF_TAG_ID = 0;

        MonthAdapter(Context context, List<?> sdata)    {
            super(context, sdata, R.layout.li_yearly_show_detail);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            ViewHolder viewHolder = ViewHolder.get(getRootActivity(),
                    view, R.layout.li_yearly_show_detail);
            if(null == viewHolder.getSelfTag(SELF_TAG_ID))   {
                viewHolder.setSelfTag(SELF_TAG_ID, new Object());

                final SubItemHolder holder = UtilFun.cast(getItem(position));
                final SubAdapterItem hm = holder.getData();
                final String sub_tag = hm.subTag;

                final ImageView ib = viewHolder.getView(R.id.iv_action);
                ib.setBackgroundColor(mLLSubFilter.contains(sub_tag) ?
                        ResourceHelper.INSTANCE.getMCRLVItemSel() : ResourceHelper.INSTANCE.getMCRLVItemTransFull());
                ib.setOnClickListener(v -> {
                    String sub_tag1 = hm.subTag;

                    if (!mLLSubFilter.contains(sub_tag1)) {
                        v.setBackgroundColor(ResourceHelper.INSTANCE.getMCRLVItemSel());

                        mLLSubFilter.add(sub_tag1);
                        mLLSubFilterVW.add(v);

                        if (!mBSelectSubFilter) {
                            mBSelectSubFilter = true;
                            refreshAttachLayout();
                        }
                    } else {
                        v.setBackgroundColor(ResourceHelper.INSTANCE.getMCRLVItemTransFull());

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
                viewHolder.setText(R.id.tv_month, hm.month);

                HelperDayNotesInfo.INSTANCE.fillNoteInfo(viewHolder,
                        hm.monthDetail.mPayCount, hm.monthDetail.mPayAmount,
                        hm.monthDetail.mIncomeCount, hm.monthDetail.mIncomeAmount,
                        hm.amount);
            }
            return viewHolder.getConvertView();
        }
    }
}
