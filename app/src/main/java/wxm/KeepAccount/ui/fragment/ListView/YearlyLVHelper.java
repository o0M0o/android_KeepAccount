package wxm.KeepAccount.ui.fragment.ListView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.DataBase.NoteShowInfo;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.fragment.base.LVShowDataBase;

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

    // for expand or hide actions
    private ImageView   mIVActions;
    private GridLayout  mGLActions;
    private boolean     mBActionExpand;
    private Drawable    mDAExpand;
    private Drawable    mDAHide;

    public YearlyLVHelper()    {
        super();

        LOG_TAG = "YearlyLVHelper";
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_newpager, container, false);

        // 附加动作仅支持“更新"
        Resources res = mSelfView.getResources();
        mDAExpand = res.getDrawable(R.drawable.ic_to_up);
        mDAHide = res.getDrawable(R.drawable.ic_to_down);

        mIVActions = UtilFun.cast_t(mSelfView.findViewById(R.id.iv_expand));
        mGLActions = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_action));

        mIVActions.setImageDrawable(mDAExpand);
        setLayoutVisible(mGLActions, View.INVISIBLE);

        mIVActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBActionExpand = !mBActionExpand;
                if(mBActionExpand)  {
                    mIVActions.setImageDrawable(mDAHide);
                    setLayoutVisible(mGLActions, View.VISIBLE);
                } else  {
                    mIVActions.setImageDrawable(mDAExpand);
                    setLayoutVisible(mGLActions, View.INVISIBLE);
                }
            }
        });

        RelativeLayout rl = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_act_add));
        ViewGroup.LayoutParams param = rl.getLayoutParams();
        param.width = 0;
        param.height = 0;
        rl.setLayoutParams(param);

        rl = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_act_delete));
        param = rl.getLayoutParams();
        param.width = 0;
        param.height = 0;
        rl.setLayoutParams(param);

        rl = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_act_refresh));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(v.getContext(), false);
            }
        });

        rl = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_act_sort));
        final ImageView iv_sort = UtilFun.cast_t(rl.findViewById(R.id.iv_sort));
        final TextView tv_sort = UtilFun.cast_t(rl.findViewById(R.id.tv_sort));
        iv_sort.setImageDrawable(mSelfView.getContext().getResources()
                .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
        tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBTimeDownOrder = !mBTimeDownOrder;

                iv_sort.setImageDrawable(mSelfView.getContext().getResources()
                        .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
                tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

                reloadData();
                refreshView();
            }
        });

        return mSelfView;
    }

    @Override
    public void loadView() {
        super.loadView();

        if(ContextUtil.getPayIncomeUtility().getDataLastChangeTime().after(mTSLastLoadViewTime)) {
            reloadData();
        }

        refreshView();
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
    public void onDataChange() {
        if(null != mSelfView) {
            reloadData();
            refreshView();
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
     * 重新加载数据
     */
    private void reloadData() {
        mTSLastLoadViewTime.setTime(Calendar.getInstance().getTimeInMillis());

        mMainPara.clear();
        mHMSubPara.clear();

        // for year
        HashMap<String, NoteShowInfo> hm_y = NoteShowDataHelper.getInstance().getYearInfo();
        ArrayList<String> set_k = new ArrayList<>(hm_y.keySet());
        Collections.sort(set_k, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1);
            }
        });
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
        Collections.sort(set_k_m, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1);
            }
        });
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
            if(ToolUtil.ListIsNullOrEmpty(ls_hm))   {
                ls_hm = new LinkedList<>();
            }

            ls_hm.add(map);
            mHMSubPara.put(ky, ls_hm);
        }
    }

    /**
     * 不重新加载数据，仅更新视图
     */
    @Override
    protected void refreshView()  {
        refreshAttachLayout();

        // update data
        LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
        if(mBFilter) {
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
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
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
            super(context, mdata, R.layout.li_yearly_show, from, to);

            Resources res   = context.getResources();
            mClOne = res.getColor(R.color.color_1);
            mClTwo = res.getColor(R.color.color_2);

            mDAFold = res.getDrawable(R.drawable.ic_hide_1);
            mDAUnFold = res.getDrawable(R.drawable.ic_show_1);
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
                final View pv = v;
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);

                // set fold status
                init_detail_view(pv, hm);
                ib.setImageDrawable(V_SHOW_UNFOLD.equals(hm.get(K_SHOW)) ? mDAFold : mDAUnFold);

                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageButton ib = UtilFun.cast(v);
                        boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
                        hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);
                        init_detail_view(pv, hm);

                        ib.setImageDrawable(bf ? mDAFold : mDAUnFold);
                        if(bf)
                            addUnfoldItem(hm.get(K_TAG));
                        else
                            removeUnfoldItem(hm.get(K_TAG));
                    }
                });

                // adjust line color
                RelativeLayout rl = UtilFun.cast_t(v.findViewById(R.id.rl_header));
                rl.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

                // for show
                TextView tv = UtilFun.cast_t(v.findViewById(R.id.tv_year));
                tv.setText(hm.get(K_YEAR));

                RelativeLayout rl_info = UtilFun.cast_t(v.findViewById(R.id.rl_info));
                fillNoteInfo(rl_info, hm.get(K_YEAR_PAY_COUNT), hm.get(K_YEAR_PAY_AMOUNT),
                        hm.get(K_YEAR_INCOME_COUNT), hm.get(K_YEAR_INCOME_AMOUNT),
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
            super(context, sdata, R.layout.li_yearly_show_detail, from, to);
            //Resources res = getRootActivity().getResources();
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
                final String sub_tag = hm.get(K_SUB_TAG);

                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_action));
                ib.getBackground().setAlpha(mLLSubFilter.contains(sub_tag) ? 255 : 0);

                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageButton ibv = UtilFun.cast_t(v);
                        boolean bsel = mLLSubFilter.contains(sub_tag);
                        ibv.getBackground().setAlpha(!bsel ? 255 : 0);
                        if(!bsel) {
                            mLLSubFilter.add(sub_tag);
                            mLLSubFilterVW.add(v);

                            if(!mBSelectSubFilter) {
                                mBSelectSubFilter = true;
                                refreshAttachLayout();
                            }
                        }   else    {
                            mLLSubFilter.remove(sub_tag);
                            mLLSubFilterVW.remove(v);

                            if(mLLSubFilter.isEmpty()) {
                                mLLSubFilterVW.clear();;
                                mBSelectSubFilter = false;
                                refreshAttachLayout();
                            }
                        }
                    }
                });

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
}
