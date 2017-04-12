package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.OnClick;
import cn.wxm.andriodutillib.util.UtilFun;
import cn.wxm.andriodutillib.util.FastViewHolder;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.extend.ValueShow.ValueShow;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
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
        if ((NoteDataHelper.TAB_TITLE_YEARLY.equals(event.getSender()))
                && (null != e_p)) {
            mBFilter = true;
            mBSelectSubFilter = false;

            mFilterPara.clear();
            mFilterPara.addAll(e_p);
            loadUIUtility(true);
        }
    }


    /**
     * 附加动作
     * @param v   动作view
     */
    @OnClick({R.id.rl_act_sort, R.id.rl_act_refresh})
    public void onActionClick(View v) {
        switch (v.getId())  {
            case R.id.rl_act_sort :     {
                mBTimeDownOrder = !mBTimeDownOrder;

                ImageView iv_sort = UtilFun.cast_t(v.findViewById(R.id.iv_sort));
                TextView tv_sort = UtilFun.cast_t(v.findViewById(R.id.tv_sort));
                iv_sort.setImageDrawable(mBTimeDownOrder ? LVResource.mDASortUp : LVResource.mDASortDown);
                tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

                reorderData();
                loadUIUtility(true);
            }
            break;

            case R.id.rl_act_refresh :  {
                reloadView(v.getContext(), false);
            }
            break;
        }
    }


    /**
     * "接受"或者"取消"后动作
     * @param v     动作view
     */
    @OnClick({R.id.bt_accpet, R.id.bt_giveup})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_accpet :
                if(mBSelectSubFilter) {
                    if(!UtilFun.ListIsNullOrEmpty(mLLSubFilter)) {
                        ACNoteShow ac = getRootActivity();
                        ac.jumpByTabName(NoteDataHelper.TAB_TITLE_DAILY);

                        ArrayList<String> al_s = new ArrayList<>();
                        al_s.addAll(mLLSubFilter);
                        EventBus.getDefault().post(new FilterShowEvent(NoteDataHelper.TAB_TITLE_MONTHLY, al_s));

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

        ImageView iv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.iv_sort));
        TextView tv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.tv_sort));
        iv_sort.setImageDrawable(mBTimeDownOrder ? LVResource.mDASortUp : LVResource.mDASortDown);
        tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
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
                List<String> set_k_m = NoteDataHelper.getNotesMonths();
                Collections.sort(set_k_m, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                for(String k : set_k_m)   {
                    NoteShowInfo ni = NoteDataHelper.getInfoByMonth(k);
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
                List<String> set_k_d = NoteDataHelper.getNotesDays();
                Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));
                for(String k : set_k_d)   {
                    String mk = k.substring(0, 7);
                    NoteShowInfo ni = NoteDataHelper.getInfoByDay(k);
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
     * 加载详细视图
     * @param lv        视图
     * @param tag       数据tag
     */
    private void load_detail_view(ListView lv, String tag)  {
        LinkedList<HashMap<String, String>> llhm = mHMSubPara.get(tag);
        if(!UtilFun.ListIsNullOrEmpty(llhm))    {
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
        private final static String TAG = "SelfAdapter";

        SelfAdapter(Context context,
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
            FastViewHolder viewHolder = FastViewHolder.get(getRootActivity(),
                                    view, R.layout.li_monthly_show);

            final HashMap<String, String> hm = UtilFun.cast(getItem(position));
            final ListView lv = viewHolder.getView(R.id.lv_show_detail);
            final String tag = hm.get(K_TAG);
            if(V_SHOW_FOLD.equals(hm.get(K_SHOW))) {
                lv.setVisibility(View.GONE);
            } else {
                lv.setVisibility(View.VISIBLE);
                if(0 == lv.getCount())
                    load_detail_view(lv, tag);
            }

            View.OnClickListener local_cl = v -> {
                boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
                hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);

                if (bf) {
                    lv.setVisibility(View.VISIBLE);
                    if(0 == lv.getCount())
                        load_detail_view(lv, tag);

                    addUnfoldItem(tag);
                } else {
                    lv.setVisibility(View.GONE);

                    removeUnfoldItem(tag);
                }
            };

            ConstraintLayout rl = viewHolder.getView(R.id.cl_header);
            rl.setBackgroundColor(0 == position % 2 ?
                    LVResource.mCRLVLineOne : LVResource.mCRLVLineTwo);
            rl.setOnClickListener(local_cl);

            // for month
            viewHolder.setText(R.id.tv_month, hm.get(K_MONTH));

            // for graph value
            ValueShow vs = viewHolder.getView(R.id.vs_monthly_info);
            HashMap<String, Object> hm_attr = new HashMap<>();
            hm_attr.put(ValueShow.ATTR_PAY_COUNT, hm.get(K_MONTH_PAY_COUNT));
            hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, hm.get(K_MONTH_PAY_AMOUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_COUNT, hm.get(K_MONTH_INCOME_COUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, hm.get(K_MONTH_INCOME_AMOUNT));
            vs.adjustAttribute(hm_attr);
            return viewHolder.getConvertView();
        }
    }


    /**
     * 次级adapter
     */
    private class SelfSubAdapter  extends SimpleAdapter  {
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
            FastViewHolder viewHolder = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_monthly_show_detail);

            View root_view = viewHolder.getConvertView();
            HashMap<String, String> hm = UtilFun.cast(getItem(position));
            String sub_tag = hm.get(K_SUB_TAG);

            ImageView ib = viewHolder.getView(R.id.iv_action);
            ib.setBackgroundColor(mLLSubFilter.contains(sub_tag) ?
                    LVResource.mCRLVItemSel : LVResource.mCRLVItemNoSel);
            ib.setOnClickListener(v -> {
                String sub_tag1 = hm.get(K_SUB_TAG);

                if(!mLLSubFilter.contains(sub_tag1)) {
                    v.setBackgroundColor(LVResource.mCRLVItemSel);

                    mLLSubFilter.add(sub_tag1);
                    mLLSubFilterVW.add(v);

                    if(!mBSelectSubFilter) {
                        mBSelectSubFilter = true;
                        refreshAttachLayout();
                    }
                }   else {
                    v.setBackgroundColor(LVResource.mCRLVItemNoSel);

                    mLLSubFilter.remove(sub_tag1);
                    mLLSubFilterVW.remove(v);

                    if(mLLSubFilter.isEmpty()) {
                        mLLSubFilterVW.clear();
                        mBSelectSubFilter = false;
                        refreshAttachLayout();
                    }
                }
            });

            // for show
            viewHolder.setText(R.id.tv_day_number, hm.get(K_DAY_NUMEBER));
            viewHolder.setText(R.id.tv_day_in_week, hm.get(K_DAY_IN_WEEK));

            // for graph value
            ValueShow vs = viewHolder.getView(R.id.vs_daily_info);
            HashMap<String, Object> hm_attr = new HashMap<>();
            hm_attr.put(ValueShow.ATTR_PAY_COUNT, hm.get(K_DAY_PAY_COUNT));
            hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, hm.get(K_DAY_PAY_AMOUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_COUNT, hm.get(K_DAY_INCOME_COUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, hm.get(K_DAY_INCOME_AMOUNT));
            vs.adjustAttribute(hm_attr);
            return root_view;
        }
    }
}
