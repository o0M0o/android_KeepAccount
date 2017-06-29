package wxm.KeepAccount.ui.data.show.note.ListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import cn.wxm.andriodutillib.util.FastViewHolder;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.base.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.Note.ACPreveiwAndEdit;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * 预算数据视图辅助类
 * Created by 123 on 2016/9/15.
 */
public class LVBudget extends LVBase {
    //for action
    private final static int ACTION_DELETE = 1;
    private final static int ACTION_EDIT = 2;
    // 排列次序
    protected boolean mBODownOrder = true;
    private int mActionType = ACTION_EDIT;

    public LVBudget() {
        super();
        LOG_TAG = "LVBudget";
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
        mIBReport.setVisibility(View.GONE);

        mIBSort.setActIcon(mBODownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
        mIBSort.setActName(mBODownOrder ? R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);
    }

    /**
     * 附加动作
     *
     * @param v 动作view
     */
    @OnClick({R.id.ib_sort, R.id.ib_refresh, R.id.ib_delete, R.id.ib_add})
    public void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.ib_sort: {
                mBODownOrder = !mBODownOrder;

                mIBSort.setActIcon(mBODownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
                mIBSort.setActName(mBODownOrder ? R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);

                reorderData();
                loadUIUtility(true);
            }
            break;

            case R.id.ib_refresh: {
                mActionType = ACTION_EDIT;
                reloadView(getContext(), false);
            }
            break;

            case R.id.ib_delete: {
                if (ACTION_DELETE != mActionType) {
                    mActionType = ACTION_DELETE;
                    reloadView(v.getContext(), false);
                }
            }
            break;

            case R.id.ib_add: {
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
            protected void onPreExecute() {
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
     *
     * @param b_fully 若为true则加载数据
     */
    protected void loadUIUtility(boolean b_fully) {
        Log.d(LOG_TAG, "in loadUIUtility, b_fully = " + Boolean.toString(b_fully));
        String[] calls = ToolUtil.getCallStack(8);
        for (int i = 0; i < calls.length; ++i) {
            Log.d(LOG_TAG, "in loadUIUtility, [" + i + "] = " + calls[i]);
        }

        refreshAttachLayout();

        if (b_fully) {
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
        mMainPara.clear();
        mHMSubPara.clear();

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
    protected class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        private ArrayList<Integer> mALWaitDeleteItems = new ArrayList<>();

        private View.OnClickListener mCLAdapter = v -> {
            int vid = v.getId();
            int pos = mLVShow.getPositionForView(v);

            HashMap<String, String> hm = UtilFun.cast(getItem(pos));

            switch (vid) {
                case R.id.rl_delete: {
                    String k_tag = hm.get(K_ID);
                    Integer id = Integer.parseInt(k_tag);
                    if (mALWaitDeleteItems.contains(id)) {
                        mALWaitDeleteItems.remove((Object) id);
                        v.setBackgroundColor(ResourceHelper.mCRLVItemNoSel);
                    } else {
                        mALWaitDeleteItems.add(id);
                        v.setBackgroundColor(ResourceHelper.mCRLVItemSel);
                    }
                }
                break;

                case R.id.iv_edit: {
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
            final ListView lv = viewHolder.getView(R.id.lv_show_detail);
            final String tag = hm.get(K_TAG);
            if (V_SHOW_FOLD.equals(hm.get(K_SHOW))) {
                lv.setVisibility(View.GONE);
            } else {
                lv.setVisibility(View.VISIBLE);
                load_detail_view(lv, tag);
            }

            View.OnClickListener local_cl = v -> {
                boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
                hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);

                if (bf) {
                    lv.setVisibility(View.VISIBLE);
                    load_detail_view(lv, tag);

                    addUnfoldItem(tag);
                } else {
                    lv.setVisibility(View.GONE);

                    removeUnfoldItem(tag);
                }
            };

            // for background color
            ConstraintLayout rl = viewHolder.getView(R.id.cl_header);
            rl.setOnClickListener(local_cl);
            rl.setBackgroundColor(0 == position % 2 ?
                    ResourceHelper.mCRLVLineOne : ResourceHelper.mCRLVLineTwo);

            // for delete
            View rl_del = viewHolder.getView(R.id.rl_delete);
            rl_del.setVisibility(mActionType == ACTION_EDIT ? View.GONE : View.VISIBLE);
            rl_del.setOnClickListener(mCLAdapter);

            // for note
            String nt = hm.get(K_NOTE);
            boolean b_nt = UtilFun.StringIsNullOrEmpty(nt);
            viewHolder.getView(R.id.iv_note).setVisibility(b_nt ? View.GONE : View.VISIBLE);
            viewHolder.getView(R.id.tv_budget_note).setVisibility(b_nt ? View.GONE : View.VISIBLE);
            if (!b_nt)
                viewHolder.setText(R.id.tv_budget_note, nt);

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
