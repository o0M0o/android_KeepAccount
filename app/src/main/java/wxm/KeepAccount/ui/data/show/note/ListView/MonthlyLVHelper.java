package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.utility.FastViewHolder;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo;

/**
 * 月数据辅助类
 * Created by 123 on 2016/9/10.
 */
public class MonthlyLVHelper
        extends LVShowDataBase {
    private final static String TAG = "MonthlyLVHelper";

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;


    public MonthlyLVHelper()    {
        super();

        LOG_TAG = "MonthlyLVHelper";
        mBActionExpand = false;
    }


    /**
     * 过滤视图事件
     * @param event     事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
        List<String> e_p = event.getFilterTag();
        if ((NoteShowDataHelper.TAB_TITLE_YEARLY.equals(event.getSender()))
                && (null != e_p)) {
            mBFilter = true;
            mBSelectSubFilter = false;

            mFilterPara.clear();
            mFilterPara.addAll(e_p);
            loadUIUtility(true);
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
                        ac.jumpByTabName(NoteShowDataHelper.TAB_TITLE_DAILY);

                        ArrayList<String> al_s = new ArrayList<>();
                        al_s.addAll(mLLSubFilter);
                        EventBus.getDefault().post(new FilterShowEvent(NoteShowDataHelper.TAB_TITLE_MONTHLY, al_s));

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

            reorderData();
            loadUIUtility(true);
        });
    }


    /**
     * 重新加载数据
     */
    @Override
    protected void refreshData() {
        super.refreshData();

        mMainPara.clear();
        mHMSubPara.clear();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // for month
                HashMap<String, NoteShowInfo> hm_m = NoteShowDataHelper.getInstance().getMonthInfo();
                ArrayList<String> set_k_m = new ArrayList<>(hm_m.keySet());
                Collections.sort(set_k_m, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                for(String k : set_k_m)   {
                    NoteShowInfo ni = hm_m.get(k);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(K_MONTH, k);
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

                    map.put(K_TAG, k);
                    map.put(K_SHOW, checkUnfoldItem(k) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
                    mMainPara.add(map);
                }

                // for day
                HashMap<String, NoteShowInfo> hm_d = NoteShowDataHelper.getInstance().getDayInfo();
                ArrayList<String> set_k_d = new ArrayList<>(hm_d.keySet());
                Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                for(String k : set_k_d)   {
                    String mk = k.substring(0, 7);
                    NoteShowInfo ni = hm_d.get(k);
                    HashMap<String, String> map = new HashMap<>();

                    String km = k.substring(8, 10);
                    km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
                    map.put(K_DAY_NUMEBER, km);

                    int year  = Integer.valueOf(k.substring(0, 4));
                    int month = Integer.valueOf(k.substring(5, 7));
                    int day   = Integer.valueOf(k.substring(8, 10));
                    Calendar cl_day = Calendar.getInstance();
                    cl_day.set(year, month, day);
                    map.put(K_DAY_IN_WEEK, ToolUtil.getDayInWeek(cl_day.get(Calendar.DAY_OF_WEEK)));

                    map.put(K_DAY_PAY_COUNT, String.valueOf(ni.getPayCount()));
                    map.put(K_DAY_INCOME_COUNT, String.valueOf(ni.getIncomeCount()));
                    map.put(K_DAY_PAY_AMOUNT, String.format(Locale.CHINA,
                            "%.02f", ni.getPayAmount()));
                    map.put(K_DAY_INCOME_AMOUNT, String.format(Locale.CHINA,
                            "%.02f", ni.getIncomeAmount()));

                    BigDecimal bd_l = ni.getBalance();
                    String v_l = String.format(Locale.CHINA,
                            0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
                    map.put(K_AMOUNT, v_l);

                    map.put(K_TAG, mk);
                    map.put(K_SUB_TAG, k);

                    LinkedList<HashMap<String, String>>  ls_hm = mHMSubPara.get(mk);
                    if(UtilFun.ListIsNullOrEmpty(ls_hm))   {
                        ls_hm = new LinkedList<>();
                    }

                    ls_hm.add(map);
                    mHMSubPara.put(mk, ls_hm);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // After completing execution of given task, control will return here.
                // Hence if you want to populate UI elements with fetched data, do it here.
                loadUIUtility(true);
            }
        }.execute();
    }

    @Override
    protected void loadUI() {
        loadUIUtility(false);
    }

    /// BEGIN PRIVATE

    /**
     * 加载UI的工作
     * @param b_fully   若为true则加载数据
     */
    private void loadUIUtility(boolean b_fully)    {
        // adjust attach layout
        refreshAttachLayout();

        if(b_fully) {
            // load show data
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
            SelfAdapter mSNAdapter = new SelfAdapter(ContextUtil.getInstance(), n_mainpara,
                    new String[]{}, new int[]{});
            mLVShow.setAdapter(mSNAdapter);
            mSNAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 调整数据排序
     */
    private void reorderData()  {
        Collections.reverse(mMainPara);
    }

    /**
     * 更新附加layout
     */
    private void refreshAttachLayout()    {
        setAttachLayoutVisible(mBFilter || mBSelectSubFilter ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(mBSelectSubFilter ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新详细显示信息
     * @param vh    viewholder
     * @param hm    数据
     */
    private void init_detail_view(FastViewHolder vh, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if(V_SHOW_UNFOLD.equals(hm.get(K_SHOW))) {
            llhm = mHMSubPara.get(hm.get(K_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        RelativeLayout rl = vh.getView(R.id.rl_detail);
        if(llhm.isEmpty())  {
            rl.setVisibility(View.GONE);
        } else {
            rl.setVisibility(View.VISIBLE);

            // init sub adapter
            ListView mLVShowDetail = vh.getView(R.id.lv_show_detail);
            SelfSubAdapter mAdapter = new SelfSubAdapter(getContext(), llhm,
                    new String[]{}, new int[]{});
            mLVShowDetail.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ListViewHelper.setListViewHeightBasedOnChildren(mLVShowDetail);
        }
    }
    /// END PRIVATE

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
            super(context, mdata, R.layout.li_monthly_show, from, to);

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
                                    view, R.layout.li_monthly_show);

            View root_view = viewHolder.getConvertView();
            HashMap<String, String> hm = UtilFun.cast(getItem(position));
            init_detail_view(viewHolder, hm);

            root_view.setOnClickListener(this);

            RelativeLayout rl = viewHolder.getView(R.id.rl_header);
            rl.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

            // for show
            TextView tv = viewHolder.getView(R.id.tv_month);
            tv.setText(hm.get(K_MONTH));

            HelperDayNotesInfo.fillNoteInfo(viewHolder,
                    hm.get(K_MONTH_PAY_COUNT), hm.get(K_MONTH_PAY_AMOUNT),
                    hm.get(K_MONTH_INCOME_COUNT), hm.get(K_MONTH_INCOME_AMOUNT),
                    hm.get(K_AMOUNT));
            return viewHolder.getConvertView();
        }

        @Override
        public void onClick(View v) {
            int pos = mLVShow.getPositionForView(v);

            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
            hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);

            init_detail_view(UtilFun.cast_t(mLVShow.getChildAt(pos).getTag()), hm);
            if (bf)
                addUnfoldItem(hm.get(K_TAG));
            else
                removeUnfoldItem(hm.get(K_TAG));
        }
    }


    /**
     * 次级adapter
     */
    private class SelfSubAdapter  extends SimpleAdapter  {
        private final static String TAG = "SelfSubAdapter";

        private int         mCINoSel;
        private int         mCISel;

        SelfSubAdapter(Context context,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_monthly_show_detail, from, to);

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
                    view, R.layout.li_monthly_show_detail);

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
            TextView tv = viewHolder.getView(R.id.tv_day_number);
            tv.setText(hm.get(K_DAY_NUMEBER));

            tv = viewHolder.getView(R.id.tv_day_in_week);
            tv.setText(hm.get(K_DAY_IN_WEEK));

            HelperDayNotesInfo.fillNoteInfo(viewHolder,
                    hm.get(K_DAY_PAY_COUNT), hm.get(K_DAY_PAY_AMOUNT),
                    hm.get(K_DAY_INCOME_COUNT), hm.get(K_DAY_INCOME_AMOUNT),
                    hm.get(K_AMOUNT));
            return root_view;
        }
    }
}
