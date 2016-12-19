package wxm.KeepAccount.ui.fragment.ListView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import butterknife.ButterKnife;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.BudgetItem;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.DataBase.NoteShowInfo;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.acutility.ACPreveiwAndEdit;
import wxm.KeepAccount.ui.fragment.base.LVShowDataBase;

/**
 * 日数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class DailyLVHelper extends LVShowDataBase
        implements OnClickListener {
    private final static String TAG = "DailyLVHelper";

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;

    /// list item data begin
    final static String V_TYPE_DAY = "v_day";
    final static String V_TYPE_PAY = "v_pay";
    final static String V_TYPE_INCOME = "v_income";
    /// list item data end

    // for action
    //private final static int ACTION_NONE    = 0;
    private final static int ACTION_DELETE = 1;
    private final static int ACTION_EDIT = 2;
    private int mActionType = ACTION_EDIT;

    // delete data
    private final LinkedList<Integer> mDelPay;
    private final LinkedList<Integer> mDelIncome;

    public DailyLVHelper() {
        super();

        LOG_TAG = "DailyLVHelper";
        mDelPay = new LinkedList<>();
        mDelIncome = new LinkedList<>();

        mBActionExpand = false;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_newpager, container, false);
        mBFilter = false;
        ButterKnife.bind(this, mSelfView);

        refreshAttachLayout();
        initActs(mSelfView);
        return mSelfView;
    }

    /**
     * 初始化可隐藏动作条
     *
     * @param pv 视图
     */
    private void initActs(View pv) {
        refreshAction();

        mIVActions.setOnClickListener(v -> {
            mBActionExpand = !mBActionExpand;
            refreshAction();
        });

        RelativeLayout rl = UtilFun.cast_t(pv.findViewById(R.id.rl_act_add));
        rl.setOnClickListener(v -> {
            ACNoteShow ac = getRootActivity();
            Intent intent = new Intent(ac, ACNoteEdit.class);
            intent.putExtra(ACNoteEdit.PARA_ACTION, GlobalDef.STR_CREATE);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            intent.putExtra(GlobalDef.STR_RECORD_DATE,
                    String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d"
                            , cal.get(Calendar.YEAR)
                            , cal.get(Calendar.MONTH) + 1
                            , cal.get(Calendar.DAY_OF_MONTH)
                            , cal.get(Calendar.HOUR_OF_DAY)
                            , cal.get(Calendar.MINUTE)));

            ac.startActivityForResult(intent, 1);
        });

        rl = UtilFun.cast_t(pv.findViewById(R.id.rl_act_delete));
        rl.setOnClickListener(v -> {
            mActionType = ACTION_DELETE;
            refreshView();
        });

        rl = UtilFun.cast_t(pv.findViewById(R.id.rl_act_refresh));
        rl.setOnClickListener(v -> {
            mActionType = ACTION_EDIT;
            reloadView(v.getContext(), false);
        });

        rl = UtilFun.cast_t(pv.findViewById(R.id.rl_act_sort));
        final ImageView iv_sort = UtilFun.cast_t(rl.findViewById(R.id.iv_sort));
        final TextView tv_sort = UtilFun.cast_t(rl.findViewById(R.id.tv_sort));
        iv_sort.setImageDrawable(pv.getContext().getResources()
                .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
        tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
        rl.setOnClickListener(v -> {
            mBTimeDownOrder = !mBTimeDownOrder;

            iv_sort.setImageDrawable(pv.getContext().getResources()
                    .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
            tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

            refreshData();
            refreshView();
        });
    }


    @Override
    public void filterView(List<String> ls_tag) {
        if (null != ls_tag) {
            mBFilter = true;
            mFilterPara.clear();
            mFilterPara.addAll(ls_tag);
            refreshView();
        } else {
            mBFilter = false;
            refreshView();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet:
                if (ACTION_DELETE == mActionType) {
                    boolean bf = false;
                    if (!ToolUtil.ListIsNullOrEmpty(mDelPay)) {
                        ContextUtil.getPayIncomeUtility().deletePayNotes(mDelPay);
                        mDelPay.clear();

                        bf = true;
                    }

                    if (!ToolUtil.ListIsNullOrEmpty(mDelIncome)) {
                        ContextUtil.getPayIncomeUtility().deleteIncomeNotes(mDelIncome);
                        mDelIncome.clear();

                        bf = true;
                    }

                    mActionType = ACTION_EDIT;

                    if (bf)
                        reloadView(v.getContext(), false);
                }

                break;

            case R.id.bt_giveup:
                mActionType = ACTION_EDIT;
                mDelPay.clear();
                mDelIncome.clear();
                refreshAttachLayout();
                break;
        }
    }

    private void refreshAction() {
        if (mBActionExpand) {
            mIVActions.setImageDrawable(mDAHide);
            mGLActions.setVisibility(View.VISIBLE);
            //setLayoutVisible(mGLActions, View.VISIBLE);
        } else {
            mIVActions.setImageDrawable(mDAExpand);
            mGLActions.setVisibility(View.GONE);
            //setLayoutVisible(mGLActions, View.INVISIBLE);
        }
    }

    /**
     * 重新加载数据
     */
    protected void refreshData() {
        super.refreshData();

        mTSLastLoadViewTime.setTime(Calendar.getInstance().getTimeInMillis());

        mMainPara.clear();
        mHMSubPara.clear();

        // for day
        HashMap<String, NoteShowInfo> hm_d = NoteShowDataHelper.getInstance().getDayInfo();
        ArrayList<String> set_k_d = new ArrayList<>(hm_d.keySet());
        Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));

        for (String k : set_k_d) {
            NoteShowInfo ni = hm_d.get(k);
            HashMap<String, String> map = new HashMap<>();
            map.put(K_MONTH, k.substring(0, 7));

            String km = k.substring(8, 10);
            km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
            map.put(K_DAY_NUMEBER, km);
            try {
                Timestamp ts = ToolUtil.StringToTimestamp(k);
                Calendar day = Calendar.getInstance();
                day.setTimeInMillis(ts.getTime());
                map.put(K_DAY_IN_WEEK, getDayInWeek(day.get(Calendar.DAY_OF_WEEK)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

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

            map.put(K_TAG, k);
            map.put(K_SHOW, checkUnfoldItem(k) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
            mMainPara.add(map);
        }

        // for note
        HashMap<String, ArrayList<INote>> hm_v =
                NoteShowDataHelper.getInstance().getNotesForDay();
        for (String k : set_k_d) {
            LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
            ArrayList<INote> v = hm_v.get(k);
            Collections.sort(v, (o1, o2) -> !mBTimeDownOrder ? o1.getTs().compareTo(o2.getTs())
                    : o2.getTs().compareTo(o1.getTs()));
            for (INote r : v) {
                HashMap<String, String> map = new HashMap<>();
                map.put(K_TITLE, r.getInfo());
                map.put(K_ID, String.valueOf(r.getId()));
                map.put(K_TIME, r.getTs().toString().substring(11, 16));

                if (r.isPayNote()) {
                    map.put(K_TYPE, V_TYPE_PAY);
                    map.put(K_AMOUNT, String.format(Locale.CHINA, "- %.02f", r.getVal()));

                    BudgetItem bi = r.getBudget();
                    if (null != bi) {
                        map.put(K_BUDGET, bi.getName());
                    }
                } else {
                    map.put(K_TYPE, V_TYPE_INCOME);
                    map.put(K_AMOUNT, String.format(Locale.CHINA, "+ %.02f", r.getVal()));
                }

                String nt = r.getNote();
                if (!UtilFun.StringIsNullOrEmpty(nt)) {
                    map.put(K_NOTE, nt.length() > 10 ? nt.substring(0, 10) + "..." : nt);
                }

                map.put(K_TAG, k);
                map.put(K_SUB_TAG, k);
                cur_llhm.add(map);
            }

            mHMSubPara.put(k, cur_llhm);
        }
    }

    /**
     * 仅更新视图
     */
    @Override
    protected void refreshView() {
        refreshAttachLayout();

        // load show data
        LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
        if (mBFilter) {
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
            n_mainpara.addAll(mMainPara);
        }

        // 设置listview adapter
        ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.lv_show));
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), n_mainpara,
                new String[]{K_MONTH, K_DAY_NUMEBER, K_DAY_IN_WEEK},
                new int[]{R.id.tv_month, R.id.tv_day_number, R.id.tv_day_in_week});
        lv.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    private void refreshAttachLayout() {
        setAttachLayoutVisible(ACTION_EDIT != mActionType || mBFilter ?
                View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType && !mBFilter ? View.VISIBLE : View.GONE);
    }

    /**
     * 初始化次级(详细数据)视图
     *
     * @param v  主级视图
     * @param hm 主级视图附带数据
     */
    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if (V_SHOW_UNFOLD.equals(hm.get(K_SHOW))) {
            llhm = mHMSubPara.get(hm.get(K_TAG));
        }

        if (null == llhm) {
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
            SelfSubAdapter mAdapter = new SelfSubAdapter(mSelfView.getContext(), mLVShowDetail,
                    llhm, new String[]{}, new int[]{});
            mLVShowDetail.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ToolUtil.setListViewHeightBasedOnChildren(mLVShowDetail);
        }
    }

    /**
     * 首级列表adapter
     */
    private class SelfAdapter extends SimpleAdapter implements OnClickListener {
        private final static String TAG = "SelfAdapter";
        private int mClOne;
        private int mClTwo;

        SelfAdapter(Context context, List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_daily_show, from, to);

            Resources res = context.getResources();
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
            View v = super.getView(position, view, arg2);
            if (null != v) {
                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                init_detail_view(v, hm);

                v.setOnClickListener(this);

                RelativeLayout rl = UtilFun.cast_t(v.findViewById(R.id.rl_header));
                rl.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

                // for show
                RelativeLayout rl_info = UtilFun.cast_t(v.findViewById(R.id.rl_info));
                fillNoteInfo(rl_info, hm.get(K_DAY_PAY_COUNT), hm.get(K_DAY_PAY_AMOUNT),
                        hm.get(K_DAY_INCOME_COUNT), hm.get(K_DAY_INCOME_AMOUNT),
                        hm.get(K_AMOUNT));
            }

            return v;
        }

        @Override
        public void onClick(View view) {
            ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.lv_show));
            int pos = lv.getPositionForView(view);

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
     * 次级列表adapter
     */
    private class SelfSubAdapter extends SimpleAdapter implements OnClickListener {
        private final static String TAG = "SelfSubAdapter";
        private final ListView mRootView;

        private Drawable mDADelete;
        private Drawable mDADedit;
        private int mCLSel;
        private int mCLNoSel;

        SelfSubAdapter(Context context, ListView fv,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_daily_show_detail, from, to);
            mRootView = fv;

            Resources res = context.getResources();
            mDADedit = res.getDrawable(R.drawable.right_arrow);
            mDADelete = res.getDrawable(R.drawable.ic_delete_1);

            mCLSel = res.getColor(R.color.trans_1);
            mCLNoSel = res.getColor(R.color.trans_full);
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
            if (null != v) {
                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                RelativeLayout rl_pay = UtilFun.cast_t(v.findViewById(R.id.rl_pay));
                RelativeLayout rl_income = UtilFun.cast_t(v.findViewById(R.id.rl_income));
                if (V_TYPE_PAY.equals(hm.get(K_TYPE))) {
                    rl_income.setVisibility(View.GONE);
                    init_pay(rl_pay, hm);
                } else {
                    rl_pay.setVisibility(View.GONE);
                    init_income(rl_income, hm);
                }
            }

            return v;
        }

        private void init_pay(RelativeLayout rl_pay, HashMap<String, String> hd) {
            TextView tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_title));
            tv.setText(hd.get(K_TITLE));

            String b_name = hd.get(K_BUDGET);
            if (!UtilFun.StringIsNullOrEmpty(b_name)) {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_budget));
                tv.setText(b_name);
            } else {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_budget));
                tv.setVisibility(View.INVISIBLE);

                ImageView iv = UtilFun.cast_t(rl_pay.findViewById(R.id.iv_pay_budget));
                iv.setVisibility(View.INVISIBLE);
            }

            tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_amount));
            tv.setText(hd.get(K_AMOUNT));

            tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_time));
            tv.setText(hd.get(K_TIME));

            ImageView iv = UtilFun.cast_t(rl_pay.findViewById(R.id.iv_pay_action));
            iv.setOnClickListener(this);
            iv.setImageDrawable(ACTION_EDIT == mActionType ? mDADedit : mDADelete);

            int did = Integer.parseInt(hd.get(K_ID));
            iv.setBackgroundColor(mDelPay.contains(did) ? mCLSel : mCLNoSel);

            // for budget
            String bd = hd.get(K_BUDGET);
            if (UtilFun.StringIsNullOrEmpty(bd)) {
                RelativeLayout rl = UtilFun.cast_t(rl_pay.findViewById(R.id.rl_budget));
                rl.setVisibility(View.GONE);
            }

            // for note
            String nt = hd.get(K_NOTE);
            if (UtilFun.StringIsNullOrEmpty(nt)) {
                RelativeLayout rl = UtilFun.cast_t(rl_pay.findViewById(R.id.rl_pay_note));
                rl.setVisibility(View.GONE);
            } else {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_note));
                tv.setText(nt);
            }
        }

        private void init_income(RelativeLayout rl_income, HashMap<String, String> hd) {
            TextView tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_title));
            tv.setText(hd.get(K_TITLE));

            tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_amount));
            tv.setText(hd.get(K_AMOUNT));

            tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_time));
            tv.setText(hd.get(K_TIME));

            ImageView iv = UtilFun.cast_t(rl_income.findViewById(R.id.iv_income_action));
            iv.setOnClickListener(this);
            iv.setImageDrawable(ACTION_EDIT == mActionType ? mDADedit : mDADelete);

            int did = Integer.parseInt(hd.get(K_ID));
            iv.setBackgroundColor(mDelIncome.contains(did) ? mCLSel : mCLNoSel);

            // for note
            String nt = hd.get(K_NOTE);
            if (UtilFun.StringIsNullOrEmpty(nt)) {
                RelativeLayout rl = UtilFun.cast_t(rl_income.findViewById(R.id.rl_income_note));
                rl.setVisibility(View.GONE);
            } else {
                tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_note));
                tv.setText(nt);
            }
        }


        @Override
        public void onClick(View v) {
            int pos = mRootView.getPositionForView(v);
            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            String tp = hm.get(K_TYPE);
            int did = Integer.parseInt(hm.get(K_ID));

            int vid = v.getId();
            switch (vid) {
                case R.id.iv_pay_action:
                case R.id.iv_income_action: {
                    ImageView iv = UtilFun.cast_t(v);
                    if (ACTION_DELETE == mActionType) {
                        boolean is_pay = V_TYPE_PAY.equals(tp);
                        boolean is_sel = is_pay ? mDelPay.contains(did) : mDelIncome.contains(did);
                        iv.setBackgroundColor(is_sel ? mCLNoSel : mCLSel);
                        if (is_sel) {
                            if (is_pay)
                                mDelPay.remove((Object) did);
                            else
                                mDelIncome.remove((Object) did);
                        } else {
                            if (is_pay)
                                mDelPay.add(did);
                            else
                                mDelIncome.add(did);
                        }
                    } else {
                        ACNoteShow ac = getRootActivity();
                        Intent intent;
                        intent = new Intent(ac, ACPreveiwAndEdit.class);
                        intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, did);
                        intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                                V_TYPE_PAY.equals(tp) ? GlobalDef.STR_RECORD_PAY
                                        : GlobalDef.STR_RECORD_INCOME);

                        ac.startActivityForResult(intent, 1);
                    }
                }
                break;
            }
        }
    }
}
