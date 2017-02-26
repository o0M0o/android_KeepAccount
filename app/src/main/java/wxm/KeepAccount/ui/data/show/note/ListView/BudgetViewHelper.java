package wxm.KeepAccount.ui.data.show.note.ListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import java.util.Set;


import butterknife.OnClick;
import cn.wxm.andriodutillib.util.UtilFun;
import butterknife.ButterKnife;
import butterknife.BindColor;
import butterknife.BindDrawable;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.edit.Note.ACPreveiwAndEdit;

/**
 * 预算数据视图辅助类
 * Created by 123 on 2016/9/15.
 */
public class BudgetViewHelper  extends LVShowDataBase {
    private final static String TAG = "BudgetViewHelper";

    // 若为true,数据按照名称降序排列
    private boolean mBNameDownOrder = true;

    //for action
    private final static int ACTION_DELETE  = 1;
    private final static int ACTION_EDIT    = 2;
    private int mActionType = ACTION_EDIT;

    // for delete
    private final LinkedList<Integer> mLLDelBudget    = new LinkedList<>();
    //private LinkedList<View>    mLLDelVW        = new LinkedList<>();

    // original data
    private HashMap<BudgetItem, List<PayNoteItem>>  mHMData;

    public BudgetViewHelper()    {
        super();
        LOG_TAG = "BudgetViewHelper";
    }


    /**
     * 过滤视图事件
     * @param event     事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
    }

    /**
     * 初始化可隐藏动作条
     */
    @Override
    protected void initActs() {
        mRLActReport.setVisibility(View.GONE);
        mRLActAdd.setOnClickListener(v -> {
            ACNoteShow ac = getRootActivity();
            Intent intent = new Intent(ac, ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET);
            ac.startActivityForResult(intent, 1);
        });

        mRLActDelete.setOnClickListener(v -> {
            mActionType = ACTION_DELETE;
            loadUI();
        });

        mRLActRefresh.setOnClickListener(v -> {
            mActionType = ACTION_EDIT;
            reloadView(v.getContext(), false);
        });

        final ImageView iv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.iv_sort));
        final TextView tv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.tv_sort));
        iv_sort.setImageDrawable(getContext().getResources()
                .getDrawable(mBNameDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
        tv_sort.setText(mBNameDownOrder ? R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);
        mRLActSort.setOnClickListener(v -> {
            mBNameDownOrder = !mBNameDownOrder;

            iv_sort.setImageDrawable(getContext().getResources()
                    .getDrawable(mBNameDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
            tv_sort.setText(mBNameDownOrder ? R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);

            reloadData();
            loadUI();
        });
    }


    @OnClick({R.id.bt_accpet, R.id.bt_giveup})
    public void onAccpetOrGiveupClick(View v) {

        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_accpet :
                if(ACTION_DELETE == mActionType)    {
                    mActionType = ACTION_EDIT;
                    if(!UtilFun.ListIsNullOrEmpty(mLLDelBudget)) {
                        ContextUtil.getBudgetUtility().removeDatas(mLLDelBudget);
                    }
                }

                mLLDelBudget.clear();
                break;

            case R.id.bt_giveup :
                mActionType = ACTION_EDIT;
                mLLDelBudget.clear();
                refreshAttachLayout();
                break;
        }
    }

    @Override
    protected void loadUI() {
        refreshAttachLayout();

        LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
        n_mainpara.addAll(mMainPara);

        // 设置listview adapter
        SelfAdapter mSNAdapter = new SelfAdapter(getContext(), mLVShow, n_mainpara,
                new String[]{K_TITLE, K_AMOUNT, K_NOTE},
                new int[]{R.id.tv_budget_name, R.id.tv_budget_amount, R.id.tv_budget_note});
        mLVShow.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        mMainPara.clear();
        mHMSubPara.clear();

        // format output
        mHMData = ContextUtil.getBudgetUtility().getBudgetWithPayNote();
        parseNotes();
    }


    private void refreshAttachLayout()    {
        setAttachLayoutVisible(ACTION_EDIT != mActionType ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(View.GONE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType ? View.VISIBLE : View.GONE);
    }


    /**
     * 重新加载数据
     */
    private void reloadData() {
    }

    /**
     * 解析数据
     */
    private void parseNotes()   {
        mMainPara.clear();
        mHMSubPara.clear();

        Set<BudgetItem> set_bi = mHMData.keySet();
        ArrayList<BudgetItem> ls_bi = new ArrayList<>(set_bi);
        Collections.sort(ls_bi, (o1, o2) -> mBNameDownOrder ? o1.getName().compareTo(o2.getName())
                    : o2.getName().compareTo(o1.getName()));
        for (BudgetItem i : ls_bi) {
            List<PayNoteItem> ls_pay = mHMData.get(i);
            String tag = String.valueOf(i.get_id());

            String show_str = String.format(Locale.CHINA,
                    "(总额)%.02f/(剩余)%.02f",
                    i.getAmount(), i.getRemainderAmount());

            HashMap<String, String> map = new HashMap<>();
            map.put(K_TITLE, i.getName());
            String nt = i.getNote();
            if(!UtilFun.StringIsNullOrEmpty(nt))    {
                map.put(K_NOTE, nt);
            }
            map.put(K_AMOUNT, show_str);
            map.put(K_TAG, tag);
            map.put(K_ID, tag);
            map.put(K_SHOW, checkUnfoldItem(tag) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
            mMainPara.add(map);

            parseSub(tag, ls_pay);
        }
    }


    private void parseSub(String main_tag, List<PayNoteItem> ls_pay)   {
        LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
        if(!UtilFun.ListIsNullOrEmpty(ls_pay)) {
            Collections.sort(ls_pay, (o1, o2) -> o1.getTs().compareTo(o2.getTs()));

            for(PayNoteItem i : ls_pay)     {
                String all_date = i.getTs().toString();
                HashMap<String, String> map = new HashMap<>();
                map.put(K_MONTH, all_date.substring(0, 7));

                String km = all_date.substring(8, 10);
                km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
                map.put(K_DAY_NUMEBER, km);
                try {
                    Timestamp ts = ToolUtil.StringToTimestamp(all_date);
                    Calendar day = Calendar.getInstance();
                    day.setTimeInMillis(ts.getTime());
                    map.put(K_DAY_IN_WEEK, ToolUtil.getDayInWeek(day.get(Calendar.DAY_OF_WEEK)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String nt = i.getNote();
                if(!UtilFun.StringIsNullOrEmpty(nt))    {
                    map.put(K_NOTE, nt.length() > 10 ? nt.substring(0, 10) + "..." : nt);
                }

                map.put(K_TIME, all_date.substring(11, 16));
                map.put(K_TITLE, i.getInfo());
                map.put(K_AMOUNT, String.format(Locale.CHINA, "%.02f", i.getVal()));
                map.put(K_TAG, main_tag);
                map.put(K_SUB_TAG, i.getTs().toString().substring(0, 10));
                map.put(K_ID, String.valueOf(i.getId()));
                cur_llhm.add(map);
            }
        }
        mHMSubPara.put(main_tag, cur_llhm);
    }


    /**
     * 初始化次级(详细数据)视图
     * @param v         主级视图
     * @param hm        主级视图附带数据
     */
    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm =
                V_SHOW_UNFOLD.equals(hm.get(K_SHOW)) ?
                    mHMSubPara.get(hm.get(K_TAG)) : new LinkedList<>();

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
        SelfSubAdapter mAdapter= new SelfSubAdapter(getContext(), llhm,
                                        new String[]{K_MONTH, K_DAY_NUMEBER, K_DAY_IN_WEEK,
                                                    K_TITLE, K_AMOUNT, K_NOTE,
                                                    K_TIME},
                                        new int[]{R.id.tv_month, R.id.tv_day_number, R.id.tv_day_in_week,
                                                  R.id.tv_pay_title, R.id.tv_pay_amount, R.id.tv_pay_note,
                                                    R.id.tv_pay_time});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ListViewHelper.setListViewHeightBasedOnChildren(mLVShowDetail);
    }

    /**
     * 首级adapter
     */
    protected class SelfAdapter extends SimpleAdapter  {
        private final static String TAG = "SelfAdapter";
        private final ListView        mRootView;

        @BindColor(R.color.color_1)
        int mClOne;

        @BindColor(R.color.color_2)
        int mClTwo;

        @BindColor(R.color.trans_1)
        int         mClSel;

        @BindColor(R.color.trans_full)
        int         mClNoSel;

        @BindDrawable(R.drawable.ic_delete_1)
        Drawable    mDADelete;

        @BindDrawable(R.drawable.right_arrow)
        Drawable    mDAEdit;

        SelfAdapter(Context context, ListView fv,
                    List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_budget_show, from, to);
            mRootView = fv;

            ButterKnife.bind(this, mRootView);
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
                // for background color
                final HashMap<String, String> hm = UtilFun.cast(getItem(position));
                final View fv = v;

                RelativeLayout rl = UtilFun.cast_t(v.findViewById(R.id.rl_header));
                rl.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

                // for note
                String nt = hm.get(K_NOTE);
                if(UtilFun.StringIsNullOrEmpty(nt)) {
                    rl = UtilFun.cast_t(v.findViewById(R.id.rl_budget_note));
                    rl.setVisibility(View.GONE);
                }

                // for fold/unfold
                v.setOnClickListener(view1 -> {
                    boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
                    hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);
                    init_detail_view(fv, hm);

                    if(bf)
                        addUnfoldItem(hm.get(K_TAG));
                    else
                        removeUnfoldItem(hm.get(K_TAG));
                });

                // for action
                ImageView ib_action = UtilFun.cast_t(v.findViewById(R.id.iv_delete));
                if(ACTION_DELETE == mActionType) {
                    ib_action.setImageDrawable(mDADelete);

                    int tag_id = Integer.parseInt(hm.get(K_ID));
                    boolean bc = mLLDelBudget.contains(tag_id);
                    ib_action.setBackgroundColor(bc ? mClSel : mClNoSel);
                } else  {
                    ib_action.setImageDrawable(mDAEdit);
                    ib_action.setBackgroundColor(mClNoSel);
                }

                ib_action.setOnClickListener(v1 -> {
                    int tag_id = Integer.parseInt(hm.get(K_ID));
                    if(ACTION_DELETE == mActionType)    {
                        //ib_action.setImageDrawable(mDADelete);
                        if(mLLDelBudget.contains(tag_id))  {
                            mLLDelBudget.remove((Object)tag_id);
                            ib_action.setBackgroundColor(mClNoSel);
                        }   else    {
                            mLLDelBudget.add(tag_id);
                            ib_action.setBackgroundColor(mClSel);
                        }
                    } else  {
                        Activity ac = getRootActivity();
                        Intent it = new Intent(ac, ACPreveiwAndEdit.class);
                        it.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, tag_id);
                        it.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET);
                        ac.startActivityForResult(it, 1);
                    }
                });
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
            super(context, sdata, R.layout.li_budget_show_detail, from, to);
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
                final String sub_id = hm.get(K_ID);

                // for note
                String nt = hm.get(K_NOTE);
                if(UtilFun.StringIsNullOrEmpty(nt)) {
                    RelativeLayout rl = UtilFun.cast_t(v.findViewById(R.id.rl_pay_note));
                    rl.setVisibility(View.GONE);
                }

                ImageView iv = UtilFun.cast_t(v.findViewById(R.id.iv_look));
                iv.setOnClickListener(v1 -> {
                    ACNoteShow ac = getRootActivity();
                    Intent intent;
                    intent = new Intent(ac, ACPreveiwAndEdit.class);
                    intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, Integer.valueOf(sub_id));
                    intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                            GlobalDef.STR_RECORD_PAY);

                    ac.startActivityForResult(intent, 1);
                });
            }

            return v;
        }
    }
}
