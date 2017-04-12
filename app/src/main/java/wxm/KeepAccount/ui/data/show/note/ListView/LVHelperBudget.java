package wxm.KeepAccount.ui.data.show.note.ListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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
import cn.wxm.andriodutillib.util.FastViewHolder;
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
public class LVHelperBudget extends LVBase {

    //for action
    private final static int ACTION_DELETE = 1;
    private final static int ACTION_EDIT = 2;
    private int mActionType = ACTION_EDIT;


    public LVHelperBudget() {
        super();
        LOG_TAG = "LVHelperBudget";
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
     * 初始化可隐藏动作条
     */
    @Override
    protected void initActs() {
        mRLActReport.setVisibility(View.GONE);

        ImageView iv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.iv_sort));
        TextView tv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.tv_sort));
        iv_sort.setImageDrawable(mBODownOrder ? LVResource.mDASortUp : LVResource.mDASortDown);
        tv_sort.setText(mBODownOrder ? R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);
    }


    /**
     * 附加动作
     * @param v   动作view
     */
    @OnClick({R.id.rl_act_sort, R.id.rl_act_refresh, R.id.rl_act_delete,
                R.id.rl_act_add})
    public void onActionClick(View v) {
        switch (v.getId())  {
            case R.id.rl_act_sort :     {
                mBODownOrder = !mBODownOrder;

                ImageView iv_sort = UtilFun.cast_t(v.findViewById(R.id.iv_sort));
                TextView tv_sort = UtilFun.cast_t(v.findViewById(R.id.tv_sort));
                iv_sort.setImageDrawable(mBODownOrder ? LVResource.mDASortUp : LVResource.mDASortDown);
                tv_sort.setText(mBODownOrder ? R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);

                reorderData();
                loadUIUtility(true);
            }
            break;

            case R.id.rl_act_refresh :  {
                mActionType = ACTION_EDIT;
                reloadView(getContext(), false);
            }
            break;

            case R.id.rl_act_delete :  {
                if(ACTION_DELETE != mActionType) {
                    mActionType = ACTION_DELETE;
                    reloadView(v.getContext(), false);
                }
            }
            break;

            case R.id.rl_act_add :  {
                ACNoteShow ac = getRootActivity();
                Intent intent = new Intent(ac, ACPreveiwAndEdit.class);
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET);
                ac.startActivityForResult(intent, 1);
            }
            break;
        }
    }


    @OnClick({R.id.bt_accpet, R.id.bt_giveup})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet:
                if (ACTION_DELETE == mActionType) {
                    mActionType = ACTION_EDIT;

                    SelfAdapter sad = UtilFun.cast_t(mLVShow.getAdapter());
                    List<Integer> ls_dels = sad.getWaitDeleteItems();
                    if (!UtilFun.ListIsNullOrEmpty(ls_dels)) {
                        ContextUtil.getBudgetUtility().removeDatas(ls_dels);
                    }
                }

                loadUIUtility(true);
                break;

            case R.id.bt_giveup:
                mActionType = ACTION_EDIT;
                loadUIUtility(true);
                break;
        }
    }

    @Override
    protected void loadUI() {
        loadUIUtility(false);
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute()   {
                mMainPara.clear();
                mHMSubPara.clear();
            }

            @Override
            protected Void doInBackground(Void... params) {
                parseNotes();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // After completing execution of given task, control will return here.
                // Hence if you want to populate UI elements with fetched data, do it here.
                loadUIUtility(true);
                //showLoadingProgress(false);
            }
        }.execute();
    }

    /// BEGIN PRIVATE
    private void refreshAttachLayout() {
        setAttachLayoutVisible(ACTION_EDIT != mActionType ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(View.GONE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType ? View.VISIBLE : View.GONE);
    }

    /**
     * 加载UI的工作
     * @param b_fully   若为true则加载数据
     */
    protected void loadUIUtility(boolean b_fully)    {
        Log.d(LOG_TAG, "in loadUIUtility, b_fully = " + Boolean.toString(b_fully));
        String[] calls = ToolUtil.getCallStack(8);
        for(int i = 0; i < calls.length; ++i)   {
            Log.d(LOG_TAG, "in loadUIUtility, [" + i + "] = " + calls[i]);
        }

        refreshAttachLayout();

        if(b_fully) {
            LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
            n_mainpara.addAll(mMainPara);

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
     * 解析数据
     */
    private void parseNotes() {
        HashMap<BudgetItem, List<PayNoteItem>> mHMData = ContextUtil.getBudgetUtility().getBudgetWithPayNote();
        Set<BudgetItem> set_bi = mHMData.keySet();
        ArrayList<BudgetItem> ls_bi = new ArrayList<>(set_bi);
        Collections.sort(ls_bi, (o1, o2) -> mBODownOrder ? o1.getName().compareTo(o2.getName())
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
            if (!UtilFun.StringIsNullOrEmpty(nt)) {
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


    private void parseSub(String main_tag, List<PayNoteItem> ls_pay) {
        LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
        if (!UtilFun.ListIsNullOrEmpty(ls_pay)) {
            Collections.sort(ls_pay, (o1, o2) -> o1.getTs().compareTo(o2.getTs()));

            for (PayNoteItem i : ls_pay) {
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
                if (!UtilFun.StringIsNullOrEmpty(nt)) {
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
     *
     * @param vh view holder
     * @param hm 主级视图附带数据
     */
    private void init_detail_view(FastViewHolder vh, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm =
                V_SHOW_UNFOLD.equals(hm.get(K_SHOW)) ?
                        mHMSubPara.get(hm.get(K_TAG)) : new LinkedList<>();

        RelativeLayout rl = vh.getView(R.id.rl_detail);
        if (llhm.isEmpty()) {
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
    protected class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        private ArrayList<Integer>   mALWaitDeleteItems = new ArrayList<>();

        private View.OnClickListener mCLAdapter = v -> {
            int vid = v.getId();
            int pos = mLVShow.getPositionForView(v);

            HashMap<String, String> hm = UtilFun.cast(getItem(pos));

            switch (vid)    {
                case R.id.rl_header :   {
                    boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
                    hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);

                    init_detail_view(UtilFun.cast_t(mLVShow.getChildAt(pos).getTag()), hm);
                    if (bf)
                        addUnfoldItem(hm.get(K_TAG));
                    else
                        removeUnfoldItem(hm.get(K_TAG));
                }
                break;

                case R.id.rl_delete :   {
                    String k_tag = hm.get(K_ID);
                    Integer id = Integer.parseInt(k_tag);
                    if(mALWaitDeleteItems.contains(id))  {
                        mALWaitDeleteItems.remove((Object)id);
                        v.setBackgroundColor(LVResource.mCRLVItemNoSel);
                    } else  {
                        mALWaitDeleteItems.add(id);
                        v.setBackgroundColor(LVResource.mCRLVItemSel);
                    }
                }
                break;

                case R.id.iv_edit : {
                    int tag_id = Integer.parseInt(hm.get(K_ID));
                    Activity ac = getRootActivity();
                    Intent it = new Intent(ac, ACPreveiwAndEdit.class);
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, tag_id);
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET);
                    ac.startActivityForResult(it, 1);
                }
                break;
            }
        };


        SelfAdapter(Context context,
                    List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_budget_show, from, to);
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

        public List<Integer> getWaitDeleteItems() {
            return mALWaitDeleteItems;
        }


        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            //View v = super.getView(position, view, arg2);
            FastViewHolder viewHolder = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_budget_show);

            final HashMap<String, String> hm = UtilFun.cast(getItem(position));
            init_detail_view(viewHolder, hm);

            // for background color
            RelativeLayout rl = viewHolder.getView(R.id.rl_header);
            rl.setBackgroundColor(0 == position % 2 ?
                                        LVResource.mCRLVLineOne : LVResource.mCRLVLineTwo);
            rl.setOnClickListener(mCLAdapter);

            // for delete
            RelativeLayout rl_del = viewHolder.getView(R.id.rl_delete);
            rl_del.setVisibility(mActionType == ACTION_EDIT ? View.GONE : View.VISIBLE);
            rl_del.setOnClickListener(mCLAdapter);

            // for note
            String nt = hm.get(K_NOTE);
            if (UtilFun.StringIsNullOrEmpty(nt)) {
                viewHolder.getView(R.id.rl_budget_note).setVisibility(View.GONE);
            } else {
                viewHolder.setText(R.id.tv_budget_note, nt);
            }

            viewHolder.setText(R.id.tv_budget_name, hm.get(K_TITLE));
            viewHolder.setText(R.id.tv_budget_amount, hm.get(K_AMOUNT));

            // for action
            viewHolder.getView(R.id.iv_edit).setOnClickListener(mCLAdapter);
            return viewHolder.getConvertView();
        }
    }


    /**
     * 次级adapter
     */
    private class SelfSubAdapter extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";

        private View.OnClickListener mCLAdapter = v -> {
            int pos = mLVShow.getPositionForView(v);

            HashMap<String, String> hm = UtilFun.cast(getItem(pos));

            ACNoteShow ac = getRootActivity();
            Intent intent;
            intent = new Intent(ac, ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, Integer.valueOf(hm.get(K_ID)));
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                    GlobalDef.STR_RECORD_PAY);

            ac.startActivityForResult(intent, 1);
        };

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
            FastViewHolder vh = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_budget_show_detail);

            HashMap<String, String> hm = UtilFun.cast(getItem(position));
            // for note
            String nt = hm.get(K_NOTE);
            if (UtilFun.StringIsNullOrEmpty(nt)) {
                vh.getView(R.id.rl_pay_note).setVisibility(View.GONE);
            }

            // for show
            vh.setText(R.id.tv_month, hm.get(K_MONTH));
            vh.setText(R.id.tv_day_number, hm.get(K_DAY_NUMEBER));
            vh.setText(R.id.tv_day_in_week, hm.get(K_DAY_IN_WEEK));
            vh.setText(R.id.tv_pay_title, hm.get(K_TITLE));
            vh.setText(R.id.tv_pay_amount, hm.get(K_AMOUNT));
            vh.setText(R.id.tv_pay_note, hm.get(K_NOTE));
            vh.setText(R.id.tv_pay_time, hm.get(K_TIME));

            // for look action
            vh.getView(R.id.iv_look).setOnClickListener(mCLAdapter);
            return vh.getConvertView();
        }
    }
}
