package wxm.KeepAccount.ui.fragment.ListView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.INote;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.fragment.base.LVShowDataBase;

/**
 * 日数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class DailyLVNewHelper extends LVShowDataBase
        implements OnClickListener {
    private final static String TAG = "DailyLVNewHelper";

    /// list item data begin
    final static String K_TITLE     = "k_title";
    final static String K_TIME      = "k_time";
    final static String K_BUDGET    = "k_budget";
    final static String K_AMOUNT    = "k_amount";
    final static String K_TAG       = "k_tag";
    final static String K_ID        = "k_id";

    final static String K_TYPE          = "k_type";
    final static String V_TYPE_DAY      = "v_day";
    final static String V_TYPE_PAY      = "v_pay";
    final static String V_TYPE_INCOME   = "v_income";
    /// list item data end

    // for action
    //private final static int ACTION_NONE    = 0;
    private final static int ACTION_DELETE  = 1;
    private final static int ACTION_EDIT    = 2;
    private int mActionType = ACTION_EDIT;

    // delete data
    private final LinkedList<Integer> mDelPay;
    private final LinkedList<Integer> mDelIncome;

    private static final int[] ITEM_DRAWABLES = {
                                    R.drawable.ic_edit
                                    ,R.drawable.ic_delete
                                    ,R.drawable.ic_add};

    public DailyLVNewHelper()    {
        super();

        mDelPay    = new LinkedList<>();
        mDelIncome = new LinkedList<>();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView       = inflater.inflate(R.layout.lv_newpager, container, false);
        mBFilter        = false;

        return mSelfView;
    }

    @Override
    public void loadView() {
        reloadData();
        refreshView();
    }

    @Override
    public void checkView() {
        if(getRootActivity().getDayNotesDirty())
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
                reloadData();
                refreshView();
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
                if(ACTION_DELETE == mActionType) {
                    boolean dirty = false;
                    if(!ToolUtil.ListIsNullOrEmpty(mDelPay)) {
                        AppModel.getPayIncomeUtility().DeletePayNotes(mDelPay);
                        mDelPay.clear();
                        dirty = true;
                    }

                    if(!ToolUtil.ListIsNullOrEmpty(mDelIncome)) {
                        AppModel.getPayIncomeUtility().DeleteIncomeNotes(mDelIncome);
                        mDelIncome.clear();
                        dirty = true;
                    }

                    if(dirty) {
                        getRootActivity().setNotesDirty();
                        reloadData();
                    }

                    mActionType = ACTION_EDIT;
                    refreshView();
                }

                break;

            case R.id.bt_giveup :
                mActionType = ACTION_EDIT;
                mDelPay.clear();
                mDelIncome.clear();
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

        HashMap<String, ArrayList<INote>> hm_data = getRootActivity().getNotesByDay();
        parseNotes(hm_data);
    }

    /**
     * 仅更新视图
     */
    @Override
    protected void refreshView()  {
        refreshAttachLayout();

        // load show data
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
                                        new String[]{K_TITLE, K_ABSTRACT},
                                        new int[]{R.id.tv_title, R.id.tv_abstract});
        lv.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    private void refreshAttachLayout()    {
        setAttachLayoutVisible(ACTION_EDIT != mActionType || mBFilter ?
                                View.VISIBLE : View.INVISIBLE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 初始化次级(详细数据)视图
     * @param v         主级视图
     * @param hm        主级视图附带数据
     */
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
        SelfSubAdapter mAdapter = new SelfSubAdapter(mSelfView.getContext(), mLVShowDetail,
                                        llhm, new String[]{}, new int[]{});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ToolUtil.setListViewHeightBasedOnChildren(mLVShowDetail);
    }

    /**
     * 解析支出/收入数据
     * @param notes   待解析数据（日期---数据HashMap）
     */
    private void parseNotes(HashMap<String, ArrayList<INote>> notes)   {
        ArrayList<String> set_k = new ArrayList<>(notes.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            String title = ToolUtil.FormatDateString(k);
            ArrayList<INote> v = notes.get(k);

            LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;
            for (INote r : v) {
                HashMap<String, String> map = new HashMap<>();
                map.put(K_TITLE, r.getInfo());
                map.put(K_ID, String.valueOf(r.getId()));

                if (r.isPayNote()) {
                    map.put(K_TYPE, V_TYPE_PAY);
                    map.put(K_AMOUNT, String.format(Locale.CHINA, "- %.02f", r.getVal()));

                    BudgetItem bi = r.getBudget();
                    if(null != bi)  {
                        map.put(K_BUDGET, String.format(Locale.CHINA, "使用预算 : %s", bi.getName()));
                    }

                    pay_cout += 1;
                    pay_amount = pay_amount.add(r.getVal());
                } else {
                    map.put(K_TYPE, V_TYPE_INCOME);
                    map.put(K_AMOUNT, String.format(Locale.CHINA, "+ %.02f", r.getVal()));

                    income_cout += 1;
                    income_amount = income_amount.add(r.getVal());
                }

                map.put(K_TAG, title);
                cur_llhm.add(map);
            }

            String show_str =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(K_TITLE, title);
            map.put(K_ABSTRACT, show_str);
            map.put(K_TAG, title);
            map.put(K_SHOW, checkUnfoldItem(title) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
            mMainPara.add(map);

            mHMSubPara.put(title, cur_llhm);
        }
    }


    /**
     * 首级列表adapter
     */
    private class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";
        private int         mClOne;
        private int         mClTwo;

        private Drawable    mDAFold;
        private Drawable    mDAUnFold;

        SelfAdapter(Context context, List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_daily_show, from, to);

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
                final Resources res = v.getResources();
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i(TAG, "onIbClick at pos = " + pos);
                        Resources res = v.getResources();
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
            }

            return v;
        }
    }


    /**
     * 次级列表adapter
     */
    private class SelfSubAdapter  extends SimpleAdapter implements OnClickListener {
        private final static String TAG = "SelfSubAdapter";
        private final ListView        mRootView;

        private Drawable    mDADelete;
        private Drawable    mDADedit;
        private int         mClSelected;

        SelfSubAdapter(Context context, ListView fv,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_daily_new_show_detail, from, to);
            mRootView = fv;

            Resources res   = context.getResources();
            mDADedit        = res.getDrawable(R.drawable.right_arrow);
            mDADelete       = res.getDrawable(R.drawable.ic_delete);
            mClSelected     = res.getColor(R.color.red);
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
                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                RelativeLayout rl_pay = UtilFun.cast_t(v.findViewById(R.id.rl_pay));
                RelativeLayout rl_income = UtilFun.cast_t(v.findViewById(R.id.rl_income));
                if(V_TYPE_PAY.equals(hm.get(K_TYPE)))   {
                    ToolUtil.setViewGroupVisible(rl_income, View.INVISIBLE);
                    init_pay(rl_pay, hm);
                } else  {
                    ToolUtil.setViewGroupVisible(rl_pay, View.INVISIBLE);
                    init_income(rl_income, hm);
                }
            }

            return v;
        }

        private void init_pay(RelativeLayout rl_pay, HashMap<String, String> hd)    {
            TextView tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_title));
            tv.setText(hd.get(K_TITLE));

            String b_name = hd.get(K_BUDGET);
            if(!UtilFun.StringIsNullOrEmpty(b_name)) {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_budget));
                tv.setText(b_name);
            } else  {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_budget));
                tv.setVisibility(View.INVISIBLE);

                ImageView iv = UtilFun.cast_t(rl_pay.findViewById(R.id.iv_pay_budget));
                iv.setVisibility(View.INVISIBLE);
            }

            tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_amount));
            tv.setText(hd.get(K_AMOUNT));

            ImageView iv = UtilFun.cast_t(rl_pay.findViewById(R.id.iv_pay_action));
            iv.setOnClickListener(this);
            if(ACTION_EDIT == mActionType)    {
                iv.setImageDrawable(mDADedit);
            }   else    {
                iv.setImageDrawable(mDADelete);
            }
        }

        private void init_income(RelativeLayout rl_income, HashMap<String, String> hd)    {
            TextView tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_title));
            tv.setText(hd.get(K_TITLE));

            tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_amount));
            tv.setText(hd.get(K_AMOUNT));

            ImageView iv = UtilFun.cast_t(rl_income.findViewById(R.id.iv_income_action));
            iv.setOnClickListener(this);
            if(ACTION_EDIT == mActionType)    {
                iv.setImageDrawable(mDADedit);
            }   else    {
                iv.setImageDrawable(mDADelete);
            }
        }


        @Override
        public void onClick(View v) {
            int pos = mRootView.getPositionForView(v);
            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            String tp = hm.get(K_TYPE);
            int did = Integer.parseInt(hm.get(K_ID));

            int vid = v.getId();
            switch (vid)    {
                case R.id.iv_pay_action :
                case R.id.iv_income_action :       {
                    ImageView iv = UtilFun.cast_t(v);
                    if(ACTION_DELETE == mActionType)    {
                        if(iv.isSelected())  {
                            if (V_TYPE_PAY.equals(tp)) {
                                mDelPay.removeFirstOccurrence(did);
                            } else {
                                mDelIncome.removeFirstOccurrence(did);
                            }

                            iv.getBackground().setAlpha(0);
                        }   else    {
                            if (V_TYPE_PAY.equals(tp)) {
                                mDelPay.add(did);
                            } else {
                                mDelIncome.add(did);
                            }

                            iv.getBackground().setAlpha(255);
                            iv.setBackgroundColor(mClSelected);
                        }

                        iv.setSelected(!iv.isSelected());
                    } else  {
                        ACNoteShow ac = getRootActivity();
                        Intent intent = new Intent(ac, ACNoteEdit.class);
                        intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_MODIFY);
                        if (V_TYPE_PAY.equals(tp)) {
                            intent.putExtra(ACNoteEdit.PARA_NOTE_PAY, did);
                        } else {
                            intent.putExtra(ACNoteEdit.PARA_NOTE_INCOME, did);
                        }

                        ac.startActivityForResult(intent, 1);
                    }
                }
                break;
            }
        }
    }
}
